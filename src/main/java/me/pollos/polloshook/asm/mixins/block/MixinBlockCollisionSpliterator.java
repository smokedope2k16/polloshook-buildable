package me.pollos.polloshook.asm.mixins.block;

import java.util.function.BiFunction;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.block.CollisionShapeEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BlockCollisionSpliterator.class})
public class MixinBlockCollisionSpliterator {
   @Unique
   private Entity e;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   private void initHook(CollisionView world, Entity entity, Box box, boolean forEntity, BiFunction resultFunction, CallbackInfo ci) {
      this.e = entity;
   }

   @Redirect(
      method = {"computeNext"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/block/BlockState;getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"
)
   )
   private VoxelShape computeNextHook(BlockState instance, BlockView blockView, BlockPos pos, ShapeContext niggerstopautocorrecting) {
      CollisionShapeEvent shapeEvent = new CollisionShapeEvent(this.e, instance, pos, instance.getCollisionShape(blockView, pos));
      PollosHook.getEventBus().dispatch(shapeEvent);
      return shapeEvent.isCanceled() ? shapeEvent.getShape() : instance.getCollisionShape(blockView, pos);
   }
}
