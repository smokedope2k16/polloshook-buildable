package me.pollos.polloshook.impl.module.movement.holesnap;

import java.util.Comparator;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.api.util.obj.hole.Hole2x1;
import me.pollos.polloshook.api.util.obj.hole.SafetyEnum;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.holesnap.mode.HolePriority;
import me.pollos.polloshook.impl.module.movement.step.Step;
import me.pollos.polloshook.impl.module.movement.tickshift.TickShift;
import me.pollos.polloshook.impl.module.movement.tickshift.mode.TickShiftMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class HoleSnap extends ToggleableModule {
   protected final NumberValue<Float> range = (new NumberValue(3.5F, 1.0F, 6.0F, 0.1F, new String[]{"HoleRange", "range"})).withTag("range");
   protected final NumberValue<Float> yRange = (new NumberValue(3.5F, 1.0F, 6.0F, 0.1F, new String[]{"YRange", "yrang", "ydistance"})).withTag("range");
   protected final Value<Boolean> reRoute = new Value(false, new String[]{"ReRoute", "route"});
   protected final NumberValue<Float> extendRange;
   protected final Value<Boolean> doubles;
   protected final Value<Boolean> toggleStep;
   protected final Value<Boolean> searchAbovePlayer;
   protected final Value<Boolean> terrainHoles;
   protected final EnumValue<HolePriority> priority;
   protected final Value<Boolean> speed;
   protected final NumberValue<Integer> factor;
   protected final NumberValue<Integer> maxYLevel;
   protected Hole hole;
   protected Hole lastHole;
   protected int boosted;
   protected boolean wasStepToggled;
   protected int noMoveTicks;
   protected Vec3d IGNORE_POS;

   public HoleSnap() {
      super(new String[]{"HoleSnap", "holepull"}, Category.MOVEMENT);
      this.extendRange = (new NumberValue(0.0F, 0.0F, 6.0F, 0.1F, new String[]{"ExtendRange", "extend"})).withTag("range").setParent(this.reRoute);
      this.doubles = new Value(true, new String[]{"2x1", "2x1s", "doubles"});
      this.toggleStep = new Value(false, new String[]{"ToggleStep", "step"});
      this.searchAbovePlayer = (new Value(false, new String[]{"SearchAbovePlayer", "lowercheck"})).setParent(this.toggleStep);
      this.terrainHoles = new Value(false, new String[]{"TerrainHoles", "terrain"});
      this.priority = new EnumValue(HolePriority.SMART, new String[]{"Priority", "prio"});
      this.speed = new Value(false, new String[]{"Speed", "sped"});
      this.factor = (new NumberValue(2, 1, 5, new String[]{"Factor", "fact", "factoid"})).setParent(this.speed);
      this.maxYLevel = new NumberValue(3, 2, 5, new String[]{"MaxY", "maxyblocked", "maxylevel"});
      this.boosted = 0;
      this.IGNORE_POS = (new Hole(BlockPos.ORIGIN.add(new Vec3i(1491412841, 58235812, 1234124)), SafetyEnum.BEDROCK)).getPos().toCenterPos();
      this.offerValues(new Value[]{this.range, this.yRange, this.reRoute, this.extendRange, this.doubles, this.toggleStep, this.searchAbovePlayer, this.terrainHoles, this.priority, this.speed, this.factor, this.maxYLevel});
      this.offerListeners(new Listener[]{new ListenerMove(this), new ListenerRender(this), new ListenerMotion(this), new ListenerTick(this)});
   }

   protected void onEnable() {
      if (!PlayerUtil.isNull()) {
         this.setTarget((Float)this.range.getValue(), (Hole)null);
         if ((Boolean)this.toggleStep.getValue()) {
            ((Step)Managers.getModuleManager().get(Step.class)).setEnabled(this.wasStepToggled = true);
         }

      }
   }

   protected void onDisable() {
      if (!PlayerUtil.isNull()) {
         this.noMoveTicks = 0;
         if (this.wasStepToggled) {
            ((Step)Managers.getModuleManager().get(Step.class)).setEnabled(this.wasStepToggled = false);
         }

         TickShift TICK_SHIFT = (TickShift)Managers.getModuleManager().get(TickShift.class);
         if (TICK_SHIFT.getMode().getValue() == TickShiftMode.INSTANT) {
            TICK_SHIFT.setEnabled(false);
         }

      }
   }

   protected void setTarget(float range, Hole ignore) {
      this.noMoveTicks = 0;
      Hole filtered = (Hole)HoleUtil.getHoles(range, (Float)this.yRange.getValue(), (Boolean)this.doubles.getValue(), false, (Boolean)this.terrainHoles.getValue()).stream().filter((h) -> {
         return this.isValidHole(h, range);
      }).filter((h) -> {
         return !h.equals(ignore);
      }).min(this.getComparator()).orElse((Hole)null);
      double distanceFactor = filtered == null ? 10.0D : BlockUtil.getDistanceSq(filtered.getPos().toCenterPos());
      this.boosted = (int)(1.0D + (double)(Integer)this.factor.getValue() + distanceFactor);
      this.lastHole = this.hole;
      this.setHole(filtered);
   }

   private Comparator<Hole> getComparator() {
      Comparator var10000;
      switch((HolePriority)this.priority.getValue()) {
      case SMART:
         var10000 = Comparator.comparing(Hole::getSafety).reversed().thenComparing(this::getObsidian).thenComparingDouble((h) -> {
            return BlockUtil.getDistanceSq(this.getPlayerHolePos(h));
         }).thenComparingDouble((h) -> {
            return BlockUtil.getDistanceSq(this.getPlayerHolePos(h));
         });
         break;
      case CLOSEST:
         var10000 = Comparator.comparingDouble((h) -> {
            return BlockUtil.getDistanceSq(this.getPlayerHolePos((Hole) h));
         });
         break;
      case FARTHEST:
         var10000 = Comparator.comparingDouble((h) -> {
            return -BlockUtil.getDistanceSq(this.getPlayerHolePos((Hole) h));
         });
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private int getObsidian(Hole hole) {
      return hole.getSafety() == SafetyEnum.MIXED ? (int)hole.getBlocks().stream().filter(BlockUtil::isObby).count() : Integer.MAX_VALUE;
   }

   protected boolean isValidHole(Hole hole, float range) {
      if (hole == null) {
         return false;
      } else {
         Vec3d playerHolePos = this.getPlayerHolePos(hole);
         if (BlockUtil.getDistanceSq(playerHolePos) > (double)MathUtil.square(range)) {
            return false;
         } else {
            boolean yPos = (double)hole.getPos().getY() < mc.player.getY() || (Boolean)this.searchAbovePlayer.getValue() && (Boolean)this.toggleStep.getValue();
            return yPos && !this.isHoleBlocked(hole);
         }
      }
   }

   protected boolean isHoleBlocked(Hole hole) {
      for(int i = 1; i <= 5; ++i) {
         BlockPos blocking = hole.getPos().up(i);
         if (i >= (Integer)this.maxYLevel.getValue() && mc.player.getY() < (double)blocking.getY()) {
            return false;
         }

         if (!BlockUtil.isAir(blocking)) {
            return true;
         }

         if (hole instanceof Hole2x1) {
            Hole2x1 hole2x1 = (Hole2x1)hole;
            if (!BlockUtil.isAir(hole2x1.getSecondPos().up(i))) {
               return true;
            }
         }
      }

      return false;
   }

   protected Vec3d getPlayerHolePos(Hole hole) {
      if (hole == null) {
         return this.IGNORE_POS;
      } else {
         Vec3d holePos = HoleUtil.getCenter(hole);
         return new Vec3d(holePos.x, mc.player.getY(), holePos.z);
      }
   }

   
   private void setHole(Hole hole) {
      this.hole = hole;
   }

   
   private void setLastHole(Hole lastHole) {
      this.lastHole = lastHole;
   }
}
