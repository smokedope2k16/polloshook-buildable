package me.pollos.polloshook.impl.module.combat.antiregear;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ListenerMotion extends SafeModuleListener<AntiRegear, MotionUpdateEvent> {
   public ListenerMotion(AntiRegear module) {
      super(module, MotionUpdateEvent.class);
   }

   public void safeCall(MotionUpdateEvent event) {
      if (((AntiRegear)this.module).renderTimer.passed(1500L)) {
         ((AntiRegear)this.module).renderPos = null;
      }

      if (((AntiRegear)this.module).timer.passed((double)(100.0F * (Float)((AntiRegear)this.module).breakDelay.getValue())) && mc.player.getMainHandStack().getItem() instanceof PickaxeItem) {
         List<PlayerEntity> targets = TargetUtil.getEnemies((double)MathUtil.square((Float)((AntiRegear)this.module).enemyRange.getValue()));
         List<BlockPos> validPositions = new ArrayList();
         Iterator var5 = targets.iterator();

         while(true) {
            PlayerEntity player;
            do {
               do {
                  if (!var5.hasNext()) {
                     validPositions.sort(Comparator.comparingDouble((posx) -> {
                        return mc.player.getEyePos().distanceTo(posx.toCenterPos());
                     }));
                     validPositions.removeIf((posx) -> {
                        return mc.player.getEyePos().distanceTo(posx.toCenterPos()) > (double)(Float)((AntiRegear)this.module).breakRange.getValue();
                     });
                     if (validPositions.isEmpty()) {
                        return;
                     }

                     BlockPos minePos = (BlockPos)validPositions.get(0);
                     if (minePos != null) {
                        float[] rotations = BlockUtil.getBlockPosRotations(minePos);
                        if ((Boolean)((AntiRegear)this.module).rotate.getValue()) {
                           PacketUtil.rotate(rotations, Managers.getPositionManager().isOnGround());
                        }

                        PacketUtil.swing();
                        PacketUtil.send(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, minePos, Direction.UP));
                        PacketUtil.swing();
                        PacketUtil.swing();
                        PacketUtil.send(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, minePos, Direction.UP));
                        PacketUtil.swing();
                        ((AntiRegear)this.module).timer.reset();
                        ((AntiRegear)this.module).renderPos = minePos;
                        ((AntiRegear)this.module).renderTimer.reset();
                     }

                     return;
                  }

                  player = (PlayerEntity)var5.next();
               } while(player == null);
            } while(EntityUtil.isDead(player));

            Iterator var7 = BlockUtil.getSphere(player, 5.0F, true).iterator();

            while(var7.hasNext()) {
               BlockPos pos = (BlockPos)var7.next();
               if (mc.world.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity) {
                  validPositions.add(pos);
               }
            }
         }
      }
   }
}