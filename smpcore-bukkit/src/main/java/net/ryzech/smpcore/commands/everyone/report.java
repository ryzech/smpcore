package net.ryzech.smpcore.commands.everyone;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.ryzech.smpcore.SmpCorePlugin;
import net.ryzech.smpcore.commands.reportDiscord;
import net.ryzech.smpcore.util.FileUtils;
import net.ryzech.smpcore.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.jagrosh.jdautilities.command.SlashCommand;

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
    }

    private void startBot() {
        try {
            CommandClientBuilder client = new CommandClientBuilder();
            FileUtils fileUtils;
            fileUtils = new FileUtils(plugin);
            client.setOwnerId("779910798759297044");
            client.addSlashCommand(new reportDiscord());
            client.forceGuildOnly(fileUtils.getMain().getString("Bot.GuildID"));
            CommandClient commandClient = client.build();
            jda = JDABuilder.createDefault(fileUtils.getMain().getString("Bot.Token")
                    ).addEventListeners(
                    commandClient
                    ).build();
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
                            player.sendMessage(MiniMessage.get().deserialize("<dark_aqua>You reported</dark_aqua><gold>" + reported.getName() + "</gold> for <gold>\"" + msgContent + "\"</gold>"));
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
        }
        return false;
    }
}
