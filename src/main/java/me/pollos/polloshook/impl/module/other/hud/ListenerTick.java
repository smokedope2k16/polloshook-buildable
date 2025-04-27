package me.pollos.polloshook.impl.module.other.hud;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.gui.editor.core.PollosHUD;

public class ListenerTick extends ModuleListener<HUD, TickEvent> {
   public ListenerTick(HUD module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      HUD.runGlobalCheck();
      if (PlayerUtil.isNull() && mc.currentScreen instanceof PollosHUD) {
         mc.currentScreen = null;
         ((HUD)this.module).openEditor.setValue(false);
         ((HUD)this.module).opened = false;
      } else {
         if (mc.currentScreen instanceof PollosHUD) {
            ((HUD)this.module).opened = true;
            ((HUD)this.module).openEditor.setValue(false);
         }

      }
   }
}
