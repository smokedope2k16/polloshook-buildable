package me.pollos.polloshook.impl.manager.friend;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.interfaces.Initializable;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

public class FriendManager implements Minecraftable, Initializable {
   private final List<Friend> friends = new ArrayList();

   public void init() {
      AbstractConfig var10001 = new AbstractConfig("friends.json") {
         public void save() {
            if (this.getFile().exists()) {
               this.getFile().delete();
            }

            if (!FriendManager.this.getFriends().isEmpty()) {
               JsonArray friends = new JsonArray();
               FriendManager.this.getFriends().forEach((friend) -> {
                  try {
                     JsonObject properties = new JsonObject();
                     properties.addProperty("friend-label", friend.getLabel());
                     properties.addProperty("friend-alias", friend.getAlias());
                     friends.add(properties);
                  } catch (Exception var3) {
                     var3.printStackTrace();
                  }

               });

               try {
                  FileWriter writer = new FileWriter(this.getFile());

                  try {
                     writer.write((new GsonBuilder()).setPrettyPrinting().create().toJson(friends));
                  } catch (Throwable var6) {
                     try {
                        writer.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }

                     throw var6;
                  }

                  writer.close();
               } catch (Exception var7) {
                  var7.printStackTrace();
               }

            }
         }

         public void load() {
            try {
               if (!this.getFile().exists()) {
                  this.getFile().createNewFile();
               }
            } catch (IOException var8) {
               var8.printStackTrace();
            }

            if (this.getFile().exists()) {
               JsonElement root;
               try {
                  FileReader reader = new FileReader(this.getFile());

                  try {
                     root = (new JsonParser()).parse(reader);
                  } catch (Throwable var6) {
                     try {
                        reader.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }

                     throw var6;
                  }

                  reader.close();
               } catch (IOException var7) {
                  var7.printStackTrace();
                  return;
               }

               if (root instanceof JsonArray) {
                  JsonArray friends = (JsonArray)root;
                  friends.forEach((friend) -> {
                     if (friend instanceof JsonObject) {
                        try {
                           JsonObject friendObj = (JsonObject)friend;
                           FriendManager.this.getFriends().add(new Friend(friendObj.get("friend-label").getAsString(), friendObj.get("friend-alias").getAsString()));
                        } catch (Throwable var3) {
                           var3.printStackTrace();
                        }

                     }
                  });
               }
            }
         }
      };
   }

   public FriendManager start(String startMessage) {
      this.info(startMessage);
      return this;
   }

   public FriendManager finish(String finishMessage) {
      this.info(finishMessage);
      return this;
   }

   public void addFriend(String name, String alias) {
      if (!this.isFriend(name)) {
         Manager.get().sendMessage(name, false);
         this.friends.add(new Friend(name, alias));
      }
   }

   public void removeFriend(String name) {
      Friend friend = this.getFriend(name);
      if (friend != null) {
         Manager.get().sendMessage(name, true);
         this.friends.remove(friend);
      }

   }

   public Friend getFriend(String label) {
      Iterator var2 = this.friends.iterator();

      Friend friend;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         friend = (Friend)var2.next();
      } while(!friend.getLabel().equalsIgnoreCase(label));

      return friend;
   }

   public boolean isFriend(String label) {
      return this.getFriend(label) != null;
   }

   public boolean isFriend(PlayerListEntry entry) {
      return this.getFriend(entry.getProfile().getName()) != null;
   }

   public boolean isFriend(PlayerEntity player) {
      return this.getFriend(player.getName().getString()) != null;
   }

   
   public List<Friend> getFriends() {
      return this.friends;
   }
}
