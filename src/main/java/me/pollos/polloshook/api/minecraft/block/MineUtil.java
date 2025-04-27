package me.pollos.polloshook.api.minecraft.block;

import java.util.Objects;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class MineUtil implements Minecraftable {
   public static int findBestTool(BlockPos pos) {
      return findBestTool(pos, mc.world.getBlockState(pos));
   }

   public static int findBestTool(BlockPos pos, BlockState state) {
      int result = mc.player.getInventory().selectedSlot;
      if (state.getHardness(mc.world, pos) > 0.0F) {
         double speed = getSpeed(state, mc.player.getMainHandStack());

         for(int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.getStack(i);
            double stackSpeed = getSpeed(state, stack);
            if (stackSpeed > speed) {
               speed = stackSpeed;
               result = i;
            }
         }
      }

      return result;
   }

   public static double getSpeed(BlockState state, ItemStack stack) {
      double str = (double)stack.getMiningSpeedMultiplier(state);
      int effect = EnchantUtil.getLevel(Enchantments.FORTUNE, stack);
      return Math.max(str + (str > 1.0D ? (double)(effect * effect) + 1.0D : 0.0D), 0.0D);
   }

   public static float getDamage(BlockState state, ItemStack stack, boolean onGround) {
      float hardness = state.getHardness((BlockView)null, (BlockPos)null);
      return hardness == -1.0F ? 0.0F : getDigSpeed(stack, state, onGround) / hardness / (float)(state.isToolRequired() && !stack.isSuitableFor(state) ? 100 : 30);
   }

   public static float getDamage(ItemStack stack, BlockPos pos, boolean onGround) {
      BlockState state = mc.world.getBlockState(pos);
      float hardness = state.getHardness((BlockView)null, (BlockPos)null);
      return hardness == -1.0F ? 0.0F : getDigSpeed(stack, state, onGround) / hardness / (float)(state.isToolRequired() && !stack.isSuitableFor(state) ? 100 : 30);
   }

   private static float getDigSpeed(ItemStack stack, BlockState state, boolean onGround) {
      float digSpeed = 1.0F;
      if (!stack.isEmpty()) {
         digSpeed *= stack.getMiningSpeedMultiplier(state);
      }

      if (digSpeed > 1.0F) {
         int i = EnchantUtil.getLevel(Enchantments.FORTUNE, stack);
         if (i > 0 && !stack.isEmpty()) {
            digSpeed += (float)(i * i + 1);
         }
      }

      if (mc.player.getStatusEffect(StatusEffects.HASTE) != null) {
         digSpeed *= 1.0F + (float)(mc.player.getStatusEffect(StatusEffects.HASTE).getAmplifier() + 1) * 0.2F;
      }

      if (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE) != null) {
         float var10000;
         switch(((StatusEffectInstance)Objects.requireNonNull(mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE))).getAmplifier()) {
         case 0:
            var10000 = 0.3F;
            break;
         case 1:
            var10000 = 0.09F;
            break;
         case 2:
            var10000 = 0.0027F;
            break;
         case 3:
            var10000 = 8.1E-4F;
            break;
         default:
            var10000 = 0.3F;
         }

         float miningFatigue = var10000;
         digSpeed *= miningFatigue;
      }

      if (mc.player.isSubmergedIn(FluidTags.WATER)) {
         digSpeed *= (float)mc.player.getAttributeValue(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED);
      }

      if (!onGround) {
         digSpeed /= 5.0F;
      }

      return digSpeed < 0.0F ? 0.0F : digSpeed;
   }

   public static boolean canBreak(BlockPos pos) {
      return canBreak(mc.world.getBlockState(pos), pos);
   }

   public static boolean canBreak(BlockState state, BlockPos pos) {
      return state.getHardness(mc.world, pos) != -1.0F && state.getBlock() != Blocks.BEDROCK && !state.isLiquid();
   }
}