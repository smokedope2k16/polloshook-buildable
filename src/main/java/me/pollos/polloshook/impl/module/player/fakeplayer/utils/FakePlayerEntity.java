package me.pollos.polloshook.impl.module.player.fakeplayer.utils;

import com.mojang.authlib.GameProfile;

import me.pollos.polloshook.api.interfaces.Labeled;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;

public class FakePlayerEntity extends OtherClientPlayerEntity implements Labeled {
   private final String label;

   public FakePlayerEntity(ClientWorld clientWorld, GameProfile gameProfile, String label) {
      super(clientWorld, gameProfile);
      this.label = label;
   }

   public boolean damage(DamageSource source, float amount) {
      return false;
   }

   public float getHealth() {
      return super.getHealth() <= 0.0F ? 1.0F : super.getHealth();
   }

   public boolean isDead() {
      return false;
   }

   public boolean isAlive() {
      return true;
   }

   
   public String getLabel() {
      return this.label;
   }
}
