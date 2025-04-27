package me.pollos.polloshook.impl.module.movement.step;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.step.mode.StepMode;
import net.minecraft.entity.attribute.EntityAttributes;

public class Step extends ToggleableModule {
   protected final EnumValue<StepMode> mode;
   protected final NumberValue<Float> height;
   protected final StopWatch timer;

   public Step() {
      super(new String[]{"Step", "stepped", "stepping", "stepbro"}, Category.MOVEMENT);
      this.mode = new EnumValue(StepMode.VANILLA, new String[]{"Mode", "stepmode"});
      this.height = new NumberValue(2.0F, 1.0F, 5.0F, 0.1F, new String[]{"Height", "stepheight"});
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.mode, this.height});
      this.offerListeners(new Listener[]{new ListenerStep(this), new ListenerPosLook(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   protected void onDisable() {
      if (mc.player != null) {
         mc.player.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue(0.6000000238418579D);
      }
   }

   public void onWorldLoad() {
      this.setEnabled(false);
   }
}