package me.pollos.polloshook.asm.mixins.render;


import me.pollos.polloshook.asm.ducks.util.ICamera;
import me.pollos.polloshook.impl.module.render.viewclip.ViewClip;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public abstract class MixinCamera implements ICamera {
   @Shadow
   private Entity focusedEntity;
   @Unique
   ViewClip.ViewClipEvent event;

   @Shadow
   protected abstract float clipToSpace(float var1);

   @Shadow
   protected abstract void moveBy(float var1, float var2, float var3);

   @Redirect(
      method = {"update"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/Camera;moveBy(FFF)V"
)
   )
   private void updateHook(Camera instance, float f, float g, float h) {
      this.event = ViewClip.ViewClipEvent.create();
      if (this.event != null) {
         this.event.dispatch();
         if (!this.event.isCanceled()) {
            this.moveBy(-this.clipToSpace(4.0F), 0.0F, 0.0F);
         } else {
            this.moveBy(-this.clipToSpace(this.event.getAdd()), 0.0F, 0.0F);
         }

      }
   }

   @Inject(
      method = {"clipToSpace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void clipToSpaceHook(float f, CallbackInfoReturnable<Float> cir) {
      if (this.event != null) {
         ViewClip.CameraNoClipEvent noClipEvent = ViewClip.CameraNoClipEvent.create();
         noClipEvent.dispatch();
         if (this.event.isCanceled() && noClipEvent.isCanceled()) {
            cir.setReturnValue(this.event.getAdd());
         }

      }
   }

   
   public void setFocusedEntity(Entity focusedEntity) {
      this.focusedEntity = focusedEntity;
   }
}
