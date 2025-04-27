package me.pollos.polloshook.impl.module.misc.antiafk;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.attribute.EntityAttributes;

public class AntiAFK extends ToggleableModule {
   protected final NumberValue<Integer> frequency = (new NumberValue(50, 1, 200, new String[]{"Frequency", "frequent"})).withTag("second");
   protected final Value<Boolean> rotate = new Value(true, new String[]{"Rotate", "rotations"});
   protected final NumberValue<Float> amount;
   protected final Value<Boolean> jump;
   protected final Value<Boolean> sneak;
   protected final Value<Boolean> walk;
   protected final StopWatch timer;
   protected Input input;

   public AntiAFK() {
      super(new String[]{"AntiAFK", "afk", "noafk", "stopafk"}, Category.MISC);
      this.amount = (new NumberValue(2.5F, 0.1F, 25.0F, 0.25F, new String[]{"Degree", "degrees"})).withTag("degree").setParent(this.rotate);
      this.jump = new Value(true, new String[]{"Jump", "hop"});
      this.sneak = new Value(true, new String[]{"Sneak", "crouch"});
      this.walk = new Value(true, new String[]{"Walk", "move"});
      this.timer = new StopWatch();
      this.input = new KeyboardInput(mc.options);
      this.offerValues(new Value[]{this.frequency, this.rotate, this.amount, this.jump, this.sneak, this.walk});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerInput(this)});
   }

   protected String getTag() {
      float seconds = (float)this.timer.getTime() / 1000.0F;
      return String.format("%.1fs", seconds);
   }

   protected void onDisable() {
      if (mc.player != null && mc.player.input.getClass() == this.input.getClass()) {
         KeyboardInput keyboardInput = new KeyboardInput(mc.options);
         keyboardInput.tick(mc.player.shouldSlowDown(), (float)mc.player.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED));
         mc.player.input = keyboardInput;
      }

   }


   public Input getInput() {
      return this.input;
   }
}