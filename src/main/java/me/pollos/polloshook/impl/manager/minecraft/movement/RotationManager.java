package me.pollos.polloshook.impl.manager.minecraft.movement;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.events.render.RenderRotationsEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;

public class RotationManager extends SubscriberImpl implements Minecraftable {
   private volatile float serverYaw;
   private volatile float serverPitch;
   private float yaw;
   private float pitch;
   private boolean rotated;
   private float renderYaw;
   private float renderPitch;
   private float rotationYawHead;
   private float renderBodyYaw;
   private float prevYaw;
   private float prevPitch;
   private float prevRenderBodyYaw;
   private float prevRotationYawHead;
   private int ticksSinceNoRotate;
   private int ticksExisted;
   private boolean isInv;

   public RotationManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<PlayerPositionLookS2CPacket>>(PacketEvent.Receive.class, Integer.MAX_VALUE, PlayerPositionLookS2CPacket.class) {
         public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
            if (mc.player != null) {
               PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)event.getPacket();
               float yaw = packet.getYaw();
               float pitch = packet.getPitch();
               if (packet.getFlags().contains(PositionFlag.Y_ROT)) {
                  yaw += mc.player.getYaw();
               }

               if (packet.getFlags().contains(PositionFlag.X_ROT)) {
                  pitch += mc.player.getPitch();
               }

               RotationManager.this.setServerRotations(yaw, pitch);
            }
         }
      });
      this.listeners.add(new Listener<RenderRotationsEvent>(RenderRotationsEvent.class, Integer.MAX_VALUE) {
         public void call(RenderRotationsEvent event) {
            RotationManager.this.rotated = true;
            RotationManager.this.ticksSinceNoRotate = 0;
            RotationManager.this.renderYaw = event.getYaw();
            RotationManager.this.renderPitch = event.getPitch();
         }
      });
      this.listeners.add(new Listener<MotionUpdateEvent>(MotionUpdateEvent.class, Integer.MAX_VALUE) {
         public void call(MotionUpdateEvent event) {
            switch(event.getStage()) {
            case PRE:
               RotationManager.this.yaw = mc.player.getYaw();
               RotationManager.this.pitch = mc.player.getPitch();
               break;
            case POST:
               if (mc.player.age == RotationManager.this.ticksExisted) {
                  return;
               }

               RotationManager.this.ticksExisted = mc.player.age;
               RotationManager.this.prevYaw = RotationManager.this.renderYaw;
               RotationManager.this.prevPitch = RotationManager.this.renderPitch;
               RotationManager.this.prevRenderBodyYaw = RotationManager.this.renderBodyYaw;
               RotationManager.this.renderBodyYaw = RotationManager.this.getRenderBodyYaw(mc.player, RotationManager.this.renderYaw, RotationManager.this.renderYaw);
               RotationManager.this.prevRotationYawHead = RotationManager.this.rotationYawHead;
               RotationManager.this.rotationYawHead = RotationManager.this.renderYaw;
               ++RotationManager.this.ticksSinceNoRotate;
               if (RotationManager.this.ticksSinceNoRotate > 2) {
                  RotationManager.this.renderYaw = mc.player.getYaw();
                  RotationManager.this.renderPitch = mc.player.getPitch();
                  RotationManager.this.rotated = false;
               }

               event.setYaw(RotationManager.this.yaw);
               event.setPitch(RotationManager.this.pitch);
            }

         }
      });
      this.listeners.add(new Listener<PacketEvent.Post<LookAndOnGround>>(PacketEvent.Post.class, LookAndOnGround.class) {
         public void call(PacketEvent.Post<LookAndOnGround> event) {
            RotationManager.this.readCPacket((PlayerMoveC2SPacket)event.getPacket());
         }
      });
      this.listeners.add(new Listener<PacketEvent.Post<Full>>(PacketEvent.Post.class, Full.class) {
         public void call(PacketEvent.Post<Full> event) {
            RotationManager.this.readCPacket((PlayerMoveC2SPacket)event.getPacket());
         }
      });
   }

   public void readCPacket(PlayerMoveC2SPacket packetIn) {
      ((IClientPlayerEntity)mc.player).setLastYaw(packetIn.getYaw(((IClientPlayerEntity)mc.player).getLastYaw()));
      ((IClientPlayerEntity)mc.player).setLastPitch(packetIn.getPitch(((IClientPlayerEntity)mc.player).getLastPitch()));
      this.setServerRotations(packetIn.getYaw(this.serverYaw), packetIn.getPitch(this.serverPitch));
   }

   public void setServerRotations(float yaw, float pitch) {
      this.serverYaw = yaw;
      this.serverPitch = pitch;
   }

   public void setRotations(float[] rotations, MotionUpdateEvent event) {
      this.setRotations(rotations[0], rotations[1], event);
   }

   public void setRotations(float yaw, float pitch, MotionUpdateEvent event) {
      this.rotated = true;
      this.ticksSinceNoRotate = 0;
      this.renderYaw = yaw;
      this.renderPitch = pitch;
      event.setYaw(yaw);
      event.setPitch(pitch);
   }

   public HitResult raycast(double maxDistance, float tickDelta, boolean includeFluids) {
      return this.raycast(maxDistance, tickDelta, includeFluids, this.yaw, this.pitch);
   }

   public HitResult raycast(double maxDistance, float tickDelta, boolean includeFluids, float yaw, float pitch) {
      IClientPlayerEntity access = (IClientPlayerEntity)mc.player;
      return access.raycastFromCustomAngles(maxDistance, tickDelta, includeFluids, yaw, pitch);
   }

   public float getRenderBodyYaw(LivingEntity entity, float yaw, float offsetIn) {
      float result = offsetIn;
      double xDif = entity.getX() - entity.prevX;
      double zDif = entity.getY() - entity.prevZ;
      float offset;
      if (xDif * xDif + zDif * zDif > 0.002500000176951289D) {
         offset = (float)MathHelper.atan2(zDif, xDif) * 57.295776F - 90.0F;
         float wrap = MathHelper.abs(MathHelper.wrapDegrees(yaw) - offset);
         if (95.0F < wrap && wrap < 265.0F) {
            result = offset - 180.0F;
         } else {
            result = offset;
         }
      }

      if (entity.handSwinging) {
         result = yaw;
      }

      result = offsetIn + MathHelper.wrapDegrees(result - offsetIn) * 0.3F;
      offset = MathHelper.wrapDegrees(yaw - result);
      if (offset < -75.0F) {
         offset = -75.0F;
      } else if (offset >= 75.0F) {
         offset = 75.0F;
      }

      result = yaw - offset;
      if (offset * offset > 2500.0F) {
         result += offset * 0.2F;
      }

      return result;
   }

   
   public float getServerYaw() {
      return this.serverYaw;
   }

   
   public float getServerPitch() {
      return this.serverPitch;
   }

   
   public float getYaw() {
      return this.yaw;
   }

   
   public float getPitch() {
      return this.pitch;
   }

   
   public boolean isRotated() {
      return this.rotated;
   }

   
   public float getRenderYaw() {
      return this.renderYaw;
   }

   
   public float getRenderPitch() {
      return this.renderPitch;
   }

   
   public float getRotationYawHead() {
      return this.rotationYawHead;
   }

   
   public float getRenderBodyYaw() {
      return this.renderBodyYaw;
   }

   
   public float getPrevYaw() {
      return this.prevYaw;
   }

   
   public float getPrevPitch() {
      return this.prevPitch;
   }

   
   public float getPrevRenderBodyYaw() {
      return this.prevRenderBodyYaw;
   }

   
   public float getPrevRotationYawHead() {
      return this.prevRotationYawHead;
   }

   
   public int getTicksSinceNoRotate() {
      return this.ticksSinceNoRotate;
   }

   
   public int getTicksExisted() {
      return this.ticksExisted;
   }

   
   public boolean isInv() {
      return this.isInv;
   }

   
   public void setServerYaw(float serverYaw) {
      this.serverYaw = serverYaw;
   }

   
   public void setServerPitch(float serverPitch) {
      this.serverPitch = serverPitch;
   }

   
   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   
   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   
   public void setRotated(boolean rotated) {
      this.rotated = rotated;
   }

   
   public void setRenderYaw(float renderYaw) {
      this.renderYaw = renderYaw;
   }

   
   public void setRenderPitch(float renderPitch) {
      this.renderPitch = renderPitch;
   }

   
   public void setRotationYawHead(float rotationYawHead) {
      this.rotationYawHead = rotationYawHead;
   }

   
   public void setRenderBodyYaw(float renderBodyYaw) {
      this.renderBodyYaw = renderBodyYaw;
   }

   
   public void setPrevYaw(float prevYaw) {
      this.prevYaw = prevYaw;
   }

   
   public void setPrevPitch(float prevPitch) {
      this.prevPitch = prevPitch;
   }

   
   public void setPrevRenderBodyYaw(float prevRenderBodyYaw) {
      this.prevRenderBodyYaw = prevRenderBodyYaw;
   }

   
   public void setPrevRotationYawHead(float prevRotationYawHead) {
      this.prevRotationYawHead = prevRotationYawHead;
   }

   
   public void setTicksSinceNoRotate(int ticksSinceNoRotate) {
      this.ticksSinceNoRotate = ticksSinceNoRotate;
   }

   
   public void setTicksExisted(int ticksExisted) {
      this.ticksExisted = ticksExisted;
   }

   
   public void setInv(boolean isInv) {
      this.isInv = isInv;
   }
}
