package me.pollos.polloshook.asm.mixins.gui;


import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.util.thread.NonNullList;
import me.pollos.polloshook.asm.ducks.gui.IHandledScreen;
import me.pollos.polloshook.impl.module.render.shulkerpreview.ShulkerPreview;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({HandledScreen.class})
public abstract class MixinHandledScreen implements Minecraftable, IHandledScreen {
   @Final
   @Shadow
   protected ScreenHandler handler;
   @Shadow
   protected Slot focusedSlot;
   @Shadow
   protected int x;
   @Shadow
   protected int y;
   @Unique
   private boolean mouseClicked = false;

   @Shadow
   @Nullable
   protected abstract Slot getSlotAt(double var1, double var3);

   @Shadow
   protected abstract void onMouseClick(Slot var1, int var2, int var3, SlotActionType var4);

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void renderHook_return(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ShulkerPreview.RenderHandledScreenEvent event = ShulkerPreview.RenderHandledScreenEvent.of(context);
      event.dispatch();
   }

   @Inject(
      method = {"render"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V",
   shift = Shift.AFTER
)}
   )
   private void renderHook(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      ShulkerPreview.RenderHandledScreenEvent.DrawItem event = new ShulkerPreview.RenderHandledScreenEvent.DrawItem(context, this.handler.slots);
      event.dispatch();
   }

   @Inject(
      method = {"drawMouseoverTooltip"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void drawMouseoverTooltipHook(DrawContext context, int x, int y, CallbackInfo ci) {
      ShulkerPreview SHULKER_PREVIEW_MODULE = (ShulkerPreview)Managers.getModuleManager().get(ShulkerPreview.class);
      if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
         ItemStack itemStack = this.focusedSlot.getStack();
         Item var8 = itemStack.getItem();
         if (var8 instanceof BlockItem) {
            BlockItem item = (BlockItem)var8;
            if (item.getBlock() instanceof ShulkerBoxBlock && SHULKER_PREVIEW_MODULE.isEnabled()) {
               NonNullList<ItemStack> list = NonNullList.withSize(27, ItemStack.EMPTY);
               ItemUtil.loadAllItems(itemStack, list);
               if (!SHULKER_PREVIEW_MODULE.isPressingShift() || list.isEmpty()) {
                  return;
               }

               ci.cancel();
               SHULKER_PREVIEW_MODULE.render(context, itemStack, list, x, y);
            }
         }
      }

   }

   @Inject(
      method = {"keyPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void keyPressedHook(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
      ShulkerPreview.TypeStringEvent event = ShulkerPreview.TypeStringEvent.of(GLFW.glfwGetKeyName(keyCode, scanCode), keyCode);
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(true);
      }

   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("RETURN")}
   )
   private void mouseClickedHook(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      if (button == 0) {
         this.mouseClicked = true;
      }

   }

   @Inject(
      method = {"mouseReleased"},
      at = {@At("RETURN")}
   )
   private void mouseReleasedHook(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      if (button == 0) {
         this.mouseClicked = false;
      }

   }

   @Inject(
      method = {"close"},
      at = {@At("RETURN")}
   )
   private void closeHook(CallbackInfo ci) {
      this.mouseClicked = false;
   }

   public Slot slotAt(double x, double y) {
      return this.getSlotAt(x, y);
   }

   public void onClicked(Slot slot, int slotId, int button, SlotActionType actionType) {
      this.onMouseClick(slot, slotId, button, actionType);
   }

   
   public boolean isMouseClicked() {
      return this.mouseClicked;
   }
}
