package net.ryzech.smpcore.compatibilities.provided.placeholderapi;

import net.ryzech.smpcore.SmpCorePlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class SmpCoreExpansion extends PlaceholderExpansion {

    private final SmpCorePlugin plugin;

    public SmpCoreExpansion(final SmpCorePlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "RyzechDev";
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "SmpCore";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "${projectVersion}";
    }

    @Override
    public boolean persist() {
        return true;
    }
}
