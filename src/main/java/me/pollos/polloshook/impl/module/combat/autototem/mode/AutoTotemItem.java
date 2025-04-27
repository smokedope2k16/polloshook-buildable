package me.pollos.polloshook.impl.module.combat.autototem.mode;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum AutoTotemItem {
   TOTEMS(Items.TOTEM_OF_UNDYING),
   CRYSTALS(Items.END_CRYSTAL),
   GAPPLES(Items.ENCHANTED_GOLDEN_APPLE);

   private final Item item;

   private AutoTotemItem(Item item) {
      this.item = item;
   }

   public Item getItem() {
      return this.item;
   }

   // $FF: synthetic method
   private static AutoTotemItem[] $values() {
      return new AutoTotemItem[]{TOTEMS, CRYSTALS, GAPPLES};
   }
}