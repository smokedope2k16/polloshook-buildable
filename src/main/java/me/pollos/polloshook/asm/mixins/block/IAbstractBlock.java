package me.pollos.polloshook.asm.mixins.block;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractBlock.class})
public interface IAbstractBlock {
   @Mutable
   @Accessor("slipperiness")
   void setSlipperiness(float var1);
}
