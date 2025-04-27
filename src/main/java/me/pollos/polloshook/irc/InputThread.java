package me.pollos.polloshook.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputThread extends Thread {
   private static final Logger log = LogManager.getLogger(InputThread.class);
   private IrcServerConnection _bot = null;
   private Socket _socket = null;
   private BufferedReader _breader = null;
   private BufferedWriter _bwriter = null;
   private boolean _isConnected = true;
   private boolean _disposed = false;
   public static final int MAX_LINE_LENGTH = 512;

   public InputThread(IrcServerConnection bot, Socket socket, BufferedReader breader, BufferedWriter bwriter) {
      this._bot = bot;
      this._socket = socket;
      this._breader = breader;
      this._bwriter = bwriter;
      this.setName("input-thread");
   }

   public void sendRawLine(String line) {
      OutputThread.sendRawLine(this._bot, this._bwriter, line);
   }

   public boolean isConnected() {
      return this._isConnected;
   }

   public void run() {
      try {
         boolean running = true;

         while(running) {
            try {
               String line = null;

               while((line = this._breader.readLine()) != null) {
                  try {
                     if (log.isDebugEnabled()) {
                        log.debug("<<<" + line);
                     }

                     this._bot.handleLine(line);
                  } catch (Throwable var11) {
                     StringWriter sw = new StringWriter();
                     PrintWriter pw = new PrintWriter(sw);
                     var11.printStackTrace(pw);
                     pw.flush();
                     StringTokenizer tokenizer = new StringTokenizer(sw.toString(), "\r\n");
                     synchronized(this._bot) {
                        log.error("### Your implementation of PircBot is faulty and you have");
                        log.error("### allowed an uncaught Exception or Error to propagate from your code.");
                        log.error("### It may be possible for PircBot to continue operating normally.");
                        log.error("### Here is the stack trace that was produced:");
                        log.error("### ");

                        while(tokenizer.hasMoreTokens()) {
                           log.error("### " + tokenizer.nextToken());
                        }
                     }
                  }
               }

               if (line == null) {
                  running = false;
               }
            } catch (InterruptedIOException var12) {
               long var10001 = System.currentTimeMillis();
               this.sendRawLine("PING " + var10001 / 1000L);
            }
         }
      } catch (Exception var13) {
      }

      try {
         this._socket.close();
      } catch (Exception var9) {
      }

      if (!this._disposed) {
         log.debug("*** Disconnected.");
         this._isConnected = false;
         this._bot.getEventHandler().onDisconnect();
      }

   }

   public void dispose() {
      try {
         this._disposed = true;
         this._socket.close();
      } catch (Exception var2) {
      }

   }
}
