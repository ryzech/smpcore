package com.ryzechdev.smpcore.commands;

import com.ryzechdev.smpcore.SmpCorePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.*;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final SmpCorePlugin plugin;

    public TabCompleter(SmpCorePlugin plugin) {
        this.plugin = plugin;

        Objects.requireNonNull(plugin.getCommand("tower")).setTabCompleter(this);
    }

    private static final String[] COMMANDS = {"tower"};
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        //create new array
        final List<String> completions = new ArrayList<>();
        //copy matches of first argument from list (ex: if first arg is 'm' will return just 'minecraft')
        StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        //sort the list
        Collections.sort(completions);
        return completions;
    }
}
