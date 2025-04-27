package me.pollos.polloshook.impl.module.render.newchunks;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.newchunks.util.ChunkData;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public class ListenerRender extends ModuleListener<NewChunks, RenderEvent> {
   public ListenerRender(NewChunks module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (!((NewChunks)this.module).chunkDataList.isEmpty()) {
         MatrixStack matrix = event.getMatrixStack();
         matrix.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         Iterator var5 = ((NewChunks)this.module).chunkDataList.iterator();

         while(var5.hasNext()) {
            ChunkData chunkData = (ChunkData)var5.next();
            if (chunkData != null) {
               int posX = chunkData.getX() * 16;
               int posY = mc.world.getRegistryKey().getValue().getPath().equals("overworld") ? -64 : 0;
               int posZ = chunkData.getZ() * 16;
               Box bb = new Box((double)posX, (double)posY, (double)posZ, (double)posX + 16.0D, (double)posY, (double)posZ + 16.0D);
               Box chunkBB = bb.offset(-Interpolation.getCameraPos().x, -Interpolation.getCameraPos().y, -Interpolation.getCameraPos().z);
               if (!(Interpolation.getCameraPos().distanceTo(chunkBB.getCenter()) > 250.0D) && Interpolation.isVisible(chunkBB, event)) {
                  RenderMethods.drawCrossBox(matrix, chunkBB, Colours.get().getColor(), 1.5F, false);
               }
            }
         }

         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         matrix.pop();
      }
   }
}
