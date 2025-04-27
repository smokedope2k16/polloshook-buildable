package me.pollos.polloshook.impl.module.misc.swing;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.swing.modes.CancelSwing;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

public class ListenerUseItem extends ModuleListener<Swing, PacketEvent.Send<PlayerInteractItemC2SPacket>> {
   private final List<Item> itemList;

   public ListenerUseItem(Swing module) {
      super(module, PacketEvent.Send.class, PlayerInteractItemC2SPacket.class);
      this.itemList = Arrays.asList(Items.POTION, Items.ENDER_PEARL, Items.SPLASH_POTION, Items.LINGERING_POTION);
   }

   public void call(PacketEvent.Send<PlayerInteractItemC2SPacket> event) {
      PlayerInteractItemC2SPacket packet = (PlayerInteractItemC2SPacket)event.getPacket();
      Item item = mc.player.getStackInHand(packet.getHand()).getItem();
      if (((Swing)this.module).noSwing.getValue() != CancelSwing.NONE && this.itemList.contains(item)) {
         ((Swing)this.module).cancelSwing = true;
      }

   }
}