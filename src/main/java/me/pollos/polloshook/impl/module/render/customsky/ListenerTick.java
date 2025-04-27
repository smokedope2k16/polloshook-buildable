package me.pollos.polloshook.impl.module.render.customsky;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.util.Formatting;

public class ListenerTick extends ModuleListener<CustomSky, TickEvent> {
   public ListenerTick(CustomSky module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (!PlayerUtil.isNull()) {
         NoRender NO_RENDER = (NoRender)Managers.getModuleManager().get(NoRender.class);
         if (NO_RENDER != null && (Boolean)NO_RENDER.getFog().getValue() && (Boolean)((CustomSky)this.module).getCustomFogRange().getValue()) {
            ClientLogger.getLogger().log(String.valueOf(Formatting.RED) + "Disable: NoRender - Fog");
            ((CustomSky)this.module).getCustomFogRange().setValue(false);
         }

      }
   }
}
