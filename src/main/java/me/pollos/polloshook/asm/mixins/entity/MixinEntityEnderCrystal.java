package me.pollos.polloshook.asm.mixins.entity;


import me.pollos.polloshook.asm.ducks.entity.IEndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EndCrystalEntity.class})
public abstract class MixinEntityEnderCrystal extends Entity implements IEndCrystalEntity {
   @Unique
   private long lastAttackTime = 0L;
   @Unique
   private int hitsSinceLastAttack = 0;
   @Unique
   private long spawnTime;

   public MixinEntityEnderCrystal(EntityType<?> type, World world) {
      super(type, world);
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/world/World;DDD)V"},
      at = {@At("RETURN")}
   )
   private void initHook(World world, double x, double y, double z, CallbackInfo ci) {
      this.spawnTime = System.currentTimeMillis();
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V"},
      at = {@At("RETURN")}
   )
   private void initHookTheSecond(EntityType entityType, World world, CallbackInfo ci) {
      this.spawnTime = System.currentTimeMillis();
   }

   public float getExactTicksExisted() {
      float tickRate = this.getWorld().getTickManager().getTickRate();
      return (float)(System.currentTimeMillis() - this.spawnTime) / tickRate;
   }

   
   public void setLastAttackTime(long lastAttackTime) {
      this.lastAttackTime = lastAttackTime;
   }

   
   public void setHitsSinceLastAttack(int hitsSinceLastAttack) {
      this.hitsSinceLastAttack = hitsSinceLastAttack;
   }

   
   public void setSpawnTime(long spawnTime) {
      this.spawnTime = spawnTime;
   }

   
   public long getLastAttackTime() {
      return this.lastAttackTime;
   }

   
   public int getHitsSinceLastAttack() {
      return this.hitsSinceLastAttack;
   }

   
   public long getSpawnTime() {
      return this.spawnTime;
   }
}
