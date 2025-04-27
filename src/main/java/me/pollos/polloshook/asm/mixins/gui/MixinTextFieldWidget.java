package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.asm.ducks.gui.ITextFieldWidget;
import me.pollos.polloshook.impl.gui.chat.PollosChatScreen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({TextFieldWidget.class})
public abstract class MixinTextFieldWidget implements ITextFieldWidget, Minecraftable {
   @Shadow
   @Final
   private TextRenderer textRenderer;
   @Shadow
   private boolean editable;
   @Shadow
   @Final
   private static String HORIZONTAL_CURSOR;

   @Shadow
   protected abstract void erase(int var1);

   @Shadow
   public abstract String getText();

   @Redirect(
      method = {"renderWidget"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I"
)
   )
   private int renderWidgetHook(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color) {
      if (mc.currentScreen == null) {
         return x;
      } else {
         int spaceOffset = 0;
         Screen var9 = mc.currentScreen;
         if (var9 instanceof PollosChatScreen) {
            PollosChatScreen pollosChatScreen = (PollosChatScreen)var9;
            if (!TextUtil.isNullOrEmpty(pollosChatScreen.getHelpString(this.getText()))) {
               String[] splitArray = this.getText().split(" ");
               if (this.getText().contains(" ") && splitArray.length > 1) {
                  spaceOffset = textRenderer.getWidth(" ") - 4;
               }

               return instance.drawTextWithShadow(this.textRenderer, HORIZONTAL_CURSOR, x + pollosChatScreen.getX() - spaceOffset, y, 6316128);
            }
         }

         return instance.drawTextWithShadow(textRenderer, text, x, y, color);
      }
   }

   public TextRenderer textRenderer() {
      return this.textRenderer;
   }

   public boolean canEdit() {
      return this.editable;
   }

   public void hook$erase(int i) {
      this.erase(i);
   }
}
