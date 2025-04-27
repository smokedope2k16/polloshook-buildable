package me.pollos.polloshook.impl.events.chat;


import me.pollos.polloshook.api.event.events.Event;

public class SendChatMessageEvent extends Event {
   private String message;

   
   public String getMessage() {
      return this.message;
   }

   
   public void setMessage(String message) {
      this.message = message;
   }

   
   public SendChatMessageEvent(String message) {
      this.message = message;
   }
}
