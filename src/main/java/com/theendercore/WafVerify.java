package com.theendercore;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public final class WafVerify extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger("WafVerify");
//    public static JDA bot;

    public static FileConfiguration config;
    @Override
    public void onEnable() {
        config = getConfig();
        LOGGER.info("Plugin Up");

        this.getServer().getPluginCommand("verify").setExecutor(new VerifyCommand());

//        JDABuilder builder = JDABuilder.createDefault(dotenv.get("TOKEN"));
//
//        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
//                .setBulkDeleteSplittingEnabled(false)+
//                .setCompression(Compression.NONE)
//                .setActivity(Activity.watching("Homa"));

        config.addDefault("mongoURI", "uri");
        config.addDefault("wsIP", "ws://localhost:8080");
        config.options().copyDefaults(true);
        saveConfig();


//        try {
//            bot = builder.build();
//        } catch (LoginException e) {
//            throw new RuntimeException(e);
//        }
        LOGGER.info("Plugin Loaded");

    }

    @Override
    public void onDisable() {
        if (Objects.equals(config.getString("mongoURI"), "uri")){
            LOGGER.warn("WafVerify not setup Properly!");
        }
    }
}
