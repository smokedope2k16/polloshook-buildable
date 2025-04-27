package me.pollos.polloshook.impl.module.render.fullbright;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.LightTextureEvent;
import me.pollos.polloshook.impl.module.render.fullbright.mode.FullbrightMode;

public class ListenerLightTexture extends ModuleListener<Fullbright, LightTextureEvent> {
   public ListenerLightTexture(Fullbright module) {
      super(module, LightTextureEvent.class);
   }

   public void call(LightTextureEvent event) {
      if (((Fullbright)this.module).mode.getValue() == FullbrightMode.TEXTURE) {
         event.setColor(-1);
      }

   }
}
