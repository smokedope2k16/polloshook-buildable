package me.pollos.polloshook.asm.mixins.item;

import me.pollos.polloshook.impl.module.render.oldpotions.OldPotions;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemStack.class})
public class MixinItemStack {
   /** @deprecated */
   @Shadow
   @Final
   @Deprecated
   @Nullable
   private Item item;

   @Inject(
      method = {"hasGlint"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void hasGlintHook(CallbackInfoReturnable<Boolean> cir) {
      ItemStack cast = (ItemStack)ItemStack.class.cast(this);
      PotionContentsComponent potionContents = (PotionContentsComponent)cast.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
      if (this.item instanceof PotionItem) {
         OldPotions.OldGlintEvent event = OldPotions.OldGlintEvent.create();
         event.dispatch();
         if (event.isCanceled()) {
            cir.setReturnValue(potionContents.hasEffects());
         }
      }

   }
}