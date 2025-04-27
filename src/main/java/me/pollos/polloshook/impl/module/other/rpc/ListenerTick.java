package me.pollos.polloshook.impl.module.other.rpc;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.discordrpc.DiscordIPC;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerTick extends ModuleListener<RPC, TickEvent> {
   protected String lastServerIP = "";
   protected RPCImage last;

   public ListenerTick(RPC module) {
      super(module, TickEvent.class);
      this.last = RPCImage.CAT;
   }

   public void call(TickEvent event) {
      StopWatch var10000 = ((RPC)this.module).enableTimer;
      Objects.requireNonNull((RPC)this.module);
      if (var10000.passed(2500L)) {
         if (((RPC)this.module).image.getValue() == RPCImage.SHUFFLED && ((RPC)this.module).timer.passed((double)((Float)((RPC)this.module).delay.getValue() * 1000.0F))) {
            PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
               RPC.RPC.setLargeImage(this.last.next().getKey(), RPC.LARGE_TEXT);
               this.last = this.last.next();
               DiscordIPC.setActivity(RPC.RPC);
            }, 250L, TimeUnit.MILLISECONDS);
            ((RPC)this.module).timer.reset();
         }

         if ((mc.isInSingleplayer() || ((RPC)this.module).currentServer == null) && (Boolean)((RPC)this.module).server.getValue()) {
            this.lastServerIP = "chachooxgang";
            RPC.RPC.setDetails("playin jenny mod");
            DiscordIPC.setActivity(RPC.RPC);
         } else {
            if ((Boolean)((RPC)this.module).server.getValue()) {
               var10000 = ((RPC)this.module).serverTimer;
               Objects.requireNonNull((RPC)this.module);
               boolean display = var10000.passed(60000L) || !TextUtil.isNullOrEmpty(this.lastServerIP) && !this.lastServerIP.equals(((RPC)this.module).currentServer.address);
               if (!display) {
                  return;
               }

               PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
                  RPC.RPC.setDetails("Playing %s".formatted(new Object[]{((RPC)this.module).currentServer.address}));
                  DiscordIPC.setActivity(RPC.RPC);
               }, 250L, TimeUnit.MILLISECONDS);
               this.lastServerIP = Managers.getServerManager().getLastServer().address;
            }

         }
      }
   }
}
