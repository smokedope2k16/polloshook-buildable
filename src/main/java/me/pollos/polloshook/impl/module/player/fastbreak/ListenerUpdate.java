package me.pollos.polloshook.impl.module.player.fastbreak;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.MineUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.SwapMode;
import net.minecraft.block.Blocks;

public class ListenerUpdate extends ModuleListener<FastBreak, UpdateEvent> {
   public ListenerUpdate(FastBreak module) {
      super(module, UpdateEvent.class, 10);
   }

   public void call(UpdateEvent event) {
      if (!PlayerUtil.isCreative() && !PlayerUtil.isSpectator()) {
         if (((FastBreak)this.module).pos != null) {
            if (PositionUtil.getEyesPos().squaredDistanceTo(((FastBreak)this.module).pos.toCenterPos()) > (double)MathUtil.square((Float)((FastBreak)this.module).range.getValue())) {
               ((FastBreak)this.module).abortCurrentPos();
               return;
            }

            if (((FastBreak)this.module).getBlock() != Blocks.WATER) {
               ((FastBreak)this.module).state = mc.world.getBlockState(((FastBreak)this.module).pos);
            }

            ((FastBreak)this.module).updateDamages();
            int pickSlot = MineUtil.findBestTool(((FastBreak)this.module).pos);
            if (((FastBreak)this.module).maxDamage >= (Float)((FastBreak)this.module).damage.getValue() - 0.1F) {
               ((FastBreak)this.module).render = true;
            } else if (((FastBreak)this.module).maxDamage <= (Float)((FastBreak)this.module).damage.getValue() - 0.2F && ((FastBreak)this.module).maxDamage >= 0.2F) {
               ((FastBreak)this.module).render = false;
            }

            boolean multiTask = PlayerUtil.isEating() || PlayerUtil.isDrinking() || PlayerUtil.isUsingBow();
            if (!(Boolean)((FastBreak)this.module).multiTask.getValue() && multiTask) {
               return;
            }

            if (((FastBreak)this.module).swap.getValue() == SwapMode.HOLD && Managers.getInventoryManager().getSlot() != pickSlot) {
               return;
            }

            if (((FastBreak)this.module).damages[mc.player.getInventory().selectedSlot] >= (Float)((FastBreak)this.module).damage.getValue() || pickSlot >= 0 && ((FastBreak)this.module).damages[pickSlot] >= (Float)((FastBreak)this.module).damage.getValue()) {
               ((FastBreak)this.module).tryBreak(pickSlot);
            }
         }

      } else {
         ((FastBreak)this.module).reset();
      }
   }
}