package com.ryzechdev.smpcore.commands.afk;

import com.ryzechdev.smpcore.SmpCorePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class afk implements CommandExecutor {

    private final SmpCorePlugin plugin;
    public Map<UUID, String> afkmap = new HashMap<java.util.UUID, String>();
    public afk(SmpCorePlugin plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand("afk")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("afk")) {
            Player player = (Player) sender;
            afkmap.put(player.getUniqueId(), "true");
        }
        return true;
    }

    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        afkmap.put(player.getUniqueId(), "false");
    }
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        afkmap.put(player.getUniqueId(), "false");
    }
}
