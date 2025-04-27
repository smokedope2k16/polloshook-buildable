package me.pollos.polloshook.irc.beans;

public class ConnectionSettings {
   public String server = null;
   public int port = 6667;
   public boolean useSSL = false;
   public boolean verifySSL = false;
   public String password = null;

   public ConnectionSettings(String server) {
      this.server = server;
   }

   public ConnectionSettings clone() {
      ConnectionSettings cs = new ConnectionSettings(this.server);
      cs.port = this.port;
      cs.useSSL = this.useSSL;
      cs.verifySSL = this.verifySSL;
      cs.password = this.password;
      return cs;
   }
}
