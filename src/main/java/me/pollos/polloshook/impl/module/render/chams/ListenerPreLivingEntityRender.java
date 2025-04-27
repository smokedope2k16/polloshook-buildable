package me.pollos.polloshook.impl.module.render.chams;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderLivingEntityEvent;
import me.pollos.polloshook.impl.module.render.chams.util.ChamsType;
import me.pollos.polloshook.impl.module.render.skeleton.util.CacheConsumerProvider;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerPreLivingEntityRender extends ModuleListener<Chams, RenderLivingEntityEvent.Pre> {
   public ListenerPreLivingEntityRender(Chams module) {
      super(module, RenderLivingEntityEvent.Pre.class);
   }

   public void call(RenderLivingEntityEvent.Pre event) {
      LivingEntity var3 = event.getLivingEntity();
      if (var3 instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)var3;
         CacheConsumerProvider provider = new CacheConsumerProvider();
         provider.getCacheConsumer().start();
         RenderMethods.drawEntity(event.getMatrix(), player, mc.getRenderTickCounter().getTickDelta(true), provider, false);
         if (((Chams)this.module).vertexes.containsKey(player)) {
            ((Chams)this.module).vertexes.replace(player, provider);
         } else {
            ((Chams)this.module).vertexes.put(player, provider);
         }
      }

      if (((Chams)this.module).players.getParent().isVisible()) {
         if (((Chams)this.module).players.getValue() != ChamsType.WIRE_FRAME && ((Chams)this.module).players.getValue() != ChamsType.OFF) {
            if (!((Chams)this.module).stop) {
               LivingEntity entity = event.getLivingEntity();
               Tessellator tessellator = Tessellator.getInstance();
               if (entity instanceof PlayerEntity) {
                  int overlay = LivingEntityRenderer.getOverlay(entity, 0.0F);
                  RenderSystem.enableBlend();
                  RenderSystem.defaultBlendFunc();
                  RenderSystem.disableCull();
                  if ((Boolean)((Chams)this.module).xqz.getValue()) {
                     RenderMethods.color(((Chams)this.module).getXQZColor(entity).getRGB());
                     RenderSystem.depthMask(false);
                     RenderSystem.disableDepthTest();
                     this.renderChams(tessellator, event, overlay);
                  }

                  RenderMethods.color(((Chams)this.module).getVisibleColor(entity).getRGB());
                  RenderSystem.depthMask(true);
                  RenderSystem.enableDepthTest();
                  this.renderChams(tessellator, event, overlay);
                  RenderMethods.resetColor();
                  RenderSystem.disableBlend();
                  RenderSystem.enableCull();
                  event.setCanceled(true);
               }

            }
         }
      }
   }

   private void renderChams(Tessellator tessellator, RenderLivingEntityEvent event, int overlay) {
      RenderSystem.setShader(GameRenderer::getPositionProgram);
      BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION);
      event.getModel().render(event.getMatrix(), bufferBuilder, event.getLight(), overlay);
      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }
}
