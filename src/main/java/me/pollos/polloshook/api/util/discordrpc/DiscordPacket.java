package me.pollos.polloshook.api.util.discordrpc;

import com.google.gson.JsonObject;

public record DiscordPacket(Opcode opcode, JsonObject data) {
   public DiscordPacket(Opcode opcode, JsonObject data) {
      this.opcode = opcode;
      this.data = data;
   }

   public Opcode opcode() {
      return this.opcode;
   }

   public JsonObject data() {
      return this.data;
   }
}
