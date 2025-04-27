package me.pollos.polloshook.asm.mixins.block;

import me.pollos.polloshook.asm.ducks.entity.IEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CobwebBlock.class})
public class MixinCobweb {
   @Inject(
      method = {"onEntityCollision"},
      at = {@At("HEAD")}
   )
   private void onEntityCollisionHook(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity plr = (PlayerEntity)entity;
         if (!plr.getAbilities().flying) {
            ((IEntity)entity).setInWeb(true);
         }
      } else {
         ((IEntity)entity).setInWeb(true);
      }

   }
}
