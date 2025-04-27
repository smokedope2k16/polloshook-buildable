package me.pollos.polloshook.impl.module.movement.boatfly;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;

public class BoatFly extends ToggleableModule {
   protected final Value<Boolean> fixYaw = new Value(false, new String[]{"YawFix", "fixyaw"});
   protected final NumberValue<Float> upSpeed = new NumberValue(2.0F, 0.1F, 10.0F, 0.1F, new String[]{"UpSpeed", "+speed", "up"});
   protected final NumberValue<Float> downSpeed = new NumberValue(2.0F, 0.1F, 10.0F, 0.1F, new String[]{"DownSpeed", "-speed", "down"});
   protected final Value<Boolean> glide = new Value(false, new String[]{"Glide", "g", "gslide"});
   protected final NumberValue<Float> glideSpeed;
   protected final Value<Boolean> remount;

   public BoatFly() {
      super(new String[]{"BoatFly", "bfly", "boatflying"}, Category.MOVEMENT);
      this.glideSpeed = (new NumberValue(0.033F, 0.001F, 0.5F, 0.01F, new String[]{"GlideSpeed", "glidesped"})).setParent(this.glide);
      this.remount = new Value(false, new String[]{"Remount", "remounter"});
      this.offerValues(new Value[]{this.fixYaw, this.upSpeed, this.downSpeed, this.glide, this.glideSpeed, this.remount});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerGetGravity(this), new ListenerEntityPassengers(this)});
   }

   protected boolean isValid(Entity entity) {
      return entity instanceof BoatEntity && entity.getControllingPassenger() == mc.player;
   }

   public static class GetGravityEvent extends Event {
      private final Entity entity;

      
      public Entity getEntity() {
         return this.entity;
      }

      
      public GetGravityEvent(Entity entity) {
         this.entity = entity;
      }
   }
}
