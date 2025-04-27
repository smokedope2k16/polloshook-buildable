package me.pollos.polloshook.impl.module.misc.autoreply;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.autoreply.modes.AutoReplyMode;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ListenerReceive extends SafeModuleListener<AutoReply, PacketEvent.Receive<GameMessageS2CPacket>> {
   public ListenerReceive(AutoReply module) {
      super(module, PacketEvent.Receive.class, GameMessageS2CPacket.class);
   }

   public void safeCall(PacketEvent.Receive<GameMessageS2CPacket> event) {
      Text text = event.getPacket().content(); // fixed method name
      String string = text.getString();
      String[] split = string.split(" ");
      if (split.length >= 2) {
         String ign = split[0];
         String secondWord = split[1];
         boolean anyWhispers = "whispers:".equalsIgnoreCase(secondWord) || "whispers".equalsIgnoreCase(secondWord);
         if (anyWhispers) {
            switch (((AutoReply) this.module).mode.getValue()) {
               case COORDS:
                  ArrayList<String> messages = new ArrayList<>(Arrays.asList("wya", "coords", "where are you", "where ru", "coord"));
                  if (BlockUtil.getDistanceSq(new BlockPos(0, 159, 0)) <= MathUtil.square(((AutoReply) this.module).threshold.getValue() * 1000.0F)) {
                     if (Managers.getFriendManager().isFriend(ign)) {
                        if (messages.stream().anyMatch(msg -> string.toLowerCase().contains(msg))) {
                           ((AutoReply) this.module).system.submit("/r " + mc.player.getBlockPos().toShortString());
                        }
                     }
                  } else {
                     if (messages.stream().anyMatch(msg -> string.contains(msg)) && !Managers.getFriendManager().isFriend(ign)) {
                        ((AutoReply) this.module).system.submit("/r " + this.fakePos().toShortString());
                     }
                  }
                  break;
               case POLLOS:
                  ((AutoReply) this.module).system.submit("/r [PollosHook] 2 Busy Fishing 2 Respond");
            }
         }
      }
   }
   

   private BlockPos fakePos() {
      ThreadLocalRandom random = ThreadLocalRandom.current();
      int xAxis = random.nextBoolean() ? -random.nextInt(0, 54314613) : random.nextInt(0, 53127812);
      int yAxis = random.nextInt(6, 200);
      int zAxis = random.nextBoolean() ? -random.nextInt(0, 4125421) : random.nextInt(0, 6135175);
      return new BlockPos(xAxis, yAxis, zAxis);
   }
}
