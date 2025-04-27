package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.render.HeldItemRenderEvent;
import me.pollos.polloshook.impl.module.misc.swing.Swing;
import me.pollos.polloshook.impl.module.render.modelchanger.ModelChanger;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HeldItemRenderer.class})
public abstract class MixinHeldItemRenderer {
   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
)}
   )
   private void renderFirstPersonItemHook(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrix, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
      HeldItemRenderEvent event = new HeldItemRenderEvent(hand, matrix);
      PollosHook.getEventBus().dispatch(event);
   }

   @Inject(
      method = {"renderFirstPersonItem"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
   shift = Shift.AFTER
)}
   )
   private void renderFirstPersonItemHook_pushMatrix(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
      ModelChanger.PushItemMatrixEvent event = ModelChanger.PushItemMatrixEvent.of(matrices, hand);
      event.dispatch();
   }

   @Redirect(
      method = {"updateHeldItems"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"
)
   )
   private float updateHeldItemsHook(ClientPlayerEntity instance, float v) {
      return ((Swing)Managers.getModuleManager().get(Swing.class)).isEnabled() && (Boolean)((Swing)Managers.getModuleManager().get(Swing.class)).getOldSwing().getValue() ? 1.0F : instance.getAttackCooldownProgress(1.0F);
   }
}
