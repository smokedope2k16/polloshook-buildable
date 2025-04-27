package me.pollos.polloshook.asm.ducks.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public interface IPlayerEntity {
   Vec3d[] getPredictedPositions();

   void invokeTick();

   int lastAttackedTicks();

   void setLastAttackedTicks(int var1);

   void setInventory(PlayerInventory var1);

   boolean isFirstUpdate();

   float $getDamageAgainst(LivingEntity var1, float var2, DamageSource var3);

   void $onStatusEffectUpgraded(StatusEffectInstance var1, boolean var2, @Nullable Entity var3);

   Vec3d getLastSpeedVec();

   void setLastSpeedVec(Vec3d var1);
}
