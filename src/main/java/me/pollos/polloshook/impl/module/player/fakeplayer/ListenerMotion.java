package me.pollos.polloshook.impl.module.player.fakeplayer;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.rotations.RenderRotations;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerPosition;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class ListenerMotion extends ModuleListener<FakePlayer, MotionUpdateEvent> {
   private int i;
   private boolean wasRecording;

   public ListenerMotion(FakePlayer module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (((FakePlayer)this.module).getPlayer() != null) {
         OtherClientPlayerEntity player = ((FakePlayer)this.module).getPlayer();
         if (player.getHealth() <= 1.0F) {
            player.setHealth(1.0F);
         }

         if ((Boolean)((FakePlayer)this.module).damage.getValue() && player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
            player.getOffHandStack().setCount(1);
            player.setStackInHand(Hand.OFF_HAND, new ItemStack(() -> {
               return Items.TOTEM_OF_UNDYING;
            }));
         }

         if (((FakePlayer)this.module).timer.passed(1750L)) {
            player.setAbsorptionAmount(16.0F);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 2400, 3));
            ((FakePlayer)this.module).timer.reset();
         }

         if (event.getStage() == Stage.PRE && !(Boolean)((FakePlayer)this.module).record.getValue()) {
            if ((Boolean)((FakePlayer)this.module).play.getValue()) {
               if (((FakePlayer)this.module).playerPositions.isEmpty()) {
                  ((FakePlayer)this.module).play.setValue(false);
                  ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "No recordings to play");
                  return;
               }

               if (this.i >= ((FakePlayer)this.module).playerPositions.size()) {
                  this.i = 0;
               }

               FakePlayerPosition current = (FakePlayerPosition)((FakePlayer)this.module).playerPositions.get(this.i);
               FakePlayerPosition next = (FakePlayerPosition)((FakePlayer)this.module).playerPositions.get((this.i + 1) % ((FakePlayer)this.module).playerPositions.size());
               float factor = mc.getRenderTickCounter().getTickDelta(true);
               double interpX = MathHelper.lerp((double)factor, current.getX(), next.getX());
               double interpY = MathHelper.lerp((double)factor, current.getY(), next.getY());
               double interpZ = MathHelper.lerp((double)factor, current.getZ(), next.getZ());
               RenderRotations currentRotations = current.getRenderRotations();
               RenderRotations nextRotations = next.getRenderRotations();
               double interpYaw = (double)MathHelper.lerp(factor, currentRotations.getYaw(), nextRotations.getYaw());
               double interpPitch = (double)MathHelper.lerp(factor, currentRotations.getPitch(), nextRotations.getPitch());
               double interpHeadYaw = (double)MathHelper.lerp(factor, currentRotations.getHeadYaw(), nextRotations.getHeadYaw());
               double interpBodyYaw = (double)MathHelper.lerp(factor, currentRotations.getBodyYaw(), nextRotations.getBodyYaw());
               player.updateTrackedPositionAndAngles(interpX, interpY, interpZ, (float)interpYaw, (float)interpPitch, 3);
               player.setHeadYaw((float)interpHeadYaw);
               player.setBodyYaw((float)interpBodyYaw);
               double interpVeloX = MathHelper.lerp((double)factor, current.getMotionX(), next.getMotionX());
               double interpVeloY = MathHelper.lerp((double)factor, current.getMotionY(), next.getMotionY());
               double interpVeloZ = MathHelper.lerp((double)factor, current.getMotionZ(), next.getMotionZ());
               player.setVelocity(interpVeloX, interpVeloY, interpVeloZ);
               player.setPose(current.getPose());
               player.setPosition(Interpolation.interpolateLastTickPos(player.lastRenderX, player.getX()), Interpolation.interpolateLastTickPos(player.lastRenderY, player.getY()), Interpolation.interpolateLastTickPos(player.lastRenderZ, player.getZ()));
               if (++this.i >= ((FakePlayer)this.module).playerPositions.size()) {
                  this.i = 0;
               }
            } else {
               this.i = 0;
               ((FakePlayer)this.module).clearMotion(player);
            }
         } else if (event.getStage() == Stage.POST && (Boolean)((FakePlayer)this.module).record.getValue()) {
            ((FakePlayer)this.module).play.setValue(false);
            ((FakePlayer)this.module).clearMotion(player);
            if (!this.wasRecording) {
               ((FakePlayer)this.module).playerPositions.clear();
               this.wasRecording = true;
            }

            ((FakePlayer)this.module).playerPositions.add(new FakePlayerPosition(mc.player));
         }

      }
   }
}