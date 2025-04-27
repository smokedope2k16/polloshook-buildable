package me.pollos.polloshook.impl.module.player.fastbreak;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.RenderMode;
import net.minecraft.block.BlockState;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public class ListenerRender extends ModuleListener<FastBreak, RenderEvent> {
   private VoxelShape cacheShape = null;

   public ListenerRender(FastBreak module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (((FastBreak)this.module).pos != null && mc.world != null) {
         MatrixStack matrix = event.getMatrixStack();
         matrix.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         this.renderAttackPos(event);
         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         matrix.pop();
      }
   }

   private void renderAttackPos(RenderEvent event) {
      MatrixStack matrix = event.getMatrixStack();
      BlockState state = mc.world.getBlockState(((FastBreak)this.module).pos);
      VoxelShape shape = state.getCollisionShape(mc.world, ((FastBreak)this.module).pos);
      if (!shape.isEmpty()) {
         this.cacheShape = shape;
      }

      if (this.cacheShape != null && !this.cacheShape.isEmpty()) {
         float currentDamage = (Float)((FastBreak)this.module).damage.getValue();
         Iterator var6 = this.cacheShape.getBoundingBoxes().iterator();

         while(var6.hasNext()) {
            Box box = (Box)var6.next();
            Box bb = Interpolation.interpolateAxis(box).offset(((FastBreak)this.module).pos);
            float damage = Math.min(((FastBreak)this.module).maxDamage, currentDamage);
            if (damage < currentDamage) {
               float var10000;
               switch((RenderMode)((FastBreak)this.module).renderMode.getValue()) {
               case SHRINK:
                  var10000 = -(currentDamage - damage);
                  break;
               case GROW:
                  var10000 = -(currentDamage / 2.0F) + damage / 2.0F;
                  break;
               default:
                  var10000 = 0.0F;
               }

               float expand = var10000;
               bb = bb.expand((double)expand);
            }

            if (Interpolation.isVisible(bb, event)) {
               if (!mc.world.getBlockState(((FastBreak)this.module).pos).isAir()) {
                  RenderMethods.drawBox(matrix, bb, ColorUtil.changeAlpha(((FastBreak)this.module).getProgressColor(), (Integer)((FastBreak)this.module).boxAlpha.getValue()));
               }

               RenderMethods.drawOutlineBox(matrix, bb, ColorUtil.changeAlpha(((FastBreak)this.module).getProgressColor(), (Integer)((FastBreak)this.module).outlineAlpha.getValue()), (Float)((FastBreak)this.module).lineWidth.getValue());
            }
         }

      }
   }
}
