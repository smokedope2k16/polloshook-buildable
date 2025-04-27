package me.pollos.polloshook.api.minecraft.movement;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import net.minecraft.client.input.Input;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtil implements Minecraftable {
   public static boolean isMoving() {
      return isMoving(mc.player);
   }

   public static boolean isMoving(LivingEntity entity) {
      return entity.forwardSpeed != 0.0F || entity.sidewaysSpeed != 0.0F;
   }

   public static boolean isUpdatingPos() {
      return mc.player.getX() != mc.player.prevX || mc.player.getY() != mc.player.prevY || mc.player.getZ() != mc.player.prevZ;
   }

   public static float getSpoofYaw() {
      boolean forward = mc.player.input.pressingForward;
      boolean back = mc.player.input.pressingBack;
      boolean left = mc.player.input.pressingLeft;
      boolean right = mc.player.input.pressingRight;
      float yaw = mc.player.getYaw();
      if (forward && !back) {
         if (left && !right) {
            yaw -= 45.0F;
         } else if (right && !left) {
            yaw += 45.0F;
         }
      } else if (back && !forward) {
         yaw += 180.0F;
         if (left && !right) {
            yaw += 45.0F;
         } else if (right && !left) {
            yaw -= 45.0F;
         }
      } else if (left && !right) {
         yaw -= 90.0F;
      } else if (right && !left) {
         yaw += 90.0F;
      }

      return MathHelper.wrapDegrees(yaw);
   }

   public static void strafe(MoveEvent event, double speed) {
      strafe(event, event.getVec().y, speed);
   }

   public static void strafe(MoveEvent event, double speed, Input input) {
      strafe(event, event.getVec().y, speed, input);
   }

   public static void strafe(MoveEvent event, double y, double speed) {
      if (isMoving()) {
         double[] strafe = strafe(speed);
         event.setVec(new Vec3d(strafe[0], y, strafe[1]));
      } else {
         event.setVec(new Vec3d(0.0D, event.getVec().y, 0.0D));
      }

   }

   public static void strafe(MoveEvent event, double y, double speed, Input input) {
      if (isMoving()) {
         double[] strafe = strafe(mc.player, input, speed);
         event.setVec(new Vec3d(strafe[0], y, strafe[1]));
      } else {
         event.setVec(new Vec3d(0.0D, event.getVec().y, 0.0D));
      }

   }

   public static double[] strafe(double speed) {
      return strafe((Entity)mc.player, speed);
   }

   public static double[] strafe(Entity entity, double speed) {
      return strafe(entity, mc.player.input, speed);
   }

   public static double[] strafe(Entity entity, Input movementInput, double speed) {
      float moveForward = movementInput.movementForward;
      float moveStrafe = movementInput.movementSideways;
      float rotationYaw = entity.prevYaw + (entity.getYaw() - entity.prevYaw) * (float)mc.getRenderTime();
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

   public static boolean isMovingChina(Entity entity) {
      return entity.getX() != entity.prevX || entity.getY() != entity.prevY || entity.getZ() != entity.prevZ;
   }

   public static boolean hasVelocity(Entity entity) {
      Vec3d velo = entity.getVelocity();
      return velo.getX() != 0.0D || velo.getY() != -0.0784000015258789D || velo.getZ() != 0.0D;
   }

   public static boolean isRotating() {
      double yaw = (double)(mc.player.getYaw() - ((IClientPlayerEntity)mc.player).getLastYaw());
      double pitch = (double)(mc.player.getPitch() - ((IClientPlayerEntity)mc.player).getLastPitch());
      return yaw != 0.0D || pitch != 0.0D;
   }

   public static boolean anyMovementKeysWASD() {
      return mc.options.rightKey.isPressed() || mc.options.leftKey.isPressed() || mc.options.backKey.isPressed() || mc.options.forwardKey.isPressed();
   }

   public static boolean anyInputWASD(Input input) {
      return input.pressingBack || input.pressingForward || input.pressingRight || input.pressingLeft;
   }

   public static boolean anyMovementKeys() {
      return anyMovementKeysWASD() || mc.options.backKey.isPressed() || mc.options.forwardKey.isPressed();
   }

   public static boolean anyMovementKeysNoSneak() {
      return anyMovementKeysWASD() || mc.options.jumpKey.isPressed();
   }

   public static double getDefaultMoveSpeed() {
      double baseSpeed = 0.2873D;
      StatusEffectInstance speed = mc.player.getStatusEffect(StatusEffects.SPEED);
      if (speed != null) {
         int amplifier = speed.getAmplifier();
         baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
      }

      return baseSpeed;
   }

   public static double getDistance2D() {
      return getDistance2D(mc.player);
   }

   public static double getDistance2D(Entity entity) {
      double xDist = entity.getX() - entity.prevX;
      double zDist = entity.getZ() - entity.prevZ;
      double xDistSq = MathHelper.square(xDist);
      double zDistSq = MathHelper.square(zDist);
      return Math.sqrt(xDistSq + zDistSq);
   }

   public static double calcEffects(double speed) {
      StatusEffectInstance speedEffect = mc.player.getStatusEffect(StatusEffects.SPEED);
      if (speedEffect != null) {
         speed *= 1.0D + 0.2D * (double)(speedEffect.getAmplifier() + 1);
      }

      StatusEffectInstance slownessEffect = mc.player.getStatusEffect(StatusEffects.SLOWNESS);
      if (slownessEffect != null) {
         speed /= 1.0D + 0.2D * (double)(slownessEffect.getAmplifier() + 1);
      }

      return speed;
   }

   public static double getJumpSpeed() {
      double defaultSpeed = 0.0D;
      if (mc.player.getStatusEffect(StatusEffects.JUMP_BOOST) != null) {
         int amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
         defaultSpeed += (double)(amplifier + 1) * 0.1D;
      }

      return defaultSpeed;
   }

   public static void setXZVelocity(double[] dir, Entity entity) {
      setXZVelocity(dir[0], dir[1], entity);
   }

   public static void setXZVelocity(double x, double z, Entity entity) {
      entity.setVelocity(x, entity.getVelocity().y, z);
   }

   public static void setYVelocity(double y, Entity entity) {
      entity.setVelocity(entity.getVelocity().x, y, entity.getVelocity().z);
   }

   public static double[] directionSpeed(double speed) {
      float forward = mc.player.input.movementForward;
      float side = mc.player.input.movementSideways;
      float yaw = (float)(mc.player.prevY + (double)((mc.player.getYaw() - mc.player.prevYaw) * mc.getRenderTickCounter().getTickDelta(true)));
      if (forward != 0.0F) {
         if (side > 0.0F) {
            yaw += (float)(forward > 0.0F ? -45 : 45);
         } else if (side < 0.0F) {
            yaw += (float)(forward > 0.0F ? 45 : -45);
         }

         side = 0.0F;
         if (forward > 0.0F) {
            forward = 1.0F;
         } else if (forward < 0.0F) {
            forward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double posX = (double)forward * speed * cos + (double)side * speed * sin;
      double posZ = (double)forward * speed * sin - (double)side * speed * cos;
      return new double[]{posX, posZ};
   }
}