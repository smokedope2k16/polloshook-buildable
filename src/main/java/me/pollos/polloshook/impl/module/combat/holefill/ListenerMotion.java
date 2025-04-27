package me.pollos.polloshook.impl.module.combat.holefill;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.api.util.obj.hole.Hole2x1;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.holefill.mode.HoleFillSortingMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ListenerMotion extends ModuleListener<HoleFill, MotionUpdateEvent> {
   public ListenerMotion(HoleFill module) {
      super(module, MotionUpdateEvent.class, 1000);
   }

   public void call(MotionUpdateEvent event) {
      if (!((HoleFill)this.module).handleJump((BlockPlaceModule)this.module)) {
         List<PlayerEntity> targets = TargetUtil.getEnemies((double)MathUtil.square((Float)((HoleFill)this.module).targetDistance.getValue()));
         targets.sort(Comparator.comparingDouble((playerx) -> {
            double i = 0.0D;
            switch((HoleFillSortingMode)((HoleFill)this.module).sorting.getValue()) {
            case ARMOR:
               i = (double)EntityUtil.getArmor(playerx);
               break;
            case HEALTH:
               i = (double)EntityUtil.getHealth(playerx);
               break;
            case CLOSEST:
               i = playerx.squaredDistanceTo(playerx);
            }

            return i;
         }));
         if (!targets.isEmpty() || !(Boolean)((HoleFill)this.module).auto.getValue()) {
            boolean multiTask = PlayerUtil.isEating() || PlayerUtil.isDrinking() || PlayerUtil.isUsingBow();
            if ((Boolean)((HoleFill)this.module).multitask.getValue() || !multiTask) {
               List<BlockPos> positions = new ArrayList();
               if ((Boolean)((HoleFill)this.module).auto.getValue()) {
                  Iterator var8 = targets.iterator();

                  while(var8.hasNext()) {
                     PlayerEntity player = (PlayerEntity)var8.next();
                     List<Hole> holes = HoleUtil.getHoles(player, (Float)((HoleFill)this.module).xDist.getValue(), (Float)((HoleFill)this.module).yDist.getValue(), (Boolean)((HoleFill)this.module).twoByOne.getValue(), ((HoleFill)this.module).isWebs(), (Boolean)((HoleFill)this.module).terrain.getValue());
                     if (!holes.isEmpty()) {
                        if (!EntityUtil.isSafe(mc.player)) {
                           holes.removeIf((hole) -> {
                              return mc.player.squaredDistanceTo(hole.getPos().up().toCenterPos()) < (double)MathUtil.square((Float)((HoleFill)this.module).noSelfRange.getValue());
                           });
                        }

                        if (EntityUtil.isSafe(player) || this.insideOfHole(holes, player) || EntityUtil.isTrapped(player)) {
                           return;
                        }

                        holes.sort(Comparator.comparingDouble((hole) -> {
                           return BlockUtil.getDistanceSq(player, (BlockPos)hole.getPos());
                        }));
                        holes.removeIf((hole) -> {
                           return mc.player.squaredDistanceTo(hole.getPos().toCenterPos()) > (double)MathUtil.square((Float)((HoleFill)this.module).getPlaceRange().getValue());
                        });
                        holes.removeIf((hole) -> {
                           return !((HoleFill)this.module).webCheck(hole) || ((HoleFill)this.module).entityCheck(hole) && !((HoleFill)this.module).isWebs();
                        });
                        this.add(positions, holes);
                     }
                  }
               } else {
                  List<Hole> holes = HoleUtil.getHoles(mc.player, (Float)((HoleFill)this.module).xDist.getValue(), (Float)((HoleFill)this.module).yDist.getValue(), (Boolean)((HoleFill)this.module).twoByOne.getValue(), ((HoleFill)this.module).isWebs(), (Boolean)((HoleFill)this.module).terrain.getValue());
                  if (holes.isEmpty()) {
                     ((HoleFill)this.module).setEnabled(false);
                     return;
                  }

                  holes.removeIf((hole) -> {
                     return mc.player.squaredDistanceTo(hole.getPos().toCenterPos()) > (double)MathUtil.square((Float)((HoleFill)this.module).getPlaceRange().getValue());
                  });
                  holes.removeIf((hole) -> {
                     return !((HoleFill)this.module).webCheck(hole) || ((HoleFill)this.module).entityCheck(hole) && !((HoleFill)this.module).isWebs();
                  });
                  holes.sort(Comparator.comparingDouble((hole) -> {
                     return BlockUtil.getDistanceSq(hole.getPos());
                  }));
                  this.add(positions, holes);
               }

               ((HoleFill)this.module).onEvent(positions, event);
            }
         }
      }
   }

   private void add(List<BlockPos> positions, List<Hole> holes) {
      Iterator var3 = holes.iterator();

      while(var3.hasNext()) {
         Hole hole = (Hole)var3.next();
         if (hole instanceof Hole2x1) {
            Hole2x1 hole2x1 = (Hole2x1)hole;
            if (this.isAir(hole.getPos())) {
               positions.add(hole.getPos());
            }

            if (this.isAir(hole2x1.getSecondPos())) {
               positions.add(hole2x1.getSecondPos());
            }
         } else {
            positions.add(hole.getPos());
         }
      }

   }

   private boolean isAir(BlockPos pos) {
      return mc.world.isAir(pos.up()) && mc.world.isAir(pos.up().up());
   }

   private boolean insideOfHole(List<Hole> holes, PlayerEntity target) {
      Iterator var3 = holes.iterator();
      if (var3.hasNext()) {
         Hole hole = (Hole)var3.next();
         return target.getBoundingBox().contract(0.0625D, 0.0D, 0.0625D).intersects(((HoleFill)this.module).getHoleBB(hole));
      } else {
         return false;
      }
   }
}
