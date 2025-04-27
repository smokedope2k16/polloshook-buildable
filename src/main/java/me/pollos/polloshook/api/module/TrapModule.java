package me.pollos.polloshook.api.module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.api.minecraft.rotations.StrictDirection;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TrapModule extends BlockPlaceModule {
   protected final Value<Boolean> top = new Value<>(true, new String[]{"Head", "h", "top"});
   protected final Value<Boolean> onlyHead;
   protected final Value<Boolean> prioTop;
   protected final Value<Boolean> feet;
   protected final Value<Boolean> floor;
   protected final Value<Boolean> antiStep;
   protected final Value<Boolean> antiScaffold;

   public TrapModule(String[] aliases, Category category) {
      super(aliases, category);
      this.onlyHead = (new Value<>(false, new String[]{"OnlyHead", "head"})).setParent(this.top);
      this.prioTop = (new Value<>(false, new String[]{"PrioTop", "prioritizetop"})).setParent(() ->
         this.top.getValue() && !this.onlyHead.getValue()
      );
      this.feet = new Value<>(false, new String[]{"Feet", "mmmmnfeet"});
      this.floor = new Value<>(false, new String[]{"Floor", "flor"});
      this.antiStep = new Value<>(true, new String[]{"AntiStep", "nostep"});
      this.antiScaffold = (new Value<>(true, new String[]{"AntiScaffold", "noscaffold"})).setParent(this.top);
      this.offerValues(this.top, this.prioTop, this.onlyHead, this.feet, this.floor, this.antiScaffold, this.antiStep);
   }

   public Value<Boolean> getTop() {
      return this.top;
   }

   public Value<Boolean> getOnlyHead() {
      return this.onlyHead;
   }

   public Value<Boolean> getPrioTop() {
      return this.prioTop;
   }

   public Value<Boolean> getFeet() {
      return this.feet;
   }

   public Value<Boolean> getFloor() {
      return this.floor;
   }

   public Value<Boolean> getAntiStep() {
      return this.antiStep;
   }

   public Value<Boolean> getAntiScaffold() {
      return this.antiScaffold;
   }

   public List<BlockPos> getBlocked(PlayerEntity entity) {
      List<BlockPos> trap = new ArrayList<>();
      Set<BlockPos> head = new HashSet<>();
      Set<BlockPos> surround = PositionUtil.getSurroundOffsets(entity);
      Set<BlockPos> blocked = PositionUtil.getBlockedPositions(entity);
      Set<BlockPos> feetPos = new HashSet<>();
      List<BlockPos> others = new ArrayList<>();

      for (BlockPos surroundPos : blocked) {
         BlockPos headPos = entity.isCrawling() ? surroundPos : surroundPos.up();
         if (this.antiScaffold.getValue() && this.top.getValue()) {
            trap.add(headPos.up().up());
         }
         if (this.top.getValue()) {
            head.add(headPos.up());
         }
         if (this.floor.getValue()) {
            feetPos.add(surroundPos.down());
         }
      }

      if (!entity.isCrawling()) {
         for (BlockPos surroundPos : surround) {
            if (this.antiStep.getValue()) {
               if (this.prioTop.getValue()) {
                  others.add(surroundPos.up().up());
               } else {
                  trap.add(surroundPos.up().up());
               }
            }
            if (!this.onlyHead.getParent().isVisible() || !this.onlyHead.getValue()) {
               if (this.prioTop.getValue()) {
                  others.add(surroundPos.up());
               } else {
                  trap.add(surroundPos.up());
               }
            }
            if (this.feet.getValue()) {
               feetPos.add(surroundPos);
            }
         }
      }

      boolean help = false;
      if (this.top.getValue()) {
         for (BlockPos headPos : head) {
            trap.add(headPos);
            if (!this.stateHelper.getBlockState(headPos).isAir() && help) continue;

            List<BlockPos> surroundUp = new ArrayList<>();
            surround.forEach(s -> surroundUp.add(s.up()));
            BlockPos pos = findHelper(headPos, surroundUp, head, true);
            if (pos == null) {
               pos = findHelper(headPos, surroundUp, head, false);
            }
            if (pos == null) continue;

            if (this.onlyHead.getValue() || this.prioTop.getValue()) {
               trap.add(pos.down());
            }
            trap.add(pos);
            help = hasHelping(headPos, trap);
         }
      }

      for (BlockPos headPos : head) {
         if (this.prioTop.getValue() && !this.stateHelper.getBlockState(headPos).isAir()) {
            trap.addAll(others);
         }
      }

      trap.addAll(feetPos);
      return trap;
   }

   protected BlockPos findHelper(BlockPos pos, List<BlockPos> place, Set<BlockPos> head, boolean noAir) {
      BlockPos helper = null;
      for (Direction direction : FacingUtil.HORIZONTALS) {
         BlockPos offset = pos.offset(direction);
         if ((!this.stateHelper.getBlockState(offset.down()).isAir() || !noAir) && BlockUtil.getDistanceSq(offset) <= MathUtil.square(this.placeRange.getValue()) && !head.contains(offset) && (!this.strictDirection.getValue() || strictDirectionCheck(offset, head)) && !fastIntersectionCheck(pos, Managers.getEntitiesManager().getEntities())) {
            if (hasHelping(offset, place)) {
               helper = null;
               break;
            }
            if (place.contains(offset.down())) {
               helper = offset;
            }
         }
      }
      return helper;
   }

   protected boolean hasHelping(BlockPos pos, List<BlockPos> place) {
      return !mc.world.getBlockState(pos).isReplaceable() || place.contains(pos);
   }

   private boolean strictDirectionCheck(BlockPos pos, Set<BlockPos> head) {
      if (!this.strictDirection.getValue()) {
         return true;
      }
      if (StrictDirection.getStrictDirection(pos) != null) {
         for (Direction facing : FacingUtil.HORIZONTALS) {
            BlockPos offset = pos.offset(facing);
            if (head.contains(offset)) {
               this.stateHelper.addBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
               boolean strictDirectionCheck = StrictDirection.strictDirectionCheck(offset, facing, this.stateHelper);
               this.stateHelper.clearAllStates();
               if (strictDirectionCheck) {
                  return true;
               }
            }
         }
      }
      return false;
   }
}