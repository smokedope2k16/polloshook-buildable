package me.pollos.polloshook.asm.mixins.util;

import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BufferBuilder.class})
public interface IBufferBuilder {
   @Accessor("building")
   boolean isBuilding();
}
