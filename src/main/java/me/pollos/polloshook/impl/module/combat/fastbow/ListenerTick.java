package me.pollos.polloshook.impl.module.combat.fastbow;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ListenerTick extends SafeModuleListener<FastBow, TickEvent> {
   public ListenerTick(FastBow module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (PlayerUtil.isUsingBow()) {
         float tpsSync = (Boolean)((FastBow)this.module).tpsSync.getValue() ? 20.0F - Managers.getTpsManager().getCurrentTps() : 0.0F;
         if ((float)mc.player.getItemUseTime() + tpsSync > (float)(Integer)((FastBow)this.module).ticks.getValue()) {
            PacketUtil.send(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            mc.player.stopUsingItem();
         }
      }

   }
}