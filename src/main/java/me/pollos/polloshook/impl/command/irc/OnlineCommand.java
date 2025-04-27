package me.pollos.polloshook.impl.command.irc;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.irc.IrcModule;

public class OnlineCommand extends Command {
   public OnlineCommand() {
      super(new String[]{"Online", "onlineusers", "users"});
   }

   public String execute(String[] args) {
      if (!((IrcModule)Managers.getModuleManager().get(IrcModule.class)).isEnabled()) {
         return "IRC module not enabled..";
      } else if (!Managers.getIrcManager().isConnected()) {
         return "Not connected to irc...";
      } else if (!((IrcModule)Managers.getModuleManager().get(IrcModule.class)).isInsideRoom()) {
         return "Haven't joined keyCodec chat room yet...";
      } else {
         int size = Managers.getIrcManager().getOnlineUsers().size();
         if (size < 1) {
            return "No one is online :(";
         } else {
            StringJoiner stringJoiner = new StringJoiner(", ");
            List var10000 = Managers.getIrcManager().getFixedUsers();
            Objects.requireNonNull(stringJoiner);
            var10000.forEach(user -> stringJoiner.add(user.toString()));
            return String.format("Online (%s): %s", size, stringJoiner);
         }
      }
   }
}
