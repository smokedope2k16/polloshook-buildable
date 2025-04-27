package me.pollos.polloshook.impl.module.render.holeesp;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ListenerTick extends SafeModuleListener<HoleESP, TickEvent> {
   public ListenerTick(HoleESP module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (((HoleESP)this.module).timer.passed((long)(Integer)((HoleESP)this.module).updates.getValue())) {
         ((HoleESP)this.module).service.submit(() -> {
            if ((Boolean)((HoleESP)this.module).voidHole.getValue()) {
               ((HoleESP)this.module).voidHoles = this.getVoidHoles();
            }

            ((HoleESP)this.module).holes = HoleUtil.getHoles(Interpolation.getRenderEntity(), (float)(Integer)((HoleESP)this.module).range.getValue(), (float)(Integer)((HoleESP)this.module).range.getValue(), (Boolean)((HoleESP)this.module).doubles.getValue(), false, (Boolean)((HoleESP)this.module).terrain.getValue());
         });
         ((HoleESP)this.module).timer.reset();
      }

   }

   private List<BlockPos> getVoidHoles() {
      Vec3d camera = Interpolation.getCameraPos();
      BlockPos playerPos = BlockPos.ofFloored(camera.x, (double)this.getMinY(), camera.z);
      List<BlockPos> positions = new ArrayList(BlockUtil.getCircle(playerPos, (float)(Integer)((HoleESP)this.module).voidRange.getValue()).stream().filter(this::isVoid).toList());
      positions.removeIf((pos) -> {
         return BlockUtil.getDistanceSq(Interpolation.getRenderEntity(), pos) > (double)MathUtil.square((float)(Integer)((HoleESP)this.module).voidRange.getValue());
      });
      return positions;
   }

   private boolean isVoid(BlockPos pos) {
      return (mc.world.getBlockState(pos).getBlock() == Blocks.VOID_AIR || mc.world.getBlockState(pos).getBlock() != Blocks.ANVIL)
             && pos.getY() < this.getMinY() + 1 && pos.getY() >= this.getMinY();
   }
   

   private int getMinY() {
      return !mc.world.getRegistryKey().getValue().getPath().equals("overworld") ? 0 : ((Boolean)((HoleESP)this.module).legacyVoid.getValue() ? 0 : -64);
   }
}
