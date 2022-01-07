package com.ryzechdev.smpcore.commands.everyone;

import com.ryzechdev.smpcore.SmpCorePlugin;
import com.ryzechdev.smpcore.util.FileUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class stuck implements CommandExecutor{

    private final SmpCorePlugin plugin;
    private FileUtils fileUtils;
    private Location stuck;

    public stuck(SmpCorePlugin plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand("stuck")).setExecutor(this);
    }

    public void enable(YamlConfiguration config) {
        try {
            fileUtils = new FileUtils(plugin);
            Map<String, Object> Config = config.getConfigurationSection(
                    "Stuck.Location").getValues(false);
            stuck = new Location(plugin.getServer().getWorld((String) Objects.requireNonNull(fileUtils.getMain().getString("Stuck.Location.world"))),
                    (Double) Config.get("x"),
                    (Double) Config.get("y"),
                    (Double) Config.get("z"),
                    Float.parseFloat(Objects.requireNonNull(Config.get("yaw")).toString()),
                    Float.parseFloat(Objects.requireNonNull(Config.get("pitch")).toString()));
        } catch(Exception e) {
            SmpCorePlugin.log.severe("Error loading stuck command location from config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Location getRespawn() {
        return stuck;
    }

    public void setRespawn(Location stuck) {
        this.stuck = stuck;
    }

    public void disable(YamlConfiguration Config) {
    }


    public boolean onCommand(CommandSender sender, Command cmd, @NotNull String cmdLabel, String[] args) {
        if (sender.hasPermission("smpcore.unstuck")
                && cmd.getName().equalsIgnoreCase("stuck")) {
            Player p = (Player) sender;
            p.teleport(getRespawn());
            return true;
        } else {
        return false;
        }
    }
}

