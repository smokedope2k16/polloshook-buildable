package me.pollos.polloshook.impl.manager.minecraft.movement;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PositionManager extends SubscriberImpl implements Minecraftable {
   private volatile double last_x;
   private volatile double last_y;
   private volatile double last_z;
   private volatile double last_yaw;
   private volatile double last_pitch;
   private volatile boolean onGround;
   private volatile boolean sneaking;
   private volatile boolean sprinting;

   public PositionManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<PlayerPositionLookS2CPacket>>(PacketEvent.Receive.class, Integer.MIN_VALUE, PlayerPositionLookS2CPacket.class) {
         public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
            PlayerEntity player = mc.player;
            if (player != null) {
               PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)event.getPacket();
               double x = packet.getX();
               double y = packet.getY();
               double z = packet.getZ();
               double pitch = (double)packet.getPitch();
               double yaw = (double)packet.getYaw();
               if (packet.getFlags().contains(PositionFlag.X)) {
                  x += player.getX();
               }

               if (packet.getFlags().contains(PositionFlag.Y)) {
                  y += player.getY();
               }

               if (packet.getFlags().contains(PositionFlag.Z)) {
                  z += player.getZ();
               }

               PositionManager.this.last_x = MathHelper.clamp(x, -3.0E7D, 3.0E7D);
               PositionManager.this.last_y = y;
               PositionManager.this.last_z = MathHelper.clamp(z, -3.0E7D, 3.0E7D);
               PositionManager.this.last_pitch = pitch;
               PositionManager.this.last_yaw = yaw;
               PositionManager.this.onGround = false;
            }
         }
      });
      this.listeners.add(new Listener<PacketEvent.Post<PositionAndOnGround>>(PacketEvent.Post.class, Integer.MIN_VALUE, PositionAndOnGround.class) {
         public void call(PacketEvent.Post<PositionAndOnGround> event) {
            PositionManager.this.readCPacket((PlayerMoveC2SPacket)event.getPacket());
         }
      });
      this.listeners.add(new Listener<PacketEvent.Post<Full>>(PacketEvent.Post.class, Integer.MIN_VALUE, Full.class) {
         public void call(PacketEvent.Post<Full> event) {
            PositionManager.this.readCPacket((PlayerMoveC2SPacket)event.getPacket());
            PositionManager.this.readCPacketRotation((PlayerMoveC2SPacket)event.getPacket());
         }
      });
      this.listeners.add(new Listener<PacketEvent.Post<LookAndOnGround>>(PacketEvent.Post.class, Integer.MIN_VALUE, LookAndOnGround.class) {
         public void call(PacketEvent.Post<LookAndOnGround> event) {
            PositionManager.this.readCPacketRotation((PlayerMoveC2SPacket)event.getPacket());
         }
      });
      this.listeners.add(new Listener<PacketEvent.Send<ClientCommandC2SPacket>>(PacketEvent.Send.class, Integer.MAX_VALUE, ClientCommandC2SPacket.class) {
         public void call(PacketEvent.Send<ClientCommandC2SPacket> event) {
            switch(((ClientCommandC2SPacket)event.getPacket()).getMode()) {
            case START_SPRINTING:
               PositionManager.this.sprinting = true;
               break;
            case STOP_SPRINTING:
               PositionManager.this.sprinting = false;
               break;
            case PRESS_SHIFT_KEY:
               PositionManager.this.sneaking = true;
               break;
            case RELEASE_SHIFT_KEY:
               PositionManager.this.sneaking = false;
               default:
                  break;
            }

         }
      });
   }

   public double getX() {
      return this.last_x;
   }

   public double getY() {
      return this.last_y;
   }

   public double getZ() {
      return this.last_z;
   }

   public double getYaw() {
      return this.last_yaw;
   }

   public double getPitch() {
      return this.last_pitch;
   }

   public Box getBB() {
      double x = this.last_x;
      double y = this.last_y;
      double z = this.last_z;
      float w = mc.player.getWidth() / 2.0F;
      float h = mc.player.getHeight();
      return new Box(x - (double)w, y, z - (double)w, x + (double)w, y + (double)h, z + (double)w);
   }

   public void readCPacket(PlayerMoveC2SPacket packetIn) {
      PlayerEntity player = mc.player;
      if (player != null) {
         this.last_x = packetIn.getX(mc.player.getX());
         this.last_y = packetIn.getY(mc.player.getY());
         this.last_z = packetIn.getZ(mc.player.getZ());
         this.onGround = packetIn.isOnGround();
      }
   }

   private void readCPacketRotation(PlayerMoveC2SPacket packetIn) {
      this.last_yaw = (double)packetIn.getYaw(mc.player.getYaw());
      this.last_pitch = (double)packetIn.getPitch(mc.player.getPitch());
      this.onGround = packetIn.isOnGround();
   }

   public Vec3d getVec() {
      return new Vec3d(this.last_x, this.last_y, this.last_z);
   }

   
   public boolean isOnGround() {
      return this.onGround;
   }

   
   public boolean isSneaking() {
      return this.sneaking;
   }

   
   public boolean isSprinting() {
      return this.sprinting;
   }
}