package me.pollos.polloshook.impl.module.misc.antiinteract;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AntiInteract extends ToggleableModule {
   protected final Value<Boolean> food = new Value(true, new String[]{"OnlyFood", "food"});
   protected final Value<Boolean> packets = new Value(false, new String[]{"Packets", "packet"});
   protected final Value<Boolean> shulker = new Value(false, new String[]{"Shulkers", "shulker"});
   private final List<Item> itemBlackList;

   public AntiInteract() {
      super(new String[]{"AntiInteract", "nointeract"}, Category.MISC);
      this.itemBlackList = Arrays.asList(Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.CHORUS_FRUIT, Items.FISHING_ROD, Items.ENDER_PEARL, Items.BOW);
      this.offerValues(new Value[]{this.food, this.packets, this.shulker});
      this.offerListeners(new Listener[]{new ListenerInteract(this), new ListenerInteractBlock(this)});
   }

   protected boolean isValid(BlockPos pos, Hand hand) {
      if (!(Boolean)this.food.getValue()) {
         return this.isValid(BlockUtil.getBlock(pos));
      } else {
         return this.isValidFood(hand) || this.isInteractable(hand);
      }
   }

   private boolean isValidFood(Hand hand) {
      ItemStack item = mc.player.getInventory().getStack(Managers.getInventoryManager().getSlot());
      if (hand == Hand.OFF_HAND) {
         item = mc.player.getOffHandStack();
      }

      return ItemUtil.isFood(item);
   }

   private boolean isInteractable(Hand hand) {
      Item item = mc.player.getInventory().getStack(Managers.getInventoryManager().getSlot()).getItem();
      if (hand == Hand.OFF_HAND) {
         item = mc.player.getOffHandStack().getItem();
      }

      return this.itemBlackList.contains(item);
   }

   private boolean isValid(Block block) {
      if (block == null) {
         return false;
      } else {
         List<Block> blackList = BlockUtil.INTERACTABLES;
         List<Block> shulkerList = BlockUtil.SHULKERS;
         return blackList.contains(block) || shulkerList.contains(block) && (Boolean)this.shulker.getValue();
      }
   }
}