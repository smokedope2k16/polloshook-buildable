package me.pollos.polloshook.impl.module.other.irc;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.irc.IrcMessageEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.chatappend.ChatAppend;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

public class ListenerChat extends ModuleListener<IrcModule, PacketEvent.Send<ChatMessageC2SPacket>> {
   public ListenerChat(IrcModule module) {
      super(module, PacketEvent.Send.class, ChatMessageC2SPacket.class);
   }

   public void call(PacketEvent.Send<ChatMessageC2SPacket> event) {
      String message = ((ChatMessageC2SPacket)event.getPacket()).chatMessage();
      boolean flag = ChatAppend.shouldFilter(message);
      boolean ircFlag = (Boolean)((IrcModule)this.module).sendAllMessages.getValue() && !flag;
      String prefix = (String)((IrcModule)this.module).prefix.getValue();
      if (message.startsWith(prefix) || ircFlag && ((IrcModule)this.module).getValidMessage() != null && message.equalsIgnoreCase(((IrcModule)this.module).getValidMessage())) {
         event.setCanceled(true);
         if (!((IrcModule)this.module).isInsideRoom()) {
            ClientLogger.getLogger().log("You are not inside of keyCodec chat room yet, please wait");
            return;
         }

         if (Managers.getIrcManager().isConnected()) {
            String finalMsg = ((IrcModule)this.module).prefix.getParent().isVisible() ? message.replaceFirst(prefix, "") : message;
            if (!finalMsg.isEmpty()) {
               Managers.getIrcManager().sendMessage("#keqing4pollos", finalMsg);
               PollosHook.getEventBus().dispatch(new IrcMessageEvent(Managers.getIrcManager().getUsername(), finalMsg, IrcMessageEvent.IrcMessageType.CHAT));
            } else {
               ClientLogger.getLogger().log("type sum", false);
            }
         } else {
            ClientLogger.getLogger().log("You're not connected to irc, reconnecting...", false);
         }
      }

   }
}
