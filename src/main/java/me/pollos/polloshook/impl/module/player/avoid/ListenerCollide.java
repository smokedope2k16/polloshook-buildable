package me.pollos.polloshook.impl.module.player.avoid;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.CollisionShapeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.shape.VoxelShapes;

public class ListenerCollide extends ModuleListener<Avoid, CollisionShapeEvent> {
   public ListenerCollide(Avoid module) {
      super(module, CollisionShapeShapeEvent.class);
   }

   public void call(CollisionShapeEvent event) {
      if (event.getEntity() instanceof ClientPlayerEntity) {
         BlockState state = event.getState();
         Block block = state.getBlock();
         if (!((double)event.getPos().getY() > mc.player.getY())) {

            if ((Boolean)((Avoid)this.module).tripwire.getValue() && (block == Blocks.TRIPWIRE || block == Blocks.TRIPWIRE_HOOK)) {
               this.cancel(event);
            } else if ((Boolean)((Avoid)this.module).lava.getValue() && block == Blocks.LAVA) {
               this.cancel(event);
            } else if ((Boolean)((Avoid)this.module).fire.getValue() && block == Blocks.FIRE) {
               this.cancel(event);
            } else if ((Boolean)((Avoid)this.module).cactus.getValue() && block == Blocks.CACTUS) {
               this.cancel(event);
            } else {
               BlockPos pos = event.getPos();
               float chunkX = (float)ChunkSectionPos.getSectionCoord(pos.getX());
               float chunkZ = (float)ChunkSectionPos.getSectionCoord(pos.getZ());
               if ((Boolean)((Avoid)this.module).unloaded.getValue() && mc.world.isChunkLoaded((int)chunkX, (int)chunkZ)) {
                  this.cancel(event);
               }

            }
         }
      }
   }

   private void cancel(CollisionShapeEvent event) {
      event.setShape(VoxelShapes.fullCube());
      event.setCanceled(true);
   }
}