package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.minecraft.SessionUtil;
import net.minecraft.client.session.Session;

public class SessionCommand extends Command {
   public SessionCommand() {
      super(new String[]{"Session", "setsession"}, new Argument("[name]"));
   }

   public String execute(String[] args) {
      if (args.length < 1) {
         return "Insert keyCodec username";
      } else {
         String name = args[1];
         SessionUtil.setSession(name);
         return "Changed offline session";
      }
   }

   private String getStringSession(Session session) {
      return String.format("SessionID: %s, UUID: %s, Username: %s, AccessToken: %s, ClientID: %s, Xuid: %s, AccountType: %s", session.getSessionId(), session.getUuidOrNull(), session.getUsername(), session.getClientId(), session.getClientId(), session.getXuid(), session.getAccountType().getName());
   }
}
