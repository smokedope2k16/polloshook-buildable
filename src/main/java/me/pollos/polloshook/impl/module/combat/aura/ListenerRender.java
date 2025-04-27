package me.pollos.polloshook.impl.module.combat.aura;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<Aura, RenderEvent> {
   public ListenerRender(Aura module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      MatrixStack matrix = event.getMatrixStack();
      if (((Aura)this.module).target != null) {
         if ((Boolean)((Aura)this.module).render.getValue()) {
            if (!((Aura)this.module).canAttack()) {
               return;
            }

            matrix.push();
            RenderMethods.enable3D();
            MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
            Framebuffer framebuffer = mc.getFramebuffer();
            MSAAFramebuffer.start(smoothBuffer, framebuffer);
            Vec3d vec = Interpolation.interpolateEntity(((Aura)this.module).target);
            Box entityBox = Interpolation.getInterpolatedBox(((Aura)this.module).target, vec);
            Box entityBB = ((Aura)this.module).target.getBoundingBox();
            Box box = new Box(entityBox.minX, entityBox.minY, entityBox.minZ, entityBox.minX + entityBB.getLengthX(), entityBox.minY + entityBB.getLengthY(), entityBox.minZ + entityBB.getLengthZ());
            RenderMethods.drawBox(matrix, box, ColorUtil.changeAlpha(Colours.get().getColor(), 45));
            MSAAFramebuffer.end(smoothBuffer, framebuffer);
            RenderMethods.disable3D();
            matrix.pop();
         }

      }
   }
}
