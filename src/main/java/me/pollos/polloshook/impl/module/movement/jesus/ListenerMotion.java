package me.pollos.polloshook.impl.module.movement.jesus;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.movement.jesus.mode.JesusMode;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class ListenerMotion extends ModuleListener<Jesus, MotionUpdateEvent> {
   protected boolean jump = false;

   public ListenerMotion(Jesus module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (!mc.player.input.sneaking && event.getStage() == Stage.PRE) {
         Entity entity = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
         switch((JesusMode)((Jesus)this.module).mode.getValue()) {
         case SOLID:
         case STRICT_SOLID:
            if (PlayerUtil.isInLiquid()) {
               MovementUtil.setYVelocity(0.10000000149011612D, (Entity)entity);
            }
            break;
         case TRAMPOLINE:
            if (PositionUtil.inLiquid(false) && !((Entity)entity).isSneaking()) {
               ((Entity)entity).setOnGround(false);
            }

            Block block = mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos())).getBlock();
            if (this.jump && !mc.player.getAbilities().flying && !((Entity)entity).isTouchingWater()) {
               if (((Entity)entity).getVelocity().getY() < -0.3D || ((Entity)entity).isOnGround() || mc.player.isHoldingOntoLadder()) {
                  this.jump = false;
                  return;
               }

               MovementUtil.setYVelocity(mc.player.getVelocity().y / 0.9800000190734863D + 0.08D, (Entity)entity);
               MovementUtil.setYVelocity(mc.player.getVelocity().y - 0.03120000000005D, (Entity)entity);
            }

            if (PlayerUtil.isInLiquid()) {
               MovementUtil.setYVelocity(0.10000000149011612D, (Entity)entity);
               this.jump = false;
            } else if (!((Entity)entity).isInLava() && block instanceof FluidBlock && ((Entity)entity).getVelocity().getY() < 0.2D) {
               MovementUtil.setYVelocity(0.5D, (Entity)entity);
               this.jump = true;
            }
            break;
         case DOLPHIN:
            KeyBinding.setKeyPressed(mc.options.jumpKey.getDefaultKey(), true);
         }

      }
   }
}
