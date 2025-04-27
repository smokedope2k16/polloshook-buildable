package me.pollos.polloshook.impl.module.combat.autolog;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ListenerUpdate extends ModuleListener<AutoLog, UpdateEvent> {
   public ListenerUpdate(AutoLog module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (mc.player != null) {
         if ((Boolean)((AutoLog)this.module).onEnter.getValue()) {
            Iterator var2 = Managers.getEntitiesManager().getPlayers().iterator();

            while(var2.hasNext()) {
               PlayerEntity player = (PlayerEntity)var2.next();
               if (!Managers.getFriendManager().isFriend(player) && player != mc.player) {
                  ((AutoLog)this.module).leave("Logged out because %s entered your visual range".formatted(new Object[]{EntityUtil.getName(player)}));
               }
            }
         }

         if ((Boolean)((AutoLog)this.module).fallDistance.getValue() && mc.player.fallDistance >= (Float)((AutoLog)this.module).fallDistanceCount.getValue()) {
            ItemStack stack = ItemUtil.getHeldItemStack(Items.TOTEM_OF_UNDYING);
            ((AutoLog)this.module).leave("Logged out with fall distance %.1f %s".formatted(new Object[]{mc.player.fallDistance, stack == null ? "with no totem in hand" : "with totem in hand"}));
         } else {
            int totems = InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING);
            if (!(Boolean)((AutoLog)this.module).totems.getValue() || totems <= (Integer)((AutoLog)this.module).totemCount.getValue()) {
               if (EntityUtil.getHealth(mc.player) <= (Float)((AutoLog)this.module).health.getValue()) {
                  ((AutoLog)this.module).leave("Logged out with health at %.1f and %s totems remaining".formatted(new Object[]{EntityUtil.getHealth(mc.player), totems}));
               }

            }
         }
      }
   }
}