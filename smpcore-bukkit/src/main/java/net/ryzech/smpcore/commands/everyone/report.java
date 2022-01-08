package net.ryzech.smpcore.commands.everyone;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.ryzech.smpcore.SmpCorePlugin;
import net.ryzech.smpcore.util.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class report implements CommandExecutor {
    public SmpCorePlugin plugin;
    public JDA jda;
    public FileUtils fileUtils;

    public report(SmpCorePlugin plugin){
        this.plugin = plugin;
        startBot();
        Objects.requireNonNull(plugin.getCommand("report")).setExecutor(this);

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
            if (command.getName().equalsIgnoreCase("report")) {
                FileUtils fileUtils;
                fileUtils = new FileUtils(plugin);
                List rep = Arrays.stream(args).toList();
                String msgContent = String.join(" ", Arrays.asList(args).subList(1, rep.size()).toArray(new String[]{}));
                Player player = (Player) sender;
                Player reported = Bukkit.getPlayer(args[0]);
                TextChannel modLog = jda.getTextChannelById(fileUtils.getMain().getString("Bot.LogChannel"));
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Player Report");
                eb.addField("Reported Player", args[0], true);
                eb.addField("Sender", player.getName(), true);
                eb.addField("Message Content", msgContent, true);
                eb.addField("Report Coords", player.getLocation().getX() + ", " + player.getLocation().getY() + ", " + player.getLocation().getZ(), true);
                modLog.sendMessageEmbeds(eb.build()).queue();
                player.sendMessage(Component.text("You reported " + reported.getName() + " for: " + msgContent, NamedTextColor.DARK_AQUA));
            }

        }
        return false;
    }
}
