package me.pollos.polloshook.irc.handlers;

import java.util.Date;
import me.pollos.polloshook.irc.IrcServerConnection;
import me.pollos.polloshook.irc.api.IrcEventHandlerBase;

public class IrcProtocolEventHandler extends IrcEventHandlerBase {
   private IrcServerConnection pircBot;

   public IrcProtocolEventHandler(IrcServerConnection pircBot) {
      this.pircBot = pircBot;
   }

   public void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
      this.pircBot.sendRawLine("NOTICE " + sourceNick + this.pircBot.getVersion());
   }

   public void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue) {
      this.pircBot.sendRawLine("NOTICE " + sourceNick + pingValue);
   }

   public void onServerPing(String response) {
      this.pircBot.sendRawLine("PONG " + response);
   }

   public void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
      this.pircBot.sendRawLine("NOTICE " + sourceNick + (new Date()).toString());
   }

   public void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) {
      this.pircBot.sendRawLine("NOTICE " + sourceNick + this.pircBot.getFinger());
   }
}
