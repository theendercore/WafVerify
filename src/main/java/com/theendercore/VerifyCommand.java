package com.theendercore;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.*;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.theendercore.WafVerify.*;

public class VerifyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            LOGGER.info("Verification can only be done by a player!");
            return true;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Please provide the password you where sent in Discord! \n Or rejoin the DISCORD server to generate a new password ");
            return true;
        }
        String playerUUID = player.getUniqueId().toString();
        try (MongoClient mongoClient = MongoClients.create(dotenv.get("MONGO_URI"))) {
            MongoDatabase database = mongoClient.getDatabase("myFirstDatabase");
            MongoCollection<Document> collection = database.getCollection("temppasswordmodels");
            MongoCollection<Document> submitCluster = database.getCollection("verifymodels");
            if (collection.find(eq("password", args[0])).first() == null) {
                player.sendMessage(ChatColor.RED + "Please provide the password you where sent in Discord! \n Or rejoin the DISCORD server to generate a new password ");
                return true;
            }
            Document playerInfo = (collection.find(eq("password", args[0])).first());

            assert playerInfo != null;
            String id = (String) playerInfo.get("userID");
            String serverID = (String) playerInfo.get("_id");
            List<Document> pp = (List<Document>) ((submitCluster.find(eq("_id", id)).first())).get("verifiedSerevrs");

            int value = 0;
            for (int i = 0; i < pp.size(); i++) {
                String yes = (String) pp.get(i).get("serverID");
                if (Objects.equals(yes, serverID)) {
                    value = i;
                    break;
                }
                player.sendMessage(yes + "\n :o");
            }

            Bson updates = Updates.combine(Updates.set("minecraftUUID", playerUUID), Updates.set("verifiedSerevrs." + value + ".verified", true));

            submitCluster.updateOne(new Document().append("_id", id), updates);
            collection.findOneAndDelete(new Document().append("_id", serverID));

            TextChannel textChannel = bot.getTextChannelById("976518221064704070");
            if(textChannel.canTalk()) {
                textChannel.sendMessage("{server:\""+serverID+"\",user: \""+id+"\"}").queue();
            }
            player.sendMessage(ChatColor.AQUA + "WoW Epik Suk Sec!");
        }
        return true;
    }


    private static class TempVerify {
        public String _id;
        public String password;

        TempVerify(String _id, String password) {
            this._id = _id;
            this.password = password;
        }
    }
}
