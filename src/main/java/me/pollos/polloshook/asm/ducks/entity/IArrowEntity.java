package me.pollos.polloshook.asm.ducks.entity;

import net.minecraft.entity.effect.StatusEffectInstance;

public interface IArrowEntity {
   Iterable<StatusEffectInstance> getEffects();
}
