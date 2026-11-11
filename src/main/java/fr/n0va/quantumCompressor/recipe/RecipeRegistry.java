package fr.n0va.quantumCompressor.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class RecipeRegistry {

    private final Map<Material, CompressionRecipe> compressionRecipes = new EnumMap<>(Material.class);

    public void loadRecipes() {
        compressionRecipes.clear();
        Iterator<Recipe> iterator = Bukkit.recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (!(recipe instanceof ShapedRecipe shaped)) {
                continue;
            }

            String[] shape = shaped.getShape();
            int rows = shape.length;
            int cols = shape[0].length();

            if ((rows == 3 && cols == 3) || (rows == 2 && cols == 2)) {
                checkAndRegisterHomogeneousRecipe(shaped, rows * cols);
            }
        }
    }

    private void checkAndRegisterHomogeneousRecipe(ShapedRecipe recipe, int expectedSlots) {
        Map<Character, RecipeChoice> choiceMap = recipe.getChoiceMap();
        Material singleMaterial = null;
        int count = 0;

        for (String row : recipe.getShape()) {
            for (char c : row.toCharArray()) {
                RecipeChoice choice = choiceMap.get(c);
                if (choice == null) {
                    return;
                }

                if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    List<Material> choices = materialChoice.getChoices();
                    if (choices.size() != 1) {
                        return;
                    }

                    Material choiceMat = choices.getFirst();
                    if (singleMaterial == null) {
                        singleMaterial = choiceMat;
                    } else if (singleMaterial != choiceMat) {
                        return;
                    }
                } else {
                    return;
                }

                count++;
            }
        }

        if (count == expectedSlots && singleMaterial != null) {
            if (!compressionRecipes.containsKey(singleMaterial) || expectedSlots == 9) {
                compressionRecipes.put(singleMaterial, new CompressionRecipe(
                        singleMaterial,
                        expectedSlots,
                        recipe.getResult()
                ));
            }
        }
    }

    public Optional<CompressionRecipe> getRecipe(Material material) {
        return Optional.ofNullable(compressionRecipes.get(material));
    }

    public Map<Material, CompressionRecipe> getAllRecipes() {
        return Collections.unmodifiableMap(compressionRecipes);
    }
}