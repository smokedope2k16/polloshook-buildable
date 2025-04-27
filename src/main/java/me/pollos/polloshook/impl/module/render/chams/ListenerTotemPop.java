package me.pollos.polloshook.impl.module.render.chams;

import com.mojang.authlib.GameProfile;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.TotemPopEvent;
import me.pollos.polloshook.impl.module.render.chams.util.TotemPopPlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;

public class ListenerTotemPop extends ModuleListener<Chams, TotemPopEvent> {
   public ListenerTotemPop(Chams module) {
      super(module, TotemPopEvent.class);
   }

   public void call(TotemPopEvent event) {
      PlayerEntity entity = event.getPlayer();
      if (entity != mc.player || (Boolean)((Chams)this.module).selfTotem.getValue()) {
         OtherClientPlayerEntity fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(entity.getUuid(), entity.getName().getString()));
         fakePlayer.copyPositionAndRotation(entity);
         fakePlayer.setMainArm(entity.getMainArm());
         fakePlayer.prevPitch = entity.prevPitch;
         fakePlayer.prevYaw = entity.prevYaw;
         fakePlayer.bodyYaw = entity.bodyYaw;
         fakePlayer.prevBodyYaw = entity.prevBodyYaw;
         fakePlayer.headYaw = entity.headYaw;
         fakePlayer.prevHeadYaw = entity.prevHeadYaw;
         fakePlayer.setSneaking(entity.isSneaking());
         fakePlayer.setPose(entity.getPose());
         Context context = new Context(mc.getEntityRenderDispatcher(), mc.getItemRenderer(), mc.getBlockRenderManager(), mc.getEntityRenderDispatcher().getHeldItemRenderer(), mc.getResourceManager(), mc.getEntityModelLoader(), mc.textRenderer);
         boolean slim = this.armsBool(entity);
         PlayerEntityModel<PlayerEntity> model = new PlayerEntityModel(context.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim);
         float tickDelta = mc.getRenderTickCounter().getTickDelta(false);
         float n = (float)entity.age + tickDelta;
         float o = 0.0F;
         float p = 0.0F;
         if (!entity.hasVehicle() && entity.isAlive()) {
            o = entity.limbAnimator.getSpeed(tickDelta);
            p = entity.limbAnimator.getPos(tickDelta);
            if (o > 1.0F) {
               o = 1.0F;
            }
         }

         float m = MathHelper.lerp(tickDelta, fakePlayer.prevPitch, fakePlayer.getPitch());
         float k = 1.0F;
         if (LivingEntityRenderer.shouldFlipUpsideDown(entity)) {
            m *= -1.0F;
            k *= -1.0F;
         }

         k = MathHelper.wrapDegrees(k);
         model.animateModel(fakePlayer, p, o, tickDelta);
         model.setAngles(fakePlayer, p, o, n, k, m);
         ArmPose armPose = this.getArmPose(fakePlayer, Hand.MAIN_HAND);
         ArmPose armPose2 = this.getArmPose(fakePlayer, Hand.OFF_HAND);
         if (armPose.isTwoHanded()) {
            armPose2 = fakePlayer.getOffHandStack().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
         }

         if (fakePlayer.getMainArm() == Arm.RIGHT) {
            model.rightArmPose = armPose;
            model.leftArmPose = armPose2;
         } else {
            model.rightArmPose = armPose2;
            model.leftArmPose = armPose;
         }

         ((Chams)this.module).popped.add(new TotemPopPlayer(fakePlayer, model, System.currentTimeMillis()));
      }
   }

   protected boolean armsBool(PlayerEntity player) {
      return false;
   }

   private ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isEmpty()) {
         return ArmPose.EMPTY;
      } else {
         if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
            UseAction useAction = itemStack.getUseAction();
            if (useAction == UseAction.BLOCK) {
               return ArmPose.BLOCK;
            }

            if (useAction == UseAction.BOW) {
               return ArmPose.BOW_AND_ARROW;
            }

            if (useAction == UseAction.SPEAR) {
               return ArmPose.THROW_SPEAR;
            }

            if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
               return ArmPose.CROSSBOW_CHARGE;
            }

            if (useAction == UseAction.SPYGLASS) {
               return ArmPose.SPYGLASS;
            }

            if (useAction == UseAction.TOOT_HORN) {
               return ArmPose.TOOT_HORN;
            }

            if (useAction == UseAction.BRUSH) {
               return ArmPose.BRUSH;
            }
         } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
            return ArmPose.CROSSBOW_CHARGE;
         }

         return ArmPose.ITEM;
      }
   }
}
