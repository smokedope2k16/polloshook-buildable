package me.pollos.polloshook.api.util.obj.hole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Hole2x1 extends Hole {
   protected final BlockPos secondPos;

   public Hole2x1(BlockPos pos, BlockPos secondPos, SafetyEnum safety) {
      super(pos, safety);
      this.secondPos = secondPos;
   }

   public boolean isProtocolSafe() {
      return !BlockUtil.isAir(this.getSecondPos().up()) || !BlockUtil.isAir(this.getPos().up());
   }

   public Hole getProtocolSafePart() {
      if (!this.isProtocolSafe()) {
         ClientLogger.getLogger().warn("Hole is not 1.12.2 safe");
         return null;
      } else {
         return BlockUtil.isAir(this.getSecondPos().up()) ? new Hole(this.getPos(), SafetyEnum.MIXED) : new Hole(this.getSecondPos(), SafetyEnum.MIXED);
      }
   }

   public List<BlockPos> getBlocks() {
      if (this.getPos() != null && this.secondPos != null) {
         List<BlockPos> positions = new ArrayList();
         Direction[] var2 = FacingUtil.DOWN;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Direction dir = var2[var4];
            positions.add(this.getPos().offset(dir));
            positions.add(this.secondPos.offset(dir));
         }

         positions.removeIf((pos) -> {
            return mc.world.getBlockState(pos).isAir();
         });
         return positions;
      } else {
         return Collections.emptyList();
      }
   }

   public BlockPos getSecondPos() {
      return this.secondPos;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Hole2x1)) {
         return false;
      } else {
         Hole2x1 other = (Hole2x1)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else {
            Object this$secondPos = this.getSecondPos();
            Object other$secondPos = other.getSecondPos();
            if (this$secondPos == null) {
               if (other$secondPos != null) {
                  return false;
               }
            } else if (!this$secondPos.equals(other$secondPos)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof Hole2x1;
   }

   public int hashCode() {
      int result = super.hashCode();
      BlockPos secondPos = this.getSecondPos();
      result = result * 59 + (secondPos == null ? 43 : secondPos.hashCode());
      return result;
  }

   public String toString() {
      String var10000 = super.toString();
      return "Hole2x1(super=" + var10000 + ", secondPos=" + String.valueOf(this.getSecondPos()) + ")";
   }
}
