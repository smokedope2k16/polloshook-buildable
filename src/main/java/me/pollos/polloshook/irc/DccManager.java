package me.pollos.polloshook.irc;

import java.util.StringTokenizer;
import java.util.Vector;

public class DccManager {
   private IrcServerConnection _bot;
   private Vector _awaitingResume = new Vector();

   public DccManager(IrcServerConnection bot) {
      this._bot = bot;
   }

   boolean processRequest(String nick, String login, String hostname, String request) {
      StringTokenizer tokenizer = new StringTokenizer(request);
      tokenizer.nextToken();
      String type = tokenizer.nextToken();
      String filename = tokenizer.nextToken();
      long address;
      int port;
      if (type.equals("SEND")) {
         address = Long.parseLong(tokenizer.nextToken());
         port = Integer.parseInt(tokenizer.nextToken());
         long size = -1L;

         try {
            size = Long.parseLong(tokenizer.nextToken());
         } catch (Exception var16) {
         }

         DccFileTransfer transfer = new DccFileTransfer(this._bot, this, nick, login, hostname, type, filename, address, port, size);
         this._bot.getEventHandler().onIncomingFileTransfer(transfer);
      } else {
         long progress;
         DccFileTransfer transfer;
         int i;
         if (type.equals("RESUME")) {
            port = Integer.parseInt(tokenizer.nextToken());
            progress = Long.parseLong(tokenizer.nextToken());
            transfer = null;
            synchronized(this._awaitingResume) {
               for(i = 0; i < this._awaitingResume.size(); ++i) {
                  transfer = (DccFileTransfer)this._awaitingResume.elementAt(i);
                  if (transfer.getNick().equals(nick) && transfer.getPort() == port) {
                     this._awaitingResume.removeElementAt(i);
                     break;
                  }
               }
            }

            if (transfer != null) {
               transfer.setProgress(progress);
               this._bot.sendCTCPCommand(nick, "DCC ACCEPT file.ext " + port + " " + progress);
            }
         } else if (type.equals("ACCEPT")) {
            port = Integer.parseInt(tokenizer.nextToken());
            progress = Long.parseLong(tokenizer.nextToken());
            transfer = null;
            synchronized(this._awaitingResume) {
               for(i = 0; i < this._awaitingResume.size(); ++i) {
                  transfer = (DccFileTransfer)this._awaitingResume.elementAt(i);
                  if (transfer.getNick().equals(nick) && transfer.getPort() == port) {
                     this._awaitingResume.removeElementAt(i);
                     break;
                  }
               }
            }

            if (transfer != null) {
               transfer.doReceive(transfer.getFile(), true);
            }
         } else {
            if (!type.equals("CHAT")) {
               return false;
            }

            address = Long.parseLong(tokenizer.nextToken());
            port = Integer.parseInt(tokenizer.nextToken());
            final DccChat chat = new DccChat(this._bot, nick, login, hostname, address, port);
            (new Thread() {
               public void run() {
                  DccManager.this._bot.getEventHandler().onIncomingChatRequest(chat);
               }
            }).start();
         }
      }

      return true;
   }

   public void addAwaitingResume(DccFileTransfer transfer) {
      synchronized(this._awaitingResume) {
         this._awaitingResume.addElement(transfer);
      }
   }

   public void removeAwaitingResume(DccFileTransfer transfer) {
      this._awaitingResume.removeElement(transfer);
   }
}
