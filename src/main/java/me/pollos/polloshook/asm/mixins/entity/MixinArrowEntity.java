package me.pollos.polloshook.asm.mixins.entity;

import me.pollos.polloshook.asm.ducks.entity.IArrowEntity;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ArrowEntity.class})
public abstract class MixinArrowEntity implements IArrowEntity {
   @Shadow
   protected abstract PotionContentsComponent getPotionContents();

   public Iterable<StatusEffectInstance> getEffects() {
      return this.getPotionContents().getEffects();
   }
}
