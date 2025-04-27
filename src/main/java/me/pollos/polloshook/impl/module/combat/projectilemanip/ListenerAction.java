package me.pollos.polloshook.impl.module.combat.projectilemanip;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;

public class ListenerAction extends SafeModuleListener<ProjectileManip, PacketEvent.Send<PlayerActionC2SPacket>> {
   public ListenerAction(ProjectileManip module) {
      super(module, PacketEvent.Send.class, PlayerActionC2SPacket.class);
   }

   public void safeCall(PacketEvent.Send<PlayerActionC2SPacket> event) {
      Action action = ((PlayerActionC2SPacket)event.getPacket()).getAction();
      List<Item> list = Arrays.asList(Items.BOW, Items.CROSSBOW, Items.TRIDENT, Items.SPYGLASS); // Use items, once again I cant find shit for the according mappings.
      boolean valid = !list.contains(mc.player.getMainHandStack().getItem()) || !list.contains(mc.player.getOffHandStack().getItem());
      if (action == Action.RELEASE_USE_ITEM && valid && ((ProjectileManip)this.module).isValidItemBothHands()) {
         if (!((ProjectileManip)this.module).timer.passed((double)((Float)((ProjectileManip)this.module).delay.getValue() * 1000.0F))) {
            return;
         }

         ((ProjectileManip)this.module).send((Float)((ProjectileManip)this.module).delay.getValue() / 2.0F);
      }

   }
}