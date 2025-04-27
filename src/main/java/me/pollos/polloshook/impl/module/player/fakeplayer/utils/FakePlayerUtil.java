package me.pollos.polloshook.impl.module.player.fakeplayer.utils;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity.RemovalReason;

public class FakePlayerUtil implements Minecraftable {
   private static final Map<String, FakePlayerEntity> players = new HashMap();

   public static void addFakePlayerToWorld(String label, String name, int id) {
      if (mc.player != null) {
         GameProfile profile = new GameProfile(UUID.randomUUID(), name);
         FakePlayerEntity fake = new FakePlayerEntity(mc.world, profile, label);
         fake.getInventory().clone(mc.player.getInventory());
         fake.copyPositionAndRotation(mc.player);
         fake.prevPitch = mc.player.prevPitch;
         fake.prevYaw = mc.player.prevYaw;
         fake.bodyYaw = mc.player.bodyYaw;
         fake.prevBodyYaw = mc.player.prevBodyYaw;
         fake.headYaw = mc.player.headYaw;
         fake.prevHeadYaw = mc.player.prevHeadYaw;
         fake.setHealth(mc.player.getHealth());
         fake.setAbsorptionAmount(mc.player.getAbsorptionAmount());
         fake.setOnGround(mc.player.isOnGround());
         fake.setId(id);
         mc.world.addEntity(fake);
         players.put(label, fake);
      }

   }

   public static void removeFakePlayerFromWorld(String label, int id) {
      if (mc.player != null) {
         if (mc.world.getEntityById(id) == null) {
            ClientLogger.getLogger().warn("Player with ID %d does not exist (%s)".formatted(new Object[]{id, label}));
            return;
         }

         mc.world.removeEntity(id, RemovalReason.DISCARDED);
         players.remove(label);
      }

   }

   public static OtherClientPlayerEntity getPlayer(String label) {
      return (OtherClientPlayerEntity)players.get(label);
   }
}
