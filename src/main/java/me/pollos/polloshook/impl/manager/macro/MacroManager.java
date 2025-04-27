package me.pollos.polloshook.impl.manager.macro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Initializable;
import me.pollos.polloshook.api.macro.DualMacro;
import me.pollos.polloshook.api.macro.SimpleMacro;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyPressAction;
import me.pollos.polloshook.impl.config.macro.DualMacroConfig;
import me.pollos.polloshook.impl.config.macro.MacroConfig;
import me.pollos.polloshook.impl.events.keyboard.KeyPressEvent;

public class MacroManager extends SubscriberImpl implements Initializable {
   private final List<SimpleMacro> simpleMacros = new ArrayList();
   private final List<DualMacro> dualMacros = new ArrayList();

   public MacroManager() {
      this.getListeners().add(new Listener<KeyPressEvent>(KeyPressEvent.class) {
         public void call(KeyPressEvent event) {
            if (mc.currentScreen == null) {
               if (event.getAction() != KeyPressAction.PRESS) {
                  return;
               }

               int key = event.getKey();
               Iterator var3 = MacroManager.this.dualMacros.iterator();

               while(var3.hasNext()) {
                  DualMacro macro = (DualMacro)var3.next();
                  if (key == macro.getKey()) {
                     macro.send();
                  }
               }

               var3 = MacroManager.this.simpleMacros.iterator();

               while(var3.hasNext()) {
                  SimpleMacro macrox = (SimpleMacro)var3.next();
                  if (key == macrox.getKey() && !macrox.isPaused()) {
                     macrox.send();
                  }
               }
            }

         }
      });
   }

   public void init() {
      (new MacroConfig("macros.txt")).load();
      (new DualMacroConfig("dualmacros.txt")).load();
   }

   public MacroManager start(String startMessage) {
      this.info(startMessage);
      return this;
   }

   public MacroManager finish(String finishMessage) {
      this.info(finishMessage);
      return this;
   }

   public List<DualMacro> getDualMacros() {
      return this.dualMacros;
   }

   public boolean containsDual(String label) {
      for(int i = this.dualMacros.size() - 1; i >= 0; --i) {
         if (((DualMacro)this.dualMacros.get(i)).getLabel().equalsIgnoreCase(label)) {
            return true;
         }
      }

      return false;
   }

   public void removeDual(String label) {
      this.dualMacros.removeIf((macro) -> {
         macro.getFirst().setPaused(false);
         macro.getSecond().setPaused(false);
         return macro.getLabel().equalsIgnoreCase(label);
      });
   }

   public List<SimpleMacro> getSimpleMacros() {
      return this.simpleMacros;
   }

   public SimpleMacro getSimple(String label) {
      for(int i = this.simpleMacros.size() - 1; i >= 0; --i) {
         if (((SimpleMacro)this.simpleMacros.get(i)).getLabel().equalsIgnoreCase(label)) {
            return this.get(i);
         }
      }

      return null;
   }

   public boolean containsSimple(String label) {
      for(int i = this.simpleMacros.size() - 1; i >= 0; --i) {
         if (this.get(i).getLabel().equalsIgnoreCase(label)) {
            return true;
         }
      }

      return false;
   }

   public SimpleMacro get(int i) {
      return (SimpleMacro)this.simpleMacros.get(i);
   }

   public void removeSimple(String label) {
      this.simpleMacros.removeIf((macro) -> {
         return macro.getLabel().equalsIgnoreCase(label);
      });
   }
}
