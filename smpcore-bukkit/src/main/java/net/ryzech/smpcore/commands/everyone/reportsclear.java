package net.ryzech.smpcore.commands.everyone;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.ryzech.smpcore.SmpCorePlugin;
import net.ryzech.smpcore.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.sql.Connection;
import java.sql.SQLException;


public class reportsclear implements CommandExecutor {

    public SmpCorePlugin plugin;

    public reportsclear(SmpCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {

        } else {
            if(command.getName().equalsIgnoreCase("reportsclear") && sender.hasPermission("smpcore.clearreports")) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try (Connection conn = MySQL.getConnection()) {
                            if(MySQL.update("TRUNCATE TABLE smpcore_reports;"));
                            sender.sendMessage(MiniMessage.get().deserialize("<dark_aqua>Reports successfully cleared!</dark_aqua>"));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        return false;
    }
}
