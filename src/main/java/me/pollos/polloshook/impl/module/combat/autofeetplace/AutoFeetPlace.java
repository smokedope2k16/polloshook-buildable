package me.pollos.polloshook.impl.module.combat.autofeetplace;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.asm.ducks.entity.IEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoFeetPlace extends BlockPlaceModule {
   protected final Value<Boolean> extend = new Value(true, new String[]{"Extension", "extend"});

   public AutoFeetPlace() {
      super(new String[]{"AutoFeetPlace", "feetplace", "surround", "feettrap"}, Category.COMBAT);
      this.offerValues(new Value[]{this.extend});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
   }

   protected int getSlot() {
      int obby = ItemUtil.findHotbarItem(Items.OBSIDIAN);
      return obby == -1 ? ItemUtil.findHotbarItem(Items.ENDER_CHEST) : obby;
   }

   protected Set<BlockPos> createBlocked() {
      BlockPos playerPos = mc.player.getBlockPos();
      List<BlockPos> unfiltered = (new ArrayList(PositionUtil.getBlockedPositions((Entity)mc.player))).stream().sorted(Comparator.comparingDouble((pos) -> {
         return BlockUtil.getDistanceSq(mc.player, (BlockPos)pos);
      })).toList();
      List<BlockPos> filtered = (List)(new ArrayList(unfiltered)).stream().filter((pos) -> {
         return mc.world.getBlockState((BlockPos) pos).getBlock().getDefaultState().isReplaceable() && mc.world.getBlockState(((BlockPos) pos).up()).getBlock().getDefaultState().isReplaceable();
      }).collect(Collectors.toList());
      if (filtered.size() == 2 && unfiltered.size() == 4 && ((BlockPos)unfiltered.get(0)).equals(filtered.get(0)) && ((BlockPos)unfiltered.get(3)).equals(filtered.get(1))) {
         filtered.clear();
         filtered.add(playerPos);
      }

      if (filtered.size() == 3) {
         while(filtered.size() > 2) {
            filtered.remove(filtered.size() - 1);
         }
      }

      Set<BlockPos> blocked = new HashSet(filtered);
      if (blocked.isEmpty()) {
         blocked.add(playerPos);
      }

      return blocked;
   }

   protected Set<BlockPos> createSurrounding(Set<BlockPos> blocked, List<Entity> entities) {
      Set<BlockPos> surrounding = new HashSet();
      Iterator var4 = blocked.iterator();

      while(var4.hasNext()) {
         BlockPos pos = (BlockPos)var4.next();
         Direction[] var6 = FacingUtil.HORIZONTALS;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction facing = var6[var8];
            BlockPos offsetPos = pos.offset(facing);
            if (!blocked.contains(offsetPos)) {
               surrounding.add(offsetPos);
               Direction helping = BlockUtil.getFacing(offsetPos);
               if (helping == null) {
                  if (!this.fastIntersectionCheck(offsetPos.down(), entities)) {
                     surrounding.add(offsetPos.down());
                  } else {
                     List<BlockPos> helpings = new ArrayList();
                     Direction[] var13 = FacingUtil.HORIZONTALS;
                     int var14 = var13.length;

                     for(int var15 = 0; var15 < var14; ++var15) {
                        Direction helper = var13[var15];
                        BlockPos helpPos = offsetPos.offset(helper);
                        if (!blocked.contains(helpPos) && !surrounding.contains(helpPos) && BlockUtil.getFacing(helpPos) != null) {
                           helpings.add(helpPos);
                        }
                     }

                     if (!helpings.isEmpty()) {
                        surrounding.add((BlockPos)helpings.get(0));
                     }
                  }
               }
            }
         }

         if (mc.player.isOnGround() && ((IEntity)mc.player).isPrevOnGround() && !this.fastIntersectionCheck(pos.down(), entities)) {
            surrounding.add(pos.down());
         }
      }

      if ((Boolean)this.extend.getValue()) {
         for(int i = 0; i < 4; ++i) {
            Set<BlockPos> extendedPositions = new HashSet();
            Iterator itr = surrounding.iterator();

            label87:
            while(itr.hasNext()) {
               BlockPos pos = (BlockPos)itr.next();
               boolean remove = false;
               Iterator var23 = entities.iterator();

               while(true) {
                  Entity entity;
                  do {
                     do {
                        if (!var23.hasNext()) {
                           if (remove) {
                              itr.remove();
                           }
                           continue label87;
                        }

                        entity = (Entity)var23.next();
                     } while(entity == null);
                  } while(!this.fastIntersectionCheck(pos, entity));

                  Direction[] var25 = FacingUtil.HORIZONTALS;
                  int var26 = var25.length;

                  for(int var27 = 0; var27 < var26; ++var27) {
                     Direction facing = var25[var27];
                     BlockPos offset = pos.offset(facing);
                     if (!blocked.contains(offset)) {
                        remove = true;
                        extendedPositions.add(offset);
                     }
                  }
               }
            }

            surrounding.addAll(extendedPositions);
         }
      }

      return surrounding;
   }
}
