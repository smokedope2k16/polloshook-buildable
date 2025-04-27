package me.pollos.polloshook.impl.module.render.nametags;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.asm.ducks.world.IGameRenderer;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<Nametags, RenderEvent> {
   public ListenerRender(Nametags module) {
      super(module, RenderEvent.class, Integer.MIN_VALUE);
   }

   public void call(RenderEvent event) {
      ((Nametags)this.module).ignore = true;
      RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
      ((IGameRenderer)mc.gameRenderer).getRenderHand(event.getCamera(), event.getTickDelta(), event.getPositionMatrix());
      ((Nametags)this.module).ignore = false;
      RenderSystem.setProjectionMatrix(event.getProjectionMatrix(), VertexSorter.BY_DISTANCE);
      DiffuseLighting.disableGuiDepthLighting();
      RenderSystem.enablePolygonOffset();
      RenderSystem.polygonOffset(1.0F, -1500000.0F);
      ((Nametags)this.module).playerEntities.forEach((p) -> {
         this.renderNametag(p, event);
      });
      RenderSystem.polygonOffset(1.0F, 1500000.0F);
      RenderSystem.disablePolygonOffset();
   }

   private void renderNametag(PlayerEntity player, RenderEvent event) {
      Vec3d vec = Interpolation.interpolateEntity(player);
      if (Interpolation.isVisible(Interpolation.interpolateAxis(player.getBoundingBox().expand(0.5D)), event)) {
         ((Nametags)this.module).renderNametags(event.getMatrixStack(), player, vec.x, vec.y, vec.z, Interpolation.getMcPlayerInterpolation());
      }

   }
}
