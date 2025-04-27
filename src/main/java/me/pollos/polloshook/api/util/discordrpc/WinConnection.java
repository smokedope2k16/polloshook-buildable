package me.pollos.polloshook.api.util.discordrpc;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class WinConnection extends Connection {
   private final RandomAccessFile raf;
   private final Consumer<DiscordPacket> callback;

   WinConnection(String name, Consumer<DiscordPacket> callback) throws IOException {
      this.raf = new RandomAccessFile(name, "rw");
      this.callback = callback;
      Thread thread = new Thread(this::run);
      thread.setName("Discord IPC - Read thread");
      thread.start();
   }

   protected void write(ByteBuffer buffer) {
      try {
         this.raf.write(buffer.array());
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   private void run() {
      ByteBuffer intB = ByteBuffer.allocate(4);

      try {
         while(true) {
            this.readFully(intB);
            Opcode opcode = Opcode.valueOf(Integer.reverseBytes(intB.getInt(0)));
            this.readFully(intB);
            int length = Integer.reverseBytes(intB.getInt(0));
            ByteBuffer dataB = ByteBuffer.allocate(length);
            this.readFully(dataB);
            String data = Charset.defaultCharset().decode(dataB.rewind()).toString();
            this.callback.accept(new DiscordPacket(opcode, JsonParser.parseString(data).getAsJsonObject()));
         }
      } catch (Exception var6) {
      }
   }

   private void readFully(ByteBuffer buffer) throws IOException {
      buffer.rewind();

      while(this.raf.length() < (long)buffer.remaining()) {
         Thread.onSpinWait();

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }

      while(buffer.hasRemaining()) {
         this.raf.getChannel().read(buffer);
      }

   }

   public void close() {
      try {
         this.raf.close();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }
}
