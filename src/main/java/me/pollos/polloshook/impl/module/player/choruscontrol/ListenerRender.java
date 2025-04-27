package me.pollos.polloshook.impl.module.player.choruscontrol;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<ChorusControl, RenderEvent> {
   public ListenerRender(ChorusControl module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (((ChorusControl)this.module).packet != null) {
         PlayerPositionLookS2CPacket packet = ((ChorusControl)this.module).packet;
         MatrixStack matrix = event.getMatrixStack();
         Vec3d vec = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
         Box box = Interpolation.interpolateAxis(PlayerEntity.STANDING_DIMENSIONS.getBoxAt(vec));
         matrix.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         RenderMethods.drawOutlineBox(matrix, box, Colours.get().getColor(), 1.3F);
         RenderMethods.drawBox(matrix, box, Colours.get().getColourCustomAlpha(65));
         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         matrix.pop();
      }

   }
}
