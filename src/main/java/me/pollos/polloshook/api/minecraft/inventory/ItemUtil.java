package me.pollos.polloshook.api.minecraft.inventory;

import java.util.List;
import java.util.function.Predicate;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.thread.NonNullList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUtil implements Minecraftable {
   public static int getHotbarItemSlot(Item item) {
      int slot = -1;

      for(int i = 0; i < 9; ++i) {
         if (InventoryUtil.getStack(i).getItem().equals(item)) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   public static int getHotbarSlotByClass(Class<?> clss) {
      int itemSlot = -1;

      for(int i = 8; i > -1; --i) {
         if (InventoryUtil.getStack(i).getItem().getClass() == clss) {
            itemSlot = i;
            break;
         }
      }

      return itemSlot;
   }

   public static int getSlotByClass(Item item) {
      int itemSlot = -1;

      for(int i = 45; i > 0; --i) {
         if (InventoryUtil.getStack(i).getItem() == item) {
            itemSlot = i;
            break;
         }
      }

      return itemSlot;
   }

   public static boolean isHolding(Item item) {
      return isHolding(mc.player, item);
   }

   public static boolean isHolding(PlayerEntity entity, Item item) {
      if (entity == null) {
         return false;
      } else {
         ItemStack mainHand = entity.getMainHandStack();
         ItemStack offHand = entity.getOffHandStack();
         return areSame(mainHand, item) || areSame(offHand, item);
      }
   }

   public static ItemStack getHeldItemStack(Item item) {
      return getHeldItemStack(mc.player, item);
   }

   public static ItemStack getHeldItemStack(PlayerEntity entity, Item item) {
      if (entity != null) {
         ItemStack mainHand = entity.getMainHandStack();
         ItemStack offHand = entity.getOffHandStack();
         if (areSame(mainHand, item)) {
            return mainHand;
         } else {
            return areSame(offHand, item) ? offHand : null;
         }
      } else {
         return null;
      }
   }

   public static int findHotbarItem(Item item) {
      return findInHotbar((s) -> {
         return areSame(s, item);
      });
   }

   public static int findInHotbar(Predicate<ItemStack> condition) {
      return findInHotbar(condition, true);
   }

   public static int findInHotbar(Predicate<ItemStack> condition, boolean offhand) {
      if (offhand && condition.test(mc.player.getOffHandStack())) {
         return -2;
      } else {
         int result = -1;

         for(int i = 8; i > -1; --i) {
            if (condition.test(InventoryUtil.getStack(i))) {
               result = i;
               if (mc.player.getInventory().selectedSlot == i) {
                  break;
               }
            }
         }

         return result;
      }
   }

   public static int findInInventory(Item item) {
      return findInHotbar((s) -> {
         return areSame(s, item);
      });
   }

   public static int findInInventory(Predicate<ItemStack> condition) {
      return findInInventory(condition, true);
   }

   public static int findInInventory(Predicate<ItemStack> condition, boolean offhand) {
      if (offhand && condition.test(mc.player.getOffHandStack())) {
         return -2;
      } else {
         int result = -1;

         for(int i = 45; i > 0; --i) {
            if (condition.test(InventoryUtil.getStack(i))) {
               result = i;
               if (mc.player.getInventory().selectedSlot == i) {
                  break;
               }
            }
         }

         return result;
      }
   }

   public static boolean areSame(Item item1, Item item2) {
      return Item.getRawId(item1) == Item.getRawId(item2);
   }

   public static boolean areSame(ItemStack stack, Item item) {
      return stack != null && areSame(stack.getItem(), item);
   }

   public static double getDamageInPercent(ItemStack stack) {
      double percent = (double)stack.getDamage() / (double)stack.getMaxDamage();
      if (percent == 0.0D) {
         return 100.0D;
      } else {
         return percent == 1.0D ? 0.0D : 100.0D - percent * 100.0D;
      }
   }

   public static boolean isFood(ItemStack stack) {
      FoodComponent foodComponent = (FoodComponent)stack.get(DataComponentTypes.FOOD);
      return foodComponent != null;
   }

   public static boolean isFood(Item stack) {
      FoodComponent foodComponent = (FoodComponent)stack.getDefaultStack().get(DataComponentTypes.FOOD);
      return foodComponent != null;
   }

   public static boolean equalStack(ItemStack first, ItemStack second) {
      return first.getItem() == second.getItem() && ItemStack.areItemsEqual(first, second);
   }

   public static void loadAllItems(ItemStack itemStack, NonNullList<ItemStack> list) {
      ContainerComponent containerComponent = (ContainerComponent)itemStack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
      if (containerComponent != null) {
         List<ItemStack> stacks = containerComponent.stream().toList();
         if (!stacks.isEmpty()) {
            for(int i = 0; i < stacks.size(); ++i) {
               ItemStack stackInSlot = (ItemStack)stacks.get(i);
               if (i < list.size()) {
                  list.set(i, stackInSlot);
               }
            }

         }
      }
   }
}