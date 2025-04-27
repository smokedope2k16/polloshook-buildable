package me.pollos.polloshook.irc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import me.pollos.polloshook.irc.utils.Utils;

public class DccFileTransfer {
   public static final int BUFFER_SIZE = 1024;
   private IrcServerConnection _bot;
   private DccManager _manager;
   private String _nick;
   private String _login = null;
   private String _hostname = null;
   private String _type;
   private long _address;
   private int _port;
   private long _size;
   private boolean _received;
   private Socket _socket = null;
   private long _progress = 0L;
   private File _file = null;
   private int _timeout = 0;
   private boolean _incoming;
   private long _packetDelay = 0L;
   private long _startTime = 0L;

   public DccFileTransfer(IrcServerConnection bot, DccManager manager, String nick, String login, String hostname, String type, String filename, long address, int port, long size) {
      this._bot = bot;
      this._manager = manager;
      this._nick = nick;
      this._login = login;
      this._hostname = hostname;
      this._type = type;
      this._file = new File(filename);
      this._address = address;
      this._port = port;
      this._size = size;
      this._received = false;
      this._incoming = true;
   }

   public DccFileTransfer(IrcServerConnection bot, DccManager manager, File file, String nick, int timeout) {
      this._bot = bot;
      this._manager = manager;
      this._nick = nick;
      this._file = file;
      this._size = file.length();
      this._timeout = timeout;
      this._received = true;
      this._incoming = false;
   }

   public synchronized void receive(File file, boolean resume) {
      if (!this._received) {
         this._received = true;
         this._file = file;
         if (this._type.equals("SEND") && resume) {
            this._progress = file.length();
            if (this._progress == 0L) {
               this.doReceive(file, false);
            } else {
               this._bot.sendCTCPCommand(this._nick, "DCC RESUME file.ext " + this._port + " " + this._progress);
               this._manager.addAwaitingResume(this);
            }
         } else {
            this._progress = file.length();
            this.doReceive(file, resume);
         }
      }

   }

   public void doReceive(File file, boolean resume) {
      (new Thread(() -> {
         BufferedOutputStream foutput = null;
         Exception exception = null;

         try {
            int[] ip = Utils.longToIp(this._address);
            String ipStr = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
            this._socket = new Socket(ipStr, this._port);
            this._socket.setSoTimeout(30000);
            this._startTime = System.currentTimeMillis();
            this._manager.removeAwaitingResume(this);
            BufferedInputStream input = new BufferedInputStream(this._socket.getInputStream());
            BufferedOutputStream output = new BufferedOutputStream(this._socket.getOutputStream());
            foutput = new BufferedOutputStream(new FileOutputStream(file.getCanonicalPath(), resume));
            byte[] inBuffer = new byte[1024];
            byte[] outBuffer = new byte[4];
            boolean var11 = false;

            int bytesRead;
            while((bytesRead = input.read(inBuffer, 0, inBuffer.length)) != -1) {
               foutput.write(inBuffer, 0, bytesRead);
               this._progress += (long)bytesRead;
               outBuffer[0] = (byte)((int)(this._progress >> 24 & 255L));
               outBuffer[1] = (byte)((int)(this._progress >> 16 & 255L));
               outBuffer[2] = (byte)((int)(this._progress >> 8 & 255L));
               outBuffer[3] = (byte)((int)(this._progress >> 0 & 255L));
               output.write(outBuffer);
               output.flush();
               this.delay();
            }

            foutput.flush();
         } catch (Exception var20) {
            exception = var20;
         } finally {
            try {
               foutput.close();
               this._socket.close();
            } catch (Exception var19) {
            }

         }

         this._bot.getEventHandler().onFileTransferFinished(this, exception);
      })).start();
   }

   public void doSend(boolean allowResume) {
      (new Thread(() -> {
         BufferedInputStream finput = null;
         Exception exception = null;

         try {
            ServerSocket ss = null;
            int[] ports = this._bot.getDccPorts();
            if (ports == null) {
               ss = new ServerSocket(0);
            } else {
               int i = 0;

               while(i < ports.length) {
                  try {
                     ss = new ServerSocket(ports[i]);
                     break;
                  } catch (Exception var25) {
                     ++i;
                  }
               }

               if (ss == null) {
                  throw new IOException("All ports returned by getDccPorts() are in use.");
               }
            }

            ss.setSoTimeout(this._timeout);
            this._port = ss.getLocalPort();
            InetAddress inetAddress = this._bot.getDccInetAddress();
            if (inetAddress == null) {
               inetAddress = this._bot.getInetAddress();
            }

            byte[] ip = inetAddress.getAddress();
            long ipNum = Utils.ipToLong(ip);
            String safeFilename = this._file.getName().replace(' ', '_');
            safeFilename = safeFilename.replace('\t', '_');
            if (allowResume) {
               this._manager.addAwaitingResume(this);
            }

            this._bot.sendCTCPCommand(this._nick, "DCC SEND " + safeFilename + " " + ipNum + " " + this._port + " " + this._file.length());
            this._socket = ss.accept();
            this._socket.setSoTimeout(30000);
            this._startTime = System.currentTimeMillis();
            if (allowResume) {
               this._manager.removeAwaitingResume(this);
            }

            ss.close();
            BufferedOutputStream output = new BufferedOutputStream(this._socket.getOutputStream());
            BufferedInputStream input = new BufferedInputStream(this._socket.getInputStream());
            finput = new BufferedInputStream(new FileInputStream(this._file));
            if (this._progress > 0L) {
               for(long bytesSkipped = 0L; bytesSkipped < this._progress; bytesSkipped += finput.skip(this._progress - bytesSkipped)) {
               }
            }

            byte[] outBuffer = new byte[1024];
            byte[] inBuffer = new byte[4];
            boolean var15 = false;

            int bytesRead;
            while((bytesRead = finput.read(outBuffer, 0, outBuffer.length)) != -1) {
               output.write(outBuffer, 0, bytesRead);
               output.flush();
               input.read(inBuffer, 0, inBuffer.length);
               this._progress += (long)bytesRead;
               this.delay();
            }
         } catch (Exception var26) {
            exception = var26;
         } finally {
            try {
               finput.close();
               this._socket.close();
            } catch (Exception var24) {
            }

         }

         this._bot.getEventHandler().onFileTransferFinished(this, exception);
      })).start();
   }

   public void setProgress(long progress) {
      this._progress = progress;
   }

   private void delay() {
      if (this._packetDelay > 0L) {
         try {
            Thread.sleep(this._packetDelay);
         } catch (InterruptedException var2) {
         }
      }

   }

   public String getNick() {
      return this._nick;
   }

   public String getLogin() {
      return this._login;
   }

   public String getHostname() {
      return this._hostname;
   }

   public File getFile() {
      return this._file;
   }

   public int getPort() {
      return this._port;
   }

   public boolean isIncoming() {
      return this._incoming;
   }

   public boolean isOutgoing() {
      return !this.isIncoming();
   }

   public void setPacketDelay(long millis) {
      this._packetDelay = millis;
   }

   public long getPacketDelay() {
      return this._packetDelay;
   }

   public long getSize() {
      return this._size;
   }

   public long getProgress() {
      return this._progress;
   }

   public double getProgressPercentage() {
      return 100.0D * ((double)this.getProgress() / (double)this.getSize());
   }

   public void close() {
      try {
         this._socket.close();
      } catch (Exception var2) {
      }

   }

   public long getTransferRate() {
      long time = (System.currentTimeMillis() - this._startTime) / 1000L;
      return time <= 0L ? 0L : this.getProgress() / time;
   }

   public long getNumericalAddress() {
      return this._address;
   }
}
