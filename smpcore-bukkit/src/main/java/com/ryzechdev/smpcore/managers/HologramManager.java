package com.ryzechdev.smpcore.managers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import com.ryzechdev.smpcore.SmpCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class HologramManager {

    private final SmpCorePlugin plugin;
    private final HashSet<Hologram> activeHolograms;

    public HologramManager(SmpCorePlugin plugin) {
        this.plugin = plugin;
        activeHolograms = new HashSet<>();
        enable();
    }

    public void enable() {
        boolean enabled = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    public void disable() {
        for (Hologram h : activeHolograms) {
            h.delete();
        }
    }

    public void displayStationaryHologram(List<String> msg, Location loc, long ticks) {
        Hologram holo = buildHologram(msg, loc);
    }
    public void displayStationaryHologram(List<String> msg, Location loc, long ticks, Player ... players) {
        Hologram holo = buildHologram(msg, loc);
        holo.getVisibilityManager().setVisibleByDefault(false);
        for (Player p : players) {
            holo.getVisibilityManager().showTo(p);
        }
    }

    public void displayFallingHologram(List<String> msg, Location loc, Player ... players) {
        Hologram holo = buildHologram(msg, loc);
        holo.getVisibilityManager().setVisibleByDefault(false);
        for (Player p : players) {
            holo.getVisibilityManager().showTo(p);
        }
        animateHologram(holo);
    }
    public void displayFallingHologram(List<String> msg, Location loc) {
        Hologram holo = buildHologram(msg, loc);
        animateHologram(holo);
    }

    private Hologram buildHologram(List<String> msg, Location loc) {
        Hologram holo = HologramsAPI.createHologram(plugin, loc);
        for (String s : msg) {
            holo.appendTextLine(s);
        }
        return holo;
    }

    private void animateHologram(Hologram hologram){
        final int[] ticks = {0, 0};
        int maxTicks = 30;
        ticks[1] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

            final double angle = new Random().nextDouble() * (2 * Math.PI);
            final double x = Math.cos(angle) / 25;
            double y = 0.15; // 0.2 for good "up" animation
            final double z = Math.sin(angle) / 25;

            @Override
            public void run() {
                try {
                    if(ticks[0] < maxTicks) {
                        hologram.teleport(hologram.getLocation().add(this.x, this.y, this.z));
                        this.y -= 0.03;
                    } else {
                        hologram.delete();
                        Bukkit.getScheduler().cancelTask(ticks[1]);
                    }
                    ticks[0] += 1;
                } catch (Exception e) {
                    SmpCorePlugin.log.severe(e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 0L, 1L);
    }
}
