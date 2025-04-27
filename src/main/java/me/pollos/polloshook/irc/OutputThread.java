package me.pollos.polloshook.irc;

import java.io.BufferedWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OutputThread extends Thread {
   private static final Logger log = LogManager.getLogger(OutputThread.class);
   private IrcServerConnection _bot = null;
   private Queue _outQueue = null;

   public OutputThread(IrcServerConnection bot, Queue outQueue) {
      this._bot = bot;
      this._outQueue = outQueue;
      this.setName("output-thread");
   }

   public static void sendRawLine(IrcServerConnection bot, BufferedWriter bwriter, String line) {
      if (line.length() > bot.getMaxLineLength() - 2) {
         line = line.substring(0, bot.getMaxLineLength() - 2);
      }

      synchronized(bwriter) {
         try {
            bwriter.write(line + "\r\n");
            bwriter.flush();
            if (log.isDebugEnabled()) {
               log.debug(">>>" + line);
            }
         } catch (Exception var6) {
         }

      }
   }

   public void run() {
      try {
         boolean running = true;

         while(running) {
            Thread.sleep(this._bot.getMessageDelay());
            String line = (String)this._outQueue.next();
            if (line != null) {
               this._bot.sendRawLine(line);
            } else {
               running = false;
            }
         }
      } catch (InterruptedException var3) {
      }

   }
}
