package me.pollos.polloshook.impl.module.combat.autocrystal.util;


import net.minecraft.util.math.BlockPos;

public class CrystalPos extends BlockPos {
   private final float damage;

   public CrystalPos(BlockPos pos, float damage) {
      super(pos.getX(), pos.getY(), pos.getZ());
      this.damage = damage;
   }

   public BlockPos toPos() {
      return new BlockPos(this.getX(), this.getY(), this.getZ());
   }

   
   public float getDamage() {
      return this.damage;
   }
}
