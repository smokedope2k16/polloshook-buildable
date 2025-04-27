package me.pollos.polloshook.api.minecraft.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.impl.module.combat.aura.modes.Location;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtil implements Minecraftable {
   public static final List<Block> BURROW_BLOCKS;
   public static final List<Class<?>> PLACEABLE_ENTITES;

   public static float[] getRotations(Entity entity) {
      return getRotationsAtLocation(Location.HEAD, entity);
   }

   public static float[] getRotationsAtLocation(Location location, Entity entity) {
      double locationMath = 0.0D;

      assert mc.player != null;

      double positionX = entity.getX() - mc.player.getX();
      double positionZ = entity.getZ() - mc.player.getZ();
      switch(location) {
      case HEAD:
         locationMath = 1.0D;
         break;
      case BODY:
         locationMath = 1.3D;
         break;
      case LEGS:
         locationMath = 2.9D;
         break;
      case FEET:
         locationMath = 4.0D;
      }

      double positionY = entity.getY() + (double)entity.getEyeHeight(entity.getPose()) / locationMath - (mc.player.getY() + (double)mc.player.getEyeHeight(mc.player.getPose()));
      double positions = (double)MathHelper.sqrt((float)(positionX * positionX + positionZ * positionZ));
      float yaw = (float)(Math.atan2(positionZ, positionX) * 180.0D / 3.141592653589793D - 90.0D);
      float pitch = (float)(-(Math.atan2(positionY, positions) * 180.0D / 3.141592653589793D));
      return new float[]{yaw, pitch};
   }

   public static boolean isDead(Entity entity) {
      return !entity.isAlive();
   }

   public static PlayerEntity getClosestEnemy() {
      PlayerEntity closest = null;
      double distance = 3.4028234663852886E38D;
      Iterator var3 = (new ArrayList(mc.world.getPlayers())).iterator();

      while(var3.hasNext()) {
         PlayerEntity player = (PlayerEntity)var3.next();
         if (player != null && player.isAlive() && !player.equals(mc.player) && !Managers.getFriendManager().isFriend(player)) {
            Vec3d pos = mc.player.getPos();
            double dist = player.squaredDistanceTo(pos.x, pos.y, pos.z);
            if (dist < distance) {
               closest = player;
               distance = dist;
            }
         }
      }

      return closest;
   }

   public static float getHealth(LivingEntity living) {
      if (living == null) {
         return 0.0F;
      } else if (living instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)living;
         return player.getHealth() + player.getAbsorptionAmount();
      } else {
         return living.getHealth();
      }
   }

   public static String getName(Entity entity) {
      if (entity == null) {
         return entity == mc.player ? mc.getSession().getUsername() : "$KP$";
      } else {
         return entity.getName().getString();
      }
   }

   public static boolean isTrapped(PlayerEntity player) {
      return isTrapped(player, true);
   }

   public static boolean isTrapped(PlayerEntity player, boolean up) {
      Set<BlockPos> blocked = PositionUtil.getBlockedPositions((Entity)player);
      boolean trap = false;
      Iterator var4 = blocked.iterator();

      while(var4.hasNext()) {
         BlockPos pos = (BlockPos)var4.next();
         if (BlockUtil.isSafe(pos.up().up())) {
            trap = true;
            break;
         }
      }

      Set<BlockPos> surround = PositionUtil.getSurroundOffsets((Entity)player);
      boolean safe = true;
      Iterator var6 = surround.iterator();

      while(var6.hasNext()) {
         BlockPos pos = (BlockPos)var6.next();
         if (BlockUtil.isAir(pos) || BlockUtil.isAir(pos.up()) && up) {
            safe = false;
            break;
         }
      }

      return trap && safe;
   }

   public static boolean isInWater(Box bb) {
      for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
         for(int y = MathHelper.floor(bb.minY); y < MathHelper.floor(bb.maxY) + 1; ++y) {
            for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
               if (mc.world.getBlockState(new BlockPos(x, y, z)).getBlock().getDefaultState().isLiquid() && bb.intersects(new Box((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1)))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isInBurrow(PlayerEntity player) {
      BlockPos pos = player.getBlockPos();
      return isBurrow(pos) || isBurrow(pos.up());
   }

   public static boolean isBurrow(BlockPos pos) {
      BlockState state = mc.world.getBlockState(pos);
      return BURROW_BLOCKS.contains(state.getBlock());
   }

   public static void swingClient(Hand hand) {
      mc.player.swingHand(hand, false);
   }

   public static boolean isSafe(LivingEntity entity) {
      if (!(entity instanceof PlayerEntity)) {
         return false;
      } else {
         Iterator var1 = PositionUtil.getBlockedPositions(entity.getBoundingBox().contract(0.5D)).iterator();

         BlockPos pos;
         do {
            if (!var1.hasNext()) {
               var1 = PositionUtil.getSurroundOffsets((Entity)entity).iterator();

               do {
                  if (!var1.hasNext()) {
                     return true;
                  }

                  pos = (BlockPos)var1.next();
               } while(!mc.world.getBlockState(pos).isReplaceable());

               return false;
            }

            pos = (BlockPos)var1.next();
         } while(mc.world.getBlockState(pos).isReplaceable());

         return true;
      }
   }

   public static boolean isAboveWater(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         double y = entity.getY() - 0.2D;
         return isAboveWater(entity, y);
      }
   }

   public static boolean isAboveWater(Entity entity, double y) {
      for(int x = MathHelper.floor(entity.getX()); x < MathHelper.ceil(entity.getX()); ++x) {
         for(int z = MathHelper.floor(entity.getZ()); z < MathHelper.ceil(entity.getZ()); ++z) {
            BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
            if (mc.world.getBlockState(pos).getBlock() instanceof FluidBlock) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isPhasing(Box bb) {
      for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
         for(int y = MathHelper.floor(bb.minY); y < MathHelper.floor(bb.maxY) + 1; ++y) {
            for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
               BlockPos pos = new BlockPos(x, y, z);
               BlockState state = mc.world.getBlockState(pos);
               if (!state.getCollisionShape(mc.world, pos).isEmpty()) {
                  Box blockBB = state.getCollisionShape(mc.world, pos).getBoundingBox();
                  if (state.blocksMovement() && bb.intersects(blockBB)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static float getArmor(LivingEntity living) {
      if (!(living instanceof PlayerEntity)) {
         return 0.0F;
      } else {
         PlayerEntity player = (PlayerEntity)living;
         float full = 0.0F;

         ItemStack stack;
         for(Iterator var3 = player.getArmorItems().iterator(); var3.hasNext(); full += (float)(stack.getMaxDamage() - stack.getDamage())) {
            stack = (ItemStack)var3.next();
         }

         return full;
      }
   }

   static {
      BURROW_BLOCKS = Arrays.asList( // no fucking info about these anywhare and yarn just doesnt exist for these so I took my best guess :/
         Blocks.ANVIL,      
         Blocks.OBSIDIAN, 
         Blocks.CRYING_OBSIDIAN,         
         Blocks.RESPAWN_ANCHOR,          
         Blocks.NETHERITE_BLOCK,       
         Blocks.END_STONE,            
         Blocks.PURPUR_BLOCK,             
         Blocks.LODESTONE,                 
         Blocks.POLISHED_BLACKSTONE_BRICKS,
         Blocks.DEEPSLATE_BRICKS,          
         Blocks.DEEPSLATE_TILES,          
         Blocks.CHISELED_DEEPSLATE,       
         Blocks.POLISHED_DEEPSLATE
      );
      
      PLACEABLE_ENTITES = Arrays.asList(ItemEntity.class, ArrowEntity.class, ExperienceOrbEntity.class);
   }
}
