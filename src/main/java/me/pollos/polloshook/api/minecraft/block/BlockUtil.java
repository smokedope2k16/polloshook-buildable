package me.pollos.polloshook.api.minecraft.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.api.minecraft.world.BlockStateHelper;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;

public class BlockUtil implements Minecraftable {
   private static final BlockStateHelper CACHE_HELPER = new BlockStateHelper();
   public static final List<Block> INTERACTABLES;
   public static final List<Block> SHULKERS;
   public static final List<Block> BEDS;
   public static final List<Item> BEDS_ITEM;

   public static float[] getVecRotations(Vec3d vec3d) {
      return RotationsUtil.getRotations(vec3d.x, vec3d.y, vec3d.z);
   }

   public static float[] getBlockPosRotations(BlockPos pos) {
      return RotationsUtil.getRotations((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
   }

   public static List<BlockPos> sortByPlacing(List<BlockPos> positions) {
      List<BlockPos> sortedPositions = new ArrayList();
      List<BlockPos> toRemove = new ArrayList();

      for(int i = 1; i < 10 && !positions.isEmpty(); ++i) {
         Iterator var4 = positions.iterator();

         while(var4.hasNext()) {
            BlockPos pos = (BlockPos)var4.next();
            if (!sortedPositions.contains(pos)) {
               Direction facing = getFacing(pos, CACHE_HELPER);
               if (facing != null) {
                  sortedPositions.add(pos);
                  toRemove.add(pos);
                  CACHE_HELPER.addBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
               }
            }
         }

         positions.removeAll(toRemove);
      }

      return sortedPositions;
   }

   public static Map<BlockPos, BlockState> getRelatedStates(BlockPos pos, WorldAccess world) {
      Map<BlockPos, BlockState> relatedStates = new HashMap();
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction dir = var3[var5];
         BlockPos offset = pos.offset(dir);
         BlockState state = world.getBlockState(offset);
         if (!relatedStates.containsKey(offset)) {
            relatedStates.put(offset, state);
         }
      }

      return relatedStates;
   }

   public static Map<BlockPos, BlockState> getRelatedStates(List<BlockPos> positions, WorldAccess world) {
      Map<BlockPos, BlockState> relatedStates = new HashMap();
      Iterator var3 = positions.iterator();

      while(var3.hasNext()) {
         BlockPos pos = (BlockPos)var3.next();
         Direction[] var5 = Direction.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction dir = var5[var7];
            BlockPos offset = pos.offset(dir);
            BlockState state = world.getBlockState(offset);
            if (!relatedStates.containsKey(offset)) {
               relatedStates.put(offset, state);
            }
         }
      }

      return relatedStates;
   }

   public static List<BlockPos> sortByPlacing(List<BlockPos> positions, WorldAccess world) {
      Map<BlockPos, BlockState> relatedStates = getRelatedStates(positions, world);
      List<BlockPos> sortedPositions = new ArrayList();
      List<BlockPos> toRemove = new ArrayList();

      for(int i = 1; i < 10 && !positions.isEmpty(); ++i) {
         Iterator var6 = positions.iterator();

         while(var6.hasNext()) {
            BlockPos pos = (BlockPos)var6.next();
            if (!sortedPositions.contains(pos)) {
               Direction facing = FacingUtil.getFacing(pos, relatedStates);
               if (facing != null) {
                  sortedPositions.add(pos);
                  toRemove.add(pos);
                  relatedStates.put(pos, Blocks.OBSIDIAN.getDefaultState());
               }
            }
         }

         positions.removeAll(toRemove);
      }

      return sortedPositions;
   }

   public static List<BlockPos> getCircle(BlockPos pos, float radio) {
      ArrayList<BlockPos> blocks = new ArrayList();
      int cx = pos.getX();
      int cy = pos.getY();
      int cz = pos.getZ();

      for(int x = cx - (int)radio; (float)x <= (float)cx + radio; ++x) {
         for(int z = cz - (int)radio; (float)z <= (float)cz + radio; ++z) {
            double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z));
            if (dist < (double)(radio * radio)) {
               BlockPos position = new BlockPos(x, cy, z);
               blocks.add(position);
            }
         }
      }

      return blocks;
   }

   public static List<BlockPos> getSphere(float radius, boolean ignoreAir) {
      return getSphere(mc.player, radius, ignoreAir);
   }

   public static List<BlockPos> getSphere(Entity entity, float radius, boolean ignoreAir) {
      return getSphere(entity, radius, radius, ignoreAir);
   }

   public static List<BlockPos> getSphere(Entity entity, float radius, float radiusY, boolean ignoreAir) {
      List<BlockPos> sphere = new ArrayList();
      BlockPos pos = BlockPos.ofFloored(entity.getPos());
      int posX = pos.getX();
      int posY = pos.getY();
      int posZ = pos.getZ();
      int radiuss = (int)radius;

      for(int x = posX - radiuss; (float)x <= (float)posX + radius; ++x) {
         for(int z = posZ - radiuss; (float)z <= (float)posZ + radius; ++z) {
            for(int y = posY - radiuss; (float)y < (float)posY + radiusY; ++y) {
               if ((float)((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                  BlockPos position = new BlockPos(x, y, z);
                  if (!ignoreAir || getBlock(position) != Blocks.AIR) {
                     sphere.add(position);
                  }
               }
            }
         }
      }

      return sphere;
   }

   public static boolean canPlaceCrystal(BlockPos block) {
      return canPlaceCrystal(block, true);
   }

   public static boolean canPlaceCrystal(BlockPos pos, boolean check, boolean oldVer) {
      return canPlaceCrystal(pos, check, oldVer, 0);
   }

   public static boolean canPlaceCrystal(BlockPos pos, boolean check, boolean oldVer, int extrapolation) {
      BlockState state = mc.world.getBlockState(pos);
      if (state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.OBSIDIAN) {
          return false;
      } else {
          BlockPos up = pos.add(0, 1, 0);
          if (mc.world.getBlockState(up).getBlock() != Blocks.AIR) {
              return false;
          } else {
              BlockPos upUp = pos.add(0, 2, 0);
              if (mc.world.getBlockState(upUp).getBlock() != Blocks.AIR && oldVer) {
                  return false;
              } else if (check) {
                  AutoCrystal AUTO_CRYSTAL = (AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class);
                  Box bb = (new Box(up)).expand(0.0D, 1.0D, 0.0D);
                  Iterator var9 = Managers.getEntitiesManager().getPlayers().iterator();
                  
                  PlayerEntity player;
                  do {
                      if (!var9.hasNext()) {
                          var9 = Managers.getEntitiesManager().getAnyCollidingEntities(bb).iterator();
                          
                          while(var9.hasNext()) {
                              Entity entity = (Entity)var9.next();
                              if (entity != null && !EntityUtil.isDead(entity) && !(entity instanceof EndCrystalEntity) && !(entity instanceof ExperienceOrbEntity) && !(entity instanceof PlayerEntity)) {
                                  Box entityBB = AUTO_CRYSTAL.isReduced() ? entity.getBoundingBox().shrink(1.0E-7D, 1.0E-7D, 1.0E-7D) : entity.getBoundingBox();
                                  if (!AUTO_CRYSTAL.isReduced() || entityBB.intersects(bb)) {
                                      return false;
                                  }
                              }
                          }
                          
                          return true;
                      }
                      
                      player = (PlayerEntity)var9.next();
                      for(int i = extrapolation; i > 0; --i) {
                          Entity fakePlayer = CombatUtil.predictEntity(player, i);
                          if (fakePlayer.getBoundingBox().intersects(bb)) {
                              return false;
                          }
                      }
                  } while(!CombatUtil.predictEntity(player, 0).getBoundingBox().intersects(bb));
                  
                  return false;
              } else {
                  return true;
              }
          }
      }
  }

   public static boolean canPlaceCrystal(BlockPos pos, boolean check) {
      Block block = getBlock(pos);
      if (block == Blocks.BEDROCK && block == Blocks.OBSIDIAN) {
         if (check) {
            AutoCrystal AUTO_CRYSTAL = (AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class);
            Iterator var4 = Managers.getEntitiesManager().getAnyCollidingEntities((new Box(pos.up())).expand(0.0D, 1.0D, 0.0D)).iterator();

            while(var4.hasNext()) {
               Entity entity = (Entity)var4.next();
               if (entity != null && !EntityUtil.isDead(entity) && !(entity instanceof EndCrystalEntity) && !(entity instanceof ExperienceOrbEntity)) {
                  Box entityBB = AUTO_CRYSTAL.isReduced() ? entity.getBoundingBox().shrink(1.0E-7D, 1.0E-7D, 1.0E-7D) : entity.getBoundingBox();
                  if (!AUTO_CRYSTAL.isReduced() || entityBB.intersects((new Box(pos.up())).expand(0.0D, 1.0D, 0.0D))) {
                     return false;
                  }
               }
            }
         }

         return getState(pos.up()).isAir();
      } else {
         return false;
      }
   }

   public static boolean isAir(BlockPos pos) {
      return getState(pos).isAir();
   }

   public static double getDistanceSq(BlockPos pos) {
      return getDistanceSq(mc.player, (BlockPos)pos);
   }

   public static double getDistanceSq(Entity from, BlockPos to) {
      return from.squaredDistanceTo(to.toCenterPos());
   }

   public static double getDistanceSq(Vec3d pos) {
      return getDistanceSq(mc.player, (Vec3d)pos);
   }

   public static double getDistanceSq(Entity from, Vec3d to) {
      return from.squaredDistanceTo(to);
   }

   public static boolean isObby(BlockPos pos) {
      Block block = getBlock(pos);
      return block == Blocks.OBSIDIAN || block == Blocks.CRYING_OBSIDIAN || block == Blocks.RESPAWN_ANCHOR;
  }

   public static Block getBlock(BlockPos pos) {
      return getState(pos).getBlock();
   }

   public static BlockState getState(BlockPos pos) {
      return mc.world.getBlockState(pos);
   }

   public static boolean isBedrock(BlockPos pos) {
      return mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK;
   }

   public static boolean isEnderChest(BlockPos pos) {
      return mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
   }

   public static boolean isSafe(BlockPos pos) {
      return isObby(pos) || isBedrock(pos) || isEnderChest(pos);
   }

   public static Direction getFacing(BlockPos pos) {
      return getFacing(pos, mc.world);
   }

   public static Direction getFacing(BlockPos pos, WorldAccess provider) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction facing = var2[var4];
         if (!provider.getBlockState(pos.offset(facing)).isReplaceable()) {
            return facing;
         }
      }

      return null;
   }

   public static List<BlockEntity> getTileEntities(int viewDistance) {
      List<BlockEntity> blockEntities = new ArrayList();
      ChunkPos chunkPos = Interpolation.getRenderEntity().getChunkPos();
      List<Integer> loaded = new ArrayList();

      for(int x = 0; x < viewDistance; ++x) {
         for(int z = 0; z < viewDistance; ++z) {
            validateChunk(chunkPos, x, z, loaded, blockEntities);
            validateChunk(chunkPos, -x, z, loaded, blockEntities);
            validateChunk(chunkPos, x, -z, loaded, blockEntities);
            validateChunk(chunkPos, -x, -z, loaded, blockEntities);
         }
      }

      return blockEntities;
   }

   private static void validateChunk(ChunkPos pos, int x, int z, List<Integer> loadedChunks, List<BlockEntity> blockEntities) {
      WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(pos.x + x, pos.z + z);
      if (chunk != null && !loadedChunks.contains(chunk.hashCode())) {
         loadedChunks.add(chunk.hashCode());
         blockEntities.addAll(chunk.getBlockEntities().values());
      }
   }

   static {
      INTERACTABLES = Arrays.asList(
          Blocks.CHEST,
          Blocks.CRAFTING_TABLE,
          Blocks.FURNACE,
          Blocks.BREWING_STAND,
          Blocks.ENCHANTING_TABLE,
          Blocks.ENDER_CHEST,
          Blocks.ANVIL,
          Blocks.HOPPER,
          Blocks.DROPPER,
          Blocks.DISPENSER,
          Blocks.TRAPPED_CHEST,
          Blocks.LECTERN,
          Blocks.BEACON
      );
      
      SHULKERS = Arrays.asList(
          Blocks.SHULKER_BOX,
          Blocks.WHITE_SHULKER_BOX,
          Blocks.ORANGE_SHULKER_BOX,
          Blocks.MAGENTA_SHULKER_BOX,
          Blocks.LIGHT_BLUE_SHULKER_BOX,
          Blocks.YELLOW_SHULKER_BOX,
          Blocks.LIME_SHULKER_BOX,
          Blocks.PINK_SHULKER_BOX,
          Blocks.GRAY_SHULKER_BOX,
          Blocks.LIGHT_GRAY_SHULKER_BOX,
          Blocks.CYAN_SHULKER_BOX,
          Blocks.PURPLE_SHULKER_BOX,
          Blocks.BLUE_SHULKER_BOX,
          Blocks.BROWN_SHULKER_BOX,
          Blocks.GREEN_SHULKER_BOX,
          Blocks.RED_SHULKER_BOX,
          Blocks.BLACK_SHULKER_BOX
      );
      
      BEDS = Arrays.asList(
          Blocks.WHITE_BED,
          Blocks.ORANGE_BED,
          Blocks.MAGENTA_BED,
          Blocks.LIGHT_BLUE_BED,
          Blocks.YELLOW_BED,
          Blocks.LIME_BED,
          Blocks.PINK_BED,
          Blocks.GRAY_BED,
          Blocks.LIGHT_GRAY_BED,
          Blocks.CYAN_BED,
          Blocks.PURPLE_BED,
          Blocks.BLUE_BED,
          Blocks.BROWN_BED,
          Blocks.GREEN_BED,
          Blocks.RED_BED,
          Blocks.BLACK_BED
      );
      
      BEDS_ITEM = Arrays.asList(
          Items.WHITE_BED,
          Items.ORANGE_BED,
          Items.MAGENTA_BED,
          Items.LIGHT_BLUE_BED,
          Items.YELLOW_BED,
          Items.LIME_BED,
          Items.PINK_BED,
          Items.GRAY_BED,
          Items.LIGHT_GRAY_BED,
          Items.CYAN_BED,
          Items.PURPLE_BED,
          Items.BLUE_BED,
          Items.BROWN_BED,
          Items.GREEN_BED,
          Items.RED_BED,
          Items.BLACK_BED
      );
  }
}
