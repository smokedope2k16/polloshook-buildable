package me.pollos.polloshook.api.minecraft.network;

import java.time.Instant;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.asm.ducks.util.IClientConnection;
import me.pollos.polloshook.asm.ducks.util.IClientPlayerNetworkManager;
import me.pollos.polloshook.asm.ducks.world.IClientWorld;
import me.pollos.polloshook.asm.mixins.network.IPlayerInteractEntityC2SPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.encryption.NetworkEncryptionUtils.SecureRandomUtil;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.LastSeenMessagesCollector.LastSeenMessages;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.Hand;

public class PacketUtil implements Minecraftable {
   public static void send(Packet<?> packet) {
      ClientPlayNetworkHandler connection = mc.getNetworkHandler();
      if (connection != null && packet != null) {
         connection.sendPacket(packet);
      }

   }

   public static int incrementSequence() {
      PendingUpdateManager pendingUpdateManager = ((IClientWorld)mc.world).getPendingUpdateManager().incrementSequence();
      int i = pendingUpdateManager.getSequence();
      pendingUpdateManager.close();
      return i;
   }

   public static Packet<?> sendNoEvent(Packet<?> packet) {
      ClientPlayNetworkHandler connection = mc.getNetworkHandler();
      if (connection != null) {
         IClientConnection access = (IClientConnection)connection.getConnection();
         return access.sendPacketNoEvent(packet);
      } else {
         return null;
      }
   }

   public static void sendChatMessageNoEvent(String string) {
      Instant instant = Instant.now();
      long l = SecureRandomUtil.nextLong();
      IClientPlayerNetworkManager access = (IClientPlayerNetworkManager)mc.getNetworkHandler();
      LastSeenMessages lastSeenMessages = access.getLastSeenMessageCollector().collect();
      MessageSignatureData messageSignatureData = access.getMessageChainPacker().pack(new MessageBody(string, instant, l, lastSeenMessages.lastSeen()));
      sendNoEvent(new ChatMessageC2SPacket(string, instant, l, messageSignatureData, lastSeenMessages.update()));
   }

   public static void swing() {
      send(new HandSwingC2SPacket(Hand.MAIN_HAND));
   }

   public static void swing(Hand hand) {
      send(new HandSwingC2SPacket(hand));
   }

   public static void sneak(boolean startSneaking) {
      send(new ClientCommandC2SPacket(mc.player, startSneaking ? Mode.PRESS_SHIFT_KEY : Mode.RELEASE_SHIFT_KEY));
      ((IClientPlayerEntity)mc.player).setLastSneaking(startSneaking);
   }

   public static void sprint(boolean startSprinting) {
      send(new ClientCommandC2SPacket(mc.player, startSprinting ? Mode.START_SPRINTING : Mode.STOP_SPRINTING));
      ((IClientPlayerEntity)mc.player).setLastSprinting(startSprinting);
   }

   public static PlayerInteractEntityC2SPacket attackPacket(int id) {
      PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(mc.player, mc.player.isSneaking());
      ((IPlayerInteractEntityC2SPacket)packet).setEntityID(id);
      return packet;
   }

   public static PlayerInteractEntityC2SPacket attackPacket(Entity entity) {
      return PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking());
   }

   public static void move(double x, double y, double z, boolean ground) {
      send(getMove(x, y, z, ground));
   }

   public static PlayerMoveC2SPacket getMove(double x, double y, double z, boolean ground) {
      boolean isRotating = MovementUtil.isRotating();
      return (PlayerMoveC2SPacket)(isRotating ? new Full(x, y, z, ((IClientPlayerEntity)mc.player).getLastYaw(), ((IClientPlayerEntity)mc.player).getLastPitch(), ground) : new PositionAndOnGround(x, y, z, ground));
   }

   public static PlayerMoveC2SPacket getRotate(float[] rotations, boolean ground) {
      boolean isMoving = MovementUtil.isMovingChina(mc.player);
      return (PlayerMoveC2SPacket)(isMoving ? new Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), rotations[0], rotations[1], ground) : new LookAndOnGround(rotations[0], rotations[1], ground));
   }

   public static void rotate(float[] rotations, boolean ground) {
      boolean isMoving = MovementUtil.isMovingChina(mc.player);
      PlayerMoveC2SPacket packet = isMoving ? new Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), rotations[0], rotations[1], ground) : new LookAndOnGround(rotations[0], rotations[1], ground);
      send((Packet)packet);
   }
}