package me.pollos.polloshook.impl.module.render.shulkerpreview;

import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.util.text.TextUtil;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ListenerRenderItem extends SafeModuleListener<ShulkerPreview, ShulkerPreview.RenderHandledScreenEvent.DrawItem> {
   public ListenerRenderItem(ShulkerPreview module) {
      super(module, ShulkerPreview.RenderHandledScreenEvent.DrawItem.class);
   }

   public void safeCall(ShulkerPreview.RenderHandledScreenEvent.DrawItem event) {
      DrawContext context = event.getContext();
      if (((ShulkerPreview)this.module).searching && (Boolean)((ShulkerPreview)this.module).search.getValue() && !TextUtil.isNullOrEmpty(((ShulkerPreview)this.module).searchTarget)) {
         Iterator var3 = event.getSlots().iterator();

         while(true) {
            Slot slot;
            ItemStack stack;
            BlockItem blockItem;
            do {
               Item var7;
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  slot = (Slot)var3.next();
                  stack = slot.getStack();
                  var7 = stack.getItem();
               } while(!(var7 instanceof BlockItem));

               blockItem = (BlockItem)var7;
            } while(!(blockItem.getBlock() instanceof ShulkerBoxBlock));

            ContainerComponent component = (ContainerComponent)stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
            List<ItemStack> stackList = component.stream().toList();
            Iterator var9 = stackList.iterator();

            while(var9.hasNext()) {
               ItemStack stackInShulker = (ItemStack)var9.next();
               if (stackInShulker.getItem().getName().getString().toLowerCase().startsWith(((ShulkerPreview)this.module).searchTarget.toLowerCase())) {
                  int x = slot.x;
                  int y = slot.y;
                  context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 16, -2130706433, -2130706433, 0);
               }
            }
         }
      }
   }
}
