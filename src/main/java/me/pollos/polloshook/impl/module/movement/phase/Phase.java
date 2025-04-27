package me.pollos.polloshook.impl.module.movement.phase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.phase.util.OffsetType;
import me.pollos.polloshook.impl.module.movement.phase.util.PhaseMode;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Phase extends ToggleableModule {
   protected final EnumValue<PhaseMode> mode;
   protected final EnumValue<OffsetType> offset;
   protected final NumberValue<Integer> delay;
   protected final NumberValue<Double> movement;
   protected final Value<Boolean> checkEntities;
   protected final NumberValue<Integer> pitch;
   protected double cornerX;
   protected double cornerZ;
   protected int time;
   protected boolean doBounds;

   public Phase() {
      super(new String[]{"Phase", "noclip", "phaser", "nocliperino", "clip"}, Category.MOVEMENT);
      this.mode = new EnumValue(PhaseMode.CLIP, new String[]{"Mode", "m"});
      this.offset = (new EnumValue(OffsetType.FULL, new String[]{"Offset", "off"})).setParent(this.mode, PhaseMode.CLIP);
      this.delay = (new NumberValue(10, 1, 20, new String[]{"Delay", "del"})).setParent(this.mode, PhaseMode.CLIP);
      this.movement = (new NumberValue(0.6D, 0.0D, 1.0D, 0.1D, new String[]{"Movement", "move"})).setParent(this.mode, PhaseMode.CLIP);
      this.checkEntities = (new Value(false, new String[]{"CheckEntities", "checkentity"})).setParent(this.mode, PhaseMode.PEARL);
      this.pitch = (new NumberValue(90, -90, 90, new String[]{"Pitch", "rotation", "p"})).withTag("degree").setParent(this.mode, PhaseMode.PEARL);
      this.cornerX = 0.0D;
      this.cornerZ = 0.0D;
      this.time = 0;
      this.doBounds = false;
      this.offerValues(new Value[]{this.mode, this.offset, this.delay, this.movement, this.checkEntities, this.pitch});
      this.offerListeners(new Listener[]{new ListenerMove(this), new ListenerTick(this), new ListenerMotion(this), new ListenerRotation(this), new ListenerPositionRotation(this)});
   }

   protected String getTag() {
      return this.mode.getFixedValue();
   }

   protected void onEnable() {
      this.cornerX = 0.0D;
      this.cornerZ = 0.0D;
      this.time = 0;
      this.doBounds = false;
   }

   protected boolean findCorner(double x, double z, BlockPos pos) {
      boolean minX = !this.isReplaceable(pos.offset(Direction.WEST));
      boolean minZ = !this.isReplaceable(pos.offset(Direction.NORTH));
      boolean maxX = !this.isReplaceable(pos.offset(Direction.EAST));
      boolean maxZ = !this.isReplaceable(pos.offset(Direction.SOUTH));
      List<Pair<Double, Double>> corners = new ArrayList();
      if (minX) {
         if (minZ) {
            corners.add(new Pair(0.0D, 0.0D));
         }

         if (maxZ) {
            corners.add(new Pair(0.0D, 1.0D));
         }
      }

      if (maxX) {
         if (minZ) {
            corners.add(new Pair(1.0D, 0.0D));
         }

         if (maxZ) {
            corners.add(new Pair(1.0D, 1.0D));
         }
      }

      if (corners.isEmpty()) {
         return true;
      } else {
         double distC = 1000.0D;
         Iterator var13 = corners.iterator();

         while(var13.hasNext()) {
            Pair<Double, Double> corner = (Pair)var13.next();
            double dx = x - (Double)corner.getLeft();
            double dz = z - (Double)corner.getRight();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < distC) {
               distC = dist;
               this.set((Double)corner.getLeft(), (Double)corner.getRight());
            }
         }

         return false;
      }
   }

   private void set(double x, double z) {
      this.cornerX = x;
      this.cornerZ = z;
   }

   public boolean isReplaceable(BlockPos pos) {
      return mc.world.getBlockState(pos).isReplaceable();
   }
}