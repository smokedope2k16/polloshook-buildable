package me.pollos.polloshook.impl.events.network;

import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;

public class ConnectionEvent {
   private final PlayerEntity player;
   private final String name;
   private final UUID uuid;

   private ConnectionEvent(String name, UUID uuid, PlayerEntity player) {
      this.player = player;
      this.name = name;
      this.uuid = uuid;
   }

   public PlayerEntity getPlayer() {
      return this.player;
   }

   public String getName() {
      return this.name == null && this.player != null ? this.player.getName().getString() : this.name;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public static class Leave extends ConnectionEvent {
      public Leave(String name, UUID uuid, PlayerEntity player) {
         super(name, uuid, player);
      }
   }

   public static class Join extends ConnectionEvent {
      public Join(String name, UUID uuid, PlayerEntity player) {
         super(name, uuid, player);
      }
   }
}
