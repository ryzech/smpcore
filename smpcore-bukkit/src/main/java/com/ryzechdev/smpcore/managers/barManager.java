package com.ryzechdev.smpcore.managers;

import com.ryzechdev.smpcore.SmpCorePlugin;
import com.ryzechdev.smpcore.tasks.BossBarTask;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class barManager {

    private HashSet<BossBarTask> activeBars;

    public barManager(SmpCorePlugin plugin) {
        activeBars = new HashSet<>();
    }

    public void addBar(int seconds, String message) {
        addBar(new BossBarTask(seconds, message));
    }

    public void addBar(BossBarTask task) {
        task.run();
        activeBars.add(task);
    }

    public void removeBar(BossBarTask task){
        task.cancel();
        activeBars.remove(task);
    }

    public void addPlayer(Player p){
        for (BossBarTask task : activeBars) {
            task.addPlayer(p);
        }
    }

    public void removePlayer(Player p){
        for (BossBarTask task : activeBars) {
            task.removePlayer(p);
        }
    }

    public HashSet<BossBarTask> getActiveBars() {
        return activeBars;
    }

    public void setActiveBars(HashSet<BossBarTask> activeBars) {
        this.activeBars = activeBars;
    }

}
