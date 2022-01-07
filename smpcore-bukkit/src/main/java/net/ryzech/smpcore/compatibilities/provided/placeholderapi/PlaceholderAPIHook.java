package net.ryzech.smpcore.compatibilities.provided.placeholderapi;

import net.ryzech.smpcore.SmpCorePlugin;
import net.ryzech.smpcore.compatibilities.CompatibilityProvider;
import me.clip.placeholderapi.PlaceholderAPIPlugin;

public class PlaceholderAPIHook extends CompatibilityProvider<PlaceholderAPIPlugin> {

    public PlaceholderAPIHook() {
        new SmpCoreExpansion(SmpCorePlugin.get()).register();
    }

}
