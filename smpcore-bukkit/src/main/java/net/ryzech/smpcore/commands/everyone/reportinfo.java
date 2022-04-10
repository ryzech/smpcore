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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class reportinfo implements CommandExecutor {

    public SmpCorePlugin plugin;
    MiniMessage mm = MiniMessage.miniMessage();

    public reportinfo(SmpCorePlugin plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("reportinfo")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)){

        } else {
            if (command.getName().equalsIgnoreCase("reportinfo")) {
                if(args.length == 0) {
                    sender.sendMessage(mm.deserialize("<gold>/reportinfo <report id></gold>"));
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            try (Connection conn = MySQL.getConnection()) {
                                Statement stmt = conn.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT * FROM smpcore_reports WHERE report_id=" + args[0]);
                                rs.next();
                                String reportedPlayer = rs.getString(4);
                                String reporter = rs.getString(3);
                                String reportTime = rs.getString(6);
                                String reportReason = rs.getString(5);
                                sender.sendMessage(mm.deserialize("<gold>" + reportedPlayer + "</gold><dark_aqua> was reported by</dark_aqua> <gold>" + reporter + "</gold><dark_aqua> at </dark_aqua><gold>" + reportTime + "</gold><dark_aqua> for</dark_aqua><gold> \"" + reportReason + "\"" + "</gold>"));
                            } catch (SQLException e) {
                                e.printStackTrace();
                                sender.sendMessage(mm.deserialize("<red>The specified report id \"" + args[0] + "\" was either not found or the database is disabled. If the error persists contact your servers administrator."));
                            }
                        }
                    });
                }
                return true;
            }
        }
        return false;
    }
}
