package me.pollos.polloshook.impl.module.combat.autocrystal;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ListenerSound extends ModuleListener<AutoCrystal, PacketEvent.Receive<PlaySoundS2CPacket>> {
   public ListenerSound(AutoCrystal module) {
      super(module, PacketEvent.Receive.class, PlaySoundS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlaySoundS2CPacket> event) {
      if (mc.world != null) {
         PlaySoundS2CPacket packet = (PlaySoundS2CPacket)event.getPacket();
         if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
            this.removeCrystals(pos, Managers.getEntitiesManager().getEntities());
         } else if (packet.getSound().value() == SoundEvents.ENTITY_ENDER_DRAGON_SHOOT) {
            ((AutoCrystal)this.module).getBlockedPositions().put(BlockPos.ofFloored(packet.getX(), packet.getY() - 1.0D, packet.getZ()), System.currentTimeMillis());
         }

      }
   }

   private void removeCrystals(Vec3d pos, List<Entity> entities) {
      Iterator var3 = entities.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         if (entity instanceof EndCrystalEntity) {
            EndCrystalEntity crystal = (EndCrystalEntity)entity;
            if (entity.squaredDistanceTo(pos.x, pos.y, pos.z) <= (double)MathUtil.square(11.0F)) {
               ((AutoCrystal)this.module).getConfirmedPlacePositions().remove(crystal.getBlockPos());
               MinecraftClient var10000 = mc;
               Objects.requireNonNull(crystal);
               var10000.executeSync(crystal::kill);
            }
         }
      }

   }
}