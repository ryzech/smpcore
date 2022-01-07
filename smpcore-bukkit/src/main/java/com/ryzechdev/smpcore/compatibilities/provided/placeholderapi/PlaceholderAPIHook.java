package com.ryzechdev.smpcore.compatibilities.provided.placeholderapi;

import com.ryzechdev.smpcore.SmpCorePlugin;
import com.ryzechdev.smpcore.compatibilities.CompatibilityProvider;
import me.clip.placeholderapi.PlaceholderAPIPlugin;

public class PlaceholderAPIHook extends CompatibilityProvider<PlaceholderAPIPlugin> {

    public PlaceholderAPIHook() {
        new SmpCoreExpansion(SmpCorePlugin.get()).register();
    }

}
