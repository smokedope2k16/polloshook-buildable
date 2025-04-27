package me.pollos.polloshook.impl.module.misc.packetlogger;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketRegistry;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.packetlogger.util.PacketType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;

public class ListenerReceive extends ModuleListener<PacketLogger, PacketEvent.Receive<?>> {
   public ListenerReceive(PacketLogger module) {
      super(module, PacketEvent.Receive.class, Integer.MIN_VALUE);
   }

   public void call(PacketEvent.Receive<?> event) {
      if (((PacketLogger)this.module).type.getValue() != PacketType.CLIENT) {
         Packet packet;
         label49: {
            packet = event.getPacket();
            if (packet instanceof BlockUpdateS2CPacket) {
               BlockUpdateS2CPacket updateS2CPacket = (BlockUpdateS2CPacket)packet;
               if ((Boolean)((PacketLogger)this.module).blockUpdateS2CPacket.getValue()) {
                  this.debug("BlockPos: " + updateS2CPacket.getPos().toString() + "\nBlockState: " + updateS2CPacket.getState().getBlock().getName().getString(), event);
                  break label49;
               }
            }

            if (packet instanceof KeepAliveS2CPacket) {
               KeepAliveS2CPacket keepAliveS2CPacket = (KeepAliveS2CPacket)packet;
               if ((Boolean)((PacketLogger)this.module).keepAliveS2CPacket.getValue()) {
                  this.debug("ID: " + keepAliveS2CPacket.getId(), event);
                  break label49;
               }
            }

            if (packet instanceof CommandSuggestionsS2CPacket) {
               CommandSuggestionsS2CPacket commandSuggestionsS2CPacket = (CommandSuggestionsS2CPacket)packet;
               if ((Boolean)((PacketLogger)this.module).commandSuggestionsS2CPacket.getValue()) {
                  this.debug("ID: " + String.valueOf(commandSuggestionsS2CPacket.getPacketId()) + "\n Suggestions: " + commandSuggestionsS2CPacket.getSuggestions().toString(), event);
                  break label49;
               }
            }

            if (packet instanceof PlayerActionResponseS2CPacket) {
               PlayerActionResponseS2CPacket playerActionResponseS2CPacket = (PlayerActionResponseS2CPacket) packet;
               int sequence;
               try {
                   sequence = playerActionResponseS2CPacket.sequence();
               } catch (Throwable t) {
                   throw new MatchException(t.toString(), t);
               }
           
               if ((Boolean)((PacketLogger)this.module).playerActionResponsePacket.getValue()) {
                   this.debug("Sequence: " + sequence, event);
               }
           }
           
         }

         if ((Boolean)((PacketLogger)this.module).logAll.getValue()) {
            String name = PacketRegistry.getName(packet);
            this.debug("<PacketLogger.Receive> " + name + ", canceled: " + event.isCanceled(), event);
         }

      }
   }

   private void debug(String str, PacketEvent.Receive<?> event) {
      ((PacketLogger)this.module).debug(str, event);
   }
}
