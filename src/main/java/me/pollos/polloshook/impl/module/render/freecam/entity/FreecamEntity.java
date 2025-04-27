package me.pollos.polloshook.impl.module.render.freecam.entity;

import java.util.Collection;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

public class FreecamEntity extends ClientPlayerEntity {
   final MinecraftClient mc;

   public FreecamEntity(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
      super(client, world, networkHandler, stats, recipeBook, lastSneaking, lastSprinting);
      this.mc = client;
   }

   public boolean hasStatusEffect(RegistryEntry<StatusEffect> effect) {
      return this.mc.player.hasStatusEffect(effect);
   }

   public Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects() {
      return this.mc.player.getActiveStatusEffects();
   }

   @Nullable
   public StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect) {
      return this.mc.player.getStatusEffect(effect);
   }

   public float getAbsorptionAmount() {
      return this.mc.player.getAbsorptionAmount();
   }

   public boolean isInsideWall() {
      return false;
   }

   public Collection<StatusEffectInstance> getStatusEffects() {
      return this.mc.player.getStatusEffects();
   }

   public int getArmor() {
      return this.mc.player.getArmor();
   }

   public HungerManager getHungerManager() {
      return this.mc.player.getHungerManager();
   }

   public PlayerInventory getInventory() {
      return this.mc.player.getInventory();
   }

   public EnderChestInventory getEnderChestInventory() {
      return this.mc.player.getEnderChestInventory();
   }

   public ChunkPos getChunkPos() {
      return new ChunkPos(ChunkSectionPos.getSectionCoord(this.getX()), ChunkSectionPos.getSectionCoord(this.getZ()));
   }
}
