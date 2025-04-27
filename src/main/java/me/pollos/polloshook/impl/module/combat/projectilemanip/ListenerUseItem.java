package me.pollos.polloshook.impl.module.combat.projectilemanip;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

public class ListenerUseItem extends ModuleListener<ProjectileManip, PacketEvent.Send<PlayerInteractItemC2SPacket>> {
   public ListenerUseItem(ProjectileManip module) {
      super(module, PacketEvent.Send.class, PlayerInteractItemC2SPacket.class);
   }

   public void call(PacketEvent.Send<PlayerInteractItemC2SPacket> event) {
      if (((ProjectileManip)this.module).timer.passed((double)((Float)((ProjectileManip)this.module).delay.getValue() * 1000.0F))) {
         Item item = mc.player.getStackInHand(((PlayerInteractItemC2SPacket)event.getPacket()).getHand()).getItem();
         List<Item> list = Arrays.asList(Items.ENDER_PEARL, Items.SNOWBALL);
         boolean valid = list.contains(item);
         if (valid && ((ProjectileManip)this.module).isValidItemBothHands()) {
            ((ProjectileManip)this.module).send((Float)((ProjectileManip)this.module).delay.getValue());
         }

      }
   }
}