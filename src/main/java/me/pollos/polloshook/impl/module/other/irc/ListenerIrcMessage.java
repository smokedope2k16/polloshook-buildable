package me.pollos.polloshook.impl.module.other.irc;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.command.irc.FetchUsersCommand;
import me.pollos.polloshook.impl.events.irc.IrcMessageEvent;
import me.pollos.polloshook.impl.module.other.irc.util.IrcPing;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

public class ListenerIrcMessage extends ModuleListener<IrcModule, IrcMessageEvent> {
   public ListenerIrcMessage(IrcModule module) {
      super(module, IrcMessageEvent.class);
   }

   public void call(IrcMessageEvent event) {
      if (!((IrcModule)this.module).isInsideRoom()) {
         (new FetchUsersCommand()).execute(new String[0]);
         ((IrcModule)this.module).startingMessages.add("Connected to IRC use %s to chat".formatted(new Object[]{((IrcModule)this.module).prefix.getValue()}));
         PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
            return ((IrcModule)this.module).startingMessages.add("<FILL>");
         }, 200L, TimeUnit.MILLISECONDS);
         ((IrcModule)this.module).getAntiKickTimer().reset();
         ((IrcModule)this.module).setInsideRoom(true);
      }

      String message = event.getMessage().stripLeading();
      if (!this.handlePing(message) && !message.startsWith(IrcModule.IGNORE_PREFIX)) {
         String sender = event.getSender();
         if (sender.startsWith("MC")) {
            sender = sender.replaceFirst("MC", "");
         }

         String text;
         if (event.getType().isConnection()) {
            text = sender + event.getMessage();
         } else {
            String greenText = message.startsWith(">") ? String.valueOf(Formatting.GREEN) + message : message;
            List<String> onlineUsers = Managers.getIrcManager().getFixedUsers();
            Iterator var7 = onlineUsers.iterator();

            while(var7.hasNext()) {
               String user = (String)var7.next();
               String mention = "@" + user;
               if (greenText.contains(mention)) {
                  Formatting formatting = message.startsWith("> ") ? Formatting.GREEN : Formatting.RESET;
                  greenText = greenText.replace(mention, String.valueOf(Formatting.WHITE) + mention + String.valueOf(formatting));
                  if (user.equalsIgnoreCase(Managers.getIrcManager().getFixedName(Managers.getIrcManager().getUsername()))) {
                     Render2DMethods.ding();
                  }
               }
            }

            text = String.format("%s: %s", sender, greenText);
         }

         if (!event.getType().isConnection() && PlayerUtil.isNull() && event.getType() == IrcMessageEvent.IrcMessageType.CHAT_SERVER) {
            ((IrcModule)this.module).offlineMessages.put(text, System.currentTimeMillis());
         } else {
            ClientLogger.getLogger().log(text, false);
         }
      }
   }

   private boolean handlePing(String line) {
      boolean valid = false;
      if (line.startsWith(IrcModule.PING_PREFIX)) {
         try {
            String[] args = line.split(",");
            IrcPing ping = new IrcPing(System.currentTimeMillis(), args[1], Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]), args[5], args[6]);
            if (mc.getCurrentServerEntry() != null && ping.ip().equalsIgnoreCase(mc.getCurrentServerEntry().address)) {
               if (!ping.name().equalsIgnoreCase(EntityUtil.getName(mc.player)) && (Boolean)((IrcModule)this.module).soundOnPing.getValue()) {
                  mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 0.3F));
               }

               String coords = String.format("%s, %s, %s", ping.x(), ping.y(), ping.z());
               ClientLogger.getLogger().log("Ping: " + ping.name() + ", " + coords + ", Dimension: " + ping.dimension(), false);
            }

            ((IrcModule)this.module).pings.add(ping);
            valid = true;
         } catch (Exception var6) {
         }
      }

      return valid;
   }
}