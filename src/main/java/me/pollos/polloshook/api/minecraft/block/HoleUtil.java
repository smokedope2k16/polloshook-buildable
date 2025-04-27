package me.pollos.polloshook.api.minecraft.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.api.util.obj.hole.Hole2x1;
import me.pollos.polloshook.api.util.obj.hole.SafetyEnum;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HoleUtil implements Minecraftable {
   public static final BlockPos[] HOLE_OFFSETS = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
   public static final BlockPos[] HOLE_OFFSETS_2X2 = new BlockPos[]{new BlockPos(0, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(1, 0, 1)};

   public static boolean isHole(BlockPos pos) {
      return isMixedHole(pos) || isBedrockHole(pos) || isObbyHole(pos);
   }

   public static boolean isInHole(PlayerEntity player) {
      return isHole(player.getBlockPos());
   }

   public static boolean isWebHole(BlockPos pos) {
      if (BlockUtil.isAir(pos.up()) && BlockUtil.isAir(pos.up().up())) {
         BlockPos[] var1 = HOLE_OFFSETS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockPos off = var1[var3];
            if (!isWeb(pos.add(off))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isWeb(BlockPos pos) {
      return mc.world.getBlockState(pos).getBlock() == Blocks.COBWEB || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos).getBlock() == Blocks.ANVIL;
   }

   public static boolean isObbyHole(BlockPos pos) {
      if (BlockUtil.isAir(pos.up()) && BlockUtil.isAir(pos.up(2))) {
         BlockPos[] var1 = HOLE_OFFSETS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockPos off = var1[var3];
            if (!BlockUtil.isObby(pos.add(off))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isMixedHole(BlockPos pos) {
      if (isBedrockHole(pos)) {
         return false;
      } else if (BlockUtil.isAir(pos.up()) && BlockUtil.isAir(pos.up(2))) {
         BlockPos[] var1 = HOLE_OFFSETS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockPos off = var1[var3];
            if (!BlockUtil.isSafe(pos.add(off))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isBedrockHole(BlockPos pos) {
      if (BlockUtil.isAir(pos.up()) && BlockUtil.isAir(pos.up(2))) {
         BlockPos[] var1 = HOLE_OFFSETS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockPos off = var1[var3];
            if (!BlockUtil.isBedrock(pos.add(off))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isTerrainHole(BlockPos pos) {
      if (isHole(pos)) {
         return false;
      } else if (BlockUtil.isAir(pos.up()) && BlockUtil.isAir(pos.up(2))) {
         BlockPos[] var1 = HOLE_OFFSETS;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockPos off = var1[var3];
            Block block = mc.world.getBlockState(pos.add(off)).getBlock();
            if (block != Blocks.TALL_GRASS && block != Blocks.GLASS && block != Blocks.VINE && block != Blocks.CHEST && block != Blocks.CACTUS && block != Blocks.SUGAR_CANE && block != Blocks.SWEET_BERRY_BUSH && block != Blocks.CAVE_VINES && !BlockUtil.isSafe(pos.add(off))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean isDoubleHole(BlockPos pos) {
      Hole2x1 hole = getDoubleHole(pos);
      if (hole == null) {
         return false;
      } else {
         return BlockUtil.isAir(hole.getPos().up()) && BlockUtil.isAir(hole.getPos().up(2)) || BlockUtil.isAir(hole.getSecondPos().up()) && BlockUtil.isAir(hole.getSecondPos().up(2));
      }
   }

   public static Hole2x1 getDoubleHole(BlockPos pos) {
      if (isBedrock(pos, 0, 1)) {
         return new Hole2x1(pos, pos.add(0, 0, 1), SafetyEnum.BEDROCK);
      } else if (isBedrock(pos, 1, 0)) {
         return new Hole2x1(pos, pos.add(1, 0, 0), SafetyEnum.BEDROCK);
      } else if (isObby(pos, 0, 1)) {
         return new Hole2x1(pos, pos.add(0, 0, 1), SafetyEnum.OBBY);
      } else if (isObby(pos, 1, 0)) {
         return new Hole2x1(pos, pos.add(1, 0, 0), SafetyEnum.OBBY);
      } else if (isMixed(pos, 0, 1)) {
         return new Hole2x1(pos, pos.add(0, 0, 1), SafetyEnum.MIXED);
      } else {
         return isMixed(pos, 1, 0) ? new Hole2x1(pos, pos.add(1, 0, 0), SafetyEnum.MIXED) : null;
      }
   }

   public static Vec3d getCenter(Hole hole) {
      double x = (double)hole.getPos().getX() + 0.5D;
      double z = (double)hole.getPos().getZ() + 0.5D;
      if (hole instanceof Hole2x1) {
         Hole2x1 hole2x1 = (Hole2x1)hole;
         x = (x + (double)hole2x1.getSecondPos().getX() + 0.5D) / 2.0D;
         z = (z + (double)hole2x1.getSecondPos().getZ() + 0.5D) / 2.0D;
      }

      return new Vec3d(x, (double)hole.getPos().getY(), z);
   }

   public static boolean isObby(BlockPos pos, int offX, int offZ) {
      return BlockUtil.isAir(pos) && BlockUtil.isAir(pos.add(offX, 0, offZ)) && BlockUtil.isObby(pos.add(0, -1, 0)) && BlockUtil.isObby(pos.add(offX, -1, offZ)) && BlockUtil.isObby(pos.add(offX * 2, 0, offZ * 2)) && BlockUtil.isObby(pos.add(-offX, 0, -offZ)) && BlockUtil.isObby(pos.add(offZ, 0, offX)) && BlockUtil.isObby(pos.add(-offZ, 0, -offX)) && BlockUtil.isObby(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtil.isObby(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
   }

   public static boolean isMixed(BlockPos pos, int offX, int offZ) {
      return BlockUtil.isAir(pos) && BlockUtil.isAir(pos.add(offX, 0, offZ)) && BlockUtil.isSafe(pos.add(0, -1, 0)) && BlockUtil.isSafe(pos.add(offX, -1, offZ)) && BlockUtil.isSafe(pos.add(offX * 2, 0, offZ * 2)) && BlockUtil.isSafe(pos.add(-offX, 0, -offZ)) && BlockUtil.isSafe(pos.add(offZ, 0, offX)) && BlockUtil.isSafe(pos.add(-offZ, 0, -offX)) && BlockUtil.isSafe(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtil.isSafe(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
   }

   public static boolean isBedrock(BlockPos pos, int offX, int offZ) {
      return BlockUtil.isAir(pos) && BlockUtil.isAir(pos.add(offX, 0, offZ)) && BlockUtil.isBedrock(pos.add(0, -1, 0)) && BlockUtil.isBedrock(pos.add(offX, -1, offZ)) && BlockUtil.isBedrock(pos.add(offX * 2, 0, offZ * 2)) && BlockUtil.isBedrock(pos.add(-offX, 0, -offZ)) && BlockUtil.isBedrock(pos.add(offZ, 0, offX)) && BlockUtil.isBedrock(pos.add(-offZ, 0, -offX)) && BlockUtil.isBedrock(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtil.isBedrock(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
   }

   public static Hole getHole(PlayerEntity entity) {
      return getHole(entity.getBlockPos(), true, true);
   }

   public static Hole getHole(BlockPos pos, boolean doubles, boolean terrain) {
      if (!BlockUtil.isAir(pos)) {
         return null;
      } else {
         Hole hole = null;
         if (isBedrockHole(pos)) {
            hole = new Hole(pos, SafetyEnum.BEDROCK);
         } else if (isObbyHole(pos)) {
            hole = new Hole(pos, SafetyEnum.OBBY);
         } else if (isMixedHole(pos)) {
            hole = new Hole(pos, SafetyEnum.MIXED);
         } else if (terrain && isTerrainHole(pos)) {
            hole = new Hole(pos, SafetyEnum.TERRAIN);
         }

         if (doubles && isDoubleHole(pos)) {
            hole = getDoubleHole(pos);
         }

         return (Hole)hole;
      }
   }

   public static List<Hole> getHoles(float range, boolean doubles, boolean webs, boolean terrain) {
      return getHoles(mc.player, range, range, doubles, webs, terrain);
   }

   public static List<Hole> getHoles(float range, float yRange, boolean doubles, boolean webs, boolean terrain) {
      return getHoles(mc.player, range, yRange, doubles, webs, terrain);
   }

   public static List<Hole> getHoles(Entity player, float range, float yRange, boolean doubles, boolean webs, boolean terrain) {
      List<Hole> holes = new ArrayList();
      Iterator var7 = BlockUtil.getSphere(player, range, yRange, false).iterator();

      while(true) {
         while(true) {
            BlockPos pos;
            do {
               if (!var7.hasNext()) {
                  return holes;
               }

               pos = (BlockPos)var7.next();
            } while(!BlockUtil.isAir(pos));

            if (webs && isWebHole(pos)) {
               holes.add(new Hole(pos, SafetyEnum.MIXED));
            } else if (isBedrockHole(pos)) {
               holes.add(new Hole(pos, SafetyEnum.BEDROCK));
            } else if (isObbyHole(pos)) {
               holes.add(new Hole(pos, SafetyEnum.OBBY));
            } else if (isMixedHole(pos)) {
               holes.add(new Hole(pos, SafetyEnum.MIXED));
            } else if (terrain && isTerrainHole(pos)) {
               holes.add(new Hole(pos, SafetyEnum.TERRAIN));
            } else if (doubles && isDoubleHole(pos)) {
               holes.add(getDoubleHole(pos));
            }
         }
      }
   }
}
