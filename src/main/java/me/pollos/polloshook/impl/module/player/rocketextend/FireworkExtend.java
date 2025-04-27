package me.pollos.polloshook.impl.module.player.rocketextend;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.asm.ducks.entity.IFireworkRocketEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;

public class FireworkExtend extends ToggleableModule {
   protected final NumberValue<Float> timeout = (new NumberValue(15.0F, 0.1F, 60.0F, 0.1F, new String[]{"Timeout", "time", "life"})).withTag("second").setNoLimit(true);
   protected FireworkRocketEntity rocket;
   protected final StopWatch timer = new StopWatch();

   public FireworkExtend() {
      super(new String[]{"FireworkExtend", "extendedfirework", "fireworkextension", "rocketextend"}, Category.PLAYER);
      this.offerValues(new Value[]{this.timeout});
      this.offerListeners(new Listener[]{new ListenerFirework(this), new ListenerPosLook(this), new ListenerUpdate(this), new ListenerRemove(this)});
   }

   protected void onToggle() {
      this.timer.reset();
      this.removeRocket();
   }

   protected String getTag() {
      return this.rocket == null ? "0.0s" : String.format("%.1f", (double)this.timer.getTime() / 1000.0D);
   }

   protected void removeRocket() {
      if (this.rocket != null) {
         IFireworkRocketEntity access = (IFireworkRocketEntity)this.rocket;
         access.remove();
         this.rocket = null;
      }

   }

   public static class ExplodeAndRemoveEvent extends Event {
      
      private ExplodeAndRemoveEvent() {
      }

      
      public static FireworkExtend.ExplodeAndRemoveEvent create() {
         return new FireworkExtend.ExplodeAndRemoveEvent();
      }
   }

   public static class TickFireworkEvent extends Event {
      private final FireworkRocketEntity rocket;
      private final int lifeFactor;

      
      public FireworkRocketEntity getRocket() {
         return this.rocket;
      }

      
      public int getLifeFactor() {
         return this.lifeFactor;
      }

      
      private TickFireworkEvent(FireworkRocketEntity rocket, int lifeFactor) {
         this.rocket = rocket;
         this.lifeFactor = lifeFactor;
      }

      
      public static FireworkExtend.TickFireworkEvent of(FireworkRocketEntity rocket, int lifeFactor) {
         return new FireworkExtend.TickFireworkEvent(rocket, lifeFactor);
      }
   }
}
