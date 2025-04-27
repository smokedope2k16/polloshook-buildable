package me.pollos.polloshook.impl.module.combat.pollosresolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.player.suicide.Suicide;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ListenerMotion extends ModuleListener<PollosResolver, MotionUpdateEvent> {
   public ListenerMotion(PollosResolver module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      List<PlayerEntity> enemies = TargetUtil.getEnemies((double)MathUtil.square((Float)((PollosResolver)this.module).enemyRange.getValue()));
      Suicide SUICIDE = (Suicide)Managers.getModuleManager().get(Suicide.class);
      if (SUICIDE.isCrystal()) {
         enemies.addFirst(mc.player);
      }

      if (!enemies.isEmpty()) {
         List<BlockPos> validPositions = new ArrayList();
         BlockPos kill = null;
         float damage = 0.5F;
         HashMap<BlockPos, Float> selfDamages = new HashMap();
         Iterator var8 = enemies.iterator();

         while(true) {
            label104:
            while(true) {
               PlayerEntity player;
               do {
                  if (!var8.hasNext()) {
                     if (kill == null) {
                        return;
                     }

                     List<BlockPos> theList = new ArrayList();
                     theList.add(kill);
                     boolean multiTask = PlayerUtil.isEating() || PlayerUtil.isDrinking() || PlayerUtil.isUsingBow();
                     if (!(Boolean)((PollosResolver)this.module).multitask.getValue() && multiTask) {
                        return;
                     }

                     ((PollosResolver)this.module).onEvent(theList, event);
                     return;
                  }

                  player = (PlayerEntity)var8.next();
               } while(!player.isOnGround());

               PositionUtil.getSurroundOffsets((Entity)player).forEach((posx) -> {
                  validPositions.add(posx.down());
               });
               Iterator var10 = validPositions.iterator();

               while(true) {
                  BlockPos pos;
                  float enemyDamage;
                  boolean isSuicide;
                  boolean invalidDamage;
                  do {
                     float selfDamage;
                     do {
                        if (!var10.hasNext()) {
                           continue label104;
                        }

                        pos = (BlockPos)var10.next();
                        if (BlockUtil.canPlaceCrystal(pos, true)) {
                           kill = null;
                           continue label104;
                        }

                        enemyDamage = CombatUtil.getDamage(player, mc.world, 6.0F, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, false);
                        if (selfDamages.containsKey(pos)) {
                           selfDamage = (Float)selfDamages.get(pos);
                        } else {
                           selfDamage = CombatUtil.getDamage(mc.player, mc.world, 6.0F, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, false);
                           selfDamages.put(pos, selfDamage);
                        }

                        if (SUICIDE.isCrystal()) {
                           selfDamage = 0.0F;
                        }
                     } while(!(enemyDamage > (Float)((PollosResolver)this.module).minDMG.getValue()) && !(enemyDamage > EntityUtil.getHealth(player)));

                     isSuicide = selfDamage > (Float)((PollosResolver)this.module).maxSelfDMG.getValue() || (double)selfDamage + 2.0D > (double)EntityUtil.getHealth(mc.player);
                     invalidDamage = selfDamage >= enemyDamage || damage > enemyDamage;
                  } while(isSuicide && !SUICIDE.isCrystal());

                  if (!invalidDamage) {
                     damage = enemyDamage;
                     kill = pos;
                  }
               }
            }
         }
      }
   }
}
