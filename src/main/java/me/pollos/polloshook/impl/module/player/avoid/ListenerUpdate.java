package me.pollos.polloshook.impl.module.player.avoid;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.block.Blocks;

public class ListenerUpdate extends ModuleListener<Avoid, UpdateEvent> {
   public ListenerUpdate(Avoid module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if ((Boolean)((Avoid)this.module).voids.getValue()) {
         double y = (Boolean)((Avoid)this.module).legacyVoid.getValue() ? 0.0D : (double)mc.world.getBottomY();
         boolean correct = mc.player.getY() < y && mc.player.getY() > y - 10.0D;
         boolean voidAir = mc.world.getBlockState(mc.player.getBlockPos()).getBlock().equals(Blocks.AIR);
         if (correct || voidAir && mc.player.getY() > y) {
            PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.1D, mc.player.getZ(), true);
            if (mc.player.input.jumping || mc.player.input.sneaking) {
               return;
            }

            mc.player.setVelocity(mc.player.getVelocity().subtract(0.0D, 0.05D, 0.0D));
         }
      }

   }
}