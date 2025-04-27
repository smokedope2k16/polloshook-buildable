package me.pollos.polloshook.impl.module.combat.blocker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ListenerMotion extends ModuleListener<Blocker, MotionUpdateEvent> {
   public ListenerMotion(Blocker module) {
      super(module, MotionUpdateEvent.class, 3000);
   }

   public void call(MotionUpdateEvent event) {
      List<BlockPos> evilBlocks = new ArrayList();
      List<BlockPos> blocked = new ArrayList(PositionUtil.getBlockedPositions((Entity)mc.player));
      List<BlockPos> surround = new ArrayList(PositionUtil.getSurroundOffsets((Entity)mc.player));
      List<BlockPos> offsets = new ArrayList();
      surround.forEach((posx) -> {
         offsets.add(posx.up());
      });
      blocked.forEach((posx) -> {
         offsets.add(posx.up().up());
      });
      surround.addAll(offsets);
      ((Blocker)this.module).minePositions.forEach((posx, time) -> {
         if (System.currentTimeMillis() - time > 2500L) {
            ((Blocker)this.module).minePositions.remove(posx);
         }

      });
      if (!(Boolean)((Blocker)this.module).onlyInHole.getValue() || EntityUtil.isSafe(mc.player)) {
         Iterator var6 = surround.iterator();

         while(true) {
            BlockPos pos;
            long progress;
            long actionTime;
            do {
               BlockState state;
               do {
                  do {
                     if (!var6.hasNext()) {
                        if ((Boolean)((Blocker)this.module).replacer.getValue()) {
                           var6 = surround.iterator();

                           label91:
                           while(true) {
                              do {
                                 if (!var6.hasNext()) {
                                    var6 = offsets.iterator();

                                    while(var6.hasNext()) {
                                       pos = (BlockPos)var6.next();
                                       if ((Boolean)((Blocker)this.module).fullReplace.getValue()) {
                                          state = mc.world.getBlockState(pos);
                                          if (((Blocker)this.module).minePositions.containsKey(pos) && state.isAir()) {
                                             progress = (Long)((Blocker)this.module).minePositions.get(pos);
                                             actionTime = System.currentTimeMillis() - 1500L;
                                             if (progress - actionTime < 0L) {
                                                evilBlocks.add(pos);
                                             }
                                          }
                                       }

                                       BlockPos upper = pos.up();
                                       BlockState states = mc.world.getBlockState(upper);
                                       if (((Blocker)this.module).minePositions.containsKey(upper) && states.isAir()) {
                                          long progress1 = (Long)((Blocker)this.module).minePositions.get(upper);
                                          long actionTime1 = System.currentTimeMillis() - 1500L;
                                          if (progress1 - actionTime1 < 0L) {
                                             evilBlocks.add(upper);
                                          }
                                       }
                                    }
                                    break label91;
                                 }

                                 pos = (BlockPos)var6.next();
                              } while((double)pos.getY() != mc.player.getBoundingBox().minY);

                              Direction[] var19 = FacingUtil.HORIZONTALS;
                              int var21 = var19.length;

                              for(int var10 = 0; var10 < var21; ++var10) {
                                 Direction facing = var19[var10];
                                 BlockPos neighbour = pos.offset(facing);
                                 if (!blocked.contains(neighbour)) {
                                    boolean neighbourPlace = this.canPlaceUnder(neighbour);
                                    if (this.canPlaceUnder(pos) && neighbourPlace || (Boolean)((Blocker)this.module).ignoreAir.getValue()) {
                                       offsets.add(neighbour.down());
                                    }
                                 }
                              }
                           }
                        }

                        ((Blocker)this.module).onEvent(evilBlocks, event);
                        return;
                     }

                     pos = (BlockPos)var6.next();
                     state = mc.world.getBlockState(pos);
                  } while(!((Blocker)this.module).minePositions.containsKey(pos));
               } while(state.getBlock() != Blocks.OBSIDIAN);

               progress = (Long)((Blocker)this.module).minePositions.get(pos);
               actionTime = System.currentTimeMillis() - 1500L;
            } while(progress - actionTime >= 0L);

            if ((double)pos.getY() == mc.player.getBoundingBox().minY) {
               Direction[] var13 = FacingUtil.HORIZONTALS;
               int var14 = var13.length;

               for(int var15 = 0; var15 < var14; ++var15) {
                  Direction facing = var13[var15];
                  BlockPos neighbour = pos.offset(facing);
                  if (!blocked.contains(neighbour)) {
                     boolean neighbourPlace = this.canPlaceUnder(neighbour);
                     if (this.canPlaceUnder(pos) && neighbourPlace || (Boolean)((Blocker)this.module).ignoreAir.getValue()) {
                        evilBlocks.add(neighbour);
                        if ((Boolean)((Blocker)this.module).below.getValue()) {
                           evilBlocks.add(pos.down());
                        }
                     }
                  }
               }
            }

            evilBlocks.add(pos.up());
         }
      }
   }

   private boolean canPlaceUnder(BlockPos pos) {
      BlockState state = mc.world.getBlockState(pos.down());
      return state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.ANVIL;
   }
}
