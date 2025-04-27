package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class ListenerInteractBlock extends ModuleListener<NoSlow, PacketEvent.Send<PlayerInteractBlockC2SPacket>> {
   public ListenerInteractBlock(NoSlow module) {
      super(module, PacketEvent.Send.class, PlayerInteractBlockC2SPacket.class);
   }

   public void call(PacketEvent.Send<PlayerInteractBlockC2SPacket> event) {
      if ((Boolean)((NoSlow)this.module).items.getValue()) {
         PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket)event.getPacket();
         Item item = mc.player.getStackInHand(packet.getHand()).getItem();
         boolean isValidItem = ItemUtil.isFood(item) || item instanceof BowItem || item instanceof PotionItem;
         if (isValidItem && (Boolean)((NoSlow)this.module).ncpStrict.getValue()) {
            PacketUtil.send(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
         }

      }
   }
}
