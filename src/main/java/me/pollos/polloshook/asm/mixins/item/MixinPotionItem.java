package me.pollos.polloshook.asm.mixins.item;

import net.minecraft.item.Item;
import net.minecraft.item.PotionItem;
import net.minecraft.item.Item.Settings;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({PotionItem.class})
public abstract class MixinPotionItem extends Item {
   public MixinPotionItem(Settings settings) {
      super(settings);
   }
}
