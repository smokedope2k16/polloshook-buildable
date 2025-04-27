package me.pollos.polloshook.api.minecraft.network;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.fastlatency.FastLatency;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.text.Text;

public class NetworkUtil implements Minecraftable {
   public static int getPing() {
      return getPing(mc.player);
   }

   public static int getPing(Entity player) {
      if (mc.getNetworkHandler() != null && player != null) {
         if (player == mc.player && ((FastLatency)Managers.getModuleManager().get(FastLatency.class)).isEnabled()) {
            return (int)((FastLatency)Managers.getModuleManager().get(FastLatency.class)).getPing();
         } else {
            PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
            return playerListEntry != null ? playerListEntry.getLatency() : 0;
         }
      } else {
         return 0;
      }
   }

   public static String getServerIp() {
      return mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null ? mc.getNetworkHandler().getServerInfo().address : "";
   }

   public static void disconnectFromServer(String string) {
      if (mc.getNetworkHandler() == null) {
         mc.world.disconnect();
      } else {
         mc.getNetworkHandler().getConnection().disconnect(Text.literal(string));
      }
   }

   public static void connectToServer(ServerInfo info) {
      ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), mc, ServerAddress.parse(info.address), info, false, (CookieStorage)null);
   }

   public static List<GameProfile> getOnlinePlayersProfile() {
      List<GameProfile> profiles = new ArrayList();
      if (mc.getNetworkHandler() != null) {
         Iterator var1 = mc.getNetworkHandler().getPlayerList().iterator();

         while(var1.hasNext()) {
            PlayerListEntry info = (PlayerListEntry)var1.next();
            profiles.add(info.getProfile());
         }
      }

      return profiles;
   }

   public static void sendInChat(String message) {
      sendInChat(message, false);
   }

   public static void sendInChat(String message, boolean forcePublic) {
      if (message.startsWith("/") && !forcePublic) {
         mc.player.networkHandler.sendChatCommand(message.replace("/", ""));
      } else {
         mc.player.networkHandler.sendChatMessage(message);
      }

   }

   public static void disconnect(String message) {
      if (mc.getNetworkHandler() != null) {
         mc.getNetworkHandler().onDisconnect(new DisconnectS2CPacket(Text.literal(message)));
      }

   }
}
