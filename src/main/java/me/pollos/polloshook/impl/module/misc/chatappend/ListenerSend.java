package me.pollos.polloshook.impl.module.misc.chatappend;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.chat.SendChatMessageEvent;
import me.pollos.polloshook.impl.module.misc.chatappend.mode.ChatAppendMode;

public class ListenerSend extends SafeModuleListener<ChatAppend, SendChatMessageEvent> {
   public ListenerSend(ChatAppend module) {
      super(module, SendChatMessageEvent.class);
   }

   public void safeCall(SendChatMessageEvent event) {
      String message = event.getMessage();
      if (!ChatAppend.shouldFilter(message)) {
         StringBuilder builder = new StringBuilder(message);
         if ((Boolean)((ChatAppend)this.module).greenText.getValue()) {
            builder.insert(0, ">" + ((Boolean)((ChatAppend)this.module).spaced.getValue() ? " " : ""));
         }

         if (((ChatAppend)this.module).mode.getValue() != ChatAppendMode.OFF) {
            builder.insert(builder.length(), ((ChatAppendMode)((ChatAppend)this.module).mode.getValue()).getString());
         }

         event.setMessage(builder.toString());
         event.setCanceled(true);
      }
   }
}
