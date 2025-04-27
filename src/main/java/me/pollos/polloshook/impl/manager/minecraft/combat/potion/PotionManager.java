package me.pollos.polloshook.impl.manager.minecraft.combat.potion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;

public class PotionManager extends SubscriberImpl {
   private final Map<PlayerEntity, List<StatusEffects>> trackedMap = new HashMap();

   public PotionManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<EntityStatusEffectS2CPacket>>(PacketEvent.Receive.class, EntityStatusEffectS2CPacket.class) {
         public void call(PacketEvent.Receive<EntityStatusEffectS2CPacket> event) {
            if (mc.world != null) {
               EntityStatusEffectS2CPacket packet = (EntityStatusEffectS2CPacket)event.getPacket();
               Entity entity = mc.world.getEntityById(packet.getEntityId());
               if (entity instanceof PlayerEntity) {
                  PlayerEntity player = (PlayerEntity)entity;
                  StatusEffect simple = (StatusEffect)packet.getEffectId().value();
                  StatusEffectInstance instance = PotionManager.ofPacket(packet);
                  PotionManager.this.trackedMap.putIfAbsent(player, new ArrayList());
                  List<StatusEffects> effectsList = (List)PotionManager.this.trackedMap.get(player);
                  Iterator var8 = effectsList.iterator();

                  while(var8.hasNext()) {
                     StatusEffects currentEffect = (StatusEffects)var8.next();
                     if (currentEffect.effect().equals(simple)) {
                        if (instance.getDuration() < currentEffect.instance().getDuration()) {
                           return;
                        }

                        effectsList.remove(currentEffect);
                        break;
                     }
                  }

                  PotionManager.StartTrackPlayerEvent start = PotionManager.StartTrackPlayerEvent.of(player, packet);
                  start.dispatch();
                  effectsList.add(start.getEffects());
               }

            }
         }
      });
      this.listeners.add(new Listener<PacketEvent.Receive<RemoveEntityStatusEffectS2CPacket>>(PacketEvent.Receive.class, RemoveEntityStatusEffectS2CPacket.class) {
         public void call(PacketEvent.Receive<RemoveEntityStatusEffectS2CPacket> event) {
            if (mc.world != null) {
               RemoveEntityStatusEffectS2CPacket packet = (RemoveEntityStatusEffectS2CPacket)event.getPacket();
              // StatusEffect effect = (StatusEffect)packet.comp_2176().value();
               Entity entity = packet.getEntity(mc.world);
               if (entity instanceof PlayerEntity) {
                  PlayerEntity player = (PlayerEntity)entity;
                  if (PotionManager.this.trackedMap.containsKey(player)) {
                     List<StatusEffects> effectsList = (List)PotionManager.this.trackedMap.get(player);
                     effectsList.removeIf((currentEffect) -> {
                        PotionManager.RemoveTrackedEffectEvent end = new PotionManager.RemoveTrackedEffectEvent(player, currentEffect);
                        end.dispatch();
                        //return currentEffect.effect().equals(effect);
                        return true; // TODO: Fix this shi :pray:
                     });
                     if (effectsList.isEmpty()) {
                        PotionManager.this.trackedMap.remove(player);
                     }

                  }
               }
            }
         }
      });
      this.listeners.add(new Listener<TickEvent>(TickEvent.class) {
         public void call(TickEvent event) {
            if (!PotionManager.this.trackedMap.isEmpty()) {
               Iterator var2 = PotionManager.this.trackedMap.entrySet().iterator();

               while(var2.hasNext()) {
                  Entry<PlayerEntity, List<StatusEffects>> entry = (Entry)var2.next();
                  PlayerEntity player = (PlayerEntity)entry.getKey();
                  List<Runnable> callbacks = PotionManager.this.getRunnables(entry, player);
                  Iterator var6 = callbacks.iterator();

                  while(var6.hasNext()) {
                     Runnable callback = (Runnable)var6.next();
                     callback.run();
                  }
               }

            }
         }
      });
   }

   private List<Runnable> getRunnables(Entry<PlayerEntity, List<StatusEffects>> entry, PlayerEntity player) {
      List<StatusEffects> list = (List)entry.getValue();
      List<Runnable> callbacks = new ArrayList();
      Iterator var5 = (new ArrayList(list)).iterator();

      while(var5.hasNext()) {
         StatusEffects statusEffects = (StatusEffects)var5.next();
         if (statusEffects != null && statusEffects.instance() != null) {
            callbacks.add(() -> {
               ((IPlayerEntity)player).$onStatusEffectUpgraded(statusEffects.instance(), true, (Entity)null);
               statusEffects.instance().update(player, () -> {
               });
            });
         }
      }

      return callbacks;
   }

   protected static StatusEffectInstance ofPacket(EntityStatusEffectS2CPacket packet) {
      RegistryEntry<StatusEffect> registryEntry = packet.getEffectId();
      return new StatusEffectInstance(registryEntry, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon(), (StatusEffectInstance)null);
   }

   
   public Map<PlayerEntity, List<StatusEffects>> getTrackedMap() {
      return this.trackedMap;
   }

   public static class RemoveTrackedEffectEvent extends Event {
      private final PlayerEntity player;
      private final StatusEffects effects;

      public static PotionManager.StartTrackPlayerEvent of(PlayerEntity player, EntityStatusEffectS2CPacket packet) {
         return new PotionManager.StartTrackPlayerEvent(player, new StatusEffects((StatusEffect)packet.getEffectId().value(), PotionManager.ofPacket(packet)));
      }

      
      public PlayerEntity getPlayer() {
         return this.player;
      }

      
      public StatusEffects getEffects() {
         return this.effects;
      }

      
      protected RemoveTrackedEffectEvent(PlayerEntity player, StatusEffects effects) {
         this.player = player;
         this.effects = effects;
      }
   }

   public static class StartTrackPlayerEvent extends Event {
      private final PlayerEntity player;
      private final StatusEffects effects;

      public static PotionManager.StartTrackPlayerEvent of(PlayerEntity player, EntityStatusEffectS2CPacket packet) {
         return new PotionManager.StartTrackPlayerEvent(player, new StatusEffects((StatusEffect)packet.getEffectId().value(), PotionManager.ofPacket(packet)));
      }

      
      public PlayerEntity getPlayer() {
         return this.player;
      }

      
      public StatusEffects getEffects() {
         return this.effects;
      }

      
      protected StartTrackPlayerEvent(PlayerEntity player, StatusEffects effects) {
         this.player = player;
         this.effects = effects;
      }
   }
}
