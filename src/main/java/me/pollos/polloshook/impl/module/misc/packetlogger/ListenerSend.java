package me.pollos.polloshook.impl.module.misc.packetlogger;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketRegistry;
import me.pollos.polloshook.asm.mixins.network.IPlayerMoveC2SPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.packetlogger.util.PacketType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

public class ListenerSend extends ModuleListener<PacketLogger, PacketEvent.Send<?>> {
   public ListenerSend(PacketLogger module) {
      super(module, PacketEvent.Send.class, Integer.MIN_VALUE);
   }

   public void call(PacketEvent.Send<?> event) {
      if (((PacketLogger)this.module).type.getValue() != PacketType.SERVER) {
         Packet packet;
         label68: {
            ((PacketLogger)this.module).initializeWriter();
            packet = event.getPacket();
            if (packet instanceof UpdateSelectedSlotC2SPacket) {
               UpdateSelectedSlotC2SPacket selectedSlotC2SPacket = (UpdateSelectedSlotC2SPacket)packet;
               if ((Boolean)((PacketLogger)this.module).updateSelectedSlotPacket.getValue()) {
                  this.debug("Slot: " + selectedSlotC2SPacket.getSelectedSlot(), event);
                  break label68;
               }
            }

            if (packet instanceof ClickSlotC2SPacket) {
               ClickSlotC2SPacket clickSlotC2SPacket = (ClickSlotC2SPacket)packet;
               if ((Boolean)((PacketLogger)this.module).clickSlotC2SPacket.getValue()) {
                  int var10001 = clickSlotC2SPacket.getSlot();
                  this.debug("Slot: " + var10001 + "\nActionType: " + String.valueOf(clickSlotC2SPacket.getActionType()) + "\nStackName: " + clickSlotC2SPacket.getStack().getName().getString() + "\nButton: " + clickSlotC2SPacket.getButton() + "\nSyncID: " + clickSlotC2SPacket.getSyncId() + "\nRevision: " + clickSlotC2SPacket.getRevision(), event);
                  break label68;
               }
            }

            if (packet instanceof HandSwingC2SPacket) {
               HandSwingC2SPacket handSwingC2SPacket = (HandSwingC2SPacket)packet;
               if ((Boolean)((PacketLogger)this.module).handSwingC2SPacket.getValue()) {
                  this.debug("HandName: " + handSwingC2SPacket.getHand().name(), event);
                  break label68;
               }
            }

            if (packet instanceof PlayerActionC2SPacket) {
               PlayerActionC2SPacket playerActionC2SPacket = (PlayerActionC2SPacket)packet;
               if ((Boolean)((PacketLogger)this.module).playerActionC2SPacket.getValue()) {
                  this.handlePlayerActionC2SPacket(playerActionC2SPacket, event);
                  break label68;
               }
            }

            if (packet instanceof PlayerMoveC2SPacket) {
               PlayerMoveC2SPacket playerMoveC2SPacket = (PlayerMoveC2SPacket)packet;
               if ((Boolean)((PacketLogger)this.module).playerMoveC2SPacket.getValue()) {
                  this.debugPlayerMoveC2S(playerMoveC2SPacket, event);
                  break label68;
               }
            }

            if (packet instanceof KeepAliveC2SPacket) {
               KeepAliveC2SPacket keepAliveC2SPacket = (KeepAliveC2SPacket)packet;
               if ((Boolean)((PacketLogger)this.module).keepAliveC2SPacket.getValue()) {
                  this.debug("ID: " + keepAliveC2SPacket.getId(), event);
                  break label68;
               }
            }

            if (packet instanceof RequestCommandCompletionsC2SPacket) {
               RequestCommandCompletionsC2SPacket requestCommandCompletionsC2SPacket = (RequestCommandCompletionsC2SPacket)packet;
               if ((Boolean)((PacketLogger)this.module).requestCommandCompletionsC2SPacket.getValue()) {
                  this.debug("PartCommand: " + requestCommandCompletionsC2SPacket.getPartialCommand() + "\n ID: " + requestCommandCompletionsC2SPacket.getCompletionId(), event);
               }
            }
         }

         if ((Boolean)((PacketLogger)this.module).logAll.getValue()) {
            if (PollosHook.isRunClient() && packet instanceof PlayerMoveC2SPacket) {
               return;
            }

            String name = PacketRegistry.getName(packet);
            this.debug("<PacketLogger.Send> " + name + ", canceled: " + event.isCanceled(), event);
         }

      }
   }

   private void handlePlayerActionC2SPacket(PlayerActionC2SPacket packet, PacketEvent.Send<?> event) {
      switch(packet.getAction()) {
      case DROP_ITEM:
         if (!(Boolean)((PacketLogger)this.module).dropItem.getValue()) {
            return;
         }
         break;
      case DROP_ALL_ITEMS:
         if (!(Boolean)((PacketLogger)this.module).dropAllItems.getValue()) {
            return;
         }
         break;
      case RELEASE_USE_ITEM:
         if (!(Boolean)((PacketLogger)this.module).releaseUseItem.getValue()) {
            return;
         }
         break;
      case STOP_DESTROY_BLOCK:
         if (!(Boolean)((PacketLogger)this.module).stopDestroyBlock.getValue()) {
            return;
         }
         break;
      case ABORT_DESTROY_BLOCK:
         if (!(Boolean)((PacketLogger)this.module).abortDestroyBlock.getValue()) {
            return;
         }
         break;
      case START_DESTROY_BLOCK:
         if (!(Boolean)((PacketLogger)this.module).startDestroyBlock.getValue()) {
            return;
         }
         break;
      case SWAP_ITEM_WITH_OFFHAND:
         if (!(Boolean)((PacketLogger)this.module).swapItemWithOffhand.getValue()) {
            return;
         }
      }

      this.debugPlayerActionC2S(packet, event);
   }

   private void debugPlayerActionC2S(PlayerActionC2SPacket packet, PacketEvent.Send<?> event) {
      String var10001 = String.valueOf(packet.getAction());
      this.debug("Action: " + var10001 + "\nPos: " + String.valueOf(packet.getPos()) + "\nDirection: " + String.valueOf(packet.getDirection()) + "\nSequence: " + packet.getSequence(), event);
   }

   private void debugPlayerMoveC2S(PlayerMoveC2SPacket packet, PacketEvent.Send<?> event) {
      IPlayerMoveC2SPacket iPacket = (IPlayerMoveC2SPacket)packet;
      if (packet instanceof OnGroundOnly && (Boolean)((PacketLogger)this.module).onGroundOnly.getValue()) {
         boolean var5 = packet.isOnGround();
         this.debug("OnGround: " + var5 + "\nChangesLook: " + packet.changesLook() + "\nChangesPosition: " + packet.changesPosition(), event);
      } else if (packet instanceof LookAndOnGround && (Boolean)((PacketLogger)this.module).lookAndOnGround.getValue()) {
         float var4 = packet.getYaw(mc.player.getYaw());
         this.debug("Yaw: " + var4 + " (Raw: " + iPacket.getRawYaw() + ")\nPitch: " + packet.getPitch(mc.player.getPitch()) + " (Raw: " + iPacket.getRawPitch() + ")\nOnGround: " + packet.isOnGround() + "\nChangesLook: " + packet.changesLook() + "\nChangesPosition: " + packet.changesPosition(), event);
      } else {
         double var10001;
         if (packet instanceof PositionAndOnGround && (Boolean)((PacketLogger)this.module).positionAndOnGround.getValue()) {
            var10001 = packet.getX(mc.player.getX());
            this.debug("X: " + var10001 + " (Raw: " + iPacket.getRawX() + ")\nY: " + packet.getY(mc.player.getY()) + " (Raw: " + iPacket.getRawY() + ")\nZ: " + packet.getZ(mc.player.getZ()) + " (Raw: " + iPacket.getRawZ() + ")\nOnGround: " + packet.isOnGround() + "\nChangesLook: " + packet.changesLook() + "\nChangesPosition: " + packet.changesPosition(), event);
         } else if (packet instanceof Full && (Boolean)((PacketLogger)this.module).full.getValue()) {
            var10001 = packet.getX(mc.player.getX());
            this.debug("X: " + var10001 + " (Raw: " + iPacket.getRawX() + ")\nY: " + packet.getY(mc.player.getY()) + " (Raw: " + iPacket.getRawY() + ")\nZ: " + packet.getZ(mc.player.getZ()) + " (Raw: " + iPacket.getRawZ() + ")\nYaw: " + packet.getYaw(mc.player.getYaw()) + " (Raw: " + iPacket.getRawYaw() + ")\nPitch: " + packet.getPitch(mc.player.getPitch()) + " (Raw: " + iPacket.getRawPitch() + ")\nOnGround: " + packet.isOnGround() + "\nChangesLook: " + packet.changesLook() + "\nChangesPosition: " + packet.changesPosition(), event);
         }
      }

   }

   private void debug(String str, PacketEvent.Send<?> event) {
      ((PacketLogger)this.module).debug(str, event);
   }
}