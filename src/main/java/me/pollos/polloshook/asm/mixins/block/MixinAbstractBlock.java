package me.pollos.polloshook.asm.mixins.block;

import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractBlock.class})
public class MixinAbstractBlock {
   @Inject(
      method = {"getRenderType"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getRenderTypeHook(BlockState state, CallbackInfoReturnable<BlockRenderType> cir) {
      if (state.getBlock() instanceof VineBlock) {
         NoRender.VinesEvent event = NoRender.VinesEvent.create();
         event.dispatch();
         if (event.isCanceled()) {
            cir.setReturnValue(BlockRenderType.INVISIBLE);
         }

      }
   }
}