package me.pollos.polloshook.impl.module.player.blink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.player.blink.mode.BlinkMode;
import me.pollos.polloshook.impl.module.player.blink.mode.PulseMode;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Blink extends ToggleableModule {
   protected final EnumValue<BlinkMode> mode;
   protected final EnumValue<PulseMode> pulse;
   protected final Value<Boolean> addPlayer;
   protected final NumberValue<Integer> packets;
   protected final NumberValue<Integer> delay;
   protected final NumberValue<Float> distance;
   protected final Value<Boolean> autoDisable;
   protected final Value<Boolean> breadCrumbs;
   protected final Value<Boolean> allPackets;
   protected final Value<Boolean> subhuman;
   protected final StopWatch timer;
   protected final List<Vec3d> positions;
   protected final ArrayList<Packet<?>> queue;
   protected final Map<Packet<?>, Long> fakeLagQueue;
   protected Vec3d lastVec3d;
   public static String FAKE_PLAYER_LABEL = "blink_fakeplayer";

   public Blink() {
      super(new String[]{"Blink", "blinker"}, Category.PLAYER);
      this.mode = new EnumValue(BlinkMode.CONSTANT, new String[]{"Mode", "type", "m"});
      this.pulse = (new EnumValue(PulseMode.TIME, new String[]{"Pulse", "p", "pulser"})).setParent(this.mode, BlinkMode.PULSE);
      this.addPlayer = (new Value(false, new String[]{"AddPlayer", "player"})).setParent(this.mode, BlinkMode.PULSE);
      this.packets = (new NumberValue(80, 0, 500, new String[]{"Packets", "packet"})).setParent(this.pulse, PulseMode.PACKETS);
      this.delay = (new NumberValue(250, 0, 1000, new String[]{"Delay", "del"})).setParent(() -> {
         return this.mode.getValue() == BlinkMode.FAKE_LAG || this.pulse.getValue() == PulseMode.TIME && this.mode.getValue() == BlinkMode.PULSE;
      }).withTag("ms");
      this.distance = (new NumberValue(16.0F, 5.0F, 32.0F, 0.1F, new String[]{"Distance", "maxdistance"})).setParent(this.pulse, PulseMode.DISTANCE).withTag("range");
      this.autoDisable = (new Value(false, new String[]{"AutoDisable", "disable"})).setParent(this.mode, BlinkMode.PULSE);
      this.breadCrumbs = (new Value(false, new String[]{"BreadCrumbs", "tracer", "trace", "line"})).setParent(this.mode, BlinkMode.FAKE_LAG, true);
      this.allPackets = new Value(false, new String[]{"EveryPacket", "packet", "allpackets"});
      this.subhuman = new Value(false, new String[]{"Subhuman", "2drender"});
      this.timer = new StopWatch();
      this.positions = new ArrayList();
      this.queue = new ArrayList();
      this.fakeLagQueue = new HashMap();
      this.mode.addObserver((o) -> {
         this.queue.clear();
         this.fakeLagQueue.clear();
         this.timer.reset();
         this.positions.clear();
         this.refreshPlayer();
      });
      this.offerValues(new Value[]{this.mode, this.pulse, this.packets, this.delay, this.distance, this.addPlayer, this.breadCrumbs, this.allPackets, this.subhuman});
      this.offerListeners(new Listener[]{new ListenerSend(this), new ListenerRender(this), new ListenerRender3D(this), new ListenerTick(this)});
   }

   protected String getTag() {
      if (mc.player == null) {
         return null;
      } else {
         int var10000;
         switch((BlinkMode)this.mode.getValue()) {
         case FAKE_LAG:
            var10000 = this.fakeLagQueue.size();
            return var10000 + ", " + String.valueOf(this.delay.getValue()) + "ms";
         case PULSE:
            switch((PulseMode)this.pulse.getValue()) {
            case PACKETS:
               var10000 = this.queue.size();
               return var10000 + "/" + String.valueOf(this.packets.getValue());
            case DISTANCE:
               if (!PlayerUtil.isNull() && this.lastVec3d != null) {
                  var10000 = this.queue.size();
                  return var10000 + ", %.1fm".formatted(new Object[]{StrictMath.sqrt(mc.player.squaredDistanceTo(this.lastVec3d))});
               }

               return String.valueOf(this.queue.size());
            case TIME:
               var10000 = this.queue.size();
               return var10000 + ", " + Math.min(this.timer.getTime(), (long)(Integer)this.delay.getValue());
            }
         default:
            return super.getTag();
         case CONSTANT:
            return String.valueOf(this.queue.size());
         }
      }
   }

   protected void onToggle() {
      if (mc.player != null) {
         this.positions.clear();
         this.timer.reset();
      }
   }

   protected void onEnable() {
      if (mc.player == null) {
         this.setEnabled(false);
      } else if (this.mode.getValue() != BlinkMode.FAKE_LAG && ((Boolean)this.addPlayer.getValue() || !this.addPlayer.getParent().isVisible())) {
         this.putPlayer(true);
         this.lastVec3d = mc.player.getPos();
      }
   }

   protected void onDisable() {
      if (mc.player != null) {
         FakePlayerUtil.removeFakePlayerFromWorld(FAKE_PLAYER_LABEL, -mc.player.getId());
         this.clear(true);
         this.fakeLagQueue.clear();
      }
   }

   public static boolean isBadPacket(Packet<?> packet) {
      List<Class<?>> badPackets = Arrays.asList(ChatMessageC2SPacket.class, TeleportConfirmC2SPacket.class, KeepAliveC2SPacket.class, AdvancementTabC2SPacket.class, ClientStatusC2SPacket.class);
      return badPackets.contains(packet.getClass());
   }

   protected void clear(boolean full) {
      if ((Boolean)this.autoDisable.getValue()) {
         this.setEnabled(false);
      } else {
         if (full) {
            this.queue.forEach((packet) -> {
               if (packet != null) {
                  PacketUtil.send(packet);
               }

            });
            this.clear(false);
         } else {
            this.queue.clear();
            this.positions.clear();
            this.refreshPlayer();
         }

      }
   }

   protected void refreshPlayer() {
      if (mc.player != null) {
         this.lastVec3d = mc.player.getPos();
         if (mc.world.getEntityById(-mc.player.getId()) != null) {
            this.killPlayer();
            this.putPlayer(false);
         } else {
            this.putPlayer(false);
            this.killPlayer();
         }

      }
   }

   private void putPlayer(boolean force) {
      if (force || this.mode.getValue() != BlinkMode.FAKE_LAG && ((Boolean)this.addPlayer.getValue() || !this.addPlayer.getParent().isVisible())) {
         FakePlayerUtil.addFakePlayerToWorld(FAKE_PLAYER_LABEL, EntityUtil.getName(mc.player), -mc.player.getId());
      }
   }

   private void killPlayer() {
      FakePlayerUtil.removeFakePlayerFromWorld(FAKE_PLAYER_LABEL, -mc.player.getId());
   }
}
