package me.pollos.polloshook.api.value.value.list.toggleable.block;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.impl.ListValue;
import me.pollos.polloshook.api.value.value.list.mode.ListEnum;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BlockListValue extends ListValue<ToggleableBlock> {
   public BlockListValue() {
      super(ListEnum.BLOCKS_LIST_ALIAS);
   }

   public boolean isValid(Block block, EnumValue<ListEnum> selection) {
      if (block == null) {
         return false;
      } else if (selection.getValue() == ListEnum.ANY) {
         return true;
      } else {
         return selection.getValue() == ListEnum.WHITELIST ? ((List)this.getValue()).stream().anyMatch((tb) -> {
            return ((ToggleableBlock) tb).getBlock().equals(block) && ((ToggleableBlock) tb).isEnabled();
         }) : ((List)this.getValue()).stream().noneMatch((tb) -> {
            return ((ToggleableBlock) tb).getBlock().equals(block) && ((ToggleableBlock) tb).isEnabled();
         });
      }
   }

   public String returnValue(String[] args) {
      ArrayList<ToggleableBlock> blocks = (ArrayList)this.getValue();
      String blockStr = args[2].toUpperCase();
      byte var4 = -1;
      switch(blockStr.hashCode()) {
      case 2336926:
         if (blockStr.equals("LIST")) {
            var4 = 0;
         }
         break;
      case 64208429:
         if (blockStr.equals("CLEAR")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         if (blocks.isEmpty()) {
            return "There is no blocks added";
         }

         StringJoiner joiner = new StringJoiner(", ");
         blocks.forEach((blockx) -> {
            String var10001 = blockx.getBlockName();
            joiner.add(var10001 + " [" + ((Block)Registries.BLOCK.get(blockx.getBlockRegistryEntry())).toString().replace("Block{", "").replace("minecraft:", "").replace("}", "") + "]");
         });
         return "Blocks [%s]: %s".formatted(new Object[]{blocks.size(), joiner});
      case 1:
         if (blocks.isEmpty()) {
            return "There is no blocks added";
         }

         blocks.clear();
         return "Cleared item list";
      default:
         try {
            blockStr = args[3];
            Block block = Block.getBlockFromItem(((Block)Registries.BLOCK.get(Identifier.of(blockStr))).asItem());
            if (block != null && !(block instanceof AirBlock)) {
               String blockLabel = null;
               String resourceLocation = ((Block)Registries.BLOCK.get(Identifier.of(blockStr))).asItem().toString();
               if (resourceLocation != null) {
                  blockLabel = block.getName().getString();
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
                  if (blocks.stream().anyMatch((tb) -> {
                     return tb.getBlock().equals(block);
                  })) {
                     return "%s is already in the block list".formatted(new Object[]{blockLabel});
                  }

                  blocks.add(new ToggleableBlock(block, true));
                  return "Added %s%s%s to the block list".formatted(new Object[]{Formatting.GREEN, blockLabel, Formatting.GRAY});
               case 1:
               case 2:
               case 3:
                  if (blocks.stream().noneMatch((tb) -> {
                     return tb.getBlock().equals(block);
                  })) {
                     return "%s is not in the block list".formatted(new Object[]{blockLabel});
                  }

                  blocks.removeIf((tb) -> {
                     return tb.getBlock().equals(block);
                  });
                  return "Removed %s%s%s from the block list".formatted(new Object[]{Formatting.RED, blockLabel, Formatting.GRAY});
               default:
                  return "Invalid Arguments";
               }
            } else {
               return "Block does not exist";
            }
         } catch (Exception var9) {
            return var9.getMessage();
         }
      }
   }
}