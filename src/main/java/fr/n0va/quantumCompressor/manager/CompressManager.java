package fr.n0va.quantumCompressor.manager;

import fr.n0va.quantumCompressor.QuantumCompressor;
import fr.n0va.quantumCompressor.recipe.CompressionRecipe;
import fr.n0va.quantumCompressor.recipe.RecipeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CompressManager {

    private final QuantumCompressor plugin;
    private final RecipeRegistry recipeRegistry;
    private final Set<UUID> autoCompressPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> pendingTransactions = ConcurrentHashMap.newKeySet();

    public CompressManager(QuantumCompressor plugin, RecipeRegistry recipeRegistry) {
        this.plugin = plugin;
        this.recipeRegistry = recipeRegistry;
    }

    public boolean toggleAutoCompress(UUID playerUuid) {
        if (autoCompressPlayers.contains(playerUuid)) {
            autoCompressPlayers.remove(playerUuid);
            return false;
        } else {
            autoCompressPlayers.add(playerUuid);
            return true;
        }
    }

    public boolean hasAutoCompressEnabled(UUID playerUuid) {
        return autoCompressPlayers.contains(playerUuid);
    }

    public void compressAsync(Player player, boolean silent, Consumer<Integer> callback) {
        UUID uuid = player.getUniqueId();

        if (!pendingTransactions.add(uuid)) {
            return;
        }

        ItemStack[] contents = player.getInventory().getStorageContents();
        List<Material> presentMaterials = new ArrayList<>();
        for (ItemStack item : contents) {
            if (item != null && !item.getType().isAir() && !item.hasItemMeta()) {
                presentMaterials.add(item.getType());
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean hasPotential = false;
            for (Material mat : presentMaterials) {
                if (recipeRegistry.getRecipe(mat).isPresent()) {
                    hasPotential = true;
                    break;
                }
            }

            if (!hasPotential) {
                pendingTransactions.remove(uuid);
                if (callback != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> callback.accept(0));
                }
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    if (!player.isOnline()) {
                        if (callback != null) callback.accept(0);
                        return;
                    }

                    int totalConsumed = executeCascadeCompression(player);
                    if (totalConsumed > 0 && !silent) {
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);
                    }
                    if (callback != null) {
                        callback.accept(totalConsumed);
                    }
                } finally {
                    pendingTransactions.remove(uuid);
                }
            });
        });
    }

    private int executeCascadeCompression(Player player) {
        PlayerInventory inventory = player.getInventory();
        int totalConsumed = 0;
        boolean changed;
        int maxPasses = 8;
        int currentPass = 0;

        do {
            changed = false;
            currentPass++;

            Map<Material, Integer> counts = new HashMap<>();
            ItemStack[] contents = inventory.getStorageContents();

            for (ItemStack item : contents) {
                if (item == null || item.getType().isAir() || item.hasItemMeta()) {
                    continue;
                }
                Optional<CompressionRecipe> optionalRecipe = recipeRegistry.getRecipe(item.getType());
                if (optionalRecipe.isPresent() && optionalRecipe.get().matches(item)) {
                    counts.merge(item.getType(), item.getAmount(), Integer::sum);
                }
            }

            for (Map.Entry<Material, Integer> entry : counts.entrySet()) {
                Material material = entry.getKey();
                int totalAmount = entry.getValue();

                CompressionRecipe recipe = recipeRegistry.getRecipe(material).orElse(null);
                if (recipe == null) continue;

                int sets = totalAmount / recipe.requiredAmount();
                if (sets <= 0) continue;

                int amountToConsume = sets * recipe.requiredAmount();
                int amountToProduce = sets * recipe.resultItem().getAmount();

                removeItems(inventory, material, amountToConsume);

                ItemStack result = recipe.resultItem().clone();
                result.setAmount(amountToProduce);

                HashMap<Integer, ItemStack> overflow = inventory.addItem(result);
                for (ItemStack leftover : overflow.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }

                totalConsumed += amountToConsume;
                changed = true;
            }

        } while (changed && currentPass < maxPasses);

        return totalConsumed;
    }

    private void removeItems(PlayerInventory inventory, Material material, int amountToRemove) {
        ItemStack[] contents = inventory.getStorageContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType() != material || item.hasItemMeta()) {
                continue;
            }

            int stackAmount = item.getAmount();
            if (stackAmount <= amountToRemove) {
                amountToRemove -= stackAmount;
                contents[i] = null;
            } else {
                item.setAmount(stackAmount - amountToRemove);
                amountToRemove = 0;
            }

            if (amountToRemove <= 0) {
                break;
            }
        }
        inventory.setStorageContents(contents);
    }
}