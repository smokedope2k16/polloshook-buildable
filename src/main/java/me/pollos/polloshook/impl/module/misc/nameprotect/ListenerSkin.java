package me.pollos.polloshook.impl.module.misc.nameprotect;

import com.mojang.authlib.GameProfile;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.impl.events.misc.GetSkinEvent;
import me.pollos.polloshook.impl.module.misc.nameprotect.mode.SpoofSkinMode;

public class ListenerSkin extends ModuleListener<NameProtect, GetSkinEvent> {
   public ListenerSkin(NameProtect module) {
      super(module, GetSkinEvent.class);
   }

   public void call(GetSkinEvent event) {
      GameProfile profile = event.getGameProfile();
      String name = profile.getName();
      String mcPlayerName = EntityUtil.getName(mc.player);
      switch((SpoofSkinMode)((NameProtect)this.module).spoofSkin.getValue()) {
      case SELF:
         if (name.equalsIgnoreCase(mcPlayerName)) {
            event.setCanceled(true);
         }
         break;
      case FRIENDS:
         if (Managers.getFriendManager().isFriend(name)) {
            event.setCanceled(true);
         }
         break;
      case EVERYONE:
         event.setCanceled(true);
      }

   }
}
