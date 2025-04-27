package me.pollos.polloshook.impl.module.other.rpc;

import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.discordrpc.DiscordIPC;
import me.pollos.polloshook.api.util.discordrpc.RichPresence;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import net.minecraft.client.network.ServerInfo;

public class RPC extends ToggleableModule {
   protected final EnumValue<RPCImage> image;
   protected final NumberValue<Float> delay;
   protected final Value<Boolean> server;
   protected final StringValue activity;
   protected static final RichPresence RPC = new RichPresence();
   protected static final String LARGE_TEXT = "%s - %s".formatted(new Object[]{"polloshook", "2025-01-30T16:09:52Z"});
   protected ServerInfo currentServer;
   protected final long enableTimeout;
   protected final long serverTimeout;
   protected final StopWatch timer;
   protected final StopWatch enableTimer;
   protected final StopWatch serverTimer;

   public RPC() {
      super(new String[]{"RPC", "discordia"}, Category.OTHER);
      this.image = new EnumValue(RPCImage.SHUFFLED, new String[]{"Image", "i", "imag"});
      this.delay = (new NumberValue(5.0F, 1.0F, 15.0F, 0.5F, new String[]{"ShuffleDelay", "shuffledel"})).setParent(this.image, RPCImage.SHUFFLED).withTag("second");
      this.server = new Value(false, new String[]{"DisplayServer", "server"});
      this.activity = (new StringValue("hi lneay", new String[]{"Activity", "details"})).setParent(this.server, true);
      this.enableTimeout = 2500L;
      this.serverTimeout = 60000L;
      this.timer = new StopWatch();
      this.enableTimer = new StopWatch();
      this.serverTimer = new StopWatch();
      this.offerValues(new Value[]{this.image, this.delay, this.server, this.activity});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
      this.image.addObserver((o) -> {
         PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
            RPC.setLargeImage(((RPCImage)o.getValue()).getKey(), LARGE_TEXT);
            DiscordIPC.setActivity(RPC);
         }, 500L, TimeUnit.MILLISECONDS);
      });
      this.activity.addObserver((o) -> {
         if (this.enableTimer.passed(2500L)) {
            PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
               RPC.setDetails((String)o.getValue());
               DiscordIPC.setActivity(RPC);
            }, 500L, TimeUnit.MILLISECONDS);
         }
      });
   }

   protected void onEnable() {
      DiscordIPC.start(1333307131277086780L, (Runnable)null);
      RPC.setStart(System.currentTimeMillis() / 1000L);
      RPC.setLargeImage(RPCImage.CAT.getKey(), LARGE_TEXT);
      RPC.setSmallImage(RPCImage.POLLOS.getKey(), "yo mods ts nihh lit asf");
      RPC.setDetails((String)this.activity.getValue());
      DiscordIPC.setActivity(RPC);
      this.enableTimer.reset();
   }

   public void onWorldLoad() {
      if (mc.getNetworkHandler() != null) {
         this.currentServer = mc.getNetworkHandler().getServerInfo();
      }

      this.serverTimer.reset();
   }

   protected void onDisable() {
      DiscordIPC.stop();
   }

   public void onShutdown() {
      DiscordIPC.stop();
   }
}
