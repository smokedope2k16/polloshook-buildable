package me.pollos.polloshook.impl.command.modules;

import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.binds.keyboard.hold.HoldKeybind;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;

public class HoldBindCommand extends Command {
   public HoldBindCommand() {
      super(new String[]{"HoldBind", "holdtoggle"}, new ModuleArgument("[module]"));
   }

   public String execute(String[] args) {
      String args1 = args[1];
      Module module = Managers.getModuleManager().getModuleByAlias(args1);
      if (module == null) {
         return "No module with label %s".formatted(new Object[]{module.getLabel()});
      } else if (module instanceof ToggleableModule) {
         final ToggleableModule toggleable = (ToggleableModule)module;
         Keybind keybind = toggleable.getKeybind();
         if (keybind instanceof HoldKeybind) {
            HoldKeybind hold = (HoldKeybind)keybind;
            toggleable.setKeybind(new Keybind(hold.getKey()) {
               public void onKeyPress() {
                  toggleable.toggle();
               }
            });
            return "Reverted %s hold bind to toggle bind".formatted(new Object[]{toggleable.getLabel()});
         } else {
            Keybind oldBind = toggleable.getKeybind();
            toggleable.setKeybind(new HoldKeybind(oldBind.getKey()) {
               public void onKeyHold() {
               }

               public void onKeyRelease() {
                  toggleable.setEnabled(false);
               }

               public void onKeyPress() {
                  toggleable.setEnabled(true);
               }
            });
            return "Changed %s keybind to hold bind".formatted(new Object[]{toggleable.getLabel()});
         }
      } else {
         return "Module has no keybind";
      }
   }
}
