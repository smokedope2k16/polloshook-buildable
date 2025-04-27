package me.pollos.polloshook.impl.module.combat.autototem;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.impl.events.block.InteractEvent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

public class ListenerInteract extends ModuleListener<AutoTotem, InteractEvent> {
   public ListenerInteract(AutoTotem module) {
      super(module, InteractEvent.class);
   }

   public void call(InteractEvent event) {
      if (mc.player != null && mc.interactionManager != null) {
         if (event.getHand() == Hand.OFF_HAND && mc.player.getMainHandStack().getItem() instanceof SwordItem && mc.options.useKey.isPressed()) {
            event.setCanceled(true);
         } else if (event.getHand() == Hand.MAIN_HAND) {
            Item mainHand = mc.player.getMainHandStack().getItem();
            Item offHand = mc.player.getMainHandStack().getItem();
            if (mainHand == Items.END_CRYSTAL && offHand == Items.OBSIDIAN && event.getHand() == Hand.MAIN_HAND) {
               IClientPlayerEntity player = (IClientPlayerEntity)mc.player;
               event.setCanceled(true);
               player.setActiveHand(Hand.OFF_HAND);
               mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
            }
         }

      }
   }
}
