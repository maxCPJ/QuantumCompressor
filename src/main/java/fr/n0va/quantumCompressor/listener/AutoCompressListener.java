package fr.n0va.quantumCompressor.listener;

import fr.n0va.quantumCompressor.QuantumCompressor;
import fr.n0va.quantumCompressor.manager.CompressManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class AutoCompressListener implements Listener {

    private final QuantumCompressor plugin;
    private final CompressManager compressManager;

    public AutoCompressListener(QuantumCompressor plugin, CompressManager compressManager) {
        this.plugin = plugin;
        this.compressManager = compressManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!compressManager.hasAutoCompressEnabled(player.getUniqueId())) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                compressManager.compressAsync(player, true, null);
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        if (!compressManager.hasAutoCompressEnabled(player.getUniqueId())) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                compressManager.compressAsync(player, true, null);
            }
        }, 1L);
    }
}