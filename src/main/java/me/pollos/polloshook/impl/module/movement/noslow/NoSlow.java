package me.pollos.polloshook.impl.module.movement.noslow;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.asm.ducks.entity.IEntity;
import me.pollos.polloshook.impl.module.movement.noslow.mode.NoSlowClimbingMode;
import net.minecraft.util.math.Vec3d;

public class NoSlow extends ToggleableModule {
   protected final Value<Boolean> items = new Value(true, new String[]{"Items", "item"});
   protected final Value<Boolean> ncpStrict = new Value(false, new String[]{"NCPStrict", "ncp"});
   protected final Value<Boolean> airStrict = new Value(false, new String[]{"AirStrict", "air"});
   protected final Value<Boolean> grim = new Value(false, new String[]{"Grim", "grimothy"});
   protected final Value<Boolean> extra;
   protected final Value<Boolean> noJumpDelay;
   protected final Value<Boolean> sprint;
   protected final Value<Boolean> climbing;
   protected final EnumValue<NoSlowClimbingMode> mode;
   protected final NumberValue<Float> factor;
   protected final Value<Boolean> webs;
   protected final NumberValue<Float> webSpeed;

   public NoSlow() {
      super(new String[]{"NoSlow", "noslowdown"}, Category.MOVEMENT);
      this.extra = (new Value(false, new String[]{"ExtraGrim", "extrapacket"})).setParent(this.grim);
      this.noJumpDelay = new Value(false, new String[]{"NoJumpDelay", "nojumpcooldown"});
      this.sprint = new Value(false, new String[]{"Sprint", "keepsprint"});
      this.climbing = new Value(false, new String[]{"Climbing", "climb"});
      this.mode = (new EnumValue(NoSlowClimbingMode.SPEED, new String[]{"Mode", "m"})).setParent(this.climbing);
      this.factor = (new NumberValue(1.2F, 1.0F, 2.5F, 0.1F, new String[]{"Factor", "factoid"})).setParent(this.mode, NoSlowClimbingMode.SPEED);
      this.webs = new Value(false, new String[]{"Webs", "Web"});
      this.webSpeed = (new NumberValue(1.0F, 1.0F, 20.0F, 0.1F, new String[]{"WebSpeed", "speed"})).setParent(this.webs);
      this.offerValues(new Value[]{this.items, this.ncpStrict, this.airStrict, this.grim, this.extra, this.noJumpDelay, this.sprint, this.climbing, this.mode, this.factor, this.webs, this.webSpeed});
      this.offerListeners(new Listener[]{new ListenerItemSlow(this), new ListenerInteractBlock(this), new ListenerTick(this), new ListenerInteract(this), new ListenerClimb(this), new ListenerApplyClimbingSpeed(this), new ListenerMove(this), new ListenerKeepSprint(this), new ListenerSlotClick(this)});
   }

   public boolean isInWeb() {
      if (PlayerUtil.isNull()) {
         return false;
      } else {
         return ((IEntity)mc.player).isInWeb() && mc.player.input.sneaking && !mc.player.noClip;
      }
   }

   public boolean isAirStrict() {
      boolean action = PlayerUtil.isEating() || PlayerUtil.isUsingBow() || PlayerUtil.isDrinking();
      return this.isEnabled() && (Boolean)this.airStrict.getValue() && action;
   }

   public static class IsClimbingEvent extends Event {
      
      private IsClimbingEvent() {
      }

      
      public static NoSlow.IsClimbingEvent create() {
         return new NoSlow.IsClimbingEvent();
      }
   }

   public static class ApplyClimbingSpeedEvent extends Event {
      private Vec3d vec;

      
      public Vec3d getVec() {
         return this.vec;
      }

      
      public void setVec(Vec3d vec) {
         this.vec = vec;
      }

      
      private ApplyClimbingSpeedEvent(Vec3d vec) {
         this.vec = vec;
      }

      
      public static NoSlow.ApplyClimbingSpeedEvent of(Vec3d vec) {
         return new NoSlow.ApplyClimbingSpeedEvent(vec);
      }
   }
}
