package me.pollos.polloshook.impl.module.other.hud.elements.draggable.armorwarning;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.gui.editor.core.PollosHUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;

public class ArmorWarning extends DraggableHUDModule {
   private final Value<Boolean> reverse = new Value(false, new String[]{"ReverseOrder", "reverse"});
   private final Value<Boolean> displayLabel = new Value(false, new String[]{"DisplayLabel", "label"});
   private final Value<Boolean> readable;
   private final Value<Boolean> displayDifference;
   private final NumberValue<Integer> percent;
   private final Value<Boolean> customColor;

   public ArmorWarning() {
      super(new String[]{"ArmorWarning", "armorwarn"});
      this.readable = (new Value(true, new String[]{"Readable", "read"})).setParent(this.displayLabel, true);
      this.displayDifference = new Value(false, new String[]{"DisplayDifference", "difference"});
      this.percent = (new NumberValue(20, 1, 80, new String[]{"Percent", "%", "percentage"})).withTag("%");
      this.customColor = new Value(false, new String[]{"CustomColor", "customc"});
      this.offerValues(new Value[]{this.reverse, this.displayLabel, this.readable, this.displayDifference, this.percent, this.customColor});
   }

   public void draw(DrawContext context) {
      if (!PlayerUtil.isSpectator()) {
         List<ItemStack> items = new ArrayList();
         int offset = 0;
         if (mc.currentScreen instanceof PollosHUD) {
            Item[] pieces = new Item[]{Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS};
            Item[] var5 = pieces;
            int var6 = pieces.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Item item = var5[var7];
               items.add(new ItemStack(item));
            }
         } else {
            Iterator var9 = mc.player.getArmorItems().iterator();

            while(var9.hasNext()) {
               ItemStack stack = (ItemStack)var9.next();
               if (stack != null && !stack.isEmpty()) {
                  double damagePercent = ItemUtil.getDamageInPercent(stack);
                  if (!Double.isInfinite(damagePercent) && !Double.isNaN(damagePercent) && damagePercent < (double)(Integer)this.percent.getValue() && !items.contains(stack)) {
                     items.add(stack);
                  }
               }
            }
         }

         if ((Boolean)this.reverse.getValue()) {
            Collections.reverse(items);
         }

         List<Integer> widths = new ArrayList();

         for(Iterator var12 = items.iterator(); var12.hasNext(); offset += 10) {
            ItemStack stack = (ItemStack)var12.next();
            if (items.size() > 4) {
               break;
            }

            String text = this.getString(stack);
            int setX = (int)((float)context.getScaledWindowWidth() / 2.0F - this.getWidth(text) / 2.0F + 2.0F);
            if ((Boolean)this.getSetPos().getValue()) {
               this.setTextX((float)setX);
            }

            widths.add((int)this.getWidth(text));
            this.drawText(context, text, (int)this.getTextX(), (int)(this.getTextY() + (float)offset), Color.RED.getRGB(), !(Boolean)this.customColor.getValue());
         }

         this.setTextWidth(widths.isEmpty() ? 10.0F : (float)(Integer)Collections.max(widths));
         this.setTextHeight((float)offset);
      }
   }

   private String getString(ItemStack stack) {
      double damagePercent = ItemUtil.getDamageInPercent(stack);
      double amount = MathUtil.round(damagePercent, 2);
      int diff = (int)((double)(Integer)this.percent.getValue() - amount);
      String differenceText = (Boolean)this.displayDifference.getValue() && diff > 0 ? " (" + diff + "% below max percent)" : "";
      String name = this.getNameFromStack(stack);
      String thing = this.makeThatShiEnglish(stack);
      return "Your " + name + " " + thing + " at " + (int)amount + "%" + differenceText;
   }

   private String getNameFromStack(ItemStack stack) {
      if ((Boolean)this.displayLabel.getValue()) {
         return "\"" + stack.getName().getString() + "\"";
      } else {
         String var10000 = this.materialLabel(stack);
         return var10000 + this.equipmentLabel(stack);
      }
   }

   private String makeThatShiEnglish(ItemStack stack) {
      String type = this.equipmentLabel(stack);
      if (type.equalsIgnoreCase("leggings")) {
         return "are";
      } else {
         return type.equalsIgnoreCase("boots") ? "are" : "is";
      }
   }

   private String materialLabel(ItemStack stack) {
      if ((Boolean)this.readable.getValue()) {
         return "";
      } else {
         String s = "Nigga u naked";
         Item var4 = stack.getItem();
         if (var4 instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem)var4;
            RegistryEntry<ArmorMaterial> material = armor.getMaterial();
            if (ArmorMaterials.LEATHER.equals(material)) {
               s = "Leather";
            } else if (ArmorMaterials.IRON.equals(material)) {
               s = "Iron";
            } else if (ArmorMaterials.DIAMOND.equals(material)) {
               s = "Diamond";
            } else if (ArmorMaterials.GOLD.equals(material)) {
               s = "Gold";
            } else if (ArmorMaterials.CHAIN.equals(material)) {
               s = "Chainmail";
            } else if (ArmorMaterials.TURTLE.equals(material)) {
               s = "Turtle";
            } else if (ArmorMaterials.NETHERITE.equals(material)) {
               s = "Netherite";
            } else {
               s = "Unknown";
            }
         }

         return s + " ";
      }
   }

   private String equipmentLabel(ItemStack stack) {
      String s = "Naked ass nigga";
      Item var4 = stack.getItem();
      if (var4 instanceof ArmorItem) {
         ArmorItem armor = (ArmorItem)var4;
         switch(armor.getSlotType()) {
         case HEAD:
            s = "Helmet";
            break;
         case CHEST:
            s = "Chestplate";
            break;
         case LEGS:
            s = "Leggings";
            break;
         case FEET:
            s = "Boots";
         }
      }

      return (Boolean)this.readable.getValue() ? s.toLowerCase() : s;
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX(21.0F);
      this.setTextY(42.0F);
      this.setTextWidth(200.0F);
      this.setTextHeight(350.0F);
   }
}