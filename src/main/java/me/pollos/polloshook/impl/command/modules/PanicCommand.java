package me.pollos.polloshook.impl.command.modules;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.ToggleableModule;

public class PanicCommand extends Command {
   protected final List<ToggleableModule> toggled = new ArrayList();

   public PanicCommand() {
      super(new String[]{"Panic", "p"});
   }

   public String execute(String[] args) {
      List<ToggleableModule> toggleables = Managers.getModuleManager().getModules().stream().filter((module) -> {
         return module instanceof ToggleableModule;
      }).map((module) -> {
         return (ToggleableModule)module;
      }).toList();
      if (!this.toggled.isEmpty()) {
         int size = this.toggled.size();
         this.toggled.forEach((toggleable) -> {
            toggleable.setEnabled(true);
         });
         this.toggled.clear();
         return "Toggling %s modules".formatted(new Object[]{size});
      } else {
         toggleables.forEach((toggleable) -> {
            if (toggleable.isEnabled()) {
               this.toggled.add(toggleable);
               toggleable.setEnabled(false);
            }

         });
         return "Toggled %s modules".formatted(new Object[]{this.toggled.size()});
      }
   }
}
