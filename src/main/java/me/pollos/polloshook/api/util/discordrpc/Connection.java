package me.pollos.polloshook.api.util.discordrpc;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class Connection {
   private static final String[] UNIX_TEMP_PATHS = new String[]{"XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP"};

   public static Connection open(Consumer<DiscordPacket> callback) {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
         int i = 0;

         while(i < 10) {
            try {
               return new WinConnection("\\\\?\\pipe\\discord-ipc-" + i, callback);
            } catch (IOException var7) {
               ++i;
            }
         }
      } else {
         String name = null;
         String[] var3 = UNIX_TEMP_PATHS;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String tempPath = var3[var5];
            name = System.getenv(tempPath);
            if (name != null) {
               break;
            }
         }

         if (name == null) {
            name = "/tmp";
         }

         name = name + "/discord-ipc-";
         int i = 0;

         while(i < 10) {
            try {
               return new UnixConnection(name + i, callback);
            } catch (IOException var8) {
               ++i;
            }
         }
      }

      return null;
   }

   public void write(Opcode opcode, JsonObject o) {
      o.addProperty("nonce", UUID.randomUUID().toString());
      byte[] d = o.toString().getBytes();
      ByteBuffer packet = ByteBuffer.allocate(d.length + 8);
      packet.putInt(Integer.reverseBytes(opcode.ordinal()));
      packet.putInt(Integer.reverseBytes(d.length));
      packet.put(d);
      packet.rewind();
      this.write(packet);
   }

   protected abstract void write(ByteBuffer var1);

   public abstract void close();
}
