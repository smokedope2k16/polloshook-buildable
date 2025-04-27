package me.pollos.polloshook.impl.module.other.irc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.manager.irc.IrcManager;
import me.pollos.polloshook.impl.module.other.irc.util.RunnableClickEvent;
import me.pollos.polloshook.irc.beans.User;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.util.Formatting;

public class ListenerTick extends ModuleListener<IrcModule, TickEvent> {
   private int retries = 0;
   private long delay = 30000L;
   private boolean sentMessage = false;

   public ListenerTick(IrcModule module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (((IrcModule)this.module).getUserTimer().passed(5000L) && Managers.getIrcManager().isConnected()) {
         User[] users = Managers.getIrcManager().getUsers("#keqing4pollos");
         Managers.getIrcManager().addClientUsers(users);
         ((IrcModule)this.module).getUserTimer().reset();
      }

      if (mc.player == null) {
         String nick = Managers.getIrcManager().getFixedName(mc.getSession().getUsername());
         if (!((IrcModule)this.module).getLastNick().equals(nick)) {
            ((IrcModule)this.module).setLastNick(nick);
            Managers.getIrcManager().changeUsername(Managers.getIrcManager().getFixedName(nick));
         }
      }

      if (mc.player != null && mc.world != null && !((IrcModule)this.module).startingMessages.isEmpty()) {
         List<String> remove = new ArrayList(((IrcModule)this.module).startingMessages);
         List<String> users = Managers.getIrcManager().getOnlineUsers();
         users.removeIf((sx) -> {
            return sx.equals(Managers.getIrcManager().getUsername());
         });
         String join = String.join(", ", users);
         int size = users.size();
         String string = size == 0 ? "You're the only one online" : "You are with %s other user%s (%s)".formatted(new Object[]{size, size == 1 ? "" : "s", join});
         Iterator var7 = remove.iterator();

         while(var7.hasNext()) {
            String s = (String)var7.next();
            PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
               ClientLogger.getLogger().log(s.replace("<FILL>", string), false);
            }, 100L, TimeUnit.MILLISECONDS);
         }

         ((IrcModule)this.module).startingMessages.removeAll(remove);
      }

      if (mc.player != null && mc.world != null && !((IrcModule)this.module).offlineMessages.isEmpty()) {
         MutableText mutableText = Text.literal("You have %s offline message(s)".formatted(new Object[]{((IrcModule)this.module).offlineMessages.size()}));
         HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, Text.literal(String.valueOf(Formatting.GRAY) + "Click to show all messages"));
         RunnableClickEvent clickEvent = new RunnableClickEvent(() -> {
            Map<String, Long> remove = new HashMap(((IrcModule)this.module).offlineMessages);
            Iterator var2 = remove.entrySet().iterator();

            while(var2.hasNext()) {
               Entry<String, Long> entry = (Entry)var2.next();
               String key = (String)entry.getKey();
               Long time = (Long)entry.getValue();
               Action var10002 = Action.SHOW_TEXT;
               String var10003 = String.valueOf(Formatting.GRAY);
               HoverEvent messageHover = new HoverEvent(var10002, Text.literal(var10003 + this.getTimeAgo(time)));
               MutableText messageText = Text.literal(key).setStyle(Style.EMPTY.withHoverEvent(messageHover));
               PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
                  ClientLogger.getLogger().log((Text)messageText, false);
               }, 100L, TimeUnit.MILLISECONDS);
            }

            ((IrcModule)this.module).offlineMessages.keySet().removeAll(remove.keySet());
         });
         mutableText.setStyle(Style.EMPTY.withHoverEvent(hoverEvent).withClickEvent(clickEvent));
         if (!this.sentMessage) {
            ClientLogger.getLogger().log((Text)mutableText, false);
            this.sentMessage = true;
         }
      } else {
         this.sentMessage = false;
      }

      if (Managers.getIrcManager().isConnected() && ((IrcModule)this.module).isInsideRoom() && ((IrcModule)this.module).getAntiKickTimer().passed(60000L)) {
         IrcManager var10000 = Managers.getIrcManager();
         String var10002 = IrcModule.IGNORE_PREFIX;
         var10000.sendMessage("#keqing4pollos", var10002 + "_" + TextUtil.randomString(5));
         ((IrcModule)this.module).getAntiKickTimer().reset();
      }

      if (!Managers.getIrcManager().isConnected() && ((IrcModule)this.module).getRetryTimer().passed(this.delay) && !((IrcModule)this.module).isInitializing()) {
         ((IrcModule)this.module).setInsideRoom(false);
         ((IrcModule)this.module).getRetryTimer().reset();
         PollosHookThread.submit(() -> {
            if (this.delay < 120000L) {
               ++this.retries;
               this.delay += 5000L * (long)this.retries;
            }

            ClientLogger.getLogger().log("Reconnecting to IRC...", false);
            Managers.getIrcManager().reconnect();
            Managers.getIrcManager().join();
            ClientLogger.getLogger().log("Reconnected to IRC", false);
            this.retries = 0;
            this.delay = 30000L;
         });
      }

   }

   private String getTimeAgo(long timestamp) {
      long now = Instant.now().toEpochMilli();
      long diffMillis = now - timestamp;
      long hours;
      if (diffMillis < 60000L) {
         hours = diffMillis / 1000L;
         return hours + " seconds ago";
      } else if (diffMillis < 3600000L) {
         hours = diffMillis / 60000L;
         return hours + " minutes ago";
      } else {
         hours = diffMillis / 3600000L;
         long minutes = diffMillis % 3600000L / 60000L;
         return minutes == 0L ? hours + " hours ago" : hours + " hours and " + minutes + " minutes ago";
      }
   }
}