package me.pollos.polloshook.impl.module.combat.autototem;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;

public class ListenerDeath extends ModuleListener<AutoTotem, ScreenEvent> {
   public ListenerDeath(AutoTotem module) {
      super(module, ScreenEvent.class);
   }

   public void call(ScreenEvent event) {
      if ((Boolean)((AutoTotem)this.module).deathVerbose.getValue() && event.getScreen() instanceof DeathScreen) {
         ClientLogger var10000 = ClientLogger.getLogger();
         String var10001 = String.valueOf(Formatting.RED);
         var10000.log(var10001 + "<AutoTotem> Tick: " + ((AutoTotem)this.module).runningTick + "\nLast action: " + ((AutoTotem)this.module).lastAction + "ms \nCurrent action: " + ((AutoTotem)this.module).timer.getTime() + "ms \nOffHandItem (Server): " + ((AutoTotem)this.module).serverStack.getItem().getName().getString() + "\nOffhandItem (Client): " + mc.player.getOffHandStack().getItem().getName().getString() + "\nTotem Count: " + InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING), false);
      }

   }
}