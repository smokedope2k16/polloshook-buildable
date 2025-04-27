package me.pollos.polloshook.impl.module.movement.reversestep;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class ReverseStep extends ToggleableModule {
   protected final NumberValue<Float> height = (new NumberValue(5.0F, 1.0F, 10.0F, 0.1F, new String[]{"Height", "falldistance", "distance"})).withTag("range");
   protected final StopWatch timer = new StopWatch();

   public ReverseStep() {
      super(new String[]{"ReverseStep", "fastfall"}, Category.MOVEMENT);
      this.offerValues(new Value[]{this.height});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerPosLook(this)});
   }

   protected double traceDown() {
      int y = (int)Math.round(mc.player.getY()) - 1;

      for(int traceY = y; traceY >= mc.world.getBottomY(); --traceY) {
         HitResult trace = mc.world.raycast(new RaycastContext(mc.player.getPos(), new Vec3d(mc.player.getX(), (double)traceY, mc.player.getZ()), ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
         if (trace != null && trace.getType() == Type.BLOCK) {
            double i = this.getMaxYFromBlock(BlockPos.ofFloored(mc.player.getX(), trace.getPos().y, mc.player.getZ()));
            return this.trace(i);
         }
      }

      return -1337.0D;
   }

   protected double getMaxYFromBlock(BlockPos pos) {
      BlockState state = mc.world.getBlockState(pos);
      VoxelShape shape = state.getCollisionShape(mc.world, pos);
      return shape.isEmpty() ? (double)pos.getY() : (double)pos.getY() + shape.getBoundingBox().maxY;
   }

   protected double trace(double y) {
      Box bb = mc.player.getBoundingBox().shrink(1.0E-7D, 1.0E-7D, 1.0E-7D);
      double minX = bb.minX;
      double minZ = bb.minZ;
      double maxX = bb.maxX;
      double maxZ = bb.maxZ;
      Vec3d center = mc.player.getPos();
      Map<Vec3d, Vec3d> positions = new HashMap();
      positions.put(center, new Vec3d(center.x, y, center.z));
      positions.put(new Vec3d(minX, bb.minY, minZ), new Vec3d(minX, y, minZ));
      positions.put(new Vec3d(maxX, bb.minY, minZ), new Vec3d(maxX, y, minZ));
      positions.put(new Vec3d(minX, bb.minY, maxZ), new Vec3d(minX, y, maxZ));
      positions.put(new Vec3d(maxX, bb.minY, maxZ), new Vec3d(maxX, y, maxZ));
      double finalY = y;
      Iterator var16 = positions.keySet().iterator();

      while(var16.hasNext()) {
         Vec3d key = (Vec3d)var16.next();
         RaycastContext context = new RaycastContext(key, (Vec3d)positions.get(key), ShapeType.COLLIDER, FluidHandling.NONE, mc.player);
         BlockHitResult result = mc.world.raycast(context);
         if (result != null && result.getType() == Type.BLOCK) {
            double i = this.getMaxYFromBlock(result.getBlockPos());
            if (finalY < i) {
               finalY = i;
            }
         }
      }

      return finalY;
   }
}