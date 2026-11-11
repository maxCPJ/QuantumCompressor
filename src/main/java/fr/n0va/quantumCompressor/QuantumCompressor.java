package fr.n0va.quantumCompressor;

import fr.n0va.quantumCompressor.command.AutoCompressCommand;
import fr.n0va.quantumCompressor.command.CompressCommand;
import fr.n0va.quantumCompressor.command.QuantumCompressorCommand;
import fr.n0va.quantumCompressor.listener.AutoCompressListener;
import fr.n0va.quantumCompressor.manager.CompressManager;
import fr.n0va.quantumCompressor.recipe.RecipeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class QuantumCompressor extends JavaPlugin {

    private static QuantumCompressor instance;
    private RecipeRegistry recipeRegistry;
    private CompressManager compressManager;

    @Override
    public void onEnable() {
        instance = this;

        this.recipeRegistry = new RecipeRegistry();
        this.recipeRegistry.loadRecipes();
        getLogger().info(String.format("Loaded %d dynamic compression recipes.", recipeRegistry.getAllRecipes().size()));

        this.compressManager = new CompressManager(this, recipeRegistry);

        Objects.requireNonNull(getCommand("compress")).setExecutor(new CompressCommand(compressManager));
        Objects.requireNonNull(getCommand("autocompress")).setExecutor(new AutoCompressCommand(compressManager));

        QuantumCompressorCommand mainCmd = new QuantumCompressorCommand(this);
        Objects.requireNonNull(getCommand("quantumcompressor")).setExecutor(mainCmd);
        Objects.requireNonNull(getCommand("quantumcompressor")).setTabCompleter(mainCmd);

        Bukkit.getPluginManager().registerEvents(new AutoCompressListener(this, compressManager), this);

        getLogger().info("QuantumCompressor has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("QuantumCompressor has been disabled.");
        instance = null;
    }

    public static QuantumCompressor getInstance() {
        return instance;
    }

    public RecipeRegistry getRecipeRegistry() {
        return recipeRegistry;
    }

    public CompressManager getCompressManager() {
        return compressManager;
    }
}