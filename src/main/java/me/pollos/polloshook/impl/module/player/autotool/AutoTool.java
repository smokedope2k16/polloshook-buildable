package me.pollos.polloshook.impl.module.player.autotool;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;

public class AutoTool extends ToggleableModule {
   protected final Value<Boolean> onlyIfSneak = new Value(false, new String[]{"OnlyIfSneaking", "sneak"});
   protected int lastSlot = -1;
   protected boolean setLastSlot = false;

   public AutoTool() {
      super(new String[]{"AutoTool", "tool", "toolswitch", "tooler"}, Category.PLAYER);
      this.offerValues(new Value[]{this.onlyIfSneak});
      this.offerListeners(new Listener[]{new ListenerDamageBlock(this), new ListenerDeath(this), new ListenerLeaveGame(this), new ListenerUpdate(this)});
   }

   protected void onToggle() {
      this.reset();
   }

   public void onWorldLoad() {
      this.reset();
   }

   protected boolean sneakCheck() {
      if ((Boolean)this.onlyIfSneak.getValue()) {
         return !mc.player.isSneaking();
      } else {
         return false;
      }
   }

   protected void reset() {
      this.lastSlot = -1;
      this.setLastSlot = false;
   }
}
