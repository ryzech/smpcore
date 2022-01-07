package net.ryzech.smpcore.commands.admin;

import net.ryzech.smpcore.SmpCorePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class yell implements CommandExecutor{

    private final SmpCorePlugin plugin;

    public yell(SmpCorePlugin plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand("yell")).setExecutor((CommandExecutor) this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, @NotNull String cmdLabel, String[] args) {
        if (sender.hasPermission("smpcore.yell")
                && cmd.getName().equalsIgnoreCase("yell")) {
            int seconds;
            String message;
            try {
                seconds = Integer.parseInt(args[0]);
                message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            } catch (Exception e) {
                seconds = 60;
                message = String.join(" ", args);
            }
            plugin.getBarManager().addBar(seconds, message);
            return true;
        } else {
            return false;
        }
    }
}
