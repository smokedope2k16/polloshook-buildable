package me.pollos.polloshook.impl.manager.minecraft.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.manager.minecraft.combat.safe.SafetyRunnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SafeManager extends SubscriberImpl implements Minecraftable {
   private final AtomicBoolean safe = new AtomicBoolean(false);
   private final StopWatch timer = new StopWatch();
   private final ConcurrentHashMap<LivingEntity, Double> armorAttributes = new ConcurrentHashMap();
   private final ConcurrentHashMap<LivingEntity, Integer> armorValues = new ConcurrentHashMap();

   public SafeManager() {
      this.listeners.add(new Listener<GameLoopEvent>(GameLoopEvent.class) {
         public void call(GameLoopEvent event) {
            if (SafeManager.this.timer.passed(100L)) {
               SafeManager.this.runThread();
               SafeManager.this.timer.reset();
            }

         }
      });
      this.listeners.add(new Listener<PacketEvent.Receive<EntitySpawnS2CPacket>>(PacketEvent.Receive.class, SafeManager.class) {
         public void call(PacketEvent.Receive<EntitySpawnS2CPacket> event) {
            if (!PlayerUtil.isNull()) {
               EntitySpawnS2CPacket packet = (EntitySpawnS2CPacket)event.getPacket();
               BlockPos pos = BlockPos.ofFloored(packet.getX(), packet.getY(), packet.getZ()).down();
               if (packet.getEntityType() == EntityType.END_CRYSTAL && BlockUtil.getDistanceSq(pos) < (double)MathUtil.square(12.0F)) {
                  SafeManager.this.runPacketCalc(pos);
               }

            }
         }
      });
      this.listeners.add(new Listener<UpdateEvent>(UpdateEvent.class) {
         public void call(UpdateEvent event) {
            if (mc.world != null) {
               Iterator var2 = (new ArrayList(mc.world.getPlayers())).iterator();

               while(var2.hasNext()) {
                  PlayerEntity player = (PlayerEntity)var2.next();
                  EntityAttributeInstance attributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
                  if (attributeInstance != null) {
                     if (SafeManager.this.armorAttributes.containsKey(player)) {
                        SafeManager.this.armorAttributes.replace(player, attributeInstance.getValue());
                     } else {
                        SafeManager.this.armorAttributes.put(player, attributeInstance.getValue());
                     }
                  }

                  int armorValue = MathHelper.floor(player.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
                  if (SafeManager.this.armorValues.containsKey(player)) {
                     SafeManager.this.armorValues.replace(player, armorValue);
                  } else {
                     SafeManager.this.armorValues.put(player, armorValue);
                  }
               }
            }

         }
      });
   }

   public boolean isSafe() {
      return this.safe.get();
   }

   public void setSafe(boolean safe) {
      this.safe.set(safe);
   }

   protected void runPacketCalc(BlockPos pos) {
      PollosHookThread.submit(() -> {
         if (CombatUtil.getDamage(mc.player, mc.world, 6.0F, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), true, true) > EntityUtil.getHealth(mc.player)) {
            this.setSafe(false);
         }

      });
   }

   protected void runThread() {
      if (mc.player != null && mc.world != null) {
         Iterable<Entity> entities = mc.world.getEntities();
         SafetyRunnable runnable = new SafetyRunnable(this, entities);
         PollosHookThread.submit(runnable);
      }

   }

   
   public AtomicBoolean getSafe() {
      return this.safe;
   }

   
   public StopWatch getTimer() {
      return this.timer;
   }

   
   public ConcurrentHashMap<LivingEntity, Double> getArmorAttributes() {
      return this.armorAttributes;
   }

   
   public ConcurrentHashMap<LivingEntity, Integer> getArmorValues() {
      return this.armorValues;
   }
}