package me.pollos.polloshook.impl.module.movement.tridentfly;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.util.math.Vec3d;

public class TridentFly extends ToggleableModule {
   protected final Value<Boolean> grim = new Value(false, new String[]{"Grim", "grimothy"});
   protected final Value<Boolean> requireInput = new Value(true, new String[]{"RequireInput", "requiremovement"});
   protected final Value<Boolean> alwaysAllow = new Value(true, new String[]{"AlwaysAllow", "always"});
   protected final Value<Boolean> pitchLock = new Value(false, new String[]{"PitchLock", "lockpitch"});
   protected final NumberValue<Integer> pitch;
   protected final Value<Boolean> requireMouseDown;
   protected final Value<Boolean> autoRelease;
   protected final NumberValue<Integer> useTicks;
   protected final NumberValue<Integer> maxSpeed;
   protected final NumberValue<Float> delay;
   protected boolean flag;
   protected final StopWatch timer;

   public TridentFly() {
      super(new String[]{"TridentFly", "tfly", "tridentflight"}, Category.MOVEMENT);
      this.pitch = (new NumberValue(0, -90, 90, new String[]{"Pitch", "p"})).withTag("degree").setParent(this.pitchLock);
      this.requireMouseDown = new Value(true, new String[]{"RequireMouseDown", "requiremouse", "mousedown"});
      this.autoRelease = new Value(false, new String[]{"AutoRelease", "autoreleased"});
      this.useTicks = (new NumberValue(3, 0, 10, new String[]{"UseTicks", "ticks", "use"})).setParent(this.autoRelease);
      this.maxSpeed = (new NumberValue(250, 100, 1000, 25, new String[]{"MaxSpeed", "maxsped", "speed"})).withTag("km/h").setNoLimit(true);
      this.delay = new NumberValue(1.0F, 0.0F, 10.0F, 0.1F, new String[]{"Delay", "del", "d"});
      this.flag = false;
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.grim, this.requireInput, this.alwaysAllow, this.pitchLock, this.pitch, this.requireMouseDown, this.autoRelease, this.useTicks, this.maxSpeed, this.delay});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerRain(this), new ListenerUse(this), new ListenerDelay(this)});
   }

   protected String getTag() {
      return mc.player == null ? super.getTag() : "%.2f".formatted(new Object[]{this.getVelocitySpeed()});
   }

   protected double getVelocitySpeed() {
      Vec3d vec = mc.player.getVelocity();
      return Math.abs(vec.getX() + vec.getY() + vec.getZ());
   }

   public static class MaxTridentTicksEvent extends Event {
      
      private MaxTridentTicksEvent() {
      }

      
      public static TridentFly.MaxTridentTicksEvent create() {
         return new TridentFly.MaxTridentTicksEvent();
      }
   }

   public static class UseTridentEvent extends Event {
      
      private UseTridentEvent() {
      }

      
      public static TridentFly.UseTridentEvent create() {
         return new TridentFly.UseTridentEvent();
      }
   }

   public static class TryUseTridentNoRainEvent extends Event {
      
      private TryUseTridentNoRainEvent() {
      }

      
      public static TridentFly.TryUseTridentNoRainEvent create() {
         return new TridentFly.TryUseTridentNoRainEvent();
      }
   }
}
