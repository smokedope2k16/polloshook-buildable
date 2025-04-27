package me.pollos.polloshook.api.minecraft.entity;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.MineUtil;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class PlayerUtil implements Minecraftable {
   public static boolean isCreative() {
      return isCreative(mc.player);
   }

   public static boolean isCreative(PlayerEntity player) {
      return player != null && (player.isCreative() || player.isCreative());
   }

   public static boolean isSpectator() {
      return isSpectator(mc.player);
   }

   public static boolean isSpectator(PlayerEntity player) {
      return player != null && player.isSpectator();
   }

   public static boolean isInWeb() {
      return BlockUtil.getBlock(mc.player.getBlockPos()) == Blocks.COBWEB && !mc.player.isOnGround();
   }

   public static boolean isInLiquid() {
      return isInWater() || isInLava();
   }

   public static boolean isInWater() {
      return isInWater(mc.player);
   }

   public static boolean isInWater(PlayerEntity entity) {
      return entity.isTouchingWater() || entity.isSubmergedInWater();
   }

   public static boolean isInLava() {
      return isInLava(mc.player);
   }

   public static boolean isInLava(PlayerEntity entity) {
      return entity.isInLava();
   }

   public static boolean isEating() {
      return isEating(mc.player);
   }

   public static boolean isEating(PlayerEntity player) {
      if (player.getActiveHand() == null) {
         return false;
      } else if (!player.isUsingItem()) {
         return false;
      } else {
         return player.getStackInHand(player.getActiveHand()).getUseAction() == UseAction.EAT;
      }
   }

   public static double getReach() {
      if (mc.crosshairTarget == null) {
         return 0.0D;
      } else {
         return mc.crosshairTarget instanceof EntityHitResult ? mc.player.getEntityInteractionRange() : mc.player.getBlockInteractionRange();
      }
   }

   public static boolean isDrinking() {
      return isDrinking(mc.player);
   }

   public static boolean isDrinking(PlayerEntity player) {
      if (player.getActiveHand() == null) {
         return false;
      } else if (!player.isUsingItem()) {
         return false;
      } else {
         return player.getStackInHand(player.getActiveHand()).getUseAction() == UseAction.DRINK;
      }
   }

   public static boolean isMining() {
      HitResult var1 = mc.crosshairTarget;
      if (!(var1 instanceof BlockHitResult)) {
         return false;
      } else {
         BlockHitResult result = (BlockHitResult)var1;
         return mc.interactionManager.isBreakingBlock() && MineUtil.canBreak(result.getBlockPos()) && !BlockUtil.isAir(result.getBlockPos());
      }
   }

   public static boolean isUsingBow() {
      return isUsingBow(mc.player);
   }

   public static boolean isUsingBow(PlayerEntity player) {
      if (player.getStackInHand(player.getActiveHand()) == null) {
         return false;
      } else {
         boolean bow = player.getStackInHand(player.getActiveHand()).getItem() instanceof BowItem;
         return bow && player.isUsingItem();
      }
   }

   public static boolean isUsingShield() {
      return isUsingShield(mc.player);
   }

   public static boolean isUsingShield(PlayerEntity player) {
      if (player.getStackInHand(player.getActiveHand()) == null) {
         return false;
      } else {
         boolean shield = player.getStackInHand(player.getActiveHand()).getItem() instanceof ShieldItem;
         return shield && player.isUsingItem();
      }
   }

   public static void tick(int ticks) {
      for(int i = 0; i < ticks; ++i) {
         runTick();
      }

   }

   public static void runTick() {
      int lastSwing = ((IPlayerEntity)mc.player).lastAttackedTicks();
      int useCount = getCount();
      int hurtTime = mc.player.hurtTime;
      float prevSwingProgress = mc.player.lastHandSwingProgress;
      float swingProgress = mc.player.handSwingProgress;
      int swingProgressInt = mc.player.handSwingTicks;
      boolean isSwingInProgress = mc.player.handSwinging;
      float rotationYaw = mc.player.getYaw();
      float prevRotationYaw = mc.player.prevYaw;
      float prevRenderYawOffset = mc.player.prevBodyYaw;
      float rotationYawHead = mc.player.headYaw;
      float prevRotationYawHead = mc.player.prevHeadYaw;
      float renderArmYaw = mc.player.renderYaw;
      float prevRenderArmYaw = mc.player.lastRenderYaw;
      float renderArmPitch = mc.player.lastRenderPitch;
      float prevRenderArmPitch = mc.player.renderPitch;
      float walk = mc.player.horizontalSpeed;
      float prevWalk = mc.player.prevHorizontalSpeed;
      double chasingPosX = mc.player.capeX;
      double prevChasingPosX = mc.player.prevCapeX;
      double chasingPosY = mc.player.capeY;
      double prevChasingPosY = mc.player.prevCapeY;
      double chasingPosZ = mc.player.capeZ;
      double prevChasingPosZ = mc.player.prevCapeZ;
      float limbSwingAmount = mc.player.limbAnimator.getSpeed();
      ((IClientPlayerEntity)mc.player).$tick();
      ((IPlayerEntity)mc.player).setLastAttackedTicks(lastSwing);
      setActiveItemStackUseCount(useCount);
      mc.player.hurtTime = hurtTime;
      mc.player.lastHandSwingProgress = prevSwingProgress;
      mc.player.handSwingProgress = swingProgress;
      mc.player.handSwingTicks = swingProgressInt;
      mc.player.handSwinging = isSwingInProgress;
      mc.player.setYaw(rotationYaw);
      mc.player.prevYaw = prevRotationYaw;
      mc.player.prevBodyYaw = prevRenderYawOffset;
      mc.player.headYaw = rotationYawHead;
      mc.player.prevHeadYaw = prevRotationYawHead;
      mc.player.renderYaw = renderArmYaw;
      mc.player.lastRenderYaw = prevRenderArmYaw;
      mc.player.lastRenderPitch = renderArmPitch;
      mc.player.renderPitch = prevRenderArmPitch;
      mc.player.horizontalSpeed = walk;
      mc.player.prevHorizontalSpeed = prevWalk;
      mc.player.capeX = chasingPosX;
      mc.player.prevCapeX = prevChasingPosX;
      mc.player.capeY = chasingPosY;
      mc.player.prevCapeY = prevChasingPosY;
      mc.player.capeZ = chasingPosZ;
      mc.player.prevCapeZ = prevChasingPosZ;
      mc.player.limbAnimator.setSpeed(limbSwingAmount);
   }

   private static int getCount() {
      return mc.player.getActiveHand() == null ? 0 : mc.player.getStackInHand(mc.player.getActiveHand()).getCount();
   }

   private static void setActiveItemStackUseCount(int count) {
      if (mc.player.getActiveHand() != null) {
         mc.player.getStackInHand(mc.player.getActiveHand()).setCount(count);
      }
   }

   public static boolean isNull() {
      return mc.player == null || mc.world == null || mc.interactionManager == null;
   }
}
