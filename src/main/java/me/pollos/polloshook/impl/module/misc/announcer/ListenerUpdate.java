package me.pollos.polloshook.impl.module.misc.announcer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.RandomUtil;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerMode;
import net.minecraft.util.Formatting;

public class ListenerUpdate extends SafeModuleListener<Announcer, UpdateEvent> {
   private final String[] sunriseMessages = new String[]{"Goodmoorning!", "Top of the morning to ya!", "Its time for keyCodec great day everyone!", "You survived another night!", "Goodmorning everyone!", "Today is going to be keyCodec great day everyone!"};
   private final String[] middleDayMessages = new String[]{"Let's go to the beach!", "It's keyCodec great day for the beach!", "Grab your sunglasses everyone!", "Enjoy the great day everyone!", "It's the brightest time of the day!", "Let's go tanning!"};
   private final String[] noonMessages = new String[]{"Let's go grab lunch!", "Lunch time!", "Good afternoon everyone!", "Good afternoon!", "Halfway through the day everyone!", "Half of the day has passed!"};
   private final String[] sunsetMessages = new String[]{"The sun is setting!", "Enjoy the sunset everybody!", "The day is coming to an end everyone!", "Time to get ready for bed!", "Almost time to sleep everyone!"};
   private final String[] nightTimeMessages = new String[]{"Let's get comfy!", "Time to go to sleep everyone!", "Sunset has now ended, Muslims may now eat their dinner", "It's dark outside...", "It's the oppsite of noon!", "Zombies will now begin to spawn, take shelter everyone!"};
   private final String[] dayStartMessages = new String[]{"Zombies are now burning!", "You survived the night!", "The night is over!", "Burn baby burn!", "Monsters will stop spawning..."};

   public ListenerUpdate(Announcer module) {
      super(module, UpdateEvent.class);
   }

   public void safeCall(UpdateEvent event) {
      if ((Boolean)((Announcer)this.module).worldTime.getValue() && ((Announcer)this.module).worldTimer.passed((double)((Float)((Announcer)this.module).worldTimeDelay.getValue() * 1000.0F))) {
         String toSay = "";
         if (MathUtil.isBetween(mc.world.getTimeOfDay(), 0, 4500)) {
            toSay = this.sunriseMessages[RandomUtil.getRandom().nextInt(this.sunriseMessages.length)];
         }

         if (MathUtil.isBetween(mc.world.getTimeOfDay(), 4500, 6000)) {
            toSay = this.middleDayMessages[RandomUtil.getRandom().nextInt(this.middleDayMessages.length)];
         }

         if (MathUtil.isBetween(mc.world.getTimeOfDay(), 6000, 12000)) {
            toSay = this.noonMessages[RandomUtil.getRandom().nextInt(this.noonMessages.length)];
         }

         if (MathUtil.isBetween(mc.world.getTimeOfDay(), 12000, 14000)) {
            toSay = this.sunsetMessages[RandomUtil.getRandom().nextInt(this.sunsetMessages.length)];
         }

         if (MathUtil.isBetween(mc.world.getTimeOfDay(), 14000, 22000)) {
            toSay = this.nightTimeMessages[RandomUtil.getRandom().nextInt(this.nightTimeMessages.length)];
         }

         if (MathUtil.isBetween(mc.world.getTimeOfDay(), 22000, 24000)) {
            toSay = this.dayStartMessages[RandomUtil.getRandom().nextInt(this.dayStartMessages.length)];
         }

         if (TextUtil.isNullOrEmpty(toSay)) {
            return;
         }

         switch((AnnouncerMode)((Announcer)this.module).mode.getValue()) {
         case BROADCAST:
            mc.getNetworkHandler().sendChatMessage(toSay);
            break;
         case CLIENTSIDE:
            ClientLogger.getLogger().log(toSay);
         }

         ((Announcer)this.module).worldTimer.reset();
      }

      ((Announcer)this.module).setStrings();
      if (((Announcer)this.module).walkTimer.passed(15000L) && (Boolean)((Announcer)this.module).walk.getValue()) {
         ((Announcer)this.module).queued.put(AnnouncerAction.WALK, ((Announcer)this.module).walkDistance);
         ((Announcer)this.module).walkTimer.reset();
      }

      if (!((Announcer)this.module).queued.isEmpty() && ((Announcer)this.module).timer.passed(9000L)) {
         try {
            int size = ((Announcer)this.module).queued.size();
            if (size > 0) {
               List<Entry<AnnouncerAction, Float>> list = new ArrayList(((Announcer)this.module).queued.entrySet());
               Entry<AnnouncerAction, Float> entry = null;

               for(int attempts = 0; attempts < size; ++attempts) {
                  int rnd = RandomUtil.getRandom().nextInt(size);
                  entry = (Entry)list.get(rnd);
                  if (entry.getKey() == AnnouncerAction.WALK && (Float)entry.getValue() <= 1.0F) {
                     ((Announcer)this.module).queued.remove(entry.getKey());
                     break;
                  }
               }

               if (entry.getValue() != null && entry.getKey() != null && mc.player.isAlive()) {
                  ((Announcer)this.module).execute(entry);
                  if (entry.getKey() == AnnouncerAction.WALK) {
                     ((Announcer)this.module).walkDistance = 0.0F;
                  }
               }
            }
         } catch (Exception var7) {
            ClientLogger var10000 = ClientLogger.getLogger();
            String var10001 = String.valueOf(Formatting.RED);
            var10000.log(var10001 + "Error sending announcer message: " + String.valueOf(var7.getCause()));
            var7.printStackTrace();
         }

         ((Announcer)this.module).timer.reset();
         ((Announcer)this.module).queued.clear();
      }

   }
}
