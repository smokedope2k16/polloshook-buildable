package me.pollos.polloshook.impl.module.combat.pearlcatch;

import java.util.Iterator;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.AutoSwitch;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class ListenerMotion extends ModuleListener<PearlCatch, MotionUpdateEvent> {
   public ListenerMotion(PearlCatch module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      int crystalSlot = ItemUtil.getHotbarItemSlot(Items.END_CRYSTAL);
      if (crystalSlot != -1) {
         Iterator var3 = mc.world.getEntities().iterator();

         while(true) {
            EnderPearlEntity pearlEntity;
            while(true) {
               Entity entity;
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  entity = (Entity)var3.next();
               } while(!(entity instanceof EnderPearlEntity));

               pearlEntity = (EnderPearlEntity)entity;
               boolean debug = (Boolean)((PearlCatch)this.module).debug.getValue() && PollosHook.isRunClient();
               if (pearlEntity.getOwner() == null || debug) {
                  break;
               }

               if (!pearlEntity.getOwner().equals(mc.player)) {
                  Entity var8 = pearlEntity.getOwner();
                  if (!(var8 instanceof PlayerEntity)) {
                     break;
                  }

                  PlayerEntity player = (PlayerEntity)var8;
                  if (!Managers.getFriendManager().isFriend(player)) {
                     break;
                  }
               }
            }

            Vec3d velocity = pearlEntity.getVelocity();

            for(float i2 = 1.0F; i2 <= (Float)((PearlCatch)this.module).range.getValue(); ++i2) {
               BlockPos extrapolatedPos = pearlEntity.getBlockPos().add((int)(velocity.x * (double)i2), (int)(velocity.y * (double)i2), (int)(velocity.z * (double)i2));
               Box extrapolatedPosBox = new Box(extrapolatedPos);
               BlockPos pos = null;

               int lastSlot;
               for(lastSlot = 0; lastSlot <= 3; ++lastSlot) {
                  BlockPos down = extrapolatedPos.down(lastSlot);
                  if (this.canPlace(down)) {
                     pos = extrapolatedPos.down(lastSlot);
                  }

                  if (pos == null) {
                     for(int i1 = 0; i1 <= 2; ++i1) {
                        Direction[] var15 = FacingUtil.HORIZONTALS;
                        int var16 = var15.length;

                        for(int var17 = 0; var17 < var16; ++var17) {
                           Direction direction = var15[var17];
                           BlockPos down2 = down.offset(direction, i1);
                           Box box = (new Box(down2)).expand(0.5D, (double)(i1 * 2), 0.5D);
                           if (this.canPlace(down2) && box.intersects(extrapolatedPosBox)) {
                              pos = down2;
                           }
                        }
                     }
                  }
               }

               if (pos != null && ((PearlCatch)this.module).timer.passed((double)((Float)((PearlCatch)this.module).delay.getValue() * 50.0F))) {
                  if ((Boolean)((PearlCatch)this.module).rotate.getValue() && event.getStage() == Stage.PRE) {
                     float[] rots = BlockUtil.getBlockPosRotations(pos);
                     Managers.getRotationManager().setRotations(rots, event);
                  }

                  lastSlot = mc.player.getInventory().selectedSlot;
                  switch((AutoSwitch)((PearlCatch)this.module).swap.getValue()) {
                  case NORMAL:
                  case SILENT:
                     InventoryUtil.switchToSlot(crystalSlot);
                     break;
                  case ALT_SWAP:
                     InventoryUtil.altSwap(crystalSlot);
                  }

                  BlockHitResult result = this.handlePlacement(pos);
                  PacketUtil.send(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, result, 0));
                  switch((AutoSwitch)((PearlCatch)this.module).swap.getValue()) {
                  case NORMAL:
                  case SILENT:
                     InventoryUtil.switchToSlot(crystalSlot);
                     break;
                  case ALT_SWAP:
                     InventoryUtil.altSwap(lastSlot);
                  }

                  ((PearlCatch)this.module).timer.reset();
               }
            }
         }
      }
   }

   protected boolean canPlace(BlockPos pos) {
      if (!BlockUtil.isAir(pos.up())) {
         return false;
      } else {
         return BlockUtil.getBlock(pos) == Blocks.OBSIDIAN || BlockUtil.getBlock(pos) == Blocks.ANVIL;
      }
   }

   protected BlockHitResult handlePlacement(BlockPos pos) {
      Vec3d playerPos = PositionUtil.getEyesPos();
      BlockHitResult result = this.rayTracePlacement(pos, playerPos, false);
      if (result == null) {
         result = this.rayTracePlacement(pos, playerPos, true);
      }

      if (result == null) {
         result = new BlockHitResult(new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D), Direction.UP, pos, false);
      }

      return result;
   }

   private BlockHitResult rayTracePlacement(BlockPos pos, Vec3d playerPos, boolean throughWalls) {
      if (throughWalls && (Boolean)((PearlCatch)this.module).strictDirection.getValue()) {
         return new BlockHitResult(new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D), Direction.UP, pos, false);
      } else {
         Direction bestFacing = null;
         Vec3d bestVec = null;
         double bestDistance = 999.0D;
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction facing = var8[var10];
            Vec3d vec = new Vec3d((double)pos.getX() + 0.5D + (double)facing.getVector().getX() * 0.5D, (double)pos.getY() + 0.5D + (double)facing.getVector().getY() * 0.5D, (double)pos.getZ() + 0.5D + (double)facing.getVector().getZ() * 0.5D);
            if (throughWalls) {
               double distance = playerPos.distanceTo(vec);
               if (bestFacing == null || !(distance > bestDistance)) {
                  bestFacing = facing;
                  bestVec = vec;
                  bestDistance = distance;
               }
            } else {
               RaycastContext raycastContext = new RaycastContext(playerPos, vec, ShapeType.COLLIDER, FluidHandling.NONE, mc.player);
               BlockHitResult result = mc.world.raycast(raycastContext);
               if (result != null && result.getType() == Type.BLOCK && result.getBlockPos().equals(pos)) {
                  double distance = playerPos.distanceTo(result.getPos());
                  if (bestFacing == null || !(distance > bestDistance)) {
                     bestFacing = result.getSide();
                     bestVec = result.getPos();
                     bestDistance = distance;
                  }
               }
            }
         }

         if (bestFacing != null) {
            return new BlockHitResult(bestVec, bestFacing, pos, false);
         } else {
            return null;
         }
      }
   }
}