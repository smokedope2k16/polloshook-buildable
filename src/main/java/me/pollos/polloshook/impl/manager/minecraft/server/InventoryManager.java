package me.pollos.polloshook.impl.manager.minecraft.server;

import java.util.concurrent.ConcurrentHashMap;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class InventoryManager extends SubscriberImpl implements Minecraftable {
   private volatile int slot;
   private volatile int lastSlot;
   private final StopWatch timer = new StopWatch();
   private final ConcurrentHashMap<Integer, ItemStack> inventory = new ConcurrentHashMap();

   public InventoryManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<ScreenHandlerSlotUpdateS2CPacket>>(PacketEvent.Receive.class, Integer.MAX_VALUE, ScreenHandlerSlotUpdateS2CPacket.class) {
         public void call(PacketEvent.Receive<ScreenHandlerSlotUpdateS2CPacket> event) {
            ScreenHandlerSlotUpdateS2CPacket packet = (ScreenHandlerSlotUpdateS2CPacket)event.getPacket();
            int slot = packet.getSlot();
            ItemStack stack = packet.getStack();
            if (packet.getSyncId() == 0 || packet.getSyncId() == -2) {
               if (InventoryManager.this.inventory.containsKey(slot)) {
                  InventoryManager.this.inventory.replace(slot, stack);
               } else {
                  InventoryManager.this.inventory.put(slot, stack);
               }
            }

         }
      });
      this.listeners.add(new Listener<PacketEvent.Send<UpdateSelectedSlotC2SPacket>>(PacketEvent.Send.class, Integer.MAX_VALUE, UpdateSelectedSlotC2SPacket.class) {
         public void call(PacketEvent.Send<UpdateSelectedSlotC2SPacket> event) {
            InventoryManager.this.lastSlot = InventoryManager.this.slot;
            InventoryManager.this.slot = ((UpdateSelectedSlotC2SPacket)event.getPacket()).getSelectedSlot();
            InventoryManager.this.timer.reset();
         }
      });
      this.listeners.add(new Listener<PacketEvent.Receive<UpdateSelectedSlotS2CPacket>>(PacketEvent.Receive.class, Integer.MAX_VALUE, UpdateSelectedSlotS2CPacket.class) {
         public void call(PacketEvent.Receive<UpdateSelectedSlotS2CPacket> event) {
            InventoryManager.this.lastSlot = InventoryManager.this.slot;
            InventoryManager.this.slot = ((UpdateSelectedSlotS2CPacket)event.getPacket()).getSlot();
         }
      });
   }

   public ItemStack getOffHandStack() {
      return (ItemStack)this.inventory.get(45);
   }

   public ItemStack getStackInSlot(int slot) {
      return (ItemStack)this.inventory.get(slot);
   }

   public boolean passed(long ms) {
      return this.timer.passed(ms);
   }

   
   public int getSlot() {
      return this.slot;
   }

   
   public int getLastSlot() {
      return this.lastSlot;
   }
}
