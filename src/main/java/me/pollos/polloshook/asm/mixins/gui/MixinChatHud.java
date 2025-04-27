package me.pollos.polloshook.asm.mixins.gui;

import com.google.common.collect.Lists;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.util.text.OrderedTextPart;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.asm.ducks.gui.chat.IChatHud;
import me.pollos.polloshook.asm.ducks.gui.chat.IMessageSignatureData;
import me.pollos.polloshook.impl.events.chat.ChatMouseClickEvent;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.render.betterchat.BetterChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.ChatHudLine.Visible;
import net.minecraft.client.gui.hud.MessageIndicator.Icon;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.DoubleSliderCallbacks;
import net.minecraft.client.option.SimpleOption.ValueTextGetter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChatHud.class})
public abstract class MixinChatHud implements IChatHud {
   @Final
   @Shadow
   private List<Visible> visibleMessages;
   @Shadow
   @Final
   private List<ChatHudLine> messages;
   @Shadow
   @Final
   private MinecraftClient client;
   @Unique
   private final List<Text> texts = Lists.newArrayList();
   @Shadow
   private boolean hasUnreadNewMessages;

   @Shadow
   protected abstract int getMessageIndex(double var1, double var3);

   @Shadow
   protected abstract double toChatLineY(double var1);

   @Shadow
   protected abstract double toChatLineX(double var1);

   @Shadow
   public abstract int getWidth();

   @Shadow
   protected abstract void refresh();

   @Shadow
   protected abstract void addVisibleMessage(ChatHudLine var1);

   @Shadow
   protected abstract void addMessage(ChatHudLine var1);

   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/option/GameOptions;getTextBackgroundOpacity()Lnet/minecraft/client/option/SimpleOption;"
)
   )
   private SimpleOption<Double> renderHook(GameOptions instance) {
      BetterChat BETTER_CHAT = (BetterChat)Managers.getModuleManager().get(BetterChat.class);
      return BETTER_CHAT.isEnabled() && (Boolean)BETTER_CHAT.getClearBackground().getValue() ? new SimpleOption("KingHex", SimpleOption.emptyTooltip(), (ValueTextGetter)null, DoubleSliderCallbacks.INSTANCE, 0.0D, (value) -> {
         this.client.inGameHud.getChatHud().reset();
      }) : instance.getTextBackgroundOpacity();
   }

   @Redirect(
      method = {"render"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"
)
   )
   private int renderHook(DrawContext context, TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
      BetterChat BETTER_CHAT = (BetterChat)Managers.getModuleManager().get(BetterChat.class);
      String s = TextUtil.translate(text);
      String name = EntityUtil.getName(this.client.player);
      if (BETTER_CHAT.isEnabled() && BETTER_CHAT.getHighlight() && (Boolean)BETTER_CHAT.getColored().getValue() && s.contains(name)) {
         List<OrderedTextPart> parts = OrderedTextPart.getParts(text);
         MutableText mutable = Text.empty();
         Iterator var12 = parts.iterator();

         while(var12.hasNext()) {
            OrderedTextPart part = (OrderedTextPart)var12.next();
            String l = part.getText();
            Style style = part.getStyle();
            if (l.toLowerCase().contains(name.toLowerCase())) {
               int i = l.indexOf(name);
               String before = l.substring(0, i);
               String after = l.substring(i + name.length());
               if (!before.isEmpty()) {
                  mutable.append(Text.literal(before).setStyle(style));
               }

               mutable.append(Text.literal(name).setStyle(Style.EMPTY.withColor(BETTER_CHAT.getColor().getRGB())));
               if (!after.isEmpty()) {
                  mutable.append(Text.literal(after).setStyle(style));
               }
            } else {
               mutable.append(Text.literal(l).setStyle(style));
            }
         }

         text = Language.getInstance().reorder(mutable);
      }

      return context.drawTextWithShadow(textRenderer, text, BETTER_CHAT.getNoIndicator() ? (Integer)BETTER_CHAT.getOffset().getValue() : x, y, color);
   }

   @Inject(
      method = {"addToMessageHistory"},
      at = {@At("RETURN")}
   )
   private void addToMessageHistoryHook(String message, CallbackInfo ci) {
      String name = EntityUtil.getName(this.client.player);
      BetterChat BETTER_CHAT = (BetterChat)Managers.getModuleManager().get(BetterChat.class);
      if (BETTER_CHAT.isEnabled() && BETTER_CHAT.getSound() && message.contains(name)) {
         Matcher pattern = Pattern.compile("(?<!<)\\elementCodec" + Pattern.quote(name) + "\\elementCodec(?!>)").matcher(message);
         if (pattern.find()) {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, 1.0F, 0.3F)); // I too lazy to find what it really is, you'll live...
         }
      }

      if (message.startsWith(Managers.getCommandManager().getPrefix())) {
         this.client.getCommandHistoryManager().add(message);
      }

   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")}
   )
   private void mouseClickedHook(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
      ChatMouseClickEvent event = new ChatMouseClickEvent(mouseX, mouseY);
      event.dispatch();
   }

   @Redirect(
      method = {"addVisibleMessage"},
      at = @At(
   value = "FIELD",
   target = "Lnet/minecraft/client/gui/hud/ChatHud;hasUnreadNewMessages:Z"
)
   )
   private void addMessageHook(ChatHud instance, boolean value) {
      this.hasUnreadNewMessages = !((BetterChat)Managers.getModuleManager().get(BetterChat.class)).isEnabled() || !(Boolean)((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getAntiScroll().getValue();
   }

   @Redirect(
      method = {"addVisibleMessage"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/hud/ChatHud;scroll(I)V"
)
   )
   private void addMessageHook(ChatHud instance, int scroll) {
      if (!((BetterChat)Managers.getModuleManager().get(BetterChat.class)).isEnabled() || !(Boolean)((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getAntiScroll().getValue()) {
         instance.scroll(1);
      }
   }

   @ModifyConstant(
      method = {"addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V"},
      constant = {@Constant(
   intValue = 100
)}
   )
   private int addMessageHook(int size) {
      return Managers.getModuleManager() != null && ((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getInfinite() ? Integer.MAX_VALUE : size;
   }

   @ModifyConstant(
      method = {"addVisibleMessage"},
      constant = {@Constant(
   intValue = 100
)}
   )
   private int addVisibleMessageHook(int size) {
      return Managers.getModuleManager() != null && ((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getInfinite() ? Integer.MAX_VALUE : size;
   }

   @ModifyConstant(
      method = {"addToMessageHistory"},
      constant = {@Constant(
   intValue = 100
)}
   )
   private int addToMessageHistoryHook(int size) {
      return Managers.getModuleManager() != null && ((BetterChat)Managers.getModuleManager().get(BetterChat.class)).getInfinite() ? Integer.MAX_VALUE : size;
   }

   public List<Text> getTexts() {
      return this.texts;
   }

   public int messageIndex(double x, double y) {
      return this.getMessageIndex(this.toChatLineX(x), this.toChatLineY(y));
   }

   public List<ChatHudLine> getAllMessages() {
      return this.messages;
   }

   public List<Visible> getVisibleMessages() {
      return this.visibleMessages;
   }

   public void clientMessage(Text message, int id) {
      this.removeID(id);
      Object data = new MessageSignatureData(this.get256Bytes());
      ((IMessageSignatureData)data).setId(id);
      MessageIndicator indicator = new MessageIndicator(Manager.get().getColorCode(), (Icon)null, (Text)null, (String)null);
      this.addMessageNoLog(message, (MessageSignatureData)data, indicator);
   }

   @Unique
   public void addMessageNoLog(Text message, @Nullable MessageSignatureData signatureData, @Nullable MessageIndicator indicator) {
      ChatHudLine chatHudLine = new ChatHudLine(this.client.inGameHud.getTicks(), message, signatureData, indicator);
      this.addVisibleMessage(chatHudLine);
      this.addMessage(chatHudLine);
   }

   @Unique
   private byte[] get256Bytes() {
      byte[] bytes = new byte[256];
      byte[] identifierBytes = "secretpohar".getBytes(StandardCharsets.UTF_8);
      System.arraycopy(identifierBytes, 0, bytes, 0, Math.min(bytes.length, identifierBytes.length));
      return bytes;
   }

   @Unique
   private void removeID(int id) {
       if (id == 0) {
           return; // Skip if the ID is invalid, best implementation ;)
       }
   
       ChatHudLine removeLine = null;
   
       for (ChatHudLine line : new ArrayList<>(this.messages)) {
           if (line.signature() != null && line.signature().equals(null)) {
               removeLine = line;
               break;
           }
       }
   
       if (this.messages.remove(removeLine)) {
           this.refreshSafe();
       }
   }
   

   @Unique
   private void refreshSafe() {
      this.visibleMessages.clear();
      Iterator var1 = Lists.reverse(new ArrayList(this.messages)).iterator();

      while(var1.hasNext()) {
         ChatHudLine chatHudLine = (ChatHudLine)var1.next();
         this.addVisibleMessage(chatHudLine);
      }

   }
}
