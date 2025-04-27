package me.pollos.polloshook.api.util.logging;

public record ClientMessage(String message, int id) {
   public ClientMessage(String message, int id) {
      this.message = message;
      this.id = id;
   }

   public String message() {
      return this.message;
   }

   public int id() {
      return this.id;
   }
}
