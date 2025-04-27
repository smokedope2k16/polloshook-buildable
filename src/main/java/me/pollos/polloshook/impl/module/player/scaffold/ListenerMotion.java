package me.pollos.polloshook.impl.module.player.scaffold;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.render.RenderPosition;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.SwapMode;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ListenerMotion extends ModuleListener<Scaffold, MotionUpdateEvent> {
   protected BlockPos lastPos;
   protected Direction lastDirection;

   public ListenerMotion(Scaffold module) {
      super(module, MotionUpdateEvent.class, 7000);
   }

   public void call(MotionUpdateEvent event) {
      BlockPos pos = ((Scaffold)this.module).findNextPos();
      BlockPos down = mc.player.getBlockPos().down();
      int lastSlot;
      if ((pos == null || BlockUtil.getFacing(pos) == null) && BlockUtil.getFacing(down) == null) {
         Direction[] var4 = FacingUtil.DOWN;
         lastSlot = var4.length;

         for(int var6 = 0; var6 < lastSlot; ++var6) {
            Direction dir = var4[var6];
            BlockPos offset = down.offset(dir);
            if (mc.world.getBlockState(offset).isReplaceable() && BlockUtil.getFacing(offset) != null) {
               pos = offset;
            }
         }
      }

      if (event.getStage() == Stage.PRE) {
         if ((Boolean)((Scaffold)this.module).rotate.getValue()) {
            if (pos != null) {
               Direction direction = BlockUtil.getFacing(pos);
               this.sendRotations(pos, direction, event);
               this.lastDirection = direction;
               this.lastPos = pos;
               ((Scaffold)this.module).rotateTimer.reset();
            } else if (this.lastPos != null && !((Scaffold)this.module).rotateTimer.passed((Boolean)((Scaffold)this.module).strict.getValue() ? 375L : 150L)) {
               this.sendRotations(this.lastPos, this.lastDirection, event);
            }
         }

      } else if (pos != null && ((Scaffold)this.module).placeTimer.passed((double)((Float)((Scaffold)this.module).delay.getValue() * 100.0F))) {
         int slot = ((Scaffold)this.module).findItemSlot();
         lastSlot = mc.player.getInventory().selectedSlot;
         if (((Scaffold)this.module).isTowering()) {
            mc.player.jump();
         }

         if (slot != -1) {
            ((Scaffold)this.module).positionList.add(new RenderPosition(pos));
            switch((SwapMode)((Scaffold)this.module).swap.getValue()) {
            case HOLD:
            case SILENT:
               InventoryUtil.switchToSlot(slot);
               break;
            case ALTERNATIVE:
               InventoryUtil.altSwap(slot);
            }

            this.clickCurrentPos(pos, BlockUtil.getFacing(pos));
            switch((SwapMode)((Scaffold)this.module).swap.getValue()) {
            case SILENT:
               InventoryUtil.switchToSlot(lastSlot);
               break;
            case ALTERNATIVE:
               InventoryUtil.altSwap(slot);
            }

         }
      }
   }

   protected void clickCurrentPos(BlockPos pos, Direction direction) {
      if (pos != null && direction != null) {
         boolean sneakFlag = false;
         if (!Managers.getPositionManager().isSneaking()) {
            PacketUtil.sneak(true);
            sneakFlag = true;
         }

         boolean wasSprinting = false;
         if (Managers.getPositionManager().isSprinting() && (Boolean)((Scaffold)this.module).strict.getValue()) {
            PacketUtil.sprint(false);
            wasSprinting = true;
         }

         BlockPos offset = pos.offset(direction);
         BlockHitResult result = new BlockHitResult(offset.toCenterPos(), direction.getOpposite(), offset, false);
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);
         if ((Boolean)((Scaffold)this.module).swing.getValue()) {
            PacketUtil.swing();
         }

         if ((Boolean)((Scaffold)this.module).strict.getValue() && mc.player.isOnGround()) {
            Vec3d horizontal = new Vec3d(mc.player.getVelocity().getX() * 0.6000000238418579D, mc.player.getVelocity().getY(), mc.player.getVelocity().getZ() * 0.6000000238418579D);
            mc.player.setVelocity(horizontal);
         }

         if (sneakFlag) {
            PacketUtil.sneak(false);
         }

         if (wasSprinting) {
            PacketUtil.sprint(false);
         }

         ((Scaffold)this.module).placeTimer.reset();
      }
   }

   private void sendRotations(BlockPos pos, Direction direction, MotionUpdateEvent event) {
      if (direction != null) {
         float[] rotations = this.getRotations(pos.offset(direction));
         Managers.getRotationManager().setRotations(rotations, event);
      }
   }

   private float[] getRotations(BlockPos pos) {
      float[] rotations = BlockUtil.getVecRotations(pos.toCenterPos());
      rotations[1] = 85.0F;
      return rotations;
   }
}
