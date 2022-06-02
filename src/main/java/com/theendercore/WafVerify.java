package com.theendercore;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class WafVerify extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger("WafVerify");

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        config = getConfig();
        config.addDefault("mongoURI", "uri");
        config.addDefault("wsIP", "ws://localhost:8080");
        config.options().copyDefaults(true);
        saveConfig();
        this.getServer().getPluginCommand("verify").setExecutor(new VerifyCommand());
        LOGGER.info("Plugin Loaded");
    }

    @Override
    public void onDisable() {
        if (Objects.equals(config.getString("mongoURI"), "uri")) {
            LOGGER.warn("WafVerify not setup Properly!");
        }
    }
}
