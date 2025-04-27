package me.pollos.polloshook.impl.module.movement.invwalk;

import java.util.ArrayList;
import java.util.Arrays;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.gui.IHandledScreen;
import me.pollos.polloshook.asm.ducks.gui.IScreen;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class ListenerUpdate extends ModuleListener<InvWalk, UpdateEvent> {
   public ListenerUpdate(InvWalk module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (((InvWalk)this.module).isValidScreen(mc.currentScreen)) {
         this.handleInvFill();
         ArrayList<KeyBinding> keys = new ArrayList(Arrays.asList(mc.options.forwardKey, mc.options.leftKey, mc.options.rightKey, mc.options.backKey, mc.options.jumpKey));
         if ((Boolean)((InvWalk)this.module).sneak.getValue()) {
            keys.add(mc.options.sneakKey);
         } else {
            keys.remove(mc.options.sneakKey);
         }

         if ((Boolean)((InvWalk)this.module).flyingCheck.getValue() && keys.contains(mc.options.sneakKey) && !mc.player.getAbilities().flying && (Boolean)((InvWalk)this.module).sneak.getValue()) {
            keys.remove(mc.options.sneakKey);
         }

         keys.forEach((key) -> {
            if (!key.isPressed()) {
               KeyBinding.setKeyPressed(key.getDefaultKey(), InputUtil.isKeyPressed(mc.getWindow().getHandle(), key.getDefaultKey().getCode()));
            }
         });
      }
   }

   protected void handleInvFill() {
      if ((Boolean)((InvWalk)this.module).inventoryFill.getValue()) {
         boolean chest = mc.player.currentScreenHandler instanceof GenericContainerScreenHandler;
         boolean shulk = mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler;
         boolean inv = mc.currentScreen instanceof InventoryScreen || mc.currentScreen instanceof CreativeInventoryScreen;
         if (mc.player.currentScreenHandler != null && (inv || chest || shulk)) {
            try {
               IHandledScreen handled = (IHandledScreen)mc.currentScreen;
               IScreen screen = (IScreen)mc.currentScreen;
               if (!handled.isMouseClicked()) {
                  return;
               }

               Slot slot = handled.slotAt((double)screen.getXMouse(), (double)screen.getYMouse());
               if (slot == null) {
                  return;
               }

               float factor = (20.0F - Managers.getTpsManager().getCurrentTps()) * 25.0F;
               boolean passed = ((InvWalk)this.module).timer.passed((double)(factor + 15.0F));
               if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), 340) && passed) {
                  handled.onClicked(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
                  ((InvWalk)this.module).timer.reset();
               }
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }

      }
   }
}
