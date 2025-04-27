package me.pollos.polloshook.api.command.syntax.listvalue;

import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Argument;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class BlockListSyntax extends Argument {
   public BlockListSyntax() {
      super("[block.syntax]");
   }

   public String predict(String currentArg) {
      String[] parts = currentArg.split(" ");
      return parts.length == 4 ? this.getBlockFromString(parts[3]) : this.getBlockFromString(currentArg);
   }

   private String getBlockFromString(String s) {
      Iterator var2 = Registries.BLOCK.iterator();

      String resourse;
      do {
         if (!var2.hasNext()) {
            return s;
         }

         Block block = (Block)var2.next();
         resourse = Registries.BLOCK.getId(block).toString().replace("minecraft:", "");
      } while(resourse.equalsIgnoreCase("minecraft:air") || !resourse.toLowerCase().startsWith(s));

      return resourse;
   }
}
