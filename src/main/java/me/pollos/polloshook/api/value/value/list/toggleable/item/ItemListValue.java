package me.pollos.polloshook.api.value.value.list.toggleable.item;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.impl.ListValue;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ItemListValue extends ListValue<ToggleableItem> {
   public ItemListValue() {
      super(ListEnum.ITEM_LIST_ALIAS);
   }

   public boolean isValid(Item item, EnumValue<ListEnum> selection) {
      return this.isValid(item, (ListEnum)selection.getValue());
   }

   public boolean isValid(Item item, ListEnum selection) {
      if (item == null) {
         return false;
      } else if (selection == ListEnum.ANY) {
         return true;
      } else {
         return selection == ListEnum.WHITELIST ? ((List)this.getValue()).stream().anyMatch((ti) -> {
            return ((ToggleableItem) ti).getItem().equals(item) && ((ToggleableItem) ti).isEnabled();
         }) : ((List)this.getValue()).stream().noneMatch((ti) -> {
            return ((ToggleableItem) ti).getItem().equals(item) && ((ToggleableItem) ti).isEnabled();
         });
      }
   }

   public String returnValue(String[] args) {
      ArrayList<ToggleableItem> items = (ArrayList)this.getValue();
      String itemStr = args[2].toUpperCase();
      byte var4 = -1;
      switch(itemStr.hashCode()) {
      case 2336926:
         if (itemStr.equals("LIST")) {
            var4 = 0;
         }
         break;
      case 64208429:
         if (itemStr.equals("CLEAR")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         if (items.isEmpty()) {
            return "There is no items added";
         }

         StringJoiner joiner = new StringJoiner(", ");
         items.forEach((itemx) -> {
            String var10001 = itemx.getItemName();
            joiner.add("\n" + var10001 + " [" + ((Item)Registries.ITEM.get(itemx.getItemRegistryEntry())).toString().replace("Item{", "").replace("minecraft:", "").replace("}", "") + "]");
         });
         return String.format("Items [%s]: %s", items.size(), joiner);
      case 1:
         if (items.isEmpty()) {
            return "There is no items added";
         }

         items.clear();
         return "Cleared item list";
      default:
         try {
            itemStr = args[3];
            Item item = ((Item)Registries.ITEM.get(Identifier.of(itemStr))).asItem();
            if (item != null && !(item instanceof AirBlockItem)) {
               String itemLabel = null;
               String resourceLocation = ((Item)Registries.ITEM.get(Identifier.of(itemStr))).asItem().toString();
               if (resourceLocation != null) {
                  itemLabel = item.getName().getString();
               }

               String var7 = args[2].toUpperCase();
               byte var8 = -1;
               switch(var7.hashCode()) {
               case -1881281404:
                  if (var7.equals("REMOVE")) {
                     var8 = 3;
                  }
                  break;
               case 64641:
                  if (var7.equals("ADD")) {
                     var8 = 0;
                  }
                  break;
               case 67563:
                  if (var7.equals("DEL")) {
                     var8 = 1;
                  }
                  break;
               case 2012838315:
                  if (var7.equals("DELETE")) {
                     var8 = 2;
                  }
               }

               switch(var8) {
               case 0:
                  if (items.stream().anyMatch((ti) -> {
                     return ti.getItem().equals(item);
                  })) {
                     return "%s is already in the block list".formatted(new Object[]{itemLabel});
                  }

                  items.add(new ToggleableItem(item, true));
                  return "Added %s%s%s to the block list".formatted(new Object[]{Formatting.GREEN, itemLabel, Formatting.GRAY});
               case 1:
               case 2:
               case 3:
                  if (items.stream().noneMatch((ti) -> {
                     return ti.getItem().equals(item);
                  })) {
                     return "%s is not in the block list".formatted(new Object[]{itemLabel});
                  }

                  items.removeIf((ti) -> {
                     return ti.getItem().equals(item);
                  });
                  return "Removed %s%s%s from the block list".formatted(new Object[]{Formatting.RED, itemLabel, Formatting.GRAY});
               default:
                  return "Invalid Arguments";
               }
            } else {
               return "Item does not exist";
            }
         } catch (Exception var9) {
            return var9.getMessage();
         }
      }
   }
}