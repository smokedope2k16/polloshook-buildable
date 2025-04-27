package me.pollos.polloshook.asm.ducks.entity;

public interface IEndCrystalEntity {
   long getLastAttackTime();

   void setLastAttackTime(long var1);

   int getHitsSinceLastAttack();

   void setHitsSinceLastAttack(int var1);

   float getExactTicksExisted();

   long getSpawnTime();
}
