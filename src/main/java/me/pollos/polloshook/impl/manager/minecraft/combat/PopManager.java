package me.pollos.polloshook.impl.manager.minecraft.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.entity.DeathEvent;
import me.pollos.polloshook.impl.events.entity.EntityWorldEvent;
import me.pollos.polloshook.impl.events.entity.TotemPopEvent;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.events.world.WorldLoadEvent;
import me.pollos.polloshook.impl.module.misc.popcounter.PopCounter;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class PopManager extends SubscriberImpl implements Minecraftable {
   private final Map<String, Integer> popMap = new HashMap();

   public PopManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<EntityStatusS2CPacket>>(PacketEvent.Receive.class, Integer.MIN_VALUE, EntityStatusS2CPacket.class) {
         public void call(PacketEvent.Receive<EntityStatusS2CPacket> event) {
            if (mc.player != null) {
               EntityStatusS2CPacket packet = (EntityStatusS2CPacket)event.getPacket();
               if (packet.getStatus() == 35) {
                  Entity entity = packet.getEntity(mc.world);
                  if (entity != null) {
                     String name = EntityUtil.getName(entity);
                     if (entity instanceof PlayerEntity) {
                        boolean contains = PopManager.this.popMap.containsKey(name);
                        PopManager.this.popMap.put(name, contains ? (Integer)PopManager.this.popMap.get(name) + 1 : 1);
                        TotemPopEvent popEvent = new TotemPopEvent((PlayerEntity)entity);
                        PollosHook.getEventBus().dispatch(popEvent);
                     }
                  }
               }

            }
         }
      });
      this.listeners.add(new Listener<DeathEvent>(DeathEvent.class, Integer.MIN_VALUE) {
         public void call(DeathEvent event) {
            Entity entity = event.getEntity();
            if (entity instanceof PlayerEntity) {
               String name = EntityUtil.getName(entity);
               if (PopManager.this.popMap.containsKey(name)) {
                  PopManager.this.popMap.remove(name, PopManager.this.popMap.get(name));
               }
            }

         }
      });
      this.listeners.add(new Listener<EntityWorldEvent.Remove>(EntityWorldEvent.class, Integer.MIN_VALUE) {
         public void call(EntityWorldEvent.Remove event) {
            if ((Boolean)((PopCounter)Managers.getModuleManager().get(PopCounter.class)).getClearOnVisualRange().getValue()) {
               Entity entity = event.getEntity();
               if (entity instanceof PlayerEntity) {
                  if (entity == mc.player) {
                     return;
                  }

                  String name = EntityUtil.getName(entity);
                  if (PopManager.this.popMap.containsKey(name)) {
                     PopManager.this.popMap.remove(name, PopManager.this.popMap.get(name));
                  }
               }
            }

         }
      });
      this.listeners.add(new Listener<ScreenEvent>(ScreenEvent.class, Integer.MIN_VALUE) {
         public void call(ScreenEvent event) {
            if (event.getScreen() instanceof DeathScreen) {
               ScheduledExecutorService var10000 = PollosHookThread.SCHEDULED_EXECUTOR;
               Map var10001 = PopManager.this.popMap;
               Objects.requireNonNull(var10001);
               var10000.schedule(var10001::clear, 100L, TimeUnit.MILLISECONDS);
            }

         }
      });
      this.listeners.add(new Listener<WorldLoadEvent>(WorldLoadEvent.class) {
         public void call(WorldLoadEvent event) {
            PopManager.this.popMap.clear();
         }
      });
      this.listeners.add(new Listener<LeaveGameEvent>(LeaveGameEvent.class, Integer.MIN_VALUE) {
         public void call(LeaveGameEvent event) {
            PopManager.this.popMap.clear();
         }
      });
   }

   
   public Map<String, Integer> getPopMap() {
      return this.popMap;
   }
}
