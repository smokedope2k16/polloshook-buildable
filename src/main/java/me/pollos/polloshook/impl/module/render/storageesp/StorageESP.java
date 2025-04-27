package me.pollos.polloshook.impl.module.render.storageesp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.thread.ThreadUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public class StorageESP extends ToggleableModule {
   private final NumberValue<Float> distance = (new NumberValue(64.0F, 10.0F, 1000.0F, 5.0F, new String[]{"MaxDistance", "distance"})).withTag("range");
   protected List<BlockEntity> tileEntityList = new ArrayList();
   protected ExecutorService service;
   protected final StopWatch timer = new StopWatch();

   public StorageESP() {
      super(new String[]{"StorageESP", "chestesp"}, Category.RENDER);
      this.offerValues(new Value[]{this.distance});
      this.offerListeners(new Listener[]{new ListenerRender(this), new ListenerTick(this)});
      this.service = ThreadUtil.newDaemonScheduledExecutor("StorageESP");
   }

   public void onRender(RenderEvent event) {
      MatrixStack matrix = event.getMatrixStack();
      matrix.push();
      RenderMethods.enable3D();
      MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
      Framebuffer framebuffer = mc.getFramebuffer();
      MSAAFramebuffer.start(smoothBuffer, framebuffer);
      Iterator var5 = (new ArrayList(this.tileEntityList)).iterator();

      while(var5.hasNext()) {
         BlockEntity blockEntity = (BlockEntity)var5.next();
         if (!(BlockUtil.getDistanceSq(blockEntity.getPos()) > (double)MathUtil.square((Float)this.distance.getValue())) && !(blockEntity instanceof BedBlockEntity) && !(blockEntity instanceof SignBlockEntity) && !(blockEntity instanceof CampfireBlockEntity) && !(blockEntity instanceof BellBlockEntity) && !(blockEntity instanceof LecternBlockEntity)) {
            BlockPos pos = blockEntity.getPos();
            ShapeContext context = ShapeContext.of(Interpolation.getRenderEntity());
            BlockState state = blockEntity.getCachedState();
            VoxelShape shape = state.getOutlineShape(mc.world, pos, context);
            if (!shape.isEmpty()) {
               shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                  Box box = Interpolation.interpolateAxis(new Box((double)((float)((double)pos.getX() + minX)), (double)((float)((double)pos.getY() + minY)), (double)((float)((double)pos.getZ() + minZ)), (double)((float)((double)pos.getX() + maxX)), (double)((float)((double)pos.getY() + maxY)), (double)((float)((double)pos.getZ() + maxZ))));
                  if (Interpolation.isVisible(box, event)) {
                     RenderMethods.drawBox(matrix, box, ColorUtil.changeAlpha(this.getColor(blockEntity), 40));
                  }

               });
               shape.forEachEdge((minX, minY, minZ, maxX, maxY, maxZ) -> {
                  Box edge = Interpolation.interpolateAxis(new Box((double)((float)((double)pos.getX() + minX)), (double)((float)((double)pos.getY() + minY)), (double)((float)((double)pos.getZ() + minZ)), (double)((float)((double)pos.getX() + maxX)), (double)((float)((double)pos.getY() + maxY)), (double)((float)((double)pos.getZ() + maxZ))));
                  if (Interpolation.isVisible(edge, event)) {
                     RenderMethods.drawOutlineBox(matrix, edge, ColorUtil.changeAlpha(this.getColor(blockEntity), 180), 1.3F);
                  }

               });
            }
         }
      }

      MSAAFramebuffer.end(smoothBuffer, framebuffer);
      RenderMethods.disable3D();
      matrix.pop();
   }

   private Color getColor(BlockEntity blockEntity) {
      if (blockEntity instanceof ShulkerBoxBlockEntity) {
         ShulkerBoxBlockEntity shulkerBoxBlock = (ShulkerBoxBlockEntity)blockEntity;
         return shulkerBoxBlock.getColor() == null ? new Color(180, 80, 255) : new Color(shulkerBoxBlock.getColor().getEntityColor());
      } else if (blockEntity instanceof ChestBlockEntity) {
         return Color.YELLOW;
      } else if (blockEntity instanceof EnderChestBlockEntity) {
         return new Color(125, 0, 255);
      } else if (!(blockEntity instanceof FurnaceBlockEntity) && !(blockEntity instanceof HopperBlockEntity) && !(blockEntity instanceof DispenserBlockEntity)) {
         return blockEntity instanceof BarrelBlockEntity ? new Color(255, 160, 0) : Color.WHITE;
      } else {
         return Color.GRAY;
      }
   }
}
