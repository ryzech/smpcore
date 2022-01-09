package net.ryzech.smpcore.commands.everyone;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.ryzech.smpcore.SmpCorePlugin;
import net.ryzech.smpcore.util.FileUtils;
import net.ryzech.smpcore.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class report implements CommandExecutor {
    public SmpCorePlugin plugin;
    public JDA jda;
    public FileUtils fileUtils;

    public report(SmpCorePlugin plugin) {
        this.plugin = plugin;
        startBot();
        Objects.requireNonNull(plugin.getCommand("report")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("reportinfo")).setExecutor(this);
    }

    private void startBot() {
        try {
            FileUtils fileUtils;
            fileUtils = new FileUtils(plugin);
            jda = JDABuilder.createDefault(fileUtils.getMain().getString("Bot.Token")).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command must be run as a player.");
        } else {
            if(args.length == 0) {
                sender.sendMessage(MiniMessage.get().deserialize("<gold>/report <user> <reason></gold>"));
            } else if (command.getName().equalsIgnoreCase("report")) {
                FileUtils fileUtils;
                fileUtils = new FileUtils(plugin);
                List rep = Arrays.stream(args).toList();
                String msgContent = String.join(" ", Arrays.asList(args).subList(1, rep.size()).toArray(new String[]{}));
                Player player = (Player) sender;
                Player reported = Bukkit.getPlayer(args[0]);
                TextChannel modLog = jda.getTextChannelById(fileUtils.getMain().getString("Bot.LogChannel"));
                if (reported != null && reported.hasPlayedBefore()) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            int reportId = 0;
                            try (Connection conn = MySQL.getConnection()) {
                                Statement stmt = conn.createStatement();
                                ResultSet rs = stmt.executeQuery("SELECT * FROM smpcore_reports ORDER BY report_id");
                                while(rs.next()) {
                                    if(rs.isLast()) {
                                        reportId = rs.getInt(2);
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setTitle("Player Report");
                            eb.addField("Report ID", String.valueOf(reportId + 1), true);
                            eb.addField("Reported Player", args[0], true);
                            eb.addField("Sender", player.getName(), true);
                            eb.addField("Message Content", msgContent, true);
                            eb.addField("Report Coords", Math.round(player.getLocation().getX())
                                            + ", " + Math.round(player.getLocation().getY())
                                            + ", " + Math.round(player.getLocation().getZ()),
                                    true);
                            eb.addField("World", player.getWorld().getName(), true);
                            modLog.sendMessageEmbeds(eb.build()).queue();
                            player.sendMessage(Component.text("You reported " + reported.getName() + " for: " + msgContent, NamedTextColor.DARK_AQUA));
                            try (Connection conn = MySQL.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                                    "INSERT INTO smpcore_reports(uuid, reporter, reported, report_message) VALUES(?, ?, ?, ?);")) {
                                stmt.setString(1, player.getUniqueId().toString());
                                stmt.setString(2, player.getName());
                                stmt.setString(3, reported.getName());
                                stmt.setString(4, msgContent);
                                stmt.execute();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    sender.sendMessage(MiniMessage.get().deserialize("<red>This player hasn't joined the server before. Check if the name is correct.</red>"));
                }
            }
            if (command.getName().equals("reportinfo")) {
                if(args.length == 0) {
                    sender.sendMessage(MiniMessage.get().deserialize("<gold>/reportinfo <report id></gold>"));
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
                                sender.sendMessage(MiniMessage.get().deserialize("<dark_aqua>" + reportedPlayer + " was reported by " + reporter + " at " + reportTime + " for \"" + reportReason + "\"" + "</dark_aqua>"));
                            } catch (SQLException e) {
                                e.printStackTrace();
                                sender.sendMessage(MiniMessage.get().deserialize("<red>The specified report id \"" + args[0] + "\" was either not found or the database is disabled. If the error persists contact your servers administator."));
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
