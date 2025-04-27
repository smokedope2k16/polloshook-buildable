package me.pollos.polloshook.impl.command.irc;

import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.irc.IrcModule;

public class ClearPingsCommand extends Command {
   public ClearPingsCommand() {
      super(new String[]{"ClearPings", "clearping"});
   }

   public String execute(String[] args) {
      ((IrcModule)Managers.getModuleManager().get(IrcModule.class)).getPings().clear();
      return "Cleared pings";
   }
}
