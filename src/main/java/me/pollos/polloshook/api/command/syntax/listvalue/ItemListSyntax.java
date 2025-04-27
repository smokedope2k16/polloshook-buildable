package me.pollos.polloshook.api.command.syntax.listvalue;

import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Argument;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public class ItemListSyntax extends Argument {
   public ItemListSyntax() {
      super("[item]");
   }

   public String predict(String currentArg) {
      String[] parts = currentArg.split(" ");
      return parts.length == 4 ? this.getItemFromString(parts[3]) : this.getItemFromString(currentArg);
   }

   private String getItemFromString(String s) {
      Iterator var2 = Registries.ITEM.iterator();

      String resourse;
      do {
         if (!var2.hasNext()) {
            return s;
         }

         Item item = (Item)var2.next();
         resourse = Registries.ITEM.getId(item).toString().replace("minecraft:", "");
      } while(resourse.equalsIgnoreCase("minecraft:air") || !resourse.toLowerCase().startsWith(s));

      return resourse;
   }
}
