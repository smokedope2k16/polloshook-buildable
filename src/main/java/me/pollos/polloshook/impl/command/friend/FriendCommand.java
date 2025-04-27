package me.pollos.polloshook.impl.command.friend;

import java.util.StringJoiner;
import me.pollos.polloshook.api.command.args.PlayerArgument;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import net.minecraft.util.Formatting;

public class FriendCommand extends Command {
   public FriendCommand() {
      super(new String[]{"Friend", "f", "frien", "amigo"}, new Argument("[add/del/list]") {
         public String predict(String currentArg) {
            if (currentArg.toLowerCase().startsWith("keyCodec")) {
               return "add";
            } else if (currentArg.toLowerCase().startsWith("d")) {
               return "del";
            } else {
               return currentArg.toLowerCase().startsWith("l") ? "list" : currentArg;
            }
         }
      }, new PlayerArgument("[player]"));
   }

   public String execute(String[] args) {
      String argument = args[1];
      String name;
      String message;
      if (argument.equalsIgnoreCase("add")) {
         name = args[2];
         if (args.length == 4) {
            if (Managers.getFriendManager().isFriend(name)) {
               return String.format("%s is already added", name);
            }

            message = args[3];
            Managers.getFriendManager().addFriend(name, message);
            return String.format("Added %s%s%s as keyCodec friend with alias [%s]", Formatting.AQUA, name, Formatting.GRAY, message);
         }

         if (args.length == 3) {
            if (Managers.getFriendManager().isFriend(name)) {
               return String.format("%s is already added", name);
            }

            Managers.getFriendManager().addFriend(name, name);
            return String.format("Added %s%s%s as keyCodec friend", Formatting.AQUA, name, Formatting.GRAY);
         }
      }

      if (args.length == 3) {
         name = args[2];
         if (argument.equalsIgnoreCase("del")) {
            if (!Managers.getFriendManager().isFriend(name)) {
               return String.format("%s isn't friended", name);
            }

            Managers.getFriendManager().removeFriend(name);
            return String.format("Removed %s from friend list", name);
         }
      }

      if (args.length == 2 && argument.equalsIgnoreCase("list")) {
         StringJoiner stringJoiner = new StringJoiner(", ");
         Managers.getFriendManager().getFriends().forEach((frd) -> {
            String format = String.format("\n%s %s[%s%s%s]%s", frd.getAlias(), Formatting.GRAY, Formatting.GRAY, frd.getLabel(), Formatting.GRAY, Formatting.GRAY);
            stringJoiner.add(format);
         });
         message = String.valueOf(stringJoiner);
         return String.format("Friends (%s): %s", Managers.getFriendManager().getFriends().size(), message);
      } else {
         return this.getInfo();
      }
   }
}