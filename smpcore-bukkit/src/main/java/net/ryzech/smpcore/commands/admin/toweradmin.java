package net.ryzech.smpcore.commands.admin;

import net.kyori.adventure.text.minimessage.MiniMessage;
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
    MiniMessage mm = MiniMessage.miniMessage();

    public toweradmin(SmpCorePlugin plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand("tower")).setExecutor(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String cmdLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tower")) {

            if (args.length >= 1) {
                if ("reload".equals(args[0]) && sender.hasPermission("smpcore.admin.reload")) {
                    FileUtils fileUtils;
                    fileUtils = new FileUtils(plugin);
                    fileUtils.reloadMain();
                    fileUtils.reloadLang();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3[&3SMPCORE&3] &3Succesfully reloaded the config!"));
                    Bukkit.getLogger().info(ChatColor.translateAlternateColorCodes('&', "&3[&3SMPCORE&3] &3Succesfully reloaded the config!"));
                } else {
                    sender.sendMessage(mm.deserialize("<red>Sorry but you don't have permission to run this command.</red>"));
                }
            }
        }
        return false;
    }
}




