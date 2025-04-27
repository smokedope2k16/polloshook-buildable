package me.pollos.polloshook.impl.module.combat.criticals;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.entity.AttackEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.Vec3d;

public class ListenerAttackEntity extends SafeModuleListener<Criticals, AttackEntityEvent> {
   private boolean ignore = false;

   public ListenerAttackEntity(Criticals module) {
      super(module, AttackEntityEvent.class);
   }

   public void safeCall(AttackEntityEvent event) {
      Entity entity = event.getEntity();
      if (!((Criticals)this.module).onlyPhase.getParent().isVisible() || !(Boolean)((Criticals)this.module).onlyPhase.getValue() || !((Criticals)this.module).isBlocked()) {
         if (!PlayerUtil.isInLiquid() && mc.player.isOnGround()) {
            if ((Boolean)((Criticals)this.module).boats.getValue() && entity instanceof BoatEntity) {
               PlayerInteractEntityC2SPacket attack = PacketUtil.attackPacket(entity);
               boolean sword = this.isMainhand(Items.DIAMOND_SWORD) || this.isMainhand(Items.NETHERITE_SWORD) || this.isMainhand(Items.IRON_SWORD);
               if (!sword) {
                  for(int i = 3; i <= (Integer)((Criticals)this.module).boatAttacks.getValue(); ++i) {
                     PacketUtil.send(attack);
                  }

               }
            } else if ((Boolean)((Criticals)this.module).mace.getValue() && mc.player.getMainHandStack().getItem() instanceof MaceItem) {
               PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.1D, mc.player.getZ(), false);
               PacketUtil.move(mc.player.getX(), mc.player.getY() + (double)(Float)((Criticals)this.module).height.getValue(), mc.player.getZ(), false);
               PacketUtil.move(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false);
            } else {
               float progress = mc.player.getAttackCooldownProgress(0.0F);
               if (!(progress < 1.0F) || this.ignore) {
                  if (entity instanceof LivingEntity) {
                     switch(event.getStage()) {
                     case PRE:
                        switch((CriticalsType)((Criticals)this.module).mode.getValue()) {
                        case PACKET:
                           PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.05D, mc.player.getZ(), false);
                           PacketUtil.move(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false);
                           PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.03D, mc.player.getZ(), false);
                           PacketUtil.move(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false);
                           this.stopMotion();
                           break;
                        case STRICT:
                           PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.170001501788139D, mc.player.getZ(), false);
                           PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.0700018752980234D, mc.player.getZ(), false);
                           this.stopMotion();
                           break;
                        case LOW_HOP:
                           mc.player.setVelocity(mc.player.getVelocity().x, 0.35D, mc.player.getVelocity().z);
                        }

                        this.ignore = true;
                        break;
                     case POST:
                        if (((Criticals)this.module).mode.getValue() == CriticalsType.STRICT) {
                           PacketUtil.move(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false);
                        }

                        this.ignore = false;
                     }
                  }

               }
            }
         }
      }
   }

   private boolean isMainhand(Item item) {
      return mc.player.getMainHandStack().getItem().equals(item);
   }

   private void stopMotion() {
      if ((Boolean)((Criticals)this.module).motion.getValue()) {
         mc.player.setVelocity(Vec3d.ZERO);
      }

   }
}