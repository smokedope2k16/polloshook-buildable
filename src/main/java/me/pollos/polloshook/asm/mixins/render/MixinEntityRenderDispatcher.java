package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.asm.ducks.entity.IEntity;
import me.pollos.polloshook.asm.ducks.render.IEntityRenderer;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderDispatcher.class})
public abstract class MixinEntityRenderDispatcher implements IEntityRenderer {
   @Shadow
   @Final
   public GameOptions gameOptions;
   @Shadow
   private boolean renderHitboxes;
   @Shadow
   private World world;
   @Shadow
   private boolean renderShadows;
   @Shadow
   private Quaternionf rotation;

   @Shadow
   public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T var1);

   @Shadow
   protected abstract void renderFire(MatrixStack var1, VertexConsumerProvider var2, Entity var3, Quaternionf var4);

   @Shadow
   private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius) {
   }

   @Shadow
   protected static void renderHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue) {
   }

   @Inject(
      method = {"renderFire"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderFireHook(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, Quaternionf rotation, CallbackInfo ci) {
      NoRender.EntityFireEvent event = NoRender.EntityFireEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }

   public <E extends Entity> void renderNoShadows(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      EntityRenderer entityRenderer = this.getRenderer(entity);

      try {
         Vec3d vec3d = entityRenderer.getPositionOffset(entity, tickDelta);
         double d = x + vec3d.getX();
         double e = y + vec3d.getY();
         double f = z + vec3d.getZ();
         matrices.push();
         matrices.translate(d, e, f);
         IEntity access = (IEntity)entity;
         boolean visible = (Boolean)entity.getDataTracker().get(access.getNameVisible());
         if (visible) {
            entity.getDataTracker().set(access.getNameVisible(), false);
         }

         entityRenderer.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
         if (visible) {
            entity.getDataTracker().set(access.getNameVisible(), true);
         }

         if (entity.doesRenderOnFire()) {
            this.renderFire(matrices, vertexConsumers, entity, MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf()));
         }

         matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
         if (this.renderShadows && !entity.isInvisible()) {
            renderShadow(matrices, vertexConsumers, entity, 0.0F, tickDelta, this.world, 32.0F);
         }

         if (this.renderHitboxes && !entity.isInvisible() && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            renderHitbox(matrices, vertexConsumers.getBuffer(RenderLayer.getLines()), entity, tickDelta, 1.0F, 1.0F, 1.0F);
         }

         matrices.pop();
      } catch (Throwable var26) {
         CrashReport crashReport = CrashReport.create(var26, "Rendering entity in world");
         CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
         entity.populateCrashReport(crashReportSection);
         CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
         crashReportSection2.add("Assigned renderer", entityRenderer);
         crashReportSection2.add("Location", CrashReportSection.createPositionString(this.world, x, y, z));
         crashReportSection2.add("Rotation", yaw);
         crashReportSection2.add("Delta", tickDelta);
         throw new CrashException(crashReport);
      }
   }
}
