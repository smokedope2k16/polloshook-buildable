package me.pollos.polloshook.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IdentServer extends Thread {
   private static final Logger log = LogManager.getLogger(IdentServer.class);
   private IrcServerConnection _bot;
   private String _login;
   private ServerSocket _ss = null;

   public IdentServer(IrcServerConnection bot, String login) {
      this._bot = bot;
      this._login = login;

      try {
         this._ss = new ServerSocket(113);
         this._ss.setSoTimeout(60000);
      } catch (Exception var4) {
         log.warn("*** Could not start the ident server on port 113.");
         return;
      }

      log.debug("*** Ident server running on port 113 for the next 60 seconds...");
      this.setName("identServer-thread");
      this.start();
   }

   public void run() {
      try {
         Socket socket = this._ss.accept();
         socket.setSoTimeout(60000);
         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         String line = reader.readLine();
         if (line != null) {
            log.debug("*** Ident request received: " + line);
            line = line + " : USERID : UNIX : " + this._login;
            writer.write(line + "\r\n");
            writer.flush();
            log.debug("*** Ident reply sent: " + line);
            writer.close();
         }
      } catch (Exception var6) {
      }

      try {
         this._ss.close();
      } catch (Exception var5) {
      }

      log.debug("*** The Ident server has been shut down.");
   }
}
