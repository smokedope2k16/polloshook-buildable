package me.pollos.polloshook.impl.module.combat.autotrap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.autotrap.util.TrapTarget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ListenerMotion extends ModuleListener<AutoTrap, MotionUpdateEvent> {
   public ListenerMotion(AutoTrap module) {
      super(module, MotionUpdateEvent.class, 2000);
   }

   public void call(MotionUpdateEvent event) {
      if (!((AutoTrap)this.module).handleJump((BlockPlaceModule)this.module)) {
         ((AutoTrap)this.module).cached.clear();
         AutoTrap var10000 = (AutoTrap)this.module;
         double var10001 = (double)(Float)((AutoTrap)this.module).enemyRange.getValue();
         AutoTrap var10002 = (AutoTrap)this.module;
         Objects.requireNonNull(var10002);
         var10000.target = (PlayerEntity)TargetUtil.getEnemies(var10001, var10002::isValid).stream().min(Comparator.comparingDouble((enemy) -> {
            double var10003;
            switch((TrapTarget)((AutoTrap)this.module).targetPriority.getValue()) {
            case YAW:
               Vec3d playerLookVec = mc.player.getRotationVec(1.0F);
               Vec3d entityVec = enemy.getPos().subtract(mc.player.getPos()).normalize();
               double angle = Math.acos(playerLookVec.dotProduct(entityVec));
               var10003 = (double)((float)Math.toDegrees(angle));
               break;
            case CLOSEST:
            case UNTRAPPED:
            case SMART:
               var10003 = enemy.squaredDistanceTo(mc.player.getPos());
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }

            return var10003;
         })).orElse((PlayerEntity)null);
         if (((AutoTrap)this.module).target != null) {
            List<BlockPos> trap = (List)((AutoTrap)this.module).cached.get(((AutoTrap)this.module).target);
            if (trap == null) {
               trap = ((AutoTrap)this.module).getBlocked(((AutoTrap)this.module).target);
            }

            List<BlockPos> finalTargets = new ArrayList();
            Iterator var4 = trap.iterator();

            while(var4.hasNext()) {
               BlockPos pos = (BlockPos)var4.next();
               if (!((AutoTrap)this.module).fastIntersectionCheck(pos, Managers.getEntitiesManager().getEntities())) {
                  finalTargets.add(pos);
               }
            }

            ((AutoTrap)this.module).onEvent(finalTargets, event);
         }
      }
   }
}
