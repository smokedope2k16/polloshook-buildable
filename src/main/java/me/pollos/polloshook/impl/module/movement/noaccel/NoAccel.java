package me.pollos.polloshook.impl.module.movement.noaccel;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.anchor.Anchor;
import me.pollos.polloshook.impl.module.movement.elytrafly.ElytraFly;
import me.pollos.polloshook.impl.module.movement.fly.Fly;
import me.pollos.polloshook.impl.module.movement.holesnap.HoleSnap;
import me.pollos.polloshook.impl.module.movement.longjump.LongJump;
import me.pollos.polloshook.impl.module.movement.speed.Speed;
import me.pollos.polloshook.impl.module.movement.tickshift.TickShift;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;

public class NoAccel extends ToggleableModule {
   protected final Value<Boolean> air = new Value(true, new String[]{"InAir", "air"});
   protected final Value<Boolean> water = new Value(true, new String[]{"InWater", "water"});
   protected final Value<Boolean> soulSand = new Value(true, new String[]{"InSoulSand", "soulsand"});
   protected final Value<Boolean> whileClimbing = new Value(false, new String[]{"WhileClimbing", "climb"});
   protected final Value<Boolean> stopOnLagBack = new Value(false, new String[]{"StopOnLagBack", "lagback", "lagbackstop"});
   protected final StopWatch timer = new StopWatch();

   public NoAccel() {
      super(new String[]{"NoAccel", "noacceleration", "instant"}, Category.MOVEMENT);
      this.offerValues(new Value[]{this.air, this.water, this.soulSand, this.whileClimbing, this.stopOnLagBack});
      this.offerListeners(new Listener[]{new ListenerMove(this), new ListenerPosLook(this)});
   }

   public boolean cantNoAccel() {
      return mc.player.isSneaking() || !mc.player.isOnGround() && !(Boolean)this.air.getValue() || mc.player.isFallFlying() || mc.player.isCrawling() || mc.player.isClimbing() && !(Boolean)this.whileClimbing.getValue() || mc.player.isUsingRiptide() || this.isInSoulSand() && !(Boolean)this.soulSand.getValue() || ((Anchor)Managers.getModuleManager().get(Anchor.class)).isAnchoring() || ((Speed)Managers.getModuleManager().get(Speed.class)).isEnabled() || ((TickShift)Managers.getModuleManager().get(TickShift.class)).isBoosting() || ((HoleSnap)Managers.getModuleManager().get(HoleSnap.class)).isEnabled() || ((ElytraFly)Managers.getModuleManager().get(ElytraFly.class)).isElytra() || ((LongJump)Managers.getModuleManager().get(LongJump.class)).isEnabled() || ((Fly)Managers.getModuleManager().get(Fly.class)).isEnabled() || mc.player.getAbilities().flying || (Boolean)this.stopOnLagBack.getValue() && !this.timer.passed(100L) || (mc.player.isInLava() || mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || EntityUtil.isAboveWater(mc.player)) && !(Boolean)this.water.getValue();
   }

   private boolean isInSoulSand() {
      return mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock() == Blocks.SOUL_SAND || mc.world.getBlockState(mc.player.getBlockPos()).getBlock() == Blocks.SOUL_SAND;
   }

   public void strafe(MoveEvent event, double speed) {
      if (MovementUtil.isMoving()) {
         double[] strafe = this.strafe(speed);
         event.setVec(new Vec3d(strafe[0], event.getVec().y, strafe[1]));
      } else {
         event.setVec(new Vec3d(0.0D, event.getVec().y, 0.0D));
      }

   }

   private double[] strafe(double speed) {
      float moveForward = mc.player.forwardSpeed;
      float moveStrafe = mc.player.sidewaysSpeed;
      float rotationYaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * (float)mc.getRenderTime();
      if (moveForward != 0.0F) {
         if (moveStrafe > 0.0F) {
            rotationYaw += (float)(moveForward > 0.0F ? -45 : 45);
         } else if (moveStrafe < 0.0F) {
            rotationYaw += (float)(moveForward > 0.0F ? 45 : -45);
         }

         moveStrafe = 0.0F;
         if (moveForward > 0.0F) {
            moveForward = 1.0F;
         } else if (moveForward < 0.0F) {
            moveForward = -1.0F;
         }
      }

      double posX = (double)moveForward * speed * -Math.sin(Math.toRadians((double)rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians((double)rotationYaw));
      double posZ = (double)moveForward * speed * Math.cos(Math.toRadians((double)rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians((double)rotationYaw));
      return new double[]{posX, posZ};
   }
}