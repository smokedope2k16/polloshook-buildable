package me.pollos.polloshook.impl.module.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.render.RenderWorldTailEvent;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class ListenerRenderWorld extends ModuleListener<Shader, RenderWorldTailEvent> {
   public ListenerRenderWorld(Shader module) {
      super(module, RenderWorldTailEvent.class);
   }

   public void call(RenderWorldTailEvent event) {
      if (!Managers.getShaderManager().nullCheck()) {
         Quaternionf quaternionf = mc.gameRenderer.getCamera().getRotation().conjugate(new Quaternionf());
         Matrix4f matrix4f2 = (new Matrix4f()).rotation(quaternionf);
         if ((Boolean)((Shader)this.module).hand.getValue() && ((Shader)this.module).renderHand) {
            RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
            ((Shader)this.module).renderHandShader(event.getCounter().getTickDelta(true), matrix4f2);
         }

         ((Shader)this.module).renderHand = true;
      }
   }
}
