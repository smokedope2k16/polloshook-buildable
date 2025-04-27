package me.pollos.polloshook.impl.manager.minecraft.movement.speed;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class SpeedManager extends SubscriberImpl implements Minecraftable {
   public static final double BPS = 20.0D;
   public static final double KMH = 72.0D;
   private final StopWatch timer = new StopWatch();
   private final Map<PlayerEntity, Double> speeds = new HashMap();

   public SpeedManager() {
      this.listeners.add(new Listener<TickEvent>(TickEvent.class) {
         public void call(TickEvent event) {
            if (mc.world != null && SpeedManager.this.timer.passed(40L)) {
               PollosHookThread.submit(() -> {
                  SpeedManager.this.updateSpeed(mc.world);
               });
               SpeedManager.this.timer.reset();
            }

         }
      });
      this.listeners.add(new Listener<LeaveGameEvent>(LeaveGameEvent.class) {
         public void call(LeaveGameEvent event) {
            SpeedManager.this.speeds.clear();
         }
      });
   }

   private void updateSpeed(World world) {
      Iterator var2 = world.getPlayers().iterator();

      while(var2.hasNext()) {
         PlayerEntity player = (PlayerEntity)var2.next();
         IPlayerEntity access = (IPlayerEntity)player;
         this.speeds.put(player, MathUtil.distance2D(player.getPos(), access.getLastSpeedVec()));
         access.setLastSpeedVec(player.getPos());
      }

   }

   public double getSpeed(PlayerEntity player) {
      Double playerSpeed = (Double)this.speeds.get(player);
      return playerSpeed != null ? playerSpeed * 72.0D : 0.0D;
   }
}
