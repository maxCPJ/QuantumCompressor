package fr.n0va.quantumCompressor.recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public record CompressionRecipe(
        Material sourceMaterial,
        int requiredAmount,
        ItemStack resultItem
) {

    public boolean matches(ItemStack item) {
        if (item == null || item.getType() != sourceMaterial) {
            return false;
        }
        return !item.hasItemMeta();
    }
}