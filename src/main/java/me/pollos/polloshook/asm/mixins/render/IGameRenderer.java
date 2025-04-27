package me.pollos.polloshook.asm.mixins.render;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GameRenderer.class})
public interface IGameRenderer {
   @Invoker("bobView")
   void invokeBobView(MatrixStack var1, float var2);
}
