package com.theendercore;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.theendercore.WafVerify.LOGGER;
import static com.theendercore.WafVerify.config;

public class VerifyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            LOGGER.info("Verification can only be done by a player!");
            return true;
        }

        Player player = (Player) sender;

        if (Objects.equals(config.getString("mongoURI"), "uri")) {
            LOGGER.warn("WafVerify not setup Properly!");
            player.sendMessage(ChatColor.RED + "WafVerify not setup Properly! Contact admin!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Please provide the password you where sent in Discord! \n Or run !reverify in the discord server to generate a new password.");
            return true;
        }
        String playerUUID = player.getUniqueId().toString();
        try (MongoClient mongoClient = MongoClients.create(Objects.requireNonNull(config.getString("mongoURI")))) {
            MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");
            MongoCollection<Document> collection = database.getCollection("temppasswordmodels");
            MongoCollection<Document> submitCluster = database.getCollection("verifymodels");
            if (collection.find(eq("password", args[0])).first() == null) {
                player.sendMessage(ChatColor.RED + "Please provide the password you where sent in Discord! \n Or run !reverify in the discord server to generate a new password.");
                return true;
            }

            if (submitCluster.find(eq("minecraftUUID", playerUUID)).first() != null) {
                player.sendMessage(ChatColor.RED + "This Minecraft account has been linked to a discord account already!");
                return true;
            }
            Document playerInfo = (collection.find(eq("password", args[0])).first());

            assert playerInfo != null;
            String id = (String) playerInfo.get("userID");
            String serverID = (String) playerInfo.get("serverID");
            List<Document> pp = (List<Document>) ((submitCluster.find(eq("_id", id)).first())).get("verifiedSerevrs");

            int value = 0;
            for (int i = 0; i < pp.size(); i++) {
                String yes = (String) pp.get(i).get("serverID");
                if (Objects.equals(yes, serverID)) {
                    value = i;
                    break;
                }
            }

            Bson updates = Updates.combine(Updates.set("minecraftUUID", playerUUID), Updates.set("verifiedSerevrs." + value + ".verified", true));

            submitCluster.updateOne(new Document().append("_id", id), updates);
            collection.findOneAndDelete(new Document().append("_id", playerInfo.get("_id")));

            JSONObject pkg = new JSONObject();
            pkg.put("server", serverID);
            pkg.put("user", id);
            WebSocketClient c = new WebSocketClient(new URI(Objects.requireNonNull(config.getString("wsIP")))) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    LOGGER.info("Connected to server!");
                }

                @Override
                public void onMessage(String s) {
                    LOGGER.info("Message: " + s);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    LOGGER.info("Disconnected from server!");
                }

                @Override
                public void onError(Exception e) {
                    LOGGER.warn("ERROR: \n" + e);
                }
            };
            c.connectBlocking();
            c.send(pkg.toString());
            player.sendMessage(ChatColor.AQUA + "You have been verified! Welcome to the server! :)");
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
