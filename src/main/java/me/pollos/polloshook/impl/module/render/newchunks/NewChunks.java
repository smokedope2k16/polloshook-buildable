package me.pollos.polloshook.impl.module.render.newchunks;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.impl.module.render.newchunks.util.ChunkData;
import net.minecraft.world.chunk.WorldChunk;

public class NewChunks extends ToggleableModule {
   protected final List<ChunkData> chunkDataList = new ArrayList();
   protected final List<WorldChunk> loadedChunks = new ArrayList();

   public NewChunks() {
      super(new String[]{"NewChunks", "newchunk"}, Category.RENDER);
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerRender(this)});
   }

   protected void onToggle() {
      this.chunkDataList.clear();
      this.loadedChunks.clear();
   }
}
