package me.pollos.polloshook.impl.module.other.fastlatency;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.network.PingMeasurer;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;

public class FastLatency extends ToggleableModule {
   protected final NumberValue<Float> delay = (new NumberValue(5.0F, 0.0F, 10.0F, 0.1F, new String[]{"Delay", "d"})).withTag("second");
   protected long ping;
   protected PingMeasurer measurer;
   protected final StopWatch timer = new StopWatch();

   public FastLatency() {
      super(new String[]{"FastLatency", "fastping"}, Category.OTHER);
      this.offerValues(new Value[]{this.delay});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
   }

   protected String getTag() {
      return "%sms".formatted(new Object[]{this.ping});
   }

   protected void onToggle() {
      this.killMeasurer();
   }

   protected void killMeasurer() {
      if (this.measurer == null) {
         this.ping = 0L;
         this.timer.reset();
      } else {
         this.setMeasurer((PingMeasurer)null);
         this.ping = 0L;
         this.timer.reset();
      }
   }

   protected PingMeasurer newPingMeasurer() {
      return new PingMeasurer(mc.player.networkHandler, new MultiValueDebugSampleLogImpl(1)) {
         public void ping() {
            PacketUtil.send(new QueryPingC2SPacket(Util.getMeasuringTimeMs()));
         }

         public void onPingResult(PingResultS2CPacket packet) {
            FastLatency.this.ping = Util.getMeasuringTimeMs() - packet.startTime();
         }
      };
   }

   
   public long getPing() {
      return this.ping;
   }

   
   public PingMeasurer getMeasurer() {
      return this.measurer;
   }

   
   public void setMeasurer(PingMeasurer measurer) {
      this.measurer = measurer;
   }
}
