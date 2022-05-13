package com.theendercore;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.mongodb.client.model.Filters.eq;
import static com.theendercore.WafVerify.LOGGER;

public class VerifyCommand implements CommandExecutor {
    Dotenv dotenv = Dotenv.load();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println("Verification can only be done by a player!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Please provide the password you where sent in Discord!");
            return true;
        }
        String playerUUID = player.getUniqueId().toString();
//        for (String argument : args) {
//            LOGGER.info(argument);
//        }
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGO_URI"))) {
            MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");
            MongoCollection<Document> collection = database.getCollection("temppasswordmodels");
            MongoCollection<Document> submitCluster = database.getCollection("verifymodels");
            try {
                Document playerInfo = collection.find(eq("password", args[0])).first();
                LOGGER.info(playerInfo);
                player.sendMessage(playerInfo.toString());
                player.sendMessage(ChatColor.AQUA + "WoW Epik Suk Sec!");
                String x = String.valueOf(playerInfo.getObjectId("_id"));
                player.sendMessage( x);

//                submitCluster.updateOne(
//                        Filters.eq("_id", "xx"),
//                        Updates.set("minecraftUUID", playerUUID));

            } catch (Exception e) {
                LOGGER.error(e);
                player.sendMessage(ChatColor.RED + "Please provide the password you where sent in Discord!");
                return true;
            }

        }
        return true;
    }
}
