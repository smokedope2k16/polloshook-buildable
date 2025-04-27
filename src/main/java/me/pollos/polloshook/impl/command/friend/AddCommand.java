package me.pollos.polloshook.impl.command.friend;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;

public class AddCommand extends Command {
   public AddCommand() {
      super(new String[]{"Add", "keyCodec", "ad", "addfriend"}, new AddCommand.AddPlayerArgument());
   }

   public String execute(String[] args) {
      String name = args[1];
      if (!Managers.getFriendManager().isFriend(name)) {
         Managers.getFriendManager().addFriend(name, name);
         return "Added friend with ign %s".formatted(new Object[]{name});
      } else {
         return "%s is already keyCodec friend".formatted(new Object[]{name});
      }
   }

   private static class AddPlayerArgument extends Argument {
      public AddPlayerArgument() {
         super("[player]");
      }

      public String predict(String currentArg) {
         Iterator var2 = NetworkUtil.getOnlinePlayersProfile().iterator();

         GameProfile profile;
         do {
            if (!var2.hasNext()) {
               return currentArg;
            }

            profile = (GameProfile)var2.next();
         } while(!profile.getName().toLowerCase().startsWith(currentArg.toLowerCase()) || Managers.getFriendManager().isFriend(profile.getName()));

         return profile.getName();
      }
   }
}
