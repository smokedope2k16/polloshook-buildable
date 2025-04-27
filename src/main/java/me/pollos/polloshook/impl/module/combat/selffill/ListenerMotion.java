package me.pollos.polloshook.impl.module.combat.selffill;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class ListenerMotion extends ModuleListener<SelfFill, MotionUpdateEvent> {
   public ListenerMotion(SelfFill module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (!((SelfFill)this.module).isInsideBlock() && !PlayerUtil.isInLiquid()) {
         ((SelfFill)this.module).setEnablePosY();
         ((SelfFill)this.module).handleJump();
         BlockPos pos = mc.player.getBlockPos();
         if (mc.world.getBlockState(pos).isReplaceable() && !mc.world.getBlockState(pos).isLiquid()) {
            BlockPos posDown = pos.down();
            if (!mc.world.getBlockState(posDown).isLiquid() && !mc.world.getBlockState(posDown).isAir()) {
               BlockPos posHead = pos.up(2);
               if (mc.world.getBlockState(posHead).isReplaceable()) {
                  BlockPos upUp = pos.up(2);
                  BlockState upState = mc.world.getBlockState(upUp);
                  if (!upState.blocksMovement() && !mc.world.getBlockState(upUp).isLiquid()) {
                     int startSlot;
                     int slot;
                     label194: {
                        startSlot = mc.player.getInventory().selectedSlot;
                        slot = -1;
                        Item var10 = mc.player.getMainHandStack().getItem();
                        if (var10 instanceof BlockItem) {
                           BlockItem item = (BlockItem)var10;
                           if (((SelfFill)this.module).items.isValid(item, (EnumValue)((SelfFill)this.module).selection)) {
                              slot = mc.player.getInventory().selectedSlot;
                              break label194;
                           }
                        }

                        for(int i = 9; i >= 0; --i) {
                           Item var12 = InventoryUtil.getStack(i).getItem();
                           if (var12 instanceof BlockItem) {
                              BlockItem item = (BlockItem)var12;
                              if (((SelfFill)this.module).items.isValid(item, (EnumValue)((SelfFill)this.module).selection)) {
                                 slot = i;
                              }
                           }
                        }
                     }

                     if (slot == -1) {
                        ((SelfFill)this.module).setEnabled(false);
                     } else {
                        Direction facing = BlockUtil.getFacing(pos);
                        if (facing != null) {
                           float[] oldRots = new float[]{mc.player.getYaw(), mc.player.getPitch()};
                           BlockPos newPos = pos.offset(facing);
                           float[] angles = RotationsUtil.getRotationsFacing(newPos, facing.getOpposite(), mc.player);
                           if (mc.player.isOnGround() && !Managers.getPositionManager().isOnGround()) {
                              if ((Boolean)((SelfFill)this.module).rotate.getValue()) {
                                 PacketUtil.send(new Full(mc.player.getX(), mc.player.getY(), mc.player.getY(), angles[0], angles[1], true));
                              } else {
                                 PacketUtil.send(new Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), oldRots[0], oldRots[1], true));
                              }
                           } else if (Managers.getPositionManager().isOnGround()) {
                              if ((Boolean)((SelfFill)this.module).rotate.getValue()) {
                                 PacketUtil.rotate(angles, true);
                              }
                           } else if (!mc.player.isOnGround() && !Managers.getPositionManager().isOnGround()) {
                              return;
                           }

                           PlayerInteractEntityC2SPacket attacking = null;
                           boolean crystals = false;
                           float currentDmg = Float.MAX_VALUE;
                           Box box = new Box(pos);
                           Iterator var17 = Managers.getEntitiesManager().getAnyCollidingEntities(box).iterator();

                           while(true) {
                              while(true) {
                                 Entity entity;
                                 do {
                                    do {
                                       do {
                                          do {
                                             do {
                                                do {
                                                   if (!var17.hasNext()) {
                                                      int weaknessSlot = -1;
                                                      if (crystals) {
                                                         if (attacking == null) {
                                                            return;
                                                         }

                                                         if (!CombatUtil.canBreakWeakness(true) && (weaknessSlot = CombatUtil.findAntiWeakness()) == -1) {
                                                            return;
                                                         }

                                                         if (weaknessSlot != -1) {
                                                            if ((Boolean)((SelfFill)this.module).altSwap.getValue()) {
                                                               InventoryUtil.switchToSlot(weaknessSlot);
                                                            } else {
                                                               InventoryUtil.switchToSlot(weaknessSlot);
                                                            }
                                                         }
                                                      }

                                                      if (attacking != null) {
                                                         this.attack(attacking);
                                                         if ((Boolean)((SelfFill)this.module).altSwap.getValue()) {
                                                            InventoryUtil.switchToSlot(weaknessSlot);
                                                         }
                                                      }

                                                      if ((Boolean)((SelfFill)this.module).altSwap.getValue()) {
                                                         InventoryUtil.altSwap(slot);
                                                      } else {
                                                         InventoryUtil.switchToSlot(slot);
                                                      }

                                                      ((AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class)).setLock(true);
                                                      PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.41999848688698D, mc.player.getZ(), false));
                                                      PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.7500015D, mc.player.getZ(), false));
                                                      PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.999997D, mc.player.getZ(), false));
                                                      PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.17000300178814D, mc.player.getZ(), false));
                                                      PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.170010501788138D, mc.player.getZ(), false));
                                                      Vec3d center = Vec3d.ofCenter(pos);
                                                      Vec3d newCenter = center.add((double)facing.getOffsetX() * 0.5D, (double)facing.getOffsetY() * 0.5D, (double)facing.getOffsetZ() * 0.5D);
                                                      BlockPos finalPos = pos.offset(facing);
                                                      PacketUtil.sneak(true);
                                                      BlockHitResult res = new BlockHitResult(newCenter, facing.getOpposite(), finalPos, false);
                                                      ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
                                                         return new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, res, sequence);
                                                      });
                                                      if ((Boolean)((SelfFill)this.module).swing.getValue()) {
                                                         PacketUtil.swing();
                                                      }

                                                      PacketUtil.sneak(false);
                                                      if ((Boolean)((SelfFill)this.module).dynamic.getValue()) {
                                                         PacketUtil.send(new PositionAndOnGround(mc.player.getX(), ((SelfFill)this.module).getY(mc.player), mc.player.getZ(), false));
                                                      } else {
                                                         PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + 1.242610501394747D, mc.player.getZ(), false));
                                                         PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + 2.340020003576277D, mc.player.getZ(), false));
                                                      }

                                                      if ((Boolean)((SelfFill)this.module).altSwap.getValue()) {
                                                         InventoryUtil.altSwap(slot);
                                                      } else {
                                                         InventoryUtil.switchToSlot(startSlot);
                                                      }

                                                      if ((Boolean)((SelfFill)this.module).rotate.getValue()) {
                                                         PacketUtil.rotate(oldRots, false);
                                                      }

                                                      ((AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class)).setLock(false);
                                                      ((SelfFill)this.module).setEnabled(false);
                                                      return;
                                                   }

                                                   entity = (Entity)var17.next();
                                                } while(entity == null);
                                             } while(EntityUtil.isDead(entity));
                                          } while(entity == mc.player);
                                       } while(entity instanceof ItemEntity);
                                    } while(entity instanceof ExperienceOrbEntity);
                                 } while(entity instanceof ArrowEntity);

                                 if (entity instanceof LivingEntity) {
                                    LivingEntity livingEntity = (LivingEntity)entity;
                                    ILivingEntity iLivingEntity = (ILivingEntity)livingEntity;
                                    if (iLivingEntity.getServerBoundingBox().intersects(box)) {
                                       return;
                                    }
                                 }

                                 if (entity instanceof EndCrystalEntity && (Boolean)((SelfFill)this.module).attack.getValue()) {
                                    float damage = CombatUtil.getDamage(entity, mc.world, mc.player);
                                    if (damage < currentDmg) {
                                       currentDmg = damage;
                                       attacking = PlayerInteractEntityC2SPacket.attack(entity, false);
                                    } else {
                                       crystals = true;
                                    }
                                 } else if (entity.getBoundingBox().intersects(box)) {
                                    return;
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
   }

   private void attack(Packet<?> attacking) {
      PacketUtil.send(attacking);
      if ((Boolean)((SelfFill)this.module).swing.getValue()) {
         PacketUtil.swing();
      }

   }
}
