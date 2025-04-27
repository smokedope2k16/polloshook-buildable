package me.pollos.polloshook.impl.events.irc;


import me.pollos.polloshook.api.event.events.Event;

public final class IrcMessageEvent extends Event {
   private final String sender;
   private final String message;
   private final IrcMessageEvent.IrcMessageType type;

   
   public String getSender() {
      return this.sender;
   }

   
   public String getMessage() {
      return this.message;
   }

   
   public IrcMessageEvent.IrcMessageType getType() {
      return this.type;
   }

   
   public IrcMessageEvent(String sender, String message, IrcMessageEvent.IrcMessageType type) {
      this.sender = sender;
      this.message = message;
      this.type = type;
   }

   public static enum IrcMessageType {
      CHAT,
      JOIN,
      LEAVE,
      PING,
      CHAT_SERVER;

      public boolean isConnection() {
         return this == JOIN || this == LEAVE;
      }

      // $FF: synthetic method
      private static IrcMessageEvent.IrcMessageType[] $values() {
         return new IrcMessageEvent.IrcMessageType[]{CHAT, JOIN, LEAVE, PING, CHAT_SERVER};
      }
   }
}
