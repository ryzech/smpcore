package net.ryzech.smpcore.commands.admin;

import net.ryzech.smpcore.SmpCorePlugin;
import net.ryzech.smpcore.util.FileUtils;
import net.ryzech.smpcore.util.SmpCoreApi;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class toweradmin implements CommandExecutor {

    private final SmpCorePlugin plugin;

    public toweradmin(SmpCorePlugin plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand("tower")).setExecutor((CommandExecutor) this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, @NotNull String cmdLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tower")) {
            // ----- ADMIN -----

            if ("twitchstick".equals(args[0]) && args.length >= 1) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command must be run as a player.");
                } else {
                    Player p = Bukkit.getPlayer(args[1]);
                    assert p != null;
                    SmpCoreApi.giveItem(p, SmpCorePlugin.getTwitchStick());
                }
            }

            if ("tear".equals(args[0]) && args.length >= 1) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command must be run as a player.");
                } else {
                    Player p = Bukkit.getPlayer(args[1]);
                    assert p != null;
                    SmpCoreApi.giveItem(p, SmpCorePlugin.getTear());
                }
            }

            if (args.length >= 1) {
                if ("reload".equals(args[0]) && args.length >= 1) {
                    FileUtils fileUtils;
                    fileUtils = new FileUtils(plugin);
                    if (!(sender instanceof Player)) {
                        fileUtils.reloadMain();
                        fileUtils.reloadLang();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[&3SMPCORE&a] &aSuccesfully reloaded the config!"));
                    } else if (sender.hasPermission("smpcore.admin")) {
                        fileUtils.reloadMain();
                        fileUtils.reloadLang();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[&3SMPCORE&a] &aSuccesfully reloaded the config!"));
                        Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&a[&3SMPCORE&a] &aSuccesfully reloaded the config!"));
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}




