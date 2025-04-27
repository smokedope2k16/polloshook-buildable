package me.pollos.polloshook.impl.module.render.newchunks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.render.newchunks.util.ChunkData;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ListenerUpdate extends ModuleListener<NewChunks, UpdateEvent> {
   public ListenerUpdate(NewChunks module) {
      super(module, UpdateEvent.class);
   }
   ArrayList blocks;

   public void call(UpdateEvent event) {
      ChunkPos chunkPos = mc.player.getChunkPos();
      List<WorldChunk> chonkers = this.loopChunks(chunkPos.x, chunkPos.z);
      Iterator var4 = chonkers.iterator();

      while (true) {
         WorldChunk chunk;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  chunk = (WorldChunk) var4.next();
               } while (chunk == null);

               blocks = new ArrayList();
               Arrays.stream(chunk.getSectionArray()).forEach((section) -> {
                  section.getBlockStateContainer().forEachValue((blockState) -> {
                     blocks.add(blockState.getBlock());
                  });
               });
            } while (!blocks.contains(Blocks.DEEPSLATE)
                  && mc.world.getRegistryKey().getValue().getPath().equals("overworld"));
         } while (!blocks.contains(Blocks.ANCIENT_DEBRIS)
               && mc.world.getRegistryKey().getValue().getPath().equals("the_nether"));
         ChunkData chunkData = new ChunkData(chunk.getPos().x, chunk.getPos().z);
         if (!((NewChunks) this.module).chunkDataList.contains(chunkData)) {
            ((NewChunks) this.module).chunkDataList.add(chunkData);
         }
      }
   }

   private List<WorldChunk> loopChunks(int posX, int posZ) {
      List<WorldChunk> chunks = new ArrayList();

      for (int x = -17; x <= 17; ++x) {
         for (int z = -17; z <= 17; ++z) {
            WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(posX + x, posZ + z);
            if (chunk != null && !((NewChunks) this.module).loadedChunks.contains(chunk)) {
               ((NewChunks) this.module).loadedChunks.add(chunk);
               chunks.add(chunk);
            }
         }
      }

      return chunks;
   }
}
