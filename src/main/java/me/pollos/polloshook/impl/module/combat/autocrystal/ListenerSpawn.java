package me.pollos.polloshook.impl.module.combat.autocrystal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.AntiWeakness;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.Timing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ListenerSpawn extends ModuleListener<AutoCrystal, PacketEvent.Receive<EntitySpawnS2CPacket>> {
   public ListenerSpawn(AutoCrystal module) {
      super(module, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
   }

   public void call(PacketEvent.Receive<EntitySpawnS2CPacket> event) {
      if (mc.player != null && mc.world != null) {
         EntitySpawnS2CPacket packet = (EntitySpawnS2CPacket)event.getPacket();
         int id = packet.getEntityId();
         if (packet.getEntityType() == EntityType.END_CRYSTAL) {
            BlockPos pos = BlockPos.ofFloored(packet.getX(), packet.getY(), packet.getZ());
            boolean isLegit = false;
            if (((AutoCrystal)this.module).getPendingPlacePositions().containsKey(pos)) {
               if (pos.down().equals(((AutoCrystal)this.module).getLastCrystalPos().toPos())) {
                  isLegit = true;
                  ((AutoCrystal)this.module).getCrystalsPerSecond().add(System.currentTimeMillis());
               }

               ((AutoCrystal)this.module).getCrystalDelays().put(id, System.currentTimeMillis());
               ((AutoCrystal)this.module).setLastSpawnID(id);
               ((AutoCrystal)this.module).getPendingPlacePositions().remove(pos);
               ((AutoCrystal)this.module).getConfirmedPlacePositions().put(pos, System.currentTimeMillis());
               ((AutoCrystal)this.module).getCollisionTimer().reset();
               ((AutoCrystal)this.module).getRenderTimer().reset();
            }

            if (((AutoCrystal)this.module).isLocked() || !isLegit || !(Boolean)((AutoCrystal)this.module).getBoost().getValue()) {
               return;
            }

            if ((double)(Float)((AutoCrystal)this.module).getAttackDelay().getValue() != 0.0D) {
               long delay = (long)(100.0F * (Float)((AutoCrystal)this.module).getAttackDelay().getValue());
               PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
                  this.predictAttack(id, pos, packet);
               }, delay, TimeUnit.MILLISECONDS);
               return;
            }

            this.predictAttack(id, pos, packet);
         }

      }
   }

   private void predictAttack(int id, BlockPos pos, EntitySpawnS2CPacket packet) {
      if (!((AutoCrystal)this.module).isWasEating() || !mc.options.useKey.isPressed()) {
         boolean multiTasking = PlayerUtil.isUsingBow() || PlayerUtil.isEating();
         if ((Boolean)((AutoCrystal)this.module).getMultiTask().getValue() || !multiTasking) {
            if ((Boolean)((AutoCrystal)this.module).getWhileMining().getValue() || !PlayerUtil.isMining()) {
               if (((AutoCrystal)this.module).getBreakTimer().passed((double)(1000.0F - (Float)((AutoCrystal)this.module).getAttackSpeed().getValue() * 50.0F))) {
                  ((AutoCrystal)this.module).getBreakTimer().reset();
                  Vec3d playerVec = ((AutoCrystal)this.module).getPlayerPos();
                  Vec3d relativeVec = ((AutoCrystal)this.module).getRelativeVecFromCrystal(packet.getX(), packet.getY(), packet.getZ(), playerVec);
                  boolean success = false;

                  for(int i = 0; i < (Integer)((AutoCrystal)this.module).getHits().getValue() && ((AutoCrystal)this.module).isInAttackWallRange(playerVec, relativeVec, new Vec3d(packet.getX(), packet.getY() + 1.7D, packet.getZ())) && ((AutoCrystal)this.module).isInAttackRange(playerVec, relativeVec); ++i) {
                     int oldSlot = mc.player.getInventory().selectedSlot;
                     int weaknessSlot = -1;
                     if (((AutoCrystal)this.module).getAntiWeakness().getValue() != AntiWeakness.OFF) {
                        if (!CombatUtil.canBreakWeakness(true) && (weaknessSlot = CombatUtil.findAntiWeakness()) == -1) {
                           break;
                        }

                        if (weaknessSlot != -1) {
                           InventoryUtil.switchToSlot(weaknessSlot);
                        }
                     }

                     Hand hand = ((AutoCrystal)this.module).getHand();
                     if ((Boolean)((AutoCrystal)this.module).getSwing().getValue()) {
                        PacketUtil.swing(hand);
                        EntityUtil.swingClient(hand);
                     }

                     PlayerInteractEntityC2SPacket hitPacket = PacketUtil.attackPacket(id);
                     PacketUtil.send(hitPacket);
                     if (((AutoCrystal)this.module).getTiming().getValue() == Timing.FAST) {
                        ((AutoCrystal)this.module).getConfirmedPlacePositions().remove(pos);
                     }

                     ((AutoCrystal)this.module).getInhibitedCrystals().put(id, System.currentTimeMillis());
                     if (weaknessSlot != -1 && ((AutoCrystal)this.module).getAntiWeakness().getValue() == AntiWeakness.SILENT) {
                        InventoryUtil.switchToSlot(oldSlot);
                     }

                     success = true;
                  }

                  if (success) {
                     if (!((AutoCrystal)this.module).getAttacks().contains(pos)) {
                        ((AutoCrystal)this.module).getAttacks().add(pos);
                     }

                     if (((AutoCrystal)this.module).getTiming().getValue() == Timing.FAST) {
                        ((AutoCrystal)this.module).setSkipBreak(true);
                     }

                     Iterator var13 = (new ArrayList(Managers.getEntitiesManager().getEntities())).iterator();

                     while(var13.hasNext()) {
                        Entity entity = (Entity)var13.next();
                        if (entity instanceof EndCrystalEntity) {
                           EndCrystalEntity crystal = (EndCrystalEntity)entity;
                           if (!(crystal.squaredDistanceTo(new Vec3d(packet.getX(), packet.getY(), packet.getZ())) <= (double)MathUtil.square(6.0F))) {
                              ((AutoCrystal)this.module).updateHits(crystal);
                           }
                        }
                     }
                  }
               }

            }
         }
      }
   }
}