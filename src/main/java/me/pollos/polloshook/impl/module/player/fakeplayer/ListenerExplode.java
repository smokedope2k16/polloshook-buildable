package me.pollos.polloshook.impl.module.player.fakeplayer;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class ListenerExplode extends ModuleListener<FakePlayer, PacketEvent.Receive<ExplosionS2CPacket>> {
   public ListenerExplode(FakePlayer module) {
      super(module, PacketEvent.Receive.class, ExplosionS2CPacket.class);
   }

   public void call(PacketEvent.Receive<ExplosionS2CPacket> event) {
      if ((Boolean)((FakePlayer)this.module).damage.getValue()) {
         mc.executeTask(() -> {
            this.handle((ExplosionS2CPacket)event.getPacket());
         });
      }
   }

   private void handle(ExplosionS2CPacket packet) {
      OtherClientPlayerEntity player = ((FakePlayer)this.module).getPlayer();
      if (player.getHealth() <= 1.0F) {
         player.setHealth(1.0F);
      }

      double x = packet.getX();
      double y = packet.getY();
      double z = packet.getZ();
      double distance = player.squaredDistanceTo(x, y, z) / (double)MathUtil.square(12.0F);
      if (!(distance > 1.0D)) {
         float size = packet.getRadius();
         double density = (double)Explosion.getExposure(new Vec3d(x, y, z), ((FakePlayer)this.module).getPlayer());
         double densityDistance = distance = (1.0D - distance) * density;
         float damage = (float)((densityDistance * densityDistance + distance) / 2.0D * 7.0D * (double)size * 2.0D + 1.0D);
         if (damage > player.getHealth()) {
            player.setHealth(1.0F);
            player.setAbsorptionAmount(8.0F);
            player.getStatusEffects().clear();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100, 1));
            this.receive(new EntityStatusS2CPacket(player, (byte)35));
         } else {
            if (player.getHealth() - damage <= 0.0F) {
               player.setHealth(1.0F);
            }

            player.setHealth(player.getHealth() - damage);
         }

      }
   }

   private void receive(EntityStatusS2CPacket packet) {
      PacketEvent.Receive<?> event = new PacketEvent.Receive(packet);
      PollosHook.getEventBus().dispatch(event, packet.getClass());
      mc.getNetworkHandler().onEntityStatus(packet);
   }
}