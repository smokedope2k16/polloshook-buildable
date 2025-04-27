package me.pollos.polloshook.asm.mixins.network;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.block.AttackBlockEvent;
import me.pollos.polloshook.impl.events.block.BreakBlockEvent;
import me.pollos.polloshook.impl.events.block.DamageBlockEvent;
import me.pollos.polloshook.impl.events.block.InteractEvent;
import me.pollos.polloshook.impl.events.entity.AttackEntityEvent;
import me.pollos.polloshook.impl.events.entity.UseItemEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerInteractionManager.class})
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   private boolean breakingBlock;
   @Shadow
   private float currentBreakingProgress;
   @Shadow
   private BlockPos currentBreakingPos;

   @Accessor("blockBreakingCooldown")
   public abstract void setBlockHitDelay(int var1);

   @Shadow
   protected abstract void syncSelectedSlot();

   @Shadow
   protected abstract void sendSequencedPacket(ClientWorld var1, SequencedPacketCreator var2);

   @Shadow
   protected abstract boolean isCurrentlyBreaking(BlockPos var1);

   @Inject(
      method = {"attackBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void attackBlockHook(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
      AttackBlockEvent event = new AttackBlockEvent(pos, direction);
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         info.setReturnValue(false);
         info.cancel();
      }

   }

   @Inject(
      method = {"updateBlockBreakingProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void updateBlockBreakingProgressHook(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
      DamageBlockEvent event = new DamageBlockEvent(pos, direction);
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         info.setReturnValue(false);
         info.cancel();
      }

   }

   @Inject(
      method = {"interactBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void interactBlockHook(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
      InteractEvent event = new InteractEvent(hand, hitResult.getBlockPos(), hitResult.getSide());
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         info.cancel();
         info.setReturnValue(ActionResult.PASS);
      }

   }

   @Inject(
      method = {"breakBlock"},
      at = {@At("HEAD")}
   )
   private void breakBlockHook(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      BreakBlockEvent breakBlockEvent = new BreakBlockEvent(this.client.world.getBlockState(pos).getBlock(), pos);
      PollosHook.getEventBus().dispatch(breakBlockEvent);
   }

   @Inject(
      method = {"interactItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void interactItemHook(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
      UseItemEvent event = new UseItemEvent(hand);
      PollosHook.getEventBus().dispatch(event);
      if (event.isCanceled()) {
         info.cancel();
         info.setReturnValue(ActionResult.PASS);
      }

   }

   @Inject(
      method = {"attackEntity"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   shift = Shift.BEFORE
)},
      cancellable = true
   )
   private void attackEntityHookPre(PlayerEntity player, Entity target, CallbackInfo info) {
      AttackEntityEvent attackEntityEvent = new AttackEntityEvent(target, Stage.PRE);
      PollosHook.getEventBus().dispatch(attackEntityEvent);
      if (attackEntityEvent.isCanceled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"attackEntity"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
   shift = Shift.AFTER
)}
   )
   private void attackEntityHookPost(PlayerEntity player, Entity target, CallbackInfo info) {
      AttackEntityEvent attackEntityEvent = new AttackEntityEvent(target, Stage.POST);
      PollosHook.getEventBus().dispatch(attackEntityEvent);
   }

   public void syncItem() {
      this.syncSelectedSlot();
   }

   public void sendPacketWithSequence(ClientWorld world, SequencedPacketCreator packetCreator) {
      this.sendSequencedPacket(world, packetCreator);
   }

   public boolean hittingPos(BlockPos pos) {
      return this.isCurrentlyBreaking(pos);
   }

   public void setHittingPosBool(boolean bool) {
      this.breakingBlock = bool;
   }

   public void setBreakingProgress(float damage) {
      this.currentBreakingProgress = damage;
   }

   public void setCurrentBreakingPos(BlockPos pos) {
      this.currentBreakingPos = pos;
   }
}