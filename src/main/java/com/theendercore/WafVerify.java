package com.theendercore;


import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public final class WafVerify extends JavaPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger("WafVerify");
    public static final Dotenv dotenv = Dotenv.load();
    public static JDA bot;

    @Override
    public void onEnable() {
        // Plugin startup logic
        LOGGER.info("Plugin Up");
        this.getServer().getPluginCommand("verify").setExecutor(new VerifyCommand());

        JDABuilder builder = JDABuilder.createDefault(dotenv.get("TOKEN"));

        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setCompression(Compression.NONE);
        builder.setActivity(Activity.watching("Homa"));

        try {
            bot = builder.build();
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
