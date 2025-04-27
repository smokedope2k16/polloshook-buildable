package me.pollos.polloshook.impl.module.combat.autocrystal.util;


import me.pollos.polloshook.api.minecraft.render.RenderPosition;
import net.minecraft.util.math.BlockPos;

public class CrystalRenderPos extends RenderPosition {
   float damage;

   public CrystalRenderPos(BlockPos pos, float damage) {
      super(pos);
      this.damage = damage;
   }

   
   public float getDamage() {
      return this.damage;
   }
}
