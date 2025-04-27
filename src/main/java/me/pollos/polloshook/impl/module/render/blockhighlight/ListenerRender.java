package me.pollos.polloshook.impl.module.render.blockhighlight;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.RenderMode;
import me.pollos.polloshook.impl.module.player.liquidinteract.LiquidInteract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public class ListenerRender extends ModuleListener<BlockHighlight, RenderEvent> {
   public ListenerRender(BlockHighlight module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      HitResult var3 = mc.crosshairTarget;
      if (var3 instanceof BlockHitResult) {
         BlockHitResult result = (BlockHitResult)var3;
         if (!(Boolean)((BlockHighlight)this.module).noBlockOutline.getValue()) {
            BlockPos pos = result.getBlockPos();
            Block block = BlockUtil.getBlock(pos);
            MatrixStack matrix = event.getMatrixStack();
            AutoCrystal AUTO_CRYSTAL = (AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class);
            boolean autoCrystalCheck = AUTO_CRYSTAL.isEnabled() && AUTO_CRYSTAL.getRender() != null && result.getBlockPos().equals(AUTO_CRYSTAL.getRender().getPos());
            boolean alreadyBoxed = result.getBlockPos().equals(((FastBreak)Managers.getModuleManager().get(FastBreak.class)).getPos()) && ((FastBreak)Managers.getModuleManager().get(FastBreak.class)).getRenderMode() == RenderMode.STATIC || autoCrystalCheck;
            if (!block.getDefaultState().isAir() && (!(block instanceof FluidBlock) || ((LiquidInteract)Managers.getModuleManager().get(LiquidInteract.class)).isEnabled()) && !alreadyBoxed) {
               ShapeContext context = ShapeContext.of(mc.getCameraEntity());
               BlockState state = BlockUtil.getState(pos);
               VoxelShape shape = state.getOutlineShape(mc.world, pos, context);
               if (!shape.isEmpty()) {
                  matrix.push();
                  RenderMethods.enable3D();
                  MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
                  Framebuffer framebuffer = mc.getFramebuffer();
                  MSAAFramebuffer.start(smoothBuffer, framebuffer);
                  if ((Boolean)((BlockHighlight)this.module).fill.getValue()) {
                     shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                        RenderMethods.drawBox(matrix, Interpolation.interpolateAxis(new Box((double)((float)((double)pos.getX() + minX)), (double)((float)((double)pos.getY() + minY)), (double)((float)((double)pos.getZ() + minZ)), (double)((float)((double)pos.getX() + maxX)), (double)((float)((double)pos.getY() + maxY)), (double)((float)((double)pos.getZ() + maxZ)))), ((BlockHighlight)this.module).fillColor.getColor());
                     });
                  }

                  shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                     RenderMethods.drawOutlineBox(matrix, Interpolation.interpolateAxis(new Box((double)((float)((double)pos.getX() + minX)), (double)((float)((double)pos.getY() + minY)), (double)((float)((double)pos.getZ() + minZ)), (double)((float)((double)pos.getX() + maxX)), (double)((float)((double)pos.getY() + maxY)), (double)((float)((double)pos.getZ() + maxZ)))), ((BlockHighlight)this.module).outlineColor.getColor(), (Float)((BlockHighlight)this.module).lineWidth.getValue());
                  });
                  MSAAFramebuffer.end(smoothBuffer, framebuffer);
                  RenderMethods.disable3D();
                  matrix.pop();
               }
            }
         }
      }
   }
}
