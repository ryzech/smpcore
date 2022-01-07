package com.ryzechdev.smpcore.events;

import com.ryzechdev.smpcore.SmpCorePlugin;
import com.ryzechdev.smpcore.util.FileUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Entity damager;
    private Entity target;
    private Double dmg;
    private Location location;
    private boolean critical;
    private boolean cancelled;
    private SmpCorePlugin plugin;

    public DamageEvent(Entity damager, Entity target, Double dmg, Location location, boolean critical) {
        this.damager = damager;
        this.target = target;
        this.dmg = dmg;
        this.location = location;
        this.critical = critical;
        this.cancelled = false;
    }

    public DamageEvent(Entity damager, Entity target, Double dmg, Location location, boolean critical, boolean cancelled) {
        this.damager = damager;
        this.target = target;
        this.dmg = dmg;
        this.location = location;
        this.critical = critical;
        this.cancelled = cancelled;

        location.getWorld().spawnParticle(Particle.CRIT, location, 1);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Entity getDamager() {
        return damager;
    }

    public void setDamager(Entity damager) {
        this.damager = damager;
    }

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public Double getDmg(SmpCorePlugin plugin) {
        FileUtils fileUtils;
        this.plugin = plugin;
        fileUtils = new FileUtils(plugin);
        if(fileUtils.getMain().getBoolean("DamageIndicators.Hearts")) {
            return (double) dmg / 2;
        } else {
            return dmg;
        }
    }

    public void setDmg(Double dmg) {
        this.dmg = dmg;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }
}
