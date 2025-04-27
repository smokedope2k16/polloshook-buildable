package me.pollos.polloshook.impl.module.render.viewclip;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class ViewClip extends ToggleableModule {
   protected final NumberValue<Float> factor = new NumberValue(3.5F, 0.1F, 10.0F, 0.1F, new String[]{"Distance", "dist", "range"});
   protected final Value<Boolean> noClip = new Value(false, new String[]{"NoClip", "clip", "cliptospace"});

   public ViewClip() {
      super(new String[]{"ViewClip", "cameraclip", "camera"}, Category.RENDER);
      this.offerValues(new Value[]{this.factor, this.noClip});
      this.offerListeners(new Listener[]{new ListenerViewClip(this), new ListenerCameraNoClip(this)});
   }

   public static class CameraNoClipEvent extends Event {
      
      private CameraNoClipEvent() {
      }

      
      public static ViewClip.CameraNoClipEvent create() {
         return new ViewClip.CameraNoClipEvent();
      }
   }

   public static class ViewClipEvent extends Event {
      private float add = 0.0F;

      
      public float getAdd() {
         return this.add;
      }

      
      public void setAdd(float add) {
         this.add = add;
      }

      
      private ViewClipEvent() {
      }

      
      public static ViewClip.ViewClipEvent create() {
         return new ViewClip.ViewClipEvent();
      }
   }
}
