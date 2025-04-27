package me.pollos.polloshook.impl.events.misc;

import com.mojang.authlib.GameProfile;

import me.pollos.polloshook.api.event.events.Event;

public class GetSkinEvent extends Event {
   private final GameProfile gameProfile;

   
   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   
   public GetSkinEvent(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
   }
}
