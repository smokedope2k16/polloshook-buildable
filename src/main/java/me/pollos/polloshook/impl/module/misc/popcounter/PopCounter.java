package me.pollos.polloshook.impl.module.misc.popcounter;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.popcounter.mode.PopCounterStyle;

public class PopCounter extends CommandModule {
   protected final Value<Boolean> clearOnVisualRange = new Value(true, new String[]{"VisualRange", "visualrange"});
   protected final EnumValue<PopCounterStyle> style;
   protected final Value<Boolean> selfFriend;
   protected final Value<Boolean> hideSelf;

   public PopCounter() {
      super(new String[]{"PopCounter", "totempopcounter"}, Category.MISC, new String[]{"ClearPops", "removepops", "killpops"}, new PopCounter.PopMapArgument("[player]"));
      this.style = new EnumValue(PopCounterStyle.CARDINAL, new String[]{"NumberStyle", "style"});
      this.selfFriend = new Value(false, new String[]{"SelfFriend", "self"});
      this.hideSelf = new Value(false, new String[]{"HideSelf", "hiddenself"});
      this.offerListeners(new Listener[]{new ListenerPop(this), new ListenerDeath(this)});
      this.offerValues(new Value[]{this.clearOnVisualRange, this.style, this.selfFriend, this.hideSelf});
   }

   public String onCommand(String[] args) {
      Map<String, Integer> map = Managers.getPopManager().getPopMap();
      if (args.length == 1) {
         int s = map.size();
         if (s == 0) {
            return "No players in popmap";
         } else {
            map.clear();
            return "Cleared [%s] player%s".formatted(new Object[]{s, s == 1 ? "" : "s"});
         }
      } else if (args.length == 2) {
         String args1 = args[1];
         if (map.get(args1) != null) {
            Managers.getPopManager().getPopMap().remove(args1);
            return "Cleared %s totempops".formatted(new Object[]{args1});
         } else {
            return "No player in popmap labeled %s".formatted(new Object[]{args1});
         }
      } else {
         return this.getInfo();
      }
   }

   
   public Value<Boolean> getClearOnVisualRange() {
      return this.clearOnVisualRange;
   }

   
   public EnumValue<PopCounterStyle> getStyle() {
      return this.style;
   }

   
   public Value<Boolean> getSelfFriend() {
      return this.selfFriend;
   }

   
   public Value<Boolean> getHideSelf() {
      return this.hideSelf;
   }

   private static class PopMapArgument extends Argument {
      public PopMapArgument(String label) {
         super(label);
      }

      public String predict(String currentArg) {
         Map<String, Integer> map = Managers.getPopManager().getPopMap();
         Iterator var3 = map.entrySet().iterator();

         String key;
         do {
            if (!var3.hasNext()) {
               return super.predict(currentArg);
            }

            Entry<String, Integer> entry = (Entry)var3.next();
            key = (String)entry.getKey();
         } while(!key.toLowerCase().startsWith(currentArg.toLowerCase()));

         return key;
      }
   }
}
