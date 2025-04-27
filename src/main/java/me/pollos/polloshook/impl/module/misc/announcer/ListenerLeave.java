package me.pollos.polloshook.impl.module.misc.announcer;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.network.ConnectionEvent;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerMode;
import net.minecraft.util.Formatting;

public class ListenerLeave extends ModuleListener<Announcer, ConnectionEvent.Leave> {
   public ListenerLeave(Announcer module) {
      super(module, ConnectionEvent.Leave.class);
   }

   public void call(ConnectionEvent.Leave event) {
      if ((Boolean)((Announcer)this.module).greeter.getValue()) {
         if ((Boolean)((Announcer)this.module).leave.getValue()) {
            String name = event.getName();
            if (!name.equals(mc.player.getName().getString())) {
               Formatting color = Managers.getFriendManager().isFriend(name) ? Formatting.AQUA : Formatting.WHITE;
               switch((AnnouncerMode)((Announcer)this.module).mode.getValue()) {
               case BROADCAST:
                  ((Announcer)this.module).leavePlayer = name;
                  ((Announcer)this.module).addEvent(AnnouncerAction.LEAVE);
                  break;
               case CLIENTSIDE:
                  ClientLogger var10000 = ClientLogger.getLogger();
                  String var10001 = String.valueOf(color);
                  var10000.log(var10001 + ((Announcer)this.module).leaveMessage.replace("[player]", name));
               }

            }
         }
      }
   }
}
