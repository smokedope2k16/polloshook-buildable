package me.pollos.polloshook.impl.module.misc.spammer;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.RandomUtil;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.misc.spammer.mode.SpammerMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class ListenerTick extends SafeModuleListener<Spammer, TickEvent> {
   public ListenerTick(Spammer module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (PlayerUtil.isNull()) {
         ((Spammer)this.module).setEnabled(false);
      } else if (((Spammer)this.module).currentFile == null && ((Spammer)this.module).mode.getValue() == SpammerMode.FILE) {
         ClientLogger.getLogger().log("Load keyCodec file to use this " + String.valueOf(Formatting.BLACK) + "nigger");
         ((Spammer)this.module).setEnabled(false);
      } else if (!(Boolean)((Spammer)this.module).loop.getValue() && ((Spammer)this.module).strings.isEmpty()) {
         ((Spammer)this.module).toggle();
      } else {
         try {
            switch((SpammerMode)((Spammer)this.module).mode.getValue()) {
            case BAN:
               Iterator var10 = mc.world.getPlayers().iterator();
               if (var10.hasNext()) {
                  PlayerEntity entity = (PlayerEntity)var10.next();
                  String namington = entity.getName().getString();
                  if (namington.equalsIgnoreCase(mc.player.getName().getString())) {
                     return;
                  }

                  this.send("/ban " + namington);
                  return;
               }
               break;
            case FILE:
               if (!((Spammer)this.module).timer.passed((double)((Float)((Spammer)this.module).delay.getValue() * 1000.0F))) {
                  return;
               }

               int line = (Boolean)((Spammer)this.module).randomize.getValue() ? RandomUtil.getRandom().nextInt(((Spammer)this.module).strings.size()) : 0;
               String text = (String)((Spammer)this.module).strings.get(line);
               String[] split = text.split((String)((Spammer)this.module).splitter.getValue());
               if (split.length > 1 && (Boolean)((Spammer)this.module).macros.getValue()) {
                  String[] var5 = split;
                  int var6 = split.length;

                  for(int var7 = 0; var7 < var6; ++var7) {
                     String part = var5[var7];
                     if (!TextUtil.isNullOrEmpty(part)) {
                        this.send(part);
                     }
                  }
               } else {
                  this.send(text);
               }

               ((Spammer)this.module).strings.remove(text);
               if ((Boolean)((Spammer)this.module).loop.getValue()) {
                  ((Spammer)this.module).strings.add(text);
               }
            }

            ((Spammer)this.module).timer.reset();
         } catch (Exception var9) {
            ClientLogger.getLogger().error("Failed to send spammer message [%s]".formatted(new Object[]{var9.getCause().getMessage()}));
         }

      }
   }

   private void send(String message) {
      if ((!(Boolean)((Spammer)this.module).sendSlashCommands.getValue() || !message.startsWith("/")) && ((Spammer)this.module).mode.getValue() != SpammerMode.BAN) {
         String antiKickText = (Boolean)((Spammer)this.module).antiKick.getValue() ? " | " + RandomUtil.newRandomString((Integer)((Spammer)this.module).length.getValue()) : "";
         String greenText = (Boolean)((Spammer)this.module).greenText.getValue() ? "> " : "";
         mc.player.networkHandler.sendChatMessage(greenText + message + antiKickText);
      } else {
         mc.player.networkHandler.sendChatCommand(message.replaceFirst("/", ""));
      }
   }
}