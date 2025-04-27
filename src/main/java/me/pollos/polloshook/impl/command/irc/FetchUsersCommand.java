package me.pollos.polloshook.impl.command.irc;

import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.irc.beans.User;

public class FetchUsersCommand extends Command {
   public FetchUsersCommand() {
      super(new String[]{"FetchUsers", "getusers"});
   }

   public String execute(String[] args) {
      if (Managers.getIrcManager().isConnected()) {
         User[] users = Managers.getIrcManager().getUsers("#keqing4pollos");
         Managers.getIrcManager().addClientUsers(users);
         return "Fetched Users";
      } else {
         return "Not connected to IRC";
      }
   }
}
