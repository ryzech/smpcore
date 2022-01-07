package net.ryzech.smpcore.util;

import net.ryzech.smpcore.SmpCorePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private final SmpCorePlugin instance;
    private File mainFile;
    private FileConfiguration main;
    private File en_usFile;
    private FileConfiguration en_us;

    public FileUtils(SmpCorePlugin instance) {
        this.instance = instance;
    }

    public void loadMain() {
        mainFile = new File(instance.getDataFolder(), "config.yml");
        if (!mainFile.exists()) {
            mainFile.getParentFile().mkdirs();
            instance.saveResource("config.yml", false);
        }

        main = new YamlConfiguration();
        try {
            main.load(mainFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadMain() {
        mainFile = new File(instance.getDataFolder(), "config.yml");
        try {
            main = YamlConfiguration.loadConfiguration(mainFile);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to reload the config file!");
            ex.printStackTrace();
        }
    }

    public void loadLang() {
        en_usFile = new File(instance.getDataFolder(), "en_us.yml");
        if (!en_usFile.exists()) {
            en_usFile.getParentFile().mkdirs();
            instance.saveResource("en_us.yml", false);
        }

        en_us = new YamlConfiguration();
        try {
            en_us.load(en_usFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void reloadLang() {
        en_usFile = new File(instance.getDataFolder(), "en_us.yml");
        try {
            en_us = YamlConfiguration.loadConfiguration(en_usFile);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Failed to reload the language file!");
            ex.printStackTrace();
        }
    }

    public FileConfiguration getMain() {
        mainFile = new File(instance.getDataFolder(), "config.yml");
        main = YamlConfiguration.loadConfiguration(mainFile);
        return main;
    }

    public FileConfiguration getLang() {
        en_usFile = new File(instance.getDataFolder(), "en_us.yml");
        en_us = YamlConfiguration.loadConfiguration(en_usFile);
        return en_us;
    }
}
