package me.pollos.polloshook.api.command.args;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;

public class PlayerArgument extends Argument {
   public PlayerArgument(String label) {
      super(label);
   }

   public String predict(String currentArg) {
      Iterator var2 = NetworkUtil.getOnlinePlayersProfile().iterator();

      GameProfile profile;
      do {
         if (!var2.hasNext()) {
            return currentArg;
         }

         profile = (GameProfile)var2.next();
      } while(!profile.getName().toLowerCase().startsWith(currentArg.toLowerCase()));

      return profile.getName();
   }
}
