package com.ryzechdev.smpcore.tasks;

import com.ryzechdev.smpcore.SmpCorePlugin;
import com.ryzechdev.smpcore.util.SmpCoreApi;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class BossBarTask {

    private SmpCorePlugin plugin;
    private BukkitTask task;
    private double seconds;
    private double current;
    private String msg;

    private BossBar bossBar;

    public BossBarTask(int seconds, String msg) {
        plugin = SmpCorePlugin.getInstance();
        this.seconds = seconds;
        this.current = seconds;
        this.msg = SmpCoreApi.colorizeText(msg);
    }

    // G/S
    public double getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void addPlayer(Player p) {
        bossBar.addPlayer(p);
    }

    public void removePlayer(Player p) {
        bossBar.removePlayer(p);
    }

    public void run() {
        bossBar = plugin.getServer().createBossBar(
                msg,
                BarColor.BLUE,
                BarStyle.SEGMENTED_20);

        final BossBarTask parent = this;

        if (task == null) {
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if ((current -= 1) <= 0) {
                        task.cancel();
                        bossBar.removeAll();
                        plugin.getBarManager().removeBar(parent);
                    } else {
                        double progress = current / seconds;
                        try {
                            bossBar.setProgress(progress);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        }
        bossBar.setVisible(true);
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }
    }

    public void cancel() {
        current = 0;
    }
}
