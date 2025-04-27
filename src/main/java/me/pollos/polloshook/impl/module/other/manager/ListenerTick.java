package me.pollos.polloshook.impl.module.other.manager;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.asm.ducks.IMinecraftClient;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;

public class ListenerTick extends ModuleListener<Manager, GameLoopEvent> {
   public ListenerTick(Manager module) {
      super(module, GameLoopEvent.class);
   }

   public void call(GameLoopEvent event) {
      ((Manager)this.module).getFpsCalcThread().start();
      PollosHookThread.submit(() -> {
         if ((Boolean)((Manager)this.module).customDisplay.getValue()) {
            mc.getWindow().setTitle(((Manager)this.module).getDisplayText());
         }

         ((IMinecraftClient)mc).$updateWindowTitle();
         if (((Manager)this.module).getUnfocusedSound()) {
            if (!mc.isWindowFocused()) {
               mc.getSoundManager().pauseAll();
            } else {
               mc.getSoundManager().resumeAll();
            }
         }

      });
   }
}
