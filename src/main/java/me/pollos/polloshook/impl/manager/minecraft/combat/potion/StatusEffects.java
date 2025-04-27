package me.pollos.polloshook.impl.manager.minecraft.combat.potion;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public record StatusEffects(StatusEffect effect, StatusEffectInstance instance) {
   public StatusEffects(StatusEffect effect, StatusEffectInstance instance) {
      this.effect = effect;
      this.instance = instance;
   }

   public StatusEffect effect() {
      return this.effect;
   }

   public StatusEffectInstance instance() {
      return this.instance;
   }
}
