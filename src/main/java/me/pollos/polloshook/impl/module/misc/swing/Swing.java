package me.pollos.polloshook.impl.module.misc.swing;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.swing.modes.CancelSwing;
import me.pollos.polloshook.impl.module.misc.swing.modes.ForceSwing;
import net.minecraft.util.Hand;

public class Swing extends ToggleableModule {
   protected final EnumValue<CancelSwing> noSwing;
   protected final EnumValue<ForceSwing> forceSwing;
   protected final Value<Boolean> oldSwing;
   protected final Value<Boolean> slowSwing;
   protected final NumberValue<Integer> delay;
   protected boolean cancelSwing;

   public Swing() {
      super(new String[]{"Swing", "noswing"}, Category.MISC);
      this.noSwing = new EnumValue(CancelSwing.NONE, new String[]{"NoSwing", "cancelswing"});
      this.forceSwing = (new EnumValue(ForceSwing.NONE, new String[]{"ForceSwing", "force"})).setParent(this.noSwing, CancelSwing.FULL, true);
      this.oldSwing = (new Value(false, new String[]{"1.7Swing", "oldswing"})).setParent(this.noSwing, CancelSwing.FULL, true);
      this.slowSwing = (new Value(false, new String[]{"Slow", "slowswing"})).setParent(this.noSwing, CancelSwing.FULL, true);
      this.delay = (new NumberValue(6, 1, 25, new String[]{"Delay", "speed"})).setParent(this.slowSwing);
      this.offerValues(new Value[]{this.noSwing, this.forceSwing, this.oldSwing, this.slowSwing, this.delay});
      this.offerListeners(new Listener[]{new ListenerSwing(this), new ListenerUseItem(this), new ListenerDrop(this), new ListenerClick(this)});
   }

   protected String getTag() {
      if (this.noSwing.getValue() != CancelSwing.NONE) {
         return this.noSwing.getStylizedName();
      } else {
         return this.forceSwing.getValue() != ForceSwing.NONE ? this.forceSwing.getStylizedName() : null;
      }
   }

   public Hand getHand() {
      switch((ForceSwing)this.forceSwing.getValue()) {
      case MAIN_HAND:
         return Hand.MAIN_HAND;
      case OFF_HAND:
         return Hand.OFF_HAND;
      default:
         return null;
      }
   }

   
   public EnumValue<CancelSwing> getNoSwing() {
      return this.noSwing;
   }

   
   public EnumValue<ForceSwing> getForceSwing() {
      return this.forceSwing;
   }

   
   public Value<Boolean> getOldSwing() {
      return this.oldSwing;
   }

   
   public Value<Boolean> getSlowSwing() {
      return this.slowSwing;
   }

   
   public NumberValue<Integer> getDelay() {
      return this.delay;
   }

   
   public boolean isCancelSwing() {
      return this.cancelSwing;
   }

   
   public void setCancelSwing(boolean cancelSwing) {
      this.cancelSwing = cancelSwing;
   }
}
