package me.pollos.polloshook.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.irc.IrcMessageEvent;
import me.pollos.polloshook.irc.api.IIrcEventHandler;
import me.pollos.polloshook.irc.beans.ConnectionSettings;
import me.pollos.polloshook.irc.beans.ReplyConstants;
import me.pollos.polloshook.irc.beans.User;
import me.pollos.polloshook.irc.ex.IrcException;
import me.pollos.polloshook.irc.ex.NickAlreadyInUseException;
import me.pollos.polloshook.irc.handlers.IrcProtocolEventHandler;
import me.pollos.polloshook.irc.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IrcServerConnection implements ReplyConstants {
   private static final Logger log = LogManager.getLogger(IrcServerConnection.class);
   public static final String VERSION = "1.7.0";
   private static final int OP_ADD = 1;
   private static final int OP_REMOVE = 2;
   private static final int VOICE_ADD = 3;
   private static final int VOICE_REMOVE = 4;
   private IIrcEventHandler eventHandler;
   private InputThread _inputThread = null;
   private OutputThread _outputThread = null;
   private String _charset = null;
   private InetAddress _inetAddress = null;
   private ConnectionSettings _connectionSettings = null;
   private Queue _outQueue = new Queue();
   private long _messageDelay = 1000L;
   private Hashtable _channels = new Hashtable();
   private Hashtable _topics = new Hashtable();
   private DccManager _dccManager = new DccManager(this);
   private int[] _dccPorts = null;
   private InetAddress _dccInetAddress = null;
   private boolean _autoNickChange = false;
   private boolean _verbose = false;
   private String _name = "PircBot";
   private String _nick;
   private String _username;
   private String _realname;
   private String _version;
   private String _finger;
   private String _channelPrefixes;

   public IIrcEventHandler getEventHandler() {
      return this.eventHandler;
   }

   public void setEventHandler(IIrcEventHandler eventHandler) {
      this.eventHandler = eventHandler;
   }

   public IrcServerConnection() {
      this._nick = this._name;
      this._username = "PircBot";
      this._realname = "PircBot";
      this._version = "PircBot 1.7.0 Java IRC Bot - www.jibble.org";
      this._finger = "You ought to be arrested for fingering keyCodec bot!";
      this._channelPrefixes = "#&+!";
      this.eventHandler = new IrcProtocolEventHandler(this);
   }

   public final void connect(String hostname) throws IOException, IrcException, NickAlreadyInUseException {
      ConnectionSettings cs = new ConnectionSettings(hostname);
      this.connect(cs);
   }

   public final void connect(String hostname, int port) throws IOException, IrcException, NickAlreadyInUseException {
      ConnectionSettings cs = new ConnectionSettings(hostname);
      cs.port = port;
      this.connect(cs);
   }

   public final void connect(String hostname, int port, String password) throws IOException, IrcException, NickAlreadyInUseException {
      ConnectionSettings cs = new ConnectionSettings(hostname);
      cs.port = port;
      cs.password = password;
      this.connect(cs);
   }

   public final void connect(ConnectionSettings cs) throws IOException, IrcException, NickAlreadyInUseException {
      ConnectionSettings _cs = cs.clone();
      this._connectionSettings = _cs;
      if (this.isConnected()) {
         throw new IOException("The PircBot is already connected to an IRC server.  Disconnect first.");
      } else {
         this.removeAllChannels();
         Socket socket;
         Object factory;
         SSLContext sc;
         if (_cs.useSSL) {
            try {
               if (_cs.verifySSL) {
                  factory = SSLSocketFactory.getDefault();
               } else {
                  sc = UnverifiedSSL.getUnverifiedSSLContext();
                  factory = sc.getSocketFactory();
               }

               socket = ((SocketFactory)factory).createSocket(_cs.server, _cs.port);
            } catch (Exception var14) {
               throw new IOException("SSL failure");
            }
         } else {
            socket = new Socket(_cs.server, _cs.port);
         }

         log.debug("*** Connected to server.");
         this._inetAddress = socket.getLocalAddress();
         factory = null;
         sc = null;
         InputStreamReader inputStreamReader;
         OutputStreamWriter outputStreamWriter;
         if (this.getEncoding() != null) {
            inputStreamReader = new InputStreamReader(socket.getInputStream(), this.getEncoding());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), this.getEncoding());
         } else {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
         }

         BufferedReader breader = new BufferedReader(inputStreamReader);
         BufferedWriter bwriter = new BufferedWriter(outputStreamWriter);
         if (_cs.password != null && !_cs.password.equals("")) {
            OutputThread.sendRawLine(this, bwriter, "PASS " + _cs.password);
         }

         String nick = this.getName();
         OutputThread.sendRawLine(this, bwriter, "NICK " + nick);
         String var10002 = this.getUserName();
         OutputThread.sendRawLine(this, bwriter, "USER " + var10002 + " 8 * :" + this.getRealName());
         this._inputThread = new InputThread(this, socket, breader, bwriter);
         String line = null;

         for(int tries = 1; (line = breader.readLine()) != null; this.setNick(nick)) {
            this.handleLine(line);
            int firstSpace = line.indexOf(" ");
            int secondSpace = line.indexOf(" ", firstSpace + 1);
            if (secondSpace >= 0) {
               String code = line.substring(firstSpace + 1, secondSpace);
               if (code.equals("004")) {
                  break;
               }

               if (code.equals("433")) {
                  if (!this._autoNickChange) {
                     socket.close();
                     this._inputThread = null;
                     throw new NickAlreadyInUseException(line);
                  }

                  ++tries;
                  String var10000 = this.getName();
                  nick = var10000 + tries;
                  OutputThread.sendRawLine(this, bwriter, "NICK " + nick);
               } else if (!code.equals("439") && (code.startsWith("5") || code.startsWith("4"))) {
                  socket.close();
                  this._inputThread = null;
                  throw new IrcException("Could not log into the IRC server: " + line);
               }
            }
         }

         log.debug("*** Logged onto server.");
         socket.setSoTimeout(300000);
         this._inputThread.start();
         if (this._outputThread == null) {
            this._outputThread = new OutputThread(this, this._outQueue);
            this._outputThread.start();
         }

         this.eventHandler.onConnect();
      }
   }

   public final void reconnect() throws IOException, IrcException, NickAlreadyInUseException {
      if (this.getServer() == null) {
         throw new IrcException("Cannot reconnect to an IRC server because we were never connected to one previously!");
      } else {
         this.connect(this._connectionSettings);
      }
   }

   public final void disconnect() {
      this.quitServer();
   }

   public void setAutoNickChange(boolean autoNickChange) {
      this._autoNickChange = autoNickChange;
   }

   public final void startIdentServer() {
      new IdentServer(this, this.getLogin());
   }

   public final void joinChannel(String channel) {
      this.sendRawLine("JOIN " + channel);
   }

   public final void joinChannel(String channel, String key) {
      this.joinChannel(channel + " " + key);
   }

   public final void partChannel(String channel) {
      this.sendRawLine("PART " + channel);
   }

   public final void partChannel(String channel, String reason) {
      this.sendRawLine("PART " + channel + " :" + reason);
   }

   public final void quitServer() {
      this.quitServer("");
   }

   public final void quitServer(String reason) {
      this.sendRawLine("QUIT :" + reason);
   }

   public final void sendRawLine(String line) {
      if (this.isConnected()) {
         this._inputThread.sendRawLine(line);
      }

   }

   public final void sendRawLineViaQueue(String line) {
      if (line == null) {
         throw new IllegalArgumentException("Cannot send null messages to server");
      } else {
         if (this.isConnected()) {
            this._outQueue.add(line);
         }

      }
   }

   public final void sendMessage(String target, String message) {
      this._outQueue.add("PRIVMSG " + target + " :" + message);
   }

   public final void sendMessage(String user, String channel, String message) {
      if (channel == null) {
         if (user == null) {
            throw new IllegalArgumentException("Neither user nor channel set, can't send the message: " + message);
         }

         this.sendMessage(user, message);
      } else {
         String whoFor = user == null ? "" : user + ": ";
         this.sendMessage(channel, whoFor + message);
      }

   }

   public final void sendAction(String target, String action) {
      this.sendCTCPCommand(target, "ACTION " + action);
   }

   public final void sendNotice(String target, String notice) {
      this._outQueue.add("NOTICE " + target + " :" + notice);
   }

   public final void sendCTCPCommand(String target, String command) {
      this._outQueue.add("PRIVMSG " + target + command);
   }

   public final void changeNick(String newNick) {
      this.sendRawLine("NICK " + newNick);
   }

   public final void identify(String password) {
      this.sendRawLine("NICKSERV IDENTIFY " + password);
   }

   public final void setMode(String channel, String mode) {
      this.sendRawLine("MODE " + channel + " " + mode);
   }

   public final void sendInvite(String nick, String channel) {
      this.sendRawLine("INVITE " + nick + " :" + channel);
   }

   public final void ban(String channel, String hostmask) {
      this.sendRawLine("MODE " + channel + " +elementCodec " + hostmask);
   }

   public final void unBan(String channel, String hostmask) {
      this.sendRawLine("MODE " + channel + " -elementCodec " + hostmask);
   }

   public final void op(String channel, String nick) {
      this.setMode(channel, "+o " + nick);
   }

   public final void deOp(String channel, String nick) {
      this.setMode(channel, "-o " + nick);
   }

   public final void voice(String channel, String nick) {
      this.setMode(channel, "+v " + nick);
   }

   public final void deVoice(String channel, String nick) {
      this.setMode(channel, "-v " + nick);
   }

   public final void setTopic(String channel, String topic) {
      this.sendRawLine("TOPIC " + channel + " :" + topic);
   }

   public final void kick(String channel, String nick) {
      this.kick(channel, nick, "");
   }

   public final void kick(String channel, String nick, String reason) {
      this.sendRawLine("KICK " + channel + " " + nick + " :" + reason);
   }

   public final void listChannels() {
      this.listChannels((String)null);
   }

   public final void listChannels(String parameters) {
      if (parameters == null) {
         this.sendRawLine("LIST");
      } else {
         this.sendRawLine("LIST " + parameters);
      }

   }

   public final DccFileTransfer dccSendFile(File file, String nick, int timeout) {
      DccFileTransfer transfer = new DccFileTransfer(this, this._dccManager, file, nick, timeout);
      transfer.doSend(true);
      return transfer;
   }

   protected final void dccReceiveFile(File file, long address, int port, int size) {
      throw new RuntimeException("dccReceiveFile is deprecated, please use sendFile");
   }

   public final DccChat dccSendChatRequest(String nick, int timeout) {
      DccChat chat = null;

      try {
         ServerSocket ss = null;
         int[] ports = this.getDccPorts();
         int i;
         if (ports == null) {
            ss = new ServerSocket(0);
         } else {
            i = 0;

            while(i < ports.length) {
               try {
                  ss = new ServerSocket(ports[i]);
                  break;
               } catch (Exception var12) {
                  ++i;
               }
            }

            if (ss == null) {
               throw new IOException("All ports returned by getDccPorts() are in use.");
            }
         }

         ss.setSoTimeout(timeout);
         i = ss.getLocalPort();
         InetAddress inetAddress = this.getDccInetAddress();
         if (inetAddress == null) {
            inetAddress = this.getInetAddress();
         }

         byte[] ip = inetAddress.getAddress();
         long ipNum = Utils.ipToLong(ip);
         this.sendCTCPCommand(nick, "DCC CHAT chat " + ipNum + " " + i);
         Socket socket = ss.accept();
         ss.close();
         chat = new DccChat(this, nick, socket);
      } catch (Exception var13) {
      }

      return chat;
   }

   protected final DccChat dccAcceptChatRequest(String sourceNick, long address, int port) {
      throw new RuntimeException("dccAcceptChatRequest is deprecated, please use onIncomingChatRequest");
   }

   protected void handleLine(String line) {
      if (line.startsWith("PING ")) {
         this.eventHandler.onServerPing(line.substring(5));
      } else {
         String sourceNick = "";
         String sourceLogin = "";
         String sourceHostname = "";
         StringTokenizer tokenizer = new StringTokenizer(line);
         String senderInfo = tokenizer.nextToken();
         String command = tokenizer.nextToken();
         String target = null;
         int exclamation = senderInfo.indexOf("!");
         int at = senderInfo.indexOf("@");
         String mode;
         if (senderInfo.startsWith(":")) {
            int code;
            if (exclamation > 0 && at > 0 && exclamation < at) {
               sourceNick = senderInfo.substring(1, exclamation);
               sourceLogin = senderInfo.substring(exclamation + 1, at);
               sourceHostname = senderInfo.substring(at + 1);
               mode = String.format("PRIVMSG %s :", "#keqing4pollos");
               if (line.contains(mode)) {
                  code = line.indexOf(mode);
                  if (code > 0) {
                     String chatMessage = line.substring(code + mode.length());
                     IrcMessageEvent event = new IrcMessageEvent(sourceNick, chatMessage, IrcMessageEvent.IrcMessageType.CHAT_SERVER);
                     PollosHook.getEventBus().dispatch(event);
                  }
               }
            } else {
               if (!tokenizer.hasMoreTokens()) {
                  this.eventHandler.onUnknown(line);
                  return;
               }

               mode = command;
               code = -1;

               try {
                  code = Integer.parseInt(mode);
               } catch (NumberFormatException var15) {
               }

               if (code != -1) {
                  String response = line.substring(line.indexOf(command, senderInfo.length()) + 4, line.length());
                  this.processServerResponse(code, response);
                  return;
               }

               sourceNick = senderInfo;
               target = command;
            }
         }

         command = command.toUpperCase();
         if (sourceNick.startsWith(":")) {
            sourceNick = sourceNick.substring(1);
         }

         if (target == null) {
            target = tokenizer.nextToken();
         }

         if (target.startsWith(":")) {
            target = target.substring(1);
         }

         if (command.equals("PRIVMSG") && line.indexOf(":\u0001") > 0 && line.endsWith("\u0001")) {
            mode = line.substring(line.indexOf(":\u0001") + 2, line.length() - 1);
            if (mode.equals("VERSION")) {
               this.eventHandler.onVersion(sourceNick, sourceLogin, sourceHostname, target);
            } else if (mode.startsWith("ACTION ")) {
               this.eventHandler.onAction(sourceNick, sourceLogin, sourceHostname, target, mode.substring(7));
            } else if (mode.startsWith("PING ")) {
               this.eventHandler.onPing(sourceNick, sourceLogin, sourceHostname, target, mode.substring(5));
            } else if (mode.equals("TIME")) {
               this.eventHandler.onTime(sourceNick, sourceLogin, sourceHostname, target);
            } else if (mode.equals("FINGER")) {
               this.eventHandler.onFinger(sourceNick, sourceLogin, sourceHostname, target);
            } else if ((tokenizer = new StringTokenizer(mode)).countTokens() >= 5 && tokenizer.nextToken().equals("DCC")) {
               boolean success = this._dccManager.processRequest(sourceNick, sourceLogin, sourceHostname, mode);
               if (!success) {
                  this.eventHandler.onUnknown(line);
               }
            } else {
               this.eventHandler.onUnknown(line);
            }
         } else if (command.equals("PRIVMSG") && this._channelPrefixes.indexOf(target.charAt(0)) >= 0) {
            this.eventHandler.onMessage(target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
         } else if (command.equals("PRIVMSG")) {
            this.eventHandler.onPrivateMessage(sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
         } else if (command.equals("JOIN")) {
            this.addUser(target, new User("", sourceNick));
            IrcMessageEvent event = new IrcMessageEvent(sourceNick, " joined", IrcMessageEvent.IrcMessageType.JOIN);
            PollosHook.getEventBus().dispatch(event);
            this.eventHandler.onJoin(target, sourceNick, sourceLogin, sourceHostname);
         } else if (command.equals("PART")) {
            this.removeUser(target, sourceNick);
            if (sourceNick.equals(this.getNick())) {
               this.removeChannel(target);
            }

            this.eventHandler.onPart(target, sourceNick, sourceLogin, sourceHostname);
         } else if (command.equals("NICK")) {
            this.renameUser(sourceNick, target);
            if (sourceNick.equals(this.getNick())) {
               this.setNick(target);
            }

            this.eventHandler.onNickChange(sourceNick, sourceLogin, sourceHostname, target);
         } else if (command.equals("NOTICE")) {
            this.eventHandler.onNotice(sourceNick, sourceLogin, sourceHostname, target, line.substring(line.indexOf(" :") + 2));
         } else if (command.equals("QUIT")) {
            if (sourceNick.equals(this.getNick())) {
               this.removeAllChannels();
            } else {
               this.removeUser(sourceNick);
            }

            IrcMessageEvent event = new IrcMessageEvent(sourceNick, " left", IrcMessageEvent.IrcMessageType.LEAVE);
            PollosHook.getEventBus().dispatch(event);
            this.eventHandler.onQuit(sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
         } else if (command.equals("KICK")) {
            mode = tokenizer.nextToken();
            if (mode.equals(this.getNick())) {
               this.removeChannel(target);
            }

            this.removeUser(target, mode);
            this.eventHandler.onKick(target, sourceNick, sourceLogin, sourceHostname, mode, line.substring(line.indexOf(" :") + 2));
         } else if (command.equals("MODE")) {
            mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
            if (mode.startsWith(":")) {
               mode = mode.substring(1);
            }

            this.processMode(target, sourceNick, sourceLogin, sourceHostname, mode);
         } else if (command.equals("TOPIC")) {
            this.eventHandler.onTopic(target, line.substring(line.indexOf(" :") + 2), sourceNick, System.currentTimeMillis(), true);
         } else if (command.equals("INVITE")) {
            this.eventHandler.onInvite(target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
         } else {
            this.eventHandler.onUnknown(line);
         }

      }
   }

   private void processServerResponse(int code, String response) {
    this.eventHandler.onServerResponse(code, response);

    switch (code) {
        case 322:
            int firstSpaceIndex = response.indexOf(' ');
            int secondSpaceIndex = response.indexOf(' ', firstSpaceIndex + 1);
            int thirdSpaceIndex = response.indexOf(' ', secondSpaceIndex + 1);
            int colonIndex = response.indexOf(':');

            if (firstSpaceIndex == -1 || secondSpaceIndex == -1 || thirdSpaceIndex == -1 || colonIndex == -1) {
                 break;
            }

            String channelName322 = response.substring(firstSpaceIndex + 1, secondSpaceIndex);
            String topic322 = response.substring(colonIndex + 1);
            int userCount322 = 0;

            try {
                userCount322 = Integer.parseInt(response.substring(secondSpaceIndex + 1, thirdSpaceIndex));
            } catch (NumberFormatException e) {
            }

            this.eventHandler.onChannelInfo(channelName322, userCount322, topic322);
            break;

        case 332:
            int spaceIndex332 = response.indexOf(' ');
            int colonIndex332 = response.indexOf(':');

             if (spaceIndex332 == -1 || colonIndex332 == -1) {
                 break;
             }

            String channelName332 = response.substring(spaceIndex332 + 1, colonIndex332);
            String topic332 = response.substring(colonIndex332 + 1);

            this._topics.put(channelName332, topic332);
            this.eventHandler.onTopic(channelName332, topic332);
            break;

        case 333:
            StringTokenizer tokenizer333 = new StringTokenizer(response);

            if (!tokenizer333.hasMoreTokens()) break; tokenizer333.nextToken();

            if (!tokenizer333.hasMoreTokens()) break; String channelName333 = tokenizer333.nextToken();
            if (!tokenizer333.hasMoreTokens()) break; String setBy333 = tokenizer333.nextToken();
            if (!tokenizer333.hasMoreTokens()) break; String timestampStr = tokenizer333.nextToken();

            long date333 = 0L;
            try {
                date333 = Long.parseLong(timestampStr) * 1000L;
            } catch (NumberFormatException e) {
            }

            String topic333 = (String) this._topics.get(channelName333);
            this._topics.remove(channelName333);

            this.eventHandler.onTopic(channelName333, topic333, setBy333, date333, false);
            break;

        case 353:
            int colonSpaceIndex353 = response.indexOf(" :");

            if (colonSpaceIndex353 == -1) {
                 break;
            }

            int lastSpaceBeforeChannel353 = response.lastIndexOf(' ', colonSpaceIndex353 - 1);

            if (lastSpaceBeforeChannel353 == -1) {
                 break;
            }

            String channelName353 = response.substring(lastSpaceBeforeChannel353 + 1, colonSpaceIndex353);

            String userListString = response.substring(colonSpaceIndex353 + 2);
            StringTokenizer tokenizer353 = new StringTokenizer(userListString);

            while (tokenizer353.hasMoreTokens()) {
                String fullNick = tokenizer353.nextToken();
                String prefix353 = "";
                String nick353 = fullNick;

                if (nick353.startsWith("@")) {
                    prefix353 = "@";
                    nick353 = nick353.substring(1);
                } else if (nick353.startsWith("+")) {
                    prefix353 = "+";
                    nick353 = nick353.substring(1);
                }
                else if (nick353.startsWith(".")) {
                     prefix353 = ".";
                     nick353 = nick353.substring(1);
                }

                this.addUser(channelName353, new User(prefix353, nick353));
            }
            break;

        case 366:
            int firstSpaceIndex366 = response.indexOf(' ');
             if (firstSpaceIndex366 == -1) {
                 break;
             }
            int colonIndex366 = response.indexOf(':', firstSpaceIndex366 + 1);

             if (colonIndex366 == -1) {
                  break;
             }

            String channelName366 = response.substring(firstSpaceIndex366 + 1, colonIndex366);

            User[] users366 = this.getUsers(channelName366);
            Managers.getIrcManager().addClientUsers(users366);
            this.eventHandler.onUserList(channelName366, users366);
            break;

        default:
            break;
    }
}

private void processMode(String target, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
   if (this._channelPrefixes != null && this._channelPrefixes.indexOf(target.charAt(0)) >= 0) {
       String channel = target;
       StringTokenizer tok = new StringTokenizer(mode);

       String[] params = new String[tok.countTokens()];
       int tokenIndex = 0;
       while (tok.hasMoreTokens()) {
           params[tokenIndex++] = tok.nextToken();
       }

       char plusMinus = ' ';
       int paramIndex = 1;

       for (int i = 0; i < params[0].length(); ++i) {
           char modeChar = params[0].charAt(i);

           if (modeChar == '+' || modeChar == '-') {
               plusMinus = modeChar;
           } else {
               switch (modeChar) {
                   case 'o':
                       if (paramIndex < params.length) {
                           String opNick = params[paramIndex];
                           if (plusMinus == '+') {
                               this.updateUser(channel, 1, opNick);
                               this.eventHandler.onOp(channel, sourceNick, sourceLogin, sourceHostname, opNick);
                           } else {
                               this.updateUser(channel, 2, opNick);
                               this.eventHandler.onDeop(channel, sourceNick, sourceLogin, sourceHostname, opNick);
                           }
                           paramIndex++;
                       }
                       break;

                   case 'v':
                        if (paramIndex < params.length) {
                           String voiceNick = params[paramIndex];
                           if (plusMinus == '+') {
                               this.updateUser(channel, 3, voiceNick);
                               this.eventHandler.onVoice(channel, sourceNick, sourceLogin, sourceHostname, voiceNick);
                           } else {
                               this.updateUser(channel, 4, voiceNick);
                               this.eventHandler.onDeVoice(channel, sourceNick, sourceLogin, sourceHostname, voiceNick);
                           }
                           paramIndex++;
                       }
                       break;

                   case 'k':
                        if (paramIndex < params.length) {
                           String key = params[paramIndex];
                           if (plusMinus == '+') {
                               this.eventHandler.onSetChannelKey(channel, sourceNick, sourceLogin, sourceHostname, key);
                           } else {
                               this.eventHandler.onRemoveChannelKey(channel, sourceNick, sourceLogin, sourceHostname, key);
                           }
                           paramIndex++;
                        }
                       break;

                   case 'l':
                        if (plusMinus == '+') {
                            if (paramIndex < params.length) {
                               String limitStr = params[paramIndex];
                               try {
                                   int limit = Integer.parseInt(limitStr);
                                   this.eventHandler.onSetChannelLimit(channel, sourceNick, sourceLogin, sourceHostname, limit);
                               } catch (NumberFormatException e) {
                               }
                               paramIndex++;
                            }
                        } else {
                            this.eventHandler.onRemoveChannelLimit(channel, sourceNick, sourceLogin, sourceHostname);
                        }
                       break;

                   case 'b':
                        if (paramIndex < params.length) {
                           String banMask = params[paramIndex];
                           if (plusMinus == '+') {
                               this.eventHandler.onSetChannelBan(channel, sourceNick, sourceLogin, sourceHostname, banMask);
                           } else {
                               this.eventHandler.onRemoveChannelBan(channel, sourceNick, sourceLogin, sourceHostname, banMask);
                           }
                           paramIndex++;
                        }
                       break;

                   case 't':
                       if (plusMinus == '+') {
                           this.eventHandler.onSetTopicProtection(channel, sourceNick, sourceLogin, sourceHostname);
                       } else {
                           this.eventHandler.onRemoveTopicProtection(channel, sourceNick, sourceLogin, sourceHostname);
                       }
                       break;

                   case 'n':
                       if (plusMinus == '+') {
                           this.eventHandler.onSetNoExternalMessages(channel, sourceNick, sourceLogin, sourceHostname);
                       } else {
                           this.eventHandler.onRemoveNoExternalMessages(channel, sourceNick, sourceLogin, sourceHostname);
                       }
                       break;

                   case 'i':
                       if (plusMinus == '+') {
                           this.eventHandler.onSetInviteOnly(channel, sourceNick, sourceLogin, sourceHostname);
                       } else {
                           this.eventHandler.onRemoveInviteOnly(channel, sourceNick, sourceLogin, sourceHostname);
                       }
                       break;

                   case 'm':
                       if (plusMinus == '+') {
                           this.eventHandler.onSetModerated(channel, sourceNick, sourceLogin, sourceHostname);
                       } else {
                           this.eventHandler.onRemoveModerated(channel, sourceNick, sourceLogin, sourceHostname);
                       }
                       break;

                   case 'p':
                       if (plusMinus == '+') {
                           this.eventHandler.onSetPrivate(channel, sourceNick, sourceLogin, sourceHostname);
                       } else {
                           this.eventHandler.onRemovePrivate(channel, sourceNick, sourceLogin, sourceHostname);
                       }
                       break;

                   case 's':
                       if (plusMinus == '+') {
                           this.eventHandler.onSetSecret(channel, sourceNick, sourceLogin, sourceHostname);
                       } else {
                           this.eventHandler.onRemoveSecret(channel, sourceNick, sourceLogin, sourceHostname);
                       }
                       break;

                   default:
                       break;
               }
           }
       }

       this.eventHandler.onMode(channel, sourceNick, sourceLogin, sourceHostname, mode);

   } else {
       this.eventHandler.onUserMode(target, sourceNick, sourceLogin, sourceHostname, mode);
   }
}

   public final void setVerbose(boolean verbose) {
      this._verbose = verbose;
   }

   public final void setName(String name) {
      this._name = name;
   }

   private void setNick(String nick) {
      this._nick = nick;
   }

   /** @deprecated */
   @Deprecated
   protected final void setLogin(String login) {
      this._username = login;
   }

   public final void setUserName(String username) {
      this._username = username;
   }

   public final void setRealName(String realname) {
      this._realname = realname;
   }

   protected final void setVersion(String version) {
      this._version = version;
   }

   protected final void setFinger(String finger) {
      this._finger = finger;
   }

   public final String getName() {
      return this._name;
   }

   public String getNick() {
      return this._nick;
   }

   /** @deprecated */
   @Deprecated
   public final String getLogin() {
      return this._username;
   }

   public final String getUserName() {
      return this._username;
   }

   public final String getRealName() {
      return this._realname;
   }

   public final String getVersion() {
      return this._version;
   }

   public final String getFinger() {
      return this._finger;
   }

   public final synchronized boolean isConnected() {
      return this._inputThread != null && this._inputThread.isConnected();
   }

   public final void setMessageDelay(long delay) {
      if (delay < 0L) {
         throw new IllegalArgumentException("Cannot have keyCodec negative time.");
      } else {
         this._messageDelay = delay;
      }
   }

   public final long getMessageDelay() {
      return this._messageDelay;
   }

   public final int getMaxLineLength() {
      return 512;
   }

   public final int getOutgoingQueueSize() {
      return this._outQueue.size();
   }

   public final String getServer() {
      return this._connectionSettings == null ? null : this._connectionSettings.server;
   }

   public final int getPort() {
      return this._connectionSettings == null ? -1 : this._connectionSettings.port;
   }

   public final boolean useSSL() {
      return this._connectionSettings == null ? false : this._connectionSettings.useSSL;
   }

   public final String getPassword() {
      return this._connectionSettings == null ? null : this._connectionSettings.password;
   }

   public void setEncoding(String charset) throws UnsupportedEncodingException {
      "".getBytes(charset);
      this._charset = charset;
   }

   public String getEncoding() {
      return this._charset;
   }

   public InetAddress getInetAddress() {
      return this._inetAddress;
   }

   public void setDccInetAddress(InetAddress dccInetAddress) {
      this._dccInetAddress = dccInetAddress;
   }

   public InetAddress getDccInetAddress() {
      return this._dccInetAddress;
   }

   public int[] getDccPorts() {
      return this._dccPorts != null && this._dccPorts.length != 0 ? (int[])this._dccPorts.clone() : null;
   }

   public void setDccPorts(int[] ports) {
      if (ports != null && ports.length != 0) {
         this._dccPorts = (int[])ports.clone();
      } else {
         this._dccPorts = null;
      }

   }

   public boolean equals(Object o) {
      if (o instanceof IrcServerConnection) {
         IrcServerConnection other = (IrcServerConnection)o;
         return other == this;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return super.hashCode();
   }

   public String toString() {
      String var10000 = this.getVersion();
      return "Version{" + var10000 + "} Connected{" + this.isConnected() + "} Server{" + this.getServer() + "} Port{" + this.getPort() + "} Password{" + this.getPassword() + "}";
   }

   public final User[] getUsers(String channel) {
      channel = channel.toLowerCase();
      User[] userArray = new User[0];
      synchronized(this._channels) {
         Hashtable users = (Hashtable)this._channels.get(channel);
         if (users != null) {
            userArray = new User[users.size()];
            Enumeration enumeration = users.elements();

            for(int i = 0; i < userArray.length; ++i) {
               User user = (User)enumeration.nextElement();
               userArray[i] = user;
            }
         }

         return userArray;
      }
   }

   public Boolean isUserInChannel(String nick, String channel) {
      synchronized(this._channels) {
         Hashtable users = (Hashtable)this._channels.get(channel);
         if (users == null) {
            return null;
         } else {
            User user = (User)users.get(nick);
            return null != user;
         }
      }
   }

   public final String[] getChannels() {
      String[] channels = new String[0];
      synchronized(this._channels) {
         channels = new String[this._channels.size()];
         Enumeration enumeration = this._channels.keys();

         for(int i = 0; i < channels.length; ++i) {
            channels[i] = (String)enumeration.nextElement();
         }

         return channels;
      }
   }

   public void dispose() {
      this._outputThread.interrupt();
      this._inputThread.dispose();
   }

   private void addUser(String channel, User user) {
      channel = channel.toLowerCase();
      synchronized(this._channels) {
         Hashtable users = (Hashtable)this._channels.get(channel);
         if (users == null) {
            users = new Hashtable();
            this._channels.put(channel, users);
         }

         users.put(user, user);
      }
   }

   private User removeUser(String channel, String nick) {
      channel = channel.toLowerCase();
      User user = new User("", nick);
      synchronized(this._channels) {
         Hashtable users = (Hashtable)this._channels.get(channel);
         return users != null ? (User)users.remove(user) : null;
      }
   }

   private void removeUser(String nick) {
      synchronized(this._channels) {
         Enumeration enumeration = this._channels.keys();

         while(enumeration.hasMoreElements()) {
            String channel = (String)enumeration.nextElement();
            this.removeUser(channel, nick);
         }

      }
   }

   private void renameUser(String oldNick, String newNick) {
      synchronized(this._channels) {
         Enumeration enumeration = this._channels.keys();

         while(enumeration.hasMoreElements()) {
            String channel = (String)enumeration.nextElement();
            User user = this.removeUser(channel, oldNick);
            if (user != null) {
               user = new User(user.getPrefix(), newNick);
               this.addUser(channel, user);
            }
         }

      }
   }

   private void removeChannel(String channel) {
      channel = channel.toLowerCase();
      synchronized(this._channels) {
         this._channels.remove(channel);
      }
   }

   private void removeAllChannels() {
      synchronized(this._channels) {
         this._channels = new Hashtable();
      }
   }

   private void updateUser(String channel, int userMode, String nick) {
      channel = channel.toLowerCase();
      synchronized(this._channels) {
         Hashtable users = (Hashtable)this._channels.get(channel);
         User newUser = null;
         if (users != null) {
            Enumeration enumeration = users.elements();

            while(enumeration.hasMoreElements()) {
               User userObj = (User)enumeration.nextElement();
               if (userObj.getNick().equalsIgnoreCase(nick)) {
                  if (userMode == 1) {
                     if (userObj.hasVoice()) {
                        newUser = new User("@+", nick);
                     } else {
                        newUser = new User("@", nick);
                     }
                  } else if (userMode == 2) {
                     if (userObj.hasVoice()) {
                        newUser = new User("+", nick);
                     } else {
                        newUser = new User("", nick);
                     }
                  } else if (userMode == 3) {
                     if (userObj.isOp()) {
                        newUser = new User("@+", nick);
                     } else {
                        newUser = new User("+", nick);
                     }
                  } else if (userMode == 4) {
                     if (userObj.isOp()) {
                        newUser = new User("@", nick);
                     } else {
                        newUser = new User("", nick);
                     }
                  }
               }
            }
         }

         if (newUser != null) {
            users.put(newUser, newUser);
         } else {
            newUser = new User("", nick);
            users.put(newUser, newUser);
         }

      }
   }
}
