package me.pollos.polloshook.impl.module.player.suicide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.player.suicide.mode.SuicideMode;
import net.minecraft.item.Items;

public class ListenerMotion extends ModuleListener<Suicide, MotionUpdateEvent> {
   private final StopWatch timer = new StopWatch();

   public ListenerMotion(Suicide module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (ItemUtil.getHotbarItemSlot(Items.END_CRYSTAL) == -1 && ((Suicide)this.module).mode.getValue() != SuicideMode.COMMAND) {
         ((Suicide)this.module).toggle();
      } else {
         if (((Suicide)this.module).mode.getValue() == SuicideMode.COMMAND) {
            NetworkUtil.sendInChat("/kill");
            ((Suicide)this.module).toggle();
         } else if ((Boolean)((Suicide)this.module).throwOut.getValue()) {
            this.tickThrow();
         }

      }
   }

   protected void tickThrow() {
      if (this.timer.passed(125L)) {
         List<Integer> slots = new ArrayList(List.of(45));
         if ((Boolean)((Suicide)this.module).armor.getValue()) {
            List<Integer> armors = List.of(5, 6, 7, 8);
            slots.addAll(armors);
         }

         Iterator var4 = slots.iterator();

         while(var4.hasNext()) {
            int i = (Integer)var4.next();
            if (InventoryUtil.getStack(i).getItem() != Items.AIR) {
               InventoryUtil.drop(i);
               this.timer.reset();
            }
         }

      }
   }
}
