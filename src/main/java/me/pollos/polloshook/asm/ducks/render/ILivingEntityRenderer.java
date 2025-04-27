package me.pollos.polloshook.asm.ducks.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface ILivingEntityRenderer {
   void renderFast(LivingEntity var1, float var2, float var3, MatrixStack var4, VertexConsumerProvider var5, int var6, boolean var7);

   void renderFast(LivingEntity var1, float var2, float var3, MatrixStack var4, VertexConsumerProvider var5, int var6);
}
