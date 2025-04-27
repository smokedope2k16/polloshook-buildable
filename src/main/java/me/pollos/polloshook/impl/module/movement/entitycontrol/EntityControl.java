package me.pollos.polloshook.impl.module.movement.entitycontrol;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class EntityControl extends ToggleableModule {
   protected final Value<Boolean> control = new Value(true, new String[]{"Control", "autocontrol", "saddle"});
   protected final NumberValue<Float> jumpStrength = new NumberValue(0.7F, 0.1F, 3.5F, 0.1F, new String[]{"JumpStrength", "jumpstrengh"});

   public EntityControl() {
      super(new String[]{"EntityControl", "entitycontroller", "control"}, Category.MOVEMENT);
      this.offerValues(new Value[]{this.control, this.jumpStrength});
      this.offerListeners(new Listener[]{new ListenerSaddle(this), new ListenerJumpStrength(this)});
   }

   public static class HorseJumpStrengthEvent extends Event {
      private float strength;

      
      public float getStrength() {
         return this.strength;
      }

      
      public void setStrength(float strength) {
         this.strength = strength;
      }

      
      public HorseJumpStrengthEvent(float strength) {
         this.strength = strength;
      }
   }

   public static class SaddleEvent extends Event {
   }
}
