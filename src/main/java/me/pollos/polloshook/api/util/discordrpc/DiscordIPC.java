package me.pollos.polloshook.api.util.discordrpc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.lang.management.ManagementFactory;
import java.util.function.BiConsumer;

public class DiscordIPC {
   private static final Gson GSON = new Gson();
   private static BiConsumer<Integer, String> onError = DiscordIPC::defaultErrorCallback;
   private static Connection c;
   private static Runnable onReady;
   private static boolean receivedDispatch;
   private static JsonObject queuedActivity;
   private static IPCUser user;

   public static void setOnError(BiConsumer<Integer, String> onError) {
      DiscordIPC.onError = onError;
   }

   public static boolean start(long appId, Runnable onReady) {
      c = Connection.open(DiscordIPC::onPacket);
      if (c == null) {
         return false;
      } else {
         DiscordIPC.onReady = onReady;
         JsonObject o = new JsonObject();
         o.addProperty("v", 1);
         o.addProperty("client_id", Long.toString(appId));
         c.write(Opcode.Handshake, o);
         return true;
      }
   }

   public static boolean isConnected() {
      return c != null;
   }

   public static IPCUser getUser() {
      return user;
   }

   public static void setActivity(RichPresence presence) {
      if (c != null) {
         queuedActivity = presence.toJson();
         if (receivedDispatch) {
            sendActivity();
         }

      }
   }

   public static void stop() {
      if (c != null) {
         c.close();
         c = null;
         onReady = null;
         receivedDispatch = false;
         queuedActivity = null;
         user = null;
      }

   }

   private static void sendActivity() {
      JsonObject args = new JsonObject();
      args.addProperty("pid", getPID());
      args.add("activity", queuedActivity);
      JsonObject o = new JsonObject();
      o.addProperty("cmd", "SET_ACTIVITY");
      o.add("args", args);
      c.write(Opcode.Frame, o);
      queuedActivity = null;
   }

   private static void onPacket(DiscordPacket packet) {
      if (packet.opcode() == Opcode.Close) {
         if (onError != null) {
            onError.accept(packet.data().get("code").getAsInt(), packet.data().get("message").getAsString());
         }

         stop();
      } else if (packet.opcode() == Opcode.Frame) {
         if (packet.data().has("evt") && packet.data().get("evt").getAsString().equals("ERROR")) {
            JsonObject d = packet.data().getAsJsonObject("data");
            if (onError != null) {
               onError.accept(d.get("code").getAsInt(), d.get("message").getAsString());
            }
         } else if (packet.data().has("cmd") && packet.data().get("cmd").getAsString().equals("DISPATCH")) {
            receivedDispatch = true;
            user = (IPCUser)GSON.fromJson(packet.data().getAsJsonObject("data").getAsJsonObject("user"), IPCUser.class);
            if (onReady != null) {
               onReady.run();
            }

            if (queuedActivity != null) {
               sendActivity();
            }
         }
      }

   }

   private static int getPID() {
      String pr = ManagementFactory.getRuntimeMXBean().getName();
      return Integer.parseInt(pr.substring(0, pr.indexOf(64)));
   }

   private static void defaultErrorCallback(int code, String message) {
      System.err.println("Discord IPC error " + code + " with message: " + message);
   }
}
