package fr.n0va.quantumCompressor.command;

import fr.n0va.quantumCompressor.manager.CompressManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CompressCommand implements CommandExecutor {

    private final CompressManager compressManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public CompressCommand(CompressManager compressManager) {
        this.compressManager = compressManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize("<red>Only players can execute this command.</red>"));
            return true;
        }

        if (!player.hasPermission("quantumcompressor.use")) {
            player.sendMessage(miniMessage.deserialize("<red>You do not have permission to use this command.</red>"));
            return true;
        }

        compressManager.compressAsync(player, false, consumed -> {
            if (consumed > 0) {
                player.sendMessage(miniMessage.deserialize("<green>Successfully compressed <aqua>" + consumed + "</aqua> items.</green>"));
            } else {
                player.sendMessage(miniMessage.deserialize("<gray>No compressible items found in your inventory.</gray>"));
            }
        });

        return true;
    }
}