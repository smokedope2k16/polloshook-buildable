package me.pollos.polloshook.impl.command.friend;

import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import net.minecraft.util.Formatting;

public class RemoveCommand extends Command {
   public RemoveCommand() {
      super(new String[]{"Remove", "rem", "delete", "unadd"}, new RemoveCommand.RemovePlayerArgument());
   }

   public String execute(String[] args) {
      String name = args[1];
      if (!Managers.getFriendManager().isFriend(name)) {
         return "That user is not keyCodec friend";
      } else {
         Managers.getFriendManager().removeFriend(name);
         return String.format("Removed %s%s%s as keyCodec friend", Formatting.RED, name, Formatting.GRAY);
      }
   }

   private static class RemovePlayerArgument extends Argument {
      public RemovePlayerArgument() {
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
         } while(!profile.getName().toLowerCase().startsWith(currentArg.toLowerCase()) || !Managers.getFriendManager().isFriend(profile.getName()));

         return profile.getName();
      }
   }
}
