package me.pollos.polloshook.impl.module.movement.autowalk;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.attribute.EntityAttributes;

public class AutoWalk extends ToggleableModule {
   protected final Value<Boolean> lock = new Value(false, new String[]{"Lock", "l", "lockedin"});
   protected Input input;

   public AutoWalk() {
      super(new String[]{"AutoWalk", "walk", "foward"}, Category.MOVEMENT);
      this.input = new KeyboardInput(mc.options);
      this.offerListeners(new Listener[]{new ListenerInput(this), new ListenerUpdate(this)});
      this.offerValues(new Value[]{this.lock});
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
