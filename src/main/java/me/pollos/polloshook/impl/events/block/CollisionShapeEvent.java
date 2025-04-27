package me.pollos.polloshook.impl.events.block;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

public class CollisionShapeEvent extends Event {
   private final Entity entity;
   private final BlockState state;
   private final BlockPos pos;
   private VoxelShape shape;

   
   public Entity getEntity() {
      return this.entity;
   }

   
   public BlockState getState() {
      return this.state;
   }

   
   public BlockPos getPos() {
      return this.pos;
   }

   
   public VoxelShape getShape() {
      return this.shape;
   }

   
   public void setShape(VoxelShape shape) {
      this.shape = shape;
   }

   
   public CollisionShapeEvent(Entity entity, BlockState state, BlockPos pos, VoxelShape shape) {
      this.entity = entity;
      this.state = state;
      this.pos = pos;
      this.shape = shape;
   }
}
