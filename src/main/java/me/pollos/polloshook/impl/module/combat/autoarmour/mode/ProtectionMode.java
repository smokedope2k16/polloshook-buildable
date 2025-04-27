package me.pollos.polloshook.impl.module.combat.autoarmour.mode;


import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;

public enum ProtectionMode {
   PROTECTION(Enchantments.PROTECTION),
   BLAST(Enchantments.BLAST_PROTECTION),
   FIRE(Enchantments.FIRE_PROTECTION),
   PROJECTILE(Enchantments.PROJECTILE_PROTECTION);

   private final RegistryKey<Enchantment> enchant;

   
   private ProtectionMode(final RegistryKey<Enchantment> enchant) {
      this.enchant = enchant;
   }

   
   public RegistryKey<Enchantment> getEnchant() {
      return this.enchant;
   }

   // $FF: synthetic method
   private static ProtectionMode[] $values() {
      return new ProtectionMode[]{PROTECTION, BLAST, FIRE, PROJECTILE};
   }
}