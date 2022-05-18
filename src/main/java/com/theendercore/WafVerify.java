package com.theendercore;


import io.github.cdimascio.dotenv.Dotenv;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WafVerify extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger("WafVerify");
    public static final Dotenv dotenv = Dotenv.load();

    @Override
    public void onEnable() {
        // Plugin startup logic
        LOGGER.info("Plugin Up");
        this.getServer().getPluginCommand("verify").setExecutor(new VerifyCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
