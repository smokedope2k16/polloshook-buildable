package me.pollos.polloshook.api.minecraft.entity;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Iterator;
import java.util.Set;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.rotations.RaycastUtil;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import me.pollos.polloshook.impl.manager.minecraft.combat.SafeManager;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.explosion.Explosion;

public class CombatUtil implements Minecraftable {
   public static final float END_CRYSTAL_EXPLOSION = 6.0F;
   public static final float ANCHOR_EXPLOSION = 5.0F;
   public static final float BED_EXPLOSION = 5.0F;

   public static float getDamage(Entity entity, WorldAccess world, Entity crystal) {
      return getDamage(entity, world, 6.0F, crystal.getX(), crystal.getY(), crystal.getZ());
   }

   public static float getDamage(Entity entity, WorldAccess world, EndCrystalEntity crystal, boolean blockDestroy) {
      return getDamage(entity, world, 6.0F, crystal.getX(), crystal.getY(), crystal.getZ(), blockDestroy);
   }

   public static float getDamage(Entity entity, WorldAccess world, BlockPos position, boolean blockDestroy) {
      return getDamage(entity, world, 6.0F, (double)position.getX() + 0.5D, (double)(position.getY() + 1), (double)position.getZ() + 0.5D, blockDestroy);
   }

   public static float getDamage(Entity entity, WorldAccess world, float power, BlockPos position) {
      return getDamage(entity, world, power, (double)position.getX() + 0.5D, (double)(position.getY() + 1), (double)position.getZ() + 0.5D);
   }

   public static float getDamage(Entity entity, WorldAccess world, float power, double x, double y, double z) {
      return getDamage(entity, world, power, x, y, z, false);
   }

   public static float getDamage(Entity entity, WorldAccess world, float power, double x, double y, double z, boolean blockDestroy) {
      return getDamage(entity, world, power, x, y, z, blockDestroy, 0, false);
   }

   public static float getDamage(Entity entity, WorldAccess world, float power, double x, double y, double z, boolean blockDestroy, boolean async) {
      return getDamage(entity, world, power, x, y, z, blockDestroy, 0, async);
   }

   public static float getDamage(Entity entity, WorldAccess world, float power, double x, double y, double z, boolean blockDestroy, int extrapolation, boolean async) {
      if (entity == null) {
         return 0.0F;
      } else {
         Entity moveEntity = predictEntity(entity, extrapolation);
         float diameter = power * 2.0F;
         double distance = Math.sqrt(moveEntity.squaredDistanceTo(x, y, z)) / (double)diameter;
         if (!(distance > 1.0D) && mc.world.getDifficulty() != Difficulty.PEACEFUL) {
            double seenPercent = (double)getSeenPercent(world, moveEntity, x, y, z, blockDestroy);
            double seenVsDistance = (1.0D - distance) * seenPercent;
            float damage = (float)((int)((seenVsDistance * seenVsDistance + seenVsDistance) / 2.0D * 7.0D * (double)diameter + 1.0D));
            if (damage == 0.0F) {
               return 0.0F;
            } else {
               if (entity instanceof LivingEntity) {
                  LivingEntity livingEntity = (LivingEntity)entity;
                  if (entity instanceof PlayerEntity) {
                     PlayerEntity player = (PlayerEntity)entity;
                     if (player.isCreative()) {
                        return 0.0F;
                     }

                     damage = scaleByDifficulty(mc.world, damage);
                  }

                  SafeManager SAFE = Managers.getSafeManager();
                  double toughness = 0.0D;
                  if (!async) {
                     EntityAttributeInstance entityAttributeInstance = livingEntity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
                     toughness = entityAttributeInstance != null ? (double)((float)entityAttributeInstance.getValue()) : 0.0D;
                  } else if (SAFE.getArmorAttributes().containsKey(livingEntity)) {
                     toughness = (Double)SAFE.getArmorAttributes().get(livingEntity);
                  }

                  int armor = 0;
                  if (!async) {
                     armor = livingEntity.getArmor();
                  } else if (SAFE.getArmorValues().containsKey(livingEntity)) {
                     armor = (Integer)SAFE.getArmorValues().get(livingEntity);
                  }

                  DamageSource source = mc.world.getDamageSources().explosion((Explosion)null);
                  damage = DamageUtil.getDamageLeft(livingEntity, damage, source, (float)armor, (float)toughness);
                  StatusEffectInstance damageResistance = livingEntity.getStatusEffect(StatusEffects.RESISTANCE);
                  if (damageResistance != null) {
                     int damageResistanceAmplifier = (damageResistance.getAmplifier() + 1) * 5;
                     damage = Math.max(damage * (float)(25 - damageResistanceAmplifier) / 25.0F, 0.0F);
                     if (damage <= 0.0F) {
                        return 0.0F;
                     }
                  }

                  damage = protectionReduction(livingEntity, damage);
               }

               return Math.max(damage, 0.0F);
            }
         } else {
            return 0.0F;
         }
      }
   }

   private static float protectionReduction(LivingEntity player, float damage) {
      int damageProtection = 0;
      Iterator var3 = player.getAllArmorItems().iterator();

      while(var3.hasNext()) {
         ItemStack stack = (ItemStack)var3.next();
         Object2IntMap<RegistryEntry<Enchantment>> enchantments = new Object2IntOpenHashMap();
         getEnchantments(stack, enchantments);
         int protection = getEnchantmentLevel(enchantments, Enchantments.PROTECTION);
         if (protection > 0) {
            damageProtection += protection;
         }

         int blastProtection = getEnchantmentLevel(enchantments, Enchantments.BLAST_PROTECTION);
         if (blastProtection > 0) {
            damageProtection += 2 * blastProtection;
         }
      }

      return DamageUtil.getInflictedDamage(damage, (float)damageProtection);
   }

   public static void getEnchantments(ItemStack itemStack, Object2IntMap<RegistryEntry<Enchantment>> enchantments) {
      enchantments.clear();
      if (!itemStack.isEmpty()) {
         Set<Entry<RegistryEntry<Enchantment>>> itemEnchantments = itemStack.getItem() == Items.ENCHANTED_BOOK ? ((ItemEnchantmentsComponent)itemStack.get(DataComponentTypes.ENCHANTMENTS)).getEnchantmentEntries() : itemStack.getEnchantments().getEnchantmentEntries();         Iterator var3 = itemEnchantments.iterator();

         while(var3.hasNext()) {
            Entry<RegistryEntry<Enchantment>> entry = (Entry)var3.next();
            enchantments.put((RegistryEntry)entry.getKey(), entry.getIntValue());
         }
      }

   }

   public static int getEnchantmentLevel(Object2IntMap<RegistryEntry<Enchantment>> itemEnchantments, RegistryKey<Enchantment> enchantment) {
      ObjectIterator var2 = Object2IntMaps.fastIterable(itemEnchantments).iterator();

      Entry entry;
      do {
         if (!var2.hasNext()) {
            return 0;
         }

         entry = (Entry)var2.next();
      } while(!((RegistryEntry)entry.getKey()).matchesKey(enchantment));

      return entry.getIntValue();
   }

   public static float scaleByDifficulty(WorldAccess world, float damage) {
      float var10000;
      switch(world.getDifficulty()) {
      case PEACEFUL: 
         var10000 = 0.0F;
         break;
      case EASY:
         var10000 = Math.min(damage / 2.0F + 1.0F, damage);
         break;
      case HARD: 
         var10000 = damage * 3.0F / 2.0F;
         break;
      default: 
         var10000 = damage;
      }

      return var10000;
   }

   private static float getSeenPercent(WorldAccess world, Entity entity, double x, double y, double z, boolean blockDestroy) {
      Box bb = entity.getBoundingBox();
      Vec3d to = new Vec3d(x, y, z);
      double xD = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
      double yD = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
      double zD = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
      double xDD = (1.0D - Math.floor(1.0D / xD) * xD) / 2.0D;
      double zDD = (1.0D - Math.floor(1.0D / zD) * zD) / 2.0D;
      if (!(xD < 0.0D) && !(yD < 0.0D) && !(zD < 0.0D)) {
         int missed = 0;
         int blocks = 0;

         for(double xFactor = 0.0D; xFactor <= 1.0D; xFactor += xD) {
            for(double yFactor = 0.0D; yFactor <= 1.0D; yFactor += yD) {
               for(double zFactor = 0.0D; zFactor <= 1.0D; zFactor += zD) {
                  double fromX = MathHelper.lerp(xFactor, bb.minX, bb.maxX);
                  double fromY = MathHelper.lerp(yFactor, bb.minY, bb.maxY);
                  double fromZ = MathHelper.lerp(zFactor, bb.minZ, bb.maxZ);
                  Vec3d from = new Vec3d(fromX + xDD, fromY, fromZ + zDD);
                  if (RaycastUtil.raycast(world, new RaycastContext(from, to, ShapeType.COLLIDER, FluidHandling.NONE, entity), blockDestroy).getType() == Type.MISS) {
                     ++missed;
                  }

                  ++blocks;
               }
            }
         }

         return (float)missed / (float)blocks;
      } else {
         return 0.0F;
      }
   }

   public static boolean canBreakWeakness(boolean checkStack) {
      if (!mc.player.hasStatusEffect(StatusEffects.WEAKNESS)) {
         return true;
      } else {
         int strengthAmp = 0;
         StatusEffectInstance effect = mc.player.getStatusEffect(StatusEffects.STRENGTH);
         if (effect != null) {
            strengthAmp = effect.getAmplifier();
         }

         if (strengthAmp >= 1) {
            return true;
         } else {
            return checkStack && canBreakWeakness(mc.player.getMainHandStack());
         }
      }
   }
   public static boolean canBreakWeakness(ItemStack stack) {
      if (stack.getItem() instanceof SwordItem) {
         return true;
      } else {
         Item var2 = stack.getItem();
         if (var2 instanceof ToolItem) {
            ToolItem tool = (ToolItem)var2;
            return tool.getMaterial().getAttackDamage() > 4.0F;
         } else {
            return false;
         }
      }
   }

   public static int findAntiWeakness() {
      int slot = -1;

      for(int i = 8; i > -1; --i) {
         if (canBreakWeakness(mc.player.getInventory().getStack(i))) {
            slot = i;
            if (mc.player.getInventory().selectedSlot == i) {
               break;
            }
         }
      }

      return slot;
   }

   public static Entity predictEntity(Entity entity, int extrapolation) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)entity;
         FakePlayerEntity fake = new FakePlayerEntity(mc.world, player.getGameProfile(), player.getName().getString());
         fake.noClip = false;
         fake.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.6000000238418579D);
         fake.copyPositionAndRotation(player);
         fake.setOnGround(player.isOnGround());
         if (player == mc.player) {
            fake.setPosition(Managers.getPositionManager().getVec());
            return fake;
         } else {
            Vec3d interp = ((ILivingEntity)player).getServerVec();
            if (interp.x != 0.0D || interp.y != 0.0D || interp.z != 0.0D) {
               fake.setPosition(interp);
            }

            if (extrapolation > 0) {
               Vec3d[] predictions = ((IPlayerEntity)player).getPredictedPositions();
               Vec3d finalVec = predictions[extrapolation - 1];
               if (finalVec == null) {
                  return fake;
               } else {
                  fake.setPosition(finalVec);
                  return fake;
               }
            } else {
               return fake;
            }
         }
      } else {
         return entity;
      }
   }
}
