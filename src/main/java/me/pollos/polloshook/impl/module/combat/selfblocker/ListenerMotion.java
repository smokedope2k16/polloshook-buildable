package me.pollos.polloshook.impl.module.combat.selfblocker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ListenerMotion extends ModuleListener<SelfBlocker, MotionUpdateEvent> {
   public ListenerMotion(SelfBlocker module) {
      super(module, MotionUpdateEvent.class, 8000);
   }

   public void call(MotionUpdateEvent event) {
      if (!((SelfBlocker)this.module).handleJump((BlockPlaceModule)this.module)) {
         PlayerEntity dude = TargetUtil.getEnemySimple(Double.MAX_VALUE);
         if (!(Boolean)((SelfBlocker)this.module).auto.getValue() || dude != null && !(dude.distanceTo(mc.player) > (Float)((SelfBlocker)this.module).range.getValue()) && HoleUtil.getHole(mc.player) != null) {
            boolean airCheck = !BlockUtil.isAir(mc.player.getBlockPos().up(2));
            boolean airCheckAntiScaffold = !BlockUtil.isAir(mc.player.getBlockPos().up(3));
            boolean fullAirCheck = (Boolean)((SelfBlocker)this.module).getAntiScaffold().getValue() ? airCheck && airCheckAntiScaffold : airCheck;
            if ((Boolean)((SelfBlocker)this.module).autoDisable.getValue() && fullAirCheck) {
               ((SelfBlocker)this.module).setEnabled(false);
            } else {
               List<BlockPos> targets = new ArrayList(((SelfBlocker)this.module).getBlocked(mc.player));
               List<BlockPos> finalTargets = new ArrayList();
               Iterator var8 = targets.iterator();

               while(var8.hasNext()) {
                  BlockPos pos = (BlockPos)var8.next();
                  if (!((SelfBlocker)this.module).fastIntersectionCheck(pos, Managers.getEntitiesManager().getEntities())) {
                     finalTargets.add(pos);
                  }
               }

               ((SelfBlocker)this.module).onEvent(finalTargets, event);
            }
         }
      }
   }
}
