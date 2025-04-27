package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RecipeBookWidget.class})
public class MixinRecipeBookWidget {
   @Shadow
   protected ToggleButtonWidget toggleCraftableButton;
   @Unique
   private final int target = 147;

   @ModifyConstant(
      method = {"reset"},
      constant = {@Constant(
   intValue = 147
)}
   )
   private int initVisualsHook(int constant) {
      return this.getX(constant);
   }

   @ModifyConstant(
      method = {"render"},
      constant = {@Constant(
   intValue = 147,
   ordinal = 0
)}
   )
   private int renderHook(int constant) {
      return this.getX(constant);
   }

   @ModifyConstant(
      method = {"refreshTabButtons"},
      constant = {@Constant(
   intValue = 147
)}
   )
   private int updateTabsHook(int constant) {
      return this.getX(constant);
   }

   @ModifyConstant(
      method = {"mouseClicked"},
      constant = {@Constant(
   intValue = 147
)}
   )
   private int mouseClickedHook(int constant) {
      return this.getX(constant);
   }

   @ModifyConstant(
      method = {"isClickOutsideBounds"},
      constant = {@Constant(
   intValue = 147
)}
   )
   private int hasClickedOutsideHook(int constant) {
      return this.getX(constant);
   }

   @Inject(
      method = {"findLeftEdge"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateScreenPositionHook(int width, int backgroundWidth, CallbackInfoReturnable<Integer> cir) {
      if ((Boolean)Manager.get().getKeepInvCentered().getValue()) {
         int i = (width - backgroundWidth) / 2;
         cir.setReturnValue(i);
      }

   }

   @Unique
   private int getX(int constant) {
      return (Boolean)Manager.get().getKeepInvCentered().getValue() ? 301 : constant;
   }
}
