package me.pollos.polloshook.asm.mixins.gui;

import java.awt.Color;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.gui.chat.PollosChatScreen;
import me.pollos.polloshook.impl.module.other.irc.IrcModule;
import me.pollos.polloshook.impl.module.render.betterchat.BetterChat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({ChatScreen.class})
public class MixinChatScreen extends Screen {
   @Shadow
   protected TextFieldWidget chatField;

   protected MixinChatScreen(Text title) {
      super(title);
   }

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void renderHook(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      IrcModule IRC = (IrcModule)Managers.getModuleManager().get(IrcModule.class);
      if (IRC.isEnabled() && IRC.shouldDrawBorder(this.chatField.getText())) {
         int x = this.chatField.getX() - 2;
         int y = this.chatField.getY() - 2;
         int width = this.chatField.getWidth();
         int height = this.chatField.getHeight();
         context.drawBorder(x, y, width, height, (new Color(0, 255, 255)).getRGB());
      }

      if (this.client.currentScreen instanceof ChatScreen && !(this.client.currentScreen instanceof PollosChatScreen)) {
         if (this.chatField.getText().startsWith(Managers.getCommandManager().getPrefix())) {
            this.client.setScreen(new PollosChatScreen(this.chatField.getText()));
         }

      }
   }

   @Inject(
      method = {"keyPressed"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/screen/ChatScreen;sendMessage(Ljava/lang/String;Z)V",
   shift = Shift.BEFORE
)}
   )
   private void keyPressedHook(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      ((IrcModule)Managers.getModuleManager().get(IrcModule.class)).setValidMessage(this.chatField.getText());
   }

   @ModifyArgs(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
)
   )
   private void renderHook(Args args) {
      if (((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getClearTextBox()) {
         args.set(4, 0);
      }

   }
}
