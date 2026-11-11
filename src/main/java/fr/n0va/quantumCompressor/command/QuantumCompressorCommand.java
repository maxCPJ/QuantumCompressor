package fr.n0va.quantumCompressor.command;

import fr.n0va.quantumCompressor.QuantumCompressor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class QuantumCompressorCommand implements CommandExecutor, TabCompleter {

    private final QuantumCompressor plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public QuantumCompressorCommand(QuantumCompressor plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("quantumcompressor.admin")) {
                sender.sendMessage(miniMessage.deserialize("<red>You don't have permission to use this command.</red>"));
                return true;
            }

            plugin.getRecipeRegistry().loadRecipes();
            int count = plugin.getRecipeRegistry().getAllRecipes().size();
            sender.sendMessage(miniMessage.deserialize("<green>QuantumCompressor reloaded: <aqua>" + count + "</aqua> recipes registered.</green>"));
            return true;
        }

        sender.sendMessage(miniMessage.deserialize("<gold><bold>QuantumCompressor</bold></gold> <gray>v" + plugin.getDescription().getVersion() + " by Maxime</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/compress <gray>- Manually compress inventory</gray>"));
        sender.sendMessage(miniMessage.deserialize("<yellow>/autocompress <gray>- Toggle pickup auto-compress</gray>"));
        if (sender.hasPermission("quantumcompressor.admin")) {
            sender.sendMessage(miniMessage.deserialize("<yellow>/qc reload <gray>- Reload recipe cache</gray>"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("quantumcompressor.admin")) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }
}