package com.theendercore;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGO_URI"))) {
            MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");
            MongoCollection<Document> collection = database.getCollection("temppasswordmodels");
            Document user = collection.find().first();
            assert user != null;
            System.out.println(user.toJson());
        }
//        MongoClient mongoClient = MongoClients.create("mongodb+srv://WafflesAreBetter:g4Lex3wiTe118zee@cluster0.d6awo.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
//        MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");
//        MongoCollection<Document> col = database.getCollection("discord-mc-verify");
//        BasicDBObject verifyList = new BasicDBObject();
//        List<BasicDBObject> verify = new ArrayList();
//        verify.add(new BasicDBObject("vPassword", args[0]));
//        verify.add(new BasicDBObject("pendingUserName", playerName));
//        verifyList.put("$and", verify);
//        Long totalCount = col.countDocuments(verifyList);

        return false;
    }
}
