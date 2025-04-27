package me.pollos.polloshook.impl.module.player.automine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.MineUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.impl.events.block.AttackBlockEvent;
import me.pollos.polloshook.impl.module.player.automine.util.AutoMinePriority;
import me.pollos.polloshook.impl.module.player.automine.util.AutoMineTarget;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoMine extends ToggleableModule {
   protected final Value<Boolean> multitask = new Value(true, new String[]{"MultiTask", "multitasking"});
   protected final NumberValue<Float> range = (new NumberValue(4.2F, 1.0F, 6.0F, 0.1F, new String[]{"Range", "distance"})).withTag("range");
   protected final Value<Boolean> rotate = new Value(false, new String[]{"Rotations", "rotate"});
   protected final NumberValue<Float> enemyRange = (new NumberValue(8.0F, 0.1F, 12.0F, 0.1F, new String[]{"EnemyRange", "targetrange"})).withTag("range");
   protected final Value<Boolean> swing = new Value(true, new String[]{"Swing", "punch"});
   protected final Value<Boolean> stopIfAir = new Value(false, new String[]{"StopOnAir"});
   protected final Value<Boolean> enderChests = new Value(true, new String[]{"EnderChests", "echests", "echest"});
   protected final NumberValue<Float> eChestRange;
   protected final Value<Boolean> surround;
   protected final Value<Boolean> ignoreAir;
   protected final Value<Boolean> surroundFocus;
   protected final Value<Boolean> burrow;
   protected final Value<Boolean> antiTrap;
   protected final Value<Boolean> cev;
   protected final Value<Boolean> sides;
   protected final Value<Boolean> antiUpper;
   protected final Value<Boolean> actualReset;
   protected final Value<Boolean> protocol;
   protected final List<PlayerEntity> enemies;
   protected AutoMineTarget attackPos;
   protected int lastPriority;

   public AutoMine() {
      super(new String[]{"AutoMine", "autocity"}, Category.PLAYER);
      this.eChestRange = (new NumberValue(2.5F, 1.0F, 4.0F, 0.1F, new String[]{"EChestRange", "enderrange", "enderchestrange"})).withTag("range").setParent(this.enderChests);
      this.surround = new Value(true, new String[]{"Surround", "feetplace", "feettrap"});
      this.ignoreAir = (new Value(false, new String[]{"IgnoreAir", "air"})).setParent(this.surround);
      this.surroundFocus = (new Value(false, new String[]{"Focused", "focus"})).setParent(this.surround);
      this.burrow = new Value(true, new String[]{"Burrow", "burrowed", "selffill", "selffilled"});
      this.antiTrap = new Value(false, new String[]{"AntiTrap", "notrap"});
      this.cev = new Value(false, new String[]{"Cev", "cevbreak"});
      this.sides = (new Value(false, new String[]{"Sides"})).setParent(this.cev);
      this.antiUpper = (new Value(false, new String[]{"AntiUpper"})).setParent(this.cev);
      this.actualReset = new Value(false, new String[]{"Reset", "fastreset"});
      this.protocol = new Value(false, new String[]{"Protocol", "prot"});
      this.enemies = new ArrayList();
      this.attackPos = null;
      this.lastPriority = -1;
      this.offerValues(new Value[]{this.multitask, this.range, this.rotate, this.enemyRange, this.swing, this.stopIfAir, this.enderChests, this.eChestRange, this.surround, this.ignoreAir, this.surroundFocus, this.burrow, this.antiTrap, this.cev, this.antiUpper, this.sides, this.actualReset, this.protocol});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerLogout(this)});
   }

   protected void onToggle() {
      this.attackPos = null;
   }

   protected void attackBestPos(List<AutoMineTarget> positions) {
      List<AutoMineTarget> validPositions = new ArrayList();
      FastBreak FAST_BREAK = (FastBreak)Managers.getModuleManager().get(FastBreak.class);
      Vec3d playerVec = PositionUtil.getEyesPos();
      int currentHighestPriority = 0;
      Iterator var6 = positions.iterator();

      AutoMineTarget finalTarget;
      while(var6.hasNext()) {
         finalTarget = (AutoMineTarget)var6.next();
         if (!(playerVec.squaredDistanceTo(finalTarget.getPos().toCenterPos()) > (double)MathUtil.square((Float)this.range.getValue())) && !(playerVec.squaredDistanceTo(finalTarget.getPos().toCenterPos()) > (double)MathUtil.square(FAST_BREAK.getRange())) && MineUtil.canBreak(finalTarget.getPos()) && FAST_BREAK.isValid(finalTarget.getPos()) && finalTarget.getPriority().getValue() >= currentHighestPriority) {
            currentHighestPriority = finalTarget.getPriority().getValue();
            validPositions.add(finalTarget);
         }
      }

      final int highestPriority = currentHighestPriority;

      if (!validPositions.isEmpty()) {
         validPositions.removeIf((posx) -> {
            return posx.getPriority().getValue() < highestPriority;
         });
         if (!(Boolean)this.surroundFocus.getValue() || this.lastPriority != 6 || !this.hasPos(positions, this.attackPos)) {
            this.lastPriority = highestPriority;
            finalTarget = (AutoMineTarget)validPositions.get(0);
            if (highestPriority < 8 && highestPriority > 1) {
               double distance = 999.9000244140625D;
               Iterator var10 = this.enemies.iterator();

               while(var10.hasNext()) {
                  PlayerEntity enemy = (PlayerEntity)var10.next();
                  Iterator var12 = validPositions.iterator();

                  while(var12.hasNext()) {
                     AutoMineTarget pos = (AutoMineTarget)var12.next();
                     double currentDistance = BlockUtil.getDistanceSq(enemy, (BlockPos)pos.getPos());
                     if (currentDistance < distance) {
                        distance = currentDistance;
                        finalTarget = pos;
                     }
                  }
               }
            }

            if (finalTarget != null) {
               if (this.attackPos != null) {
                  boolean containsPos = this.hasPos(validPositions, (AutoMineTarget)this.attackPos);
                  boolean priority = finalTarget.getPriority().getValue() <= this.attackPos.getPriority().getValue();
                  if (containsPos && priority) {
                     return;
                  }
               }

               this.setAndAttack(finalTarget);
            }
         }
      }
   }

   protected List<AutoMineTarget> getPossiblePositions(PlayerEntity target) {
      List<AutoMineTarget> posList = new ArrayList();
      if (target != null) {
         Box targetBox = ((ILivingEntity)target).getServerBoundingBox();
         if (target instanceof FakePlayerEntity) {
            targetBox = target.getBoundingBox();
         }

         List<BlockPos> blocked = new ArrayList(PositionUtil.getBlockedPositions(targetBox));
         List<BlockPos> protectedOffsets = new ArrayList(PositionUtil.getSurroundOffsets(targetBox));
         Iterator var6;
         BlockPos surroundOffset;
         if ((Boolean)this.burrow.getValue() && target.isInsideWall()) {
            var6 = blocked.iterator();

            while(var6.hasNext()) {
               surroundOffset = (BlockPos)var6.next();
               posList.add(new AutoMineTarget(AutoMinePriority.BURROW, surroundOffset));
            }
         }

         BlockPos upperUp;
         if ((Boolean)this.surround.getValue()) {
            var6 = protectedOffsets.iterator();

            while(var6.hasNext()) {
               surroundOffset = (BlockPos)var6.next();
               boolean hasAir = false;
               Direction[] var9 = FacingUtil.HORIZONTALS;
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  Direction facing = var9[var11];
                  BlockPos neighbour = surroundOffset.offset(facing);
                  if (!blocked.contains(neighbour)) {
                     boolean neighbourPlace = BlockUtil.getBlock(neighbour.down()) == Blocks.OBSIDIAN || BlockUtil.isBedrock(neighbour.down());
                     if (mc.world.getBlockState(neighbour).isAir() && neighbourPlace) {
                        hasAir = true;
                     }
                  }
               }

               upperUp = surroundOffset.down();
               boolean placeable = BlockUtil.getBlock(upperUp) == Blocks.OBSIDIAN || BlockUtil.isBedrock(upperUp) || (Boolean)this.ignoreAir.getValue();
               if (placeable) {
                  if (hasAir) {
                     posList.add(new AutoMineTarget(AutoMinePriority.SURROUND_AIR, surroundOffset));
                  } else if (!(Boolean)this.protocol.getValue()) {
                     posList.add(new AutoMineTarget(AutoMinePriority.SURROUND, surroundOffset));
                  }
               }
            }
         }

         if ((Boolean)this.cev.getValue()) {
            var6 = blocked.iterator();

            while(var6.hasNext()) {
               surroundOffset = (BlockPos)var6.next();
               BlockPos upper = surroundOffset.up().up();
               upperUp = upper.up();
               if (BlockUtil.isAir(upperUp)) {
                  posList.add(new AutoMineTarget(AutoMinePriority.CRYSTAL_HEAD, upper));
               }

               if ((Boolean)this.antiUpper.getValue() && BlockUtil.getBlock(upperUp) == Blocks.OBSIDIAN && !BlockUtil.isAir(upperUp.up())) {
                  posList.add(new AutoMineTarget(AutoMinePriority.CRYSTAL_HEAD_BLOCK, upperUp.up()));
               }
            }

            if ((Boolean)this.sides.getValue()) {
               var6 = protectedOffsets.iterator();

               while(var6.hasNext()) {
                  surroundOffset = (BlockPos)var6.next();
                  if (BlockUtil.isAir(surroundOffset.up().up())) {
                     posList.add(new AutoMineTarget(AutoMinePriority.CRYSTAL, surroundOffset.up()));
                  }
               }
            }
         }
      }

      return posList;
   }

   protected List<AutoMineTarget> getSelfPositions() {
      List<AutoMineTarget> posList = new ArrayList();
      if ((Boolean)this.enderChests.getValue()) {
         posList.addAll(this.getEnderChests());
      }

      if ((Boolean)this.antiTrap.getValue()) {
         BlockPos upPos = mc.player.getBlockPos().up();
         posList.add(new AutoMineTarget(AutoMinePriority.SELF, upPos));
         posList.add(new AutoMineTarget(AutoMinePriority.SELF, upPos.up()));
      }

      return posList;
   }

   private List<AutoMineTarget> getEnderChests() {
      List<AutoMineTarget> targets = new ArrayList();
      Vec3d playerVec = new Vec3d(mc.player.getX(), mc.player.getY() + 1.0D, mc.player.getZ());
      Iterator var3 = BlockUtil.getSphere(mc.player, ((FastBreak)Managers.getModuleManager().get(FastBreak.class)).getRange(), true).iterator();

      while(var3.hasNext()) {
         BlockPos pos = (BlockPos)var3.next();
         if (BlockUtil.getBlock(pos) == Blocks.ENDER_CHEST && !(playerVec.squaredDistanceTo(pos.toCenterPos()) > (double)MathUtil.square((Float)this.eChestRange.getValue()))) {
            targets.add(new AutoMineTarget(AutoMinePriority.ENDER_CHEST, pos));
         }
      }

      return targets;
   }

   protected void setAndAttack(AutoMineTarget target) {
      if (this.attackPos == null || !this.attackPos.getPos().equals(target.getPos())) {
         if (target != null) {
            this.attackPos = target;
            this.attack();
         }
      }
   }

   protected void attack() {
      if (this.attackPos != null) {
         if (mc.interactionManager != null) {
            boolean air = BlockUtil.isAir(this.attackPos.getPos());
            if (!air) {
               float[] oldRots = new float[]{mc.player.getYaw(), mc.player.getPitch()};
               float[] rotations = BlockUtil.getBlockPosRotations(this.attackPos.getPos());
               if ((Boolean)this.rotate.getValue()) {
                  PacketUtil.rotate(rotations, Managers.getPositionManager().isOnGround());
               }

               if ((Boolean)this.swing.getValue()) {
                  EntityUtil.swingClient(Hand.MAIN_HAND);
                  PacketUtil.swing();
               }

               Direction dir = this.getVisibleDirection(this.attackPos.getPos());
               PollosHook.getEventBus().dispatch(new AttackBlockEvent(this.attackPos.getPos(), dir));
               PacketUtil.send(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, this.attackPos.getPos(), dir, PacketUtil.incrementSequence()));
               if ((Boolean)this.swing.getValue()) {
                  PacketUtil.swing();
               }

               if ((Boolean)this.rotate.getValue()) {
                  PacketUtil.rotate(oldRots, Managers.getPositionManager().isOnGround());
               }

            }
         }
      }
   }

   private Direction getVisibleDirection(BlockPos pos) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         if (mc.world.getBlockState(pos.offset(direction)).isReplaceable()) {
            return direction;
         }
      }

      return Direction.UP;
   }

   private boolean hasPos(List<AutoMineTarget> targets, BlockPos target) {
      if (target == null) {
         return false;
      } else {
         Iterator var3 = targets.iterator();

         AutoMineTarget t;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            t = (AutoMineTarget)var3.next();
         } while(!t.getPos().equals(target));

         return true;
      }
   }

   private boolean hasPos(List<AutoMineTarget> targets, AutoMineTarget target) {
      if (target == null) {
         return false;
      } else {
         Iterator var3 = targets.iterator();

         AutoMineTarget t;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            t = (AutoMineTarget)var3.next();
         } while(!t.getPos().equals(target.getPos()));

         return true;
      }
   }

   
   public Value<Boolean> getMultitask() {
      return this.multitask;
   }

   
   public NumberValue<Float> getRange() {
      return this.range;
   }

   
   public Value<Boolean> getRotate() {
      return this.rotate;
   }

   
   public NumberValue<Float> getEnemyRange() {
      return this.enemyRange;
   }

   
   public Value<Boolean> getSwing() {
      return this.swing;
   }

   
   public Value<Boolean> getStopIfAir() {
      return this.stopIfAir;
   }

   
   public NumberValue<Float> getEChestRange() {
      return this.eChestRange;
   }

   
   public Value<Boolean> getSurround() {
      return this.surround;
   }

   
   public Value<Boolean> getIgnoreAir() {
      return this.ignoreAir;
   }

   
   public Value<Boolean> getSurroundFocus() {
      return this.surroundFocus;
   }

   
   public Value<Boolean> getBurrow() {
      return this.burrow;
   }

   
   public Value<Boolean> getAntiTrap() {
      return this.antiTrap;
   }

   
   public Value<Boolean> getCev() {
      return this.cev;
   }

   
   public Value<Boolean> getSides() {
      return this.sides;
   }

   
   public Value<Boolean> getAntiUpper() {
      return this.antiUpper;
   }

   
   public Value<Boolean> getActualReset() {
      return this.actualReset;
   }

   
   public Value<Boolean> getProtocol() {
      return this.protocol;
   }

   
   public List<PlayerEntity> getEnemies() {
      return this.enemies;
   }

   
   public AutoMineTarget getAttackPos() {
      return this.attackPos;
   }

   
   public int getLastPriority() {
      return this.lastPriority;
   }

   
   public void setAttackPos(AutoMineTarget attackPos) {
      this.attackPos = attackPos;
   }

   
   public void setLastPriority(int lastPriority) {
      this.lastPriority = lastPriority;
   }
}