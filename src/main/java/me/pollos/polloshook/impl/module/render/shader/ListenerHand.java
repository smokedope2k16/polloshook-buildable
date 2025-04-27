package me.pollos.polloshook.impl.module.render.shader;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.render.RenderHandEvent;
import me.pollos.polloshook.impl.module.render.nametags.Nametags;

public class ListenerHand extends ModuleListener<Shader, RenderHandEvent> {
   public ListenerHand(Shader module) {
      super(module, RenderHandEvent.class);
   }

   public void call(RenderHandEvent event) {
      if (event.isCanceled() && !((Nametags)Managers.getModuleManager().get(Nametags.class)).isIgnore()) {
         ((Shader)this.module).renderHand = false;
      }

   }
}
