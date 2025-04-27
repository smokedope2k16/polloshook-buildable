package me.pollos.polloshook.api.util.discordrpc;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class UnixConnection extends Connection {
   private final Selector s = Selector.open();
   private final SocketChannel sc;
   private final Consumer<DiscordPacket> callback;

   public UnixConnection(String name, Consumer<DiscordPacket> callback) throws IOException {
      this.sc = SocketChannel.open(UnixDomainSocketAddress.of(name));
      this.callback = callback;
      this.sc.configureBlocking(false);
      this.sc.register(this.s, 1);
      Thread thread = new Thread(this::run);
      thread.setName("Discord IPC - Read thread");
      thread.start();
   }

   private void run() {
      UnixConnection.State state = UnixConnection.State.Opcode;
      ByteBuffer intB = ByteBuffer.allocate(4);
      ByteBuffer dataB = null;
      Opcode opcode = null;

      try {
         while(true) {
            while(true) {
               this.s.select();
               switch(state.ordinal()) {
               case 0:
                  this.sc.read(intB);
                  if (!intB.hasRemaining()) {
                     opcode = Opcode.valueOf(Integer.reverseBytes(intB.getInt(0)));
                     state = UnixConnection.State.Length;
                     intB.rewind();
                  }
                  break;
               case 1:
                  this.sc.read(intB);
                  if (!intB.hasRemaining()) {
                     dataB = ByteBuffer.allocate(Integer.reverseBytes(intB.getInt(0)));
                     state = UnixConnection.State.Data;
                     intB.rewind();
                  }
                  break;
               case 2:
                  this.sc.read(dataB);
                  if (!dataB.hasRemaining()) {
                     String data = Charset.defaultCharset().decode(dataB.rewind()).toString();
                     this.callback.accept(new DiscordPacket(opcode, JsonParser.parseString(data).getAsJsonObject()));
                     dataB = null;
                     state = UnixConnection.State.Opcode;
                  }
               }
            }
         }
      } catch (Exception var6) {
      }
   }

   protected void write(ByteBuffer buffer) {
      try {
         this.sc.write(buffer);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public void close() {
      try {
         this.s.close();
         this.sc.close();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   private static enum State {
      Opcode,
      Length,
      Data;

      // $FF: synthetic method
      private static UnixConnection.State[] $values() {
         return new UnixConnection.State[]{Opcode, Length, Data};
      }
   }
}
