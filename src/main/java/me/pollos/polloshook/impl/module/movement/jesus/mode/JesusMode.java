package me.pollos.polloshook.impl.module.movement.jesus.mode;


import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public enum JesusMode {
   SOLID(VoxelShapes.fullCube()),
   STRICT_SOLID(SOLID.getShape()),
   TRAMPOLINE(VoxelShapes.cuboid(new Box(0.0D, 0.0D, 0.0D, 1.0D, 0.96D, 1.0D))),
   DOLPHIN((VoxelShape)null);

   private final VoxelShape shape;

   
   public VoxelShape getShape() {
      return this.shape;
   }

   
   private JesusMode(final VoxelShape shape) {
      this.shape = shape;
   }

   // $FF: synthetic method
   private static JesusMode[] $values() {
      return new JesusMode[]{SOLID, STRICT_SOLID, TRAMPOLINE, DOLPHIN};
   }
}
