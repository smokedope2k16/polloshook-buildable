package me.pollos.polloshook.impl.command.modules;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.args.KeyArgument;
import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.binds.keyboard.hold.HoldKeybind;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyPressAction;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.binds.mouse.MouseButton;
import me.pollos.polloshook.api.util.binds.mouse.MouseClickAction;
import me.pollos.polloshook.api.util.binds.mouse.MouseUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.keyboard.KeyPressEvent;
import me.pollos.polloshook.impl.events.keyboard.MouseClickEvent;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class SetKeyBindCommand extends Command {
   private static final int DEFAULT = -10000;
   private static final int ESC_FLAG_KEY = -10001;
   private int key = -10000;
   private MouseButton mouse = null;
   private ToggleableModule toggleable;
   private final Listener<UpdateEvent> updateEventListener = new Listener<UpdateEvent>(UpdateEvent.class) {
      public void call(UpdateEvent event) {
         if (SetKeyBindCommand.this.key == -10001) {
            SetKeyBindCommand.this.toggleable.setKeybind(Keybind.noKeyBind());
            ClientLogger.getLogger().log("Removed bind for module %s".formatted(new Object[]{SetKeyBindCommand.this.toggleable.getLabel()}));
            SetKeyBindCommand.this.onKeyPress();
         } else {
            if (SetKeyBindCommand.this.key != -10000 || SetKeyBindCommand.this.mouse != null) {
               if (SetKeyBindCommand.this.key == -10000) {
                  SetKeyBindCommand.this.toggleable.setKeybind(SetKeyBindCommand.this.getKeybind(MouseUtil.reversed(SetKeyBindCommand.this.mouse), SetKeyBindCommand.this.toggleable));
                  ClientLogger.getLogger().log("Bound module %s to (%s)".formatted(new Object[]{SetKeyBindCommand.this.toggleable.getLabel(), SetKeyBindCommand.this.mouse.name().toUpperCase()}));
                  SetKeyBindCommand.this.onKeyPress();
                  return;
               }

               SetKeyBindCommand.this.toggleable.setKeybind(SetKeyBindCommand.this.getKeybind(SetKeyBindCommand.this.key, SetKeyBindCommand.this.toggleable));
               ClientLogger.getLogger().log("Bound module %s to (%s)".formatted(new Object[]{SetKeyBindCommand.this.toggleable.getLabel(), KeyboardUtil.getKeyNameFromNumber(SetKeyBindCommand.this.key).toUpperCase()}));
               SetKeyBindCommand.this.onKeyPress();
            }

         }
      }
   };
   private final Listener<KeyPressEvent> keyPressEventListener = new Listener<KeyPressEvent>(KeyPressEvent.class) {
      public void call(KeyPressEvent event) {
         if (event.getAction() == KeyPressAction.PRESS) {
            if (KeyboardUtil.REMOVE_BINDS_LIST.contains(event.getKey())) {
               SetKeyBindCommand.this.key = -10001;
               return;
            }

            SetKeyBindCommand.this.key = event.getKey();
         }

      }
   };
   private final Listener<MouseClickEvent> mouseButtonEventListener = new Listener<MouseClickEvent>(MouseClickEvent.class) {
      public void call(MouseClickEvent event) {
         if (event.getAction() == MouseClickAction.PRESS) {
            SetKeyBindCommand.this.mouse = event.getKey();
         }

      }
   };

   public SetKeyBindCommand() {
      super(new String[]{"Bind", "SetKeyBind", "elementCodec"}, new ModuleArgument("[mod]"), new KeyArgument("[key]"));
   }

   public String execute(String[] args) {
      Module module = Managers.getModuleManager().getModuleByAlias(args[1]);
      if (module == null) {
         return "That module does not exist";
      } else {
         if (module instanceof ToggleableModule) {
            ToggleableModule tog = (ToggleableModule)module;
            this.toggleable = tog;
            if (args.length == 2) {
               PollosHook.getEventBus().register(this.keyPressEventListener);
               PollosHook.getEventBus().register(this.mouseButtonEventListener);
               PollosHook.getEventBus().register(this.updateEventListener);
               mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               return "Press keyCodec key to bind the module..";
            }

            if (args.length == 3) {
               String args2 = args[2];
               if (args2.equalsIgnoreCase("NONE")) {
                  tog.setKeybind(Keybind.noKeyBind());
                  return "Removed keybind for module %s".formatted(new Object[]{tog.getLabel()});
               }

               String var10000 = args2.toLowerCase();
               int bind = KeyboardUtil.getKeyNumberFromName("key.keyboard." + var10000.replace(" ", "."));
               tog.setKeybind(this.getKeybind(bind, tog));
               return "Bound module %s to (%s)".formatted(new Object[]{tog.getLabel(), KeyboardUtil.getKeyNameFromNumber(tog.getKeybind().getKey()).toUpperCase()});
            }
         }

         return "That module is not toggleable";
      }
   }

   private Keybind getKeybind(int key, ToggleableModule toggleable) {
      HoldKeybind hold = new HoldKeybind(key) {
         public void onKeyHold() {
         }

         public void onKeyRelease() {
            toggleable.setEnabled(false);
         }

         public void onKeyPress() {
            toggleable.setEnabled(true);
         }
      };
      Keybind toggle = new Keybind(key) {
         public void onKeyPress() {
            toggleable.toggle();
         }
      };
      return (Keybind)(toggleable.getKeybind() instanceof HoldKeybind ? hold : toggle);
   }

   private void onKeyPress() {
      PollosHook.getEventBus().unregister(this.keyPressEventListener);
      PollosHook.getEventBus().unregister(this.updateEventListener);
      PollosHook.getEventBus().unregister(this.mouseButtonEventListener);
      this.mouse = null;
      this.key = -10000;
   }
}
