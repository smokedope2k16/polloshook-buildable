package me.pollos.polloshook.api.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public final class EnchantUtil {

   private EnchantUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static int getLevel(RegistryKey<Enchantment> enchantment, ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return 0;
      } else {
         Object2IntMap<RegistryEntry<Enchantment>> itemEnchantments = new Object2IntArrayMap<>();
         getEnchantments(itemStack, itemEnchantments);
         return getEnchantmentLevel(itemEnchantments, enchantment);
      }
   }

   public static int getEnchantmentLevel(Object2IntMap<RegistryEntry<Enchantment>> itemEnchantments, RegistryKey<Enchantment> enchantment) {
      ObjectIterator<Entry<RegistryEntry<Enchantment>>> iterator = Object2IntMaps.fastIterable(itemEnchantments).iterator();
      while (iterator.hasNext()) {
         Entry<RegistryEntry<Enchantment>> entry = iterator.next();
         if (entry.getKey().matchesKey(enchantment)) {
            return entry.getIntValue();
         }
      }
      return 0;
   }

   public static void getEnchantments(ItemStack itemStack, Object2IntMap<RegistryEntry<Enchantment>> enchantments) {
      enchantments.clear();
      if (!itemStack.isEmpty()) {
         Set<Entry<RegistryEntry<Enchantment>>> itemEnchantments =
             itemStack.getItem() == Items.ENCHANTED_BOOK
                 ? ((ItemEnchantmentsComponent) itemStack.get(DataComponentTypes.STORED_ENCHANTMENTS)).getEnchantmentEntries()
                 : itemStack.getEnchantments().getEnchantmentEntries();
         for (Entry<RegistryEntry<Enchantment>> entry : itemEnchantments) {
            enchantments.put(entry.getKey(), entry.getIntValue());
         }
      }
   }
}