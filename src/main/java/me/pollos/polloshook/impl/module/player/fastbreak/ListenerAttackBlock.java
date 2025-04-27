package me.pollos.polloshook.impl.module.player.fastbreak;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.MineUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.block.AttackBlockEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.MineMode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ListenerAttackBlock extends ModuleListener<FastBreak, AttackBlockEvent> {
   public ListenerAttackBlock(FastBreak module) {
      super(module, AttackBlockEvent.class);
   }

   public void call(AttackBlockEvent event) {
      if (!PlayerUtil.isCreative() && !PlayerUtil.isSpectator()) {
         BlockPos pos = event.getPos();
         if (PositionUtil.getEyesPos().squaredDistanceTo(pos.toCenterPos()) > (double)MathUtil.square((Float)((FastBreak)this.module).range.getValue())) {
            event.setCanceled(true);
         } else {
            if (((FastBreak)this.module).pos != null && ((FastBreak)this.module).pos.equals(pos)) {
               mc.player.swingHand(Hand.MAIN_HAND);
               event.setCanceled(true);
            }

            if (MineUtil.canBreak(pos) && ((FastBreak)this.module).isValid(pos) && ((FastBreak)this.module).timer.passed(250L)) {
               boolean aborted = false;
               if (((FastBreak)this.module).pos != null && !((FastBreak)this.module).pos.equals(pos)) {
                  ((FastBreak)this.module).abortCurrentPos();
                  aborted = true;
               }

               if (!aborted && ((FastBreak)this.module).pos != null && ((FastBreak)this.module).pos.equals(pos)) {
                  ((FastBreak)this.module).abortCurrentPos();
                  ((FastBreak)this.module).timer.reset();
                  return;
               }

               if (((FastBreak)this.module).pos == null) {
                  this.setPos(event);
               }

               if (((FastBreak)this.module).mode.getValue() == MineMode.INSTANT) {
                  PacketUtil.send(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, ((FastBreak)this.module).pos, ((FastBreak)this.module).direction, PacketUtil.incrementSequence()));
                  ((FastBreak)this.module).shouldAbort = true;
                  event.setCanceled(true);
               } else {
                  PacketUtil.send(((FastBreak)this.module).getAbortPacket());
                  PacketUtil.swing();
               }

               ((FastBreak)this.module).timer.reset();
            }

         }
      }
   }

   private void setPos(AttackBlockEvent event) {
      ((FastBreak)this.module).debugLog(String.valueOf(Formatting.AQUA) + "Selected Mine position");
      ((FastBreak)this.module).reset();
      ((FastBreak)this.module).pos = event.getPos();
      ((FastBreak)this.module).direction = event.getDirection();
   }
}
