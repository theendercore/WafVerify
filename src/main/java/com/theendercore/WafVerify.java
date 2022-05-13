package com.theendercore;

import org.apache.logging.log4j.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public final class WafVerify extends JavaPlugin {

    public static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Logger.class);

    @Override
    public void onEnable() {
        // Plugin startup logic
        LOGGER.info("< WafVerify > Plugin Up");
        this.getServer().getPluginCommand("verify").setExecutor(new VerifyCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
