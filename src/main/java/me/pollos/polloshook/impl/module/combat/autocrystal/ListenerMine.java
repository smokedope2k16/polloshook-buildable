package me.pollos.polloshook.impl.module.combat.autocrystal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.impl.events.block.SpeedMineEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.util.CrystalPos;
import me.pollos.polloshook.impl.module.combat.autocrystal.util.CrystalRenderPos;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ListenerMine extends ModuleListener<AutoCrystal, SpeedMineEvent> {
   public ListenerMine(AutoCrystal module) {
      super(module, SpeedMineEvent.class);
   }

   public void call(SpeedMineEvent event) {
      if (mc.world != null && mc.player != null) {
         BlockPos pos = event.getPos();
         List<Entity> enemies = new ArrayList(((AutoCrystal)this.module).getTargets());
         if (!enemies.isEmpty()) {
            float damage;
            switch(event.getStage()) {
            case PRE:
               if (((AutoCrystal)this.module).getBlockedPositions().containsKey(pos)) {
                  return;
               }

               if (!BlockUtil.canPlaceCrystal(pos, true, (Boolean)((AutoCrystal)this.module).getProtocolPlace().getValue())) {
                  return;
               }

               if (((AutoCrystal)this.module).isCollidedByUnknownCrystal(pos)) {
                  return;
               }

               Vec3d loc = ((AutoCrystal)this.module).getPlayerPos();
               Vec3d crystalVec = ((AutoCrystal)this.module).getRelativeVecFromCrystal((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, loc);
               if (!((AutoCrystal)this.module).isInAttackWallRange(loc, crystalVec, new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 2.7D, (double)pos.getZ() + 0.5D))) {
                  return;
               }

               if (!((AutoCrystal)this.module).isInAttackRange(loc, crystalVec)) {
                  return;
               }

               if (!((AutoCrystal)this.module).isInPlaceWallRange(pos)) {
                  return;
               }

               if (!((AutoCrystal)this.module).isInPlaceRange(PositionUtil.getEyesPos(), pos)) {
                  return;
               }

               ((AutoCrystal)this.module).getStateHelper().addBlockState(pos, Blocks.AIR.getDefaultState());
               float selfDamage = CombatUtil.getDamage(mc.player, ((AutoCrystal)this.module).getStateHelper(), 6.0F, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, ((AutoCrystal)this.module).isBreakingBlocks(), 0, true);
               damage = CombatUtil.getDamage(this.getTargetFromBlock(pos, enemies), ((AutoCrystal)this.module).getStateHelper(), 6.0F, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, ((AutoCrystal)this.module).isBreakingBlocks(), 0, true);
               ((AutoCrystal)this.module).getStateHelper().clearAllStates();
               if (((AutoCrystal)this.module).isSuicide()) {
                  selfDamage = 0.0F;
               }

               if (damage >= ((AutoCrystal)this.module).getMindDMG()) {
                  boolean isSuicide = selfDamage > ((AutoCrystal)this.module).getMaxSelfDamage() || (double)selfDamage + 2.0D >= (double)EntityUtil.getHealth(mc.player);
                  boolean invalidDamage = selfDamage >= damage;
                  if (isSuicide || invalidDamage) {
                     return;
                  }

                  BlockHitResult result = ((AutoCrystal)this.module).handlePlacement(pos);
                  FastBreak FAST_BREAK = (FastBreak)Managers.getModuleManager().get(FastBreak.class);
                  if (((AutoCrystal)this.module).placeAction(result, damage, FAST_BREAK.isStrict() || !((AutoCrystal)this.module).isUsingOffhand())) {
                     ((AutoCrystal)this.module).getPendingPlacePositions().put(result.getBlockPos().up(), System.currentTimeMillis());
                     CrystalRenderPos renderPos = new CrystalRenderPos(result.getBlockPos(), damage);
                     ((AutoCrystal)this.module).setRender(renderPos);
                     if (!((AutoCrystal)this.module).isFading(renderPos)) {
                        ((AutoCrystal)this.module).getFadePositions().add(renderPos);
                     }

                     ((AutoCrystal)this.module).setSkipPlace(true);
                  }
               }
               break;
            case POST:
               if (((AutoCrystal)this.module).getLastCrystalPos() != null && ((AutoCrystal)this.module).getLastCrystalPos().toPos().equals(pos)) {
                  return;
               }

               List<BlockPos> positions = new ArrayList();
               Iterator var5 = enemies.iterator();

               while(var5.hasNext()) {
                  Entity entity = (Entity)var5.next();
                  Set<BlockPos> surround = PositionUtil.getSurroundOffsets(entity);
                  positions.addAll(surround);
               }

               if (!positions.contains(pos)) {
                  return;
               }

               HashMap<BlockPos, Float> selfDamages = new HashMap();
               List<CrystalPos> crystalPositions = new ArrayList();
               ((AutoCrystal)this.module).getStateHelper().addBlockState(pos, Blocks.AIR.getDefaultState());
               Iterator var20 = this.getPositions(pos).iterator();

               float damageFloat;
               float selfDamageFloat;
               while(var20.hasNext()) {
                  BlockPos offset = (BlockPos)var20.next();
                  if (!((AutoCrystal)this.module).getBlockedPositions().containsKey(offset) && BlockUtil.canPlaceCrystal(offset, true, (Boolean)((AutoCrystal)this.module).getProtocolPlace().getValue()) && !((AutoCrystal)this.module).isCollidedByUnknownCrystal(pos)) {
                     Vec3d locVec = ((AutoCrystal)this.module).getPlayerPos();
                     Vec3d crystalVecFloat = ((AutoCrystal)this.module).getRelativeVecFromCrystal((double)offset.getX() + 0.5D, (double)offset.getY() + 1.0D, (double)offset.getZ() + 0.5D, locVec);
                     if (((AutoCrystal)this.module).isInAttackWallRange(locVec, crystalVecFloat, new Vec3d((double)offset.getX() + 0.5D, (double)offset.getY() + 2.7D, (double)offset.getZ() + 0.5D)) && ((AutoCrystal)this.module).isInAttackRange(locVec, crystalVecFloat) && ((AutoCrystal)this.module).isInPlaceWallRange(offset) && ((AutoCrystal)this.module).isInPlaceRange(PositionUtil.getEyesPos(), offset)) {
                        damageFloat = CombatUtil.getDamage(mc.player, ((AutoCrystal)this.module).getStateHelper(), 6.0F, (double)offset.getX() + 0.5D, (double)offset.getY() + 1.0D, (double)offset.getZ() + 0.5D, ((AutoCrystal)this.module).isBreakingBlocks(), 0, true);
                        selfDamages.put(offset, damageFloat);
                        selfDamageFloat = CombatUtil.getDamage(this.getTargetFromBlockPost(pos, enemies), ((AutoCrystal)this.module).getStateHelper(), 6.0F, (double)offset.getX() + 0.5D, (double)offset.getY() + 1.0D, (double)offset.getZ() + 0.5D, ((AutoCrystal)this.module).isBreakingBlocks(), 0, true);
                        crystalPositions.add(new CrystalPos(offset, selfDamageFloat));
                     }
                  }
               }

               ((AutoCrystal)this.module).getStateHelper().clearAllStates();
               damage = 0.5F;
               CrystalPos finalPos = null;
               Iterator var24 = crystalPositions.iterator();

               while(true) {
                  CrystalPos crystalPos;
                  do {
                     if (!var24.hasNext()) {
                        if (finalPos == null) {
                           return;
                        }

                        BlockHitResult result = ((AutoCrystal)this.module).handlePlacement(pos);
                        FastBreak FAST_BREAK = (FastBreak)Managers.getModuleManager().get(FastBreak.class);
                        if (((AutoCrystal)this.module).placeAction(result, damage, FAST_BREAK.isStrict() || !((AutoCrystal)this.module).isUsingOffhand())) {
                           ((AutoCrystal)this.module).getPendingPlacePositions().put(result.getBlockPos().up(), System.currentTimeMillis());
                           CrystalRenderPos renderPos = new CrystalRenderPos(result.getBlockPos(), damage);
                           ((AutoCrystal)this.module).setRender(renderPos);
                           if (!((AutoCrystal)this.module).isFading(renderPos)) {
                              ((AutoCrystal)this.module).getFadePositions().add(renderPos);
                           }

                           ((AutoCrystal)this.module).setSkipPlace(true);
                        }

                        return;
                     }

                     crystalPos = (CrystalPos)var24.next();
                     damage = crystalPos.getDamage();
                     selfDamage = (Float)selfDamages.get(crystalPos.toPos());
                     if (((AutoCrystal)this.module).isSuicide()) {
                        selfDamage = 0.0F;
                     }
                  } while(!(damage >= ((AutoCrystal)this.module).getMindDMG()));

                  boolean isSuicide = selfDamage > ((AutoCrystal)this.module).getMaxSelfDamage() || (double)selfDamage + 2.0D >= (double)EntityUtil.getHealth(mc.player);
                  boolean invalidDamage = selfDamage >= damage;
                  if (!isSuicide && !invalidDamage && damage >= damage) {
                     finalPos = crystalPos;
                     damage = crystalPos.getDamage();
                  }
               }
            }

         }
      }
   }

   private Entity getTargetFromBlock(BlockPos pos, List<Entity> enemies) {
      Entity target = (Entity)enemies.get(0);
      double distance = 999.0D;
      Iterator var6 = enemies.iterator();

      while(var6.hasNext()) {
         Entity entity = (Entity)var6.next();
         double current = BlockUtil.getDistanceSq(entity, pos);
         if (current < distance) {
            target = entity;
            distance = current;
         }
      }

      return target;
   }

   private List<BlockPos> getPositions(BlockPos pos) {
      List<BlockPos> positions = new ArrayList();
      Direction[] var3 = FacingUtil.HORIZONTALS;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction facing = var3[var5];
         BlockPos offset = pos.offset(facing).down();
         if (pos != offset) {
            positions.add(offset);
            Direction[] var8 = FacingUtil.HORIZONTALS;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Direction facingI = var8[var10];
               BlockPos offsetI = offset.offset(facingI);
               if (pos != offsetI && !positions.contains(offsetI)) {
                  positions.add(offsetI);
               }
            }
         }
      }

      return positions;
   }

   private Entity getTargetFromBlockPost(BlockPos pos, List<Entity> enemies) {
      Iterator var3 = enemies.iterator();

      Entity entity;
      Set surround;
      do {
         if (!var3.hasNext()) {
            return (Entity)enemies.get(0);
         }

         entity = (Entity)var3.next();
         surround = PositionUtil.getSurroundOffsets(entity);
      } while(!surround.contains(pos));

      return entity;
   }
}