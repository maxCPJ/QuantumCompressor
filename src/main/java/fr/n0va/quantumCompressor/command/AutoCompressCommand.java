package fr.n0va.quantumCompressor.command;

import fr.n0va.quantumCompressor.manager.CompressManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AutoCompressCommand implements CommandExecutor {

    private final CompressManager compressManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public AutoCompressCommand(CompressManager compressManager) {
        this.compressManager = compressManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize("<red>Only players can execute this command.</red>"));
            return true;
        }

        if (!player.hasPermission("quantumcompressor.auto")) {
            player.sendMessage(miniMessage.deserialize("<red>You do not have permission to use this command.</red>"));
            return true;
        }

        boolean enabled = compressManager.toggleAutoCompress(player.getUniqueId());
        if (enabled) {
            player.sendMessage(miniMessage.deserialize("<green>Auto-compress has been <bold>ENABLED</bold>.</green>"));
            compressManager.compressAsync(player, false, null);
        } else {
            player.sendMessage(miniMessage.deserialize("<yellow>Auto-compress has been <bold>DISABLED</bold>.</yellow>"));
        }

        return true;
    }
}