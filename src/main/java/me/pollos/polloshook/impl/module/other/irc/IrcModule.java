package me.pollos.polloshook.impl.module.other.irc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.misc.chatappend.ChatAppend;
import me.pollos.polloshook.impl.module.other.irc.util.IrcPing;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class IrcModule extends ToggleableModule {
   protected final Value<Boolean> sendAllMessages = new Value(false, new String[]{"SendAllMessages", "onlyirc"});
   protected final StringValue prefix;
   protected final Value<Boolean> soundOnPing;
   public static final Identifier IRC_CAPE_IDENTIFIER = Identifier.of("polloshook", "textures/cape/keqingcape.png");
   public static String PING_PREFIX = "-PING";
   public static String IGNORE_PREFIX = "-IGNORE_ME";
   protected final ArrayList<IrcPing> pings;
   protected final Map<String, Long> offlineMessages;
   protected final List<String> startingMessages;
   private String ign;
   private String validMessage;
   private boolean insideRoom;
   private boolean initializing;
   private String lastNick;
   private final StopWatch userTimer;
   private final StopWatch retryTimer;
   private final StopWatch antiKickTimer;

   public IrcModule() {
      super(new String[]{"IRC", "chat"}, Category.OTHER);
      this.prefix = (new StringValue("!", new String[]{"IRCSuffix", "suffix"})).setParent(this.sendAllMessages, true);
      this.soundOnPing = new Value(false, new String[]{"SoundOnPing", "pingsound"});
      this.pings = new ArrayList();
      this.offlineMessages = new HashMap();
      this.startingMessages = new ArrayList();
      this.lastNick = "";
      this.userTimer = new StopWatch();
      this.retryTimer = new StopWatch();
      this.antiKickTimer = new StopWatch();
      this.offerValues(new Value[]{this.sendAllMessages, this.prefix, this.soundOnPing});
      this.offerListeners(new Listener[]{new ListenerChat(this), new ListenerIrcMessage(this), new ListenerRender(this), new ListenerTick(this)});
   }

   protected void onEnable() {
      this.pings.clear();
      this.userTimer.reset();
      this.insideRoom = false;
      Managers.getIrcManager().getOnlineUsers().clear();
      if (mc.player != null) {
         PollosHookThread.submit(() -> {
            try {
               this.initializing = true;
               ClientLogger.getLogger().log("Connecting to IRC...", false);
               Managers.getIrcManager().start(mc.player.getName().getString());
               Managers.getIrcManager().join();
               this.initializing = false;
            } catch (Exception var2) {
               ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "Failed to connect to IRC", false);
               this.toggle();
            }

         });
      } else {
         this.initializing = true;
         String name = mc.getSession().getUsername();
         Managers.getIrcManager().start(name);
         Managers.getIrcManager().join();
         this.initializing = false;
      }

   }

   public void onWorldLoad() {
      if (Managers.getIrcManager().isConnected() && mc.player != null) {
         Managers.getIrcManager().changeUsername(mc.player.getName().getString());
         Managers.getIrcManager().join();
      }

   }

   protected void onDisable() {
      this.initializing = false;
      if (Managers.getIrcManager().isConnected()) {
         Managers.getIrcManager().disconnect();
         ClientLogger.getLogger().log("Disconnected from IRC", false);
      }

   }

   public boolean shouldDrawBorder(String s) {
      if (Managers.getIrcManager().isConnected() && this.insideRoom) {
         if (ChatAppend.shouldFilter(s) && !ChatAppend.getContainedFilter(s).equals(this.prefix.getValue())) {
            return false;
         } else {
            return s.startsWith((String)this.prefix.getValue()) || (Boolean)this.sendAllMessages.getValue();
         }
      } else {
         return false;
      }
   }

   
   public Value<Boolean> getSendAllMessages() {
      return this.sendAllMessages;
   }

   
   public StringValue getPrefix() {
      return this.prefix;
   }

   
   public Value<Boolean> getSoundOnPing() {
      return this.soundOnPing;
   }

   
   public ArrayList<IrcPing> getPings() {
      return this.pings;
   }

   
   public Map<String, Long> getOfflineMessages() {
      return this.offlineMessages;
   }

   
   public List<String> getStartingMessages() {
      return this.startingMessages;
   }

   
   public String getIgn() {
      return this.ign;
   }

   
   public String getValidMessage() {
      return this.validMessage;
   }

   
   public boolean isInsideRoom() {
      return this.insideRoom;
   }

   
   public boolean isInitializing() {
      return this.initializing;
   }

   
   public String getLastNick() {
      return this.lastNick;
   }

   
   public StopWatch getUserTimer() {
      return this.userTimer;
   }

   
   public StopWatch getRetryTimer() {
      return this.retryTimer;
   }

   
   public StopWatch getAntiKickTimer() {
      return this.antiKickTimer;
   }

   
   public void setIgn(String ign) {
      this.ign = ign;
   }

   
   public void setValidMessage(String validMessage) {
      this.validMessage = validMessage;
   }

   
   public void setInsideRoom(boolean insideRoom) {
      this.insideRoom = insideRoom;
   }

   
   public void setInitializing(boolean initializing) {
      this.initializing = initializing;
   }

   
   public void setLastNick(String lastNick) {
      this.lastNick = lastNick;
   }
}
