package me.pollos.polloshook.impl.module.other.hud.elements.draggable.clientmessages;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.entity.TotemPopEvent;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerPop extends ModuleListener<ClientMessages, TotemPopEvent> {
   public ListenerPop(ClientMessages module) {
      super(module, TotemPopEvent.class);
   }

   public void call(TotemPopEvent event) {
      if ((Boolean)((ClientMessages)this.module).totemPop.getValue()) {
         PlayerEntity entity = event.getPlayer();
         String name = entity.getName().getString();
         int pops = (Integer)Managers.getPopManager().getPopMap().get(name);
         ((ClientMessages)this.module).displayMessage("%s popped their %s totem".formatted(new Object[]{name, pops + TextUtil.toOrdinal(pops)}));
      }

   }
}
