package me.pollos.polloshook.impl.module.render.shulkerpreview;

import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.thread.NonNullList;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.asm.ducks.render.IDrawContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

public class ShulkerPreview extends ToggleableModule {
   protected final Value<Boolean> onlyIfShift = new Value(false, new String[]{"OnlyIfShift", "shift"});
   protected final Value<Boolean> merge = new Value(false, new String[]{"Merge", "combind"});
   protected final Value<Boolean> search = new Value(false, new String[]{"Search", "s", "searchfeature"});
   protected boolean searching;
   protected boolean paused = false;
   protected String searchTarget;

   public ShulkerPreview() {
      super(new String[]{"ShulkerPreview", "peek"}, Category.RENDER);
      this.offerValues(new Value[]{this.onlyIfShift, this.merge, this.search});
      this.offerListeners(new Listener[]{new ListenerRenderItem(this), new ListenerRender(this), new ListenerUpdate(this), new ListenerKey(this)});
   }

   public boolean isPressingShift() {
      return !(Boolean)this.onlyIfShift.getValue() ? true : KeyboardUtil.isShift();
   }

   public void render(DrawContext context, ItemStack itemStack, NonNullList<ItemStack> list, int x, int y) {
      int width = (int)Math.max(144.0F, Managers.getTextManager().getWidth(itemStack.getName().getString()) + 3.0F);
      int x1 = x + 12;
      int y1 = y - 12;
      int height = 57;
      context.getMatrices().push();
      TooltipBackgroundRenderer.render(context, x1, y1, width, height, 400);
      context.getMatrices().translate(0.0F, 0.0F, 400.0F);
      Managers.getTextManager().drawString(context, itemStack.getName().getString(), (double)(x + 12), (double)(y - 12), 16777215);
      NonNullList<ItemStack> combinedStacks = NonNullList.create();
      if ((Boolean)this.merge.getValue()) {
         Iterator var11 = list.iterator();

         label50:
         while(true) {
            ItemStack stack;
            do {
               if (!var11.hasNext()) {
                  break label50;
               }

               stack = (ItemStack)var11.next();
            } while(stack.isEmpty());

            boolean combined = false;
            Iterator var14 = combinedStacks.iterator();

            while(var14.hasNext()) {
               ItemStack combinedStack = (ItemStack)var14.next();
               if (ItemStack.areItemsEqual(stack, combinedStack)) {
                  combinedStack.increment(stack.getCount());
                  combined = true;
                  break;
               }
            }

            if (!combined) {
               combinedStacks.add(stack.copy());
            }
         }
      }

      List<ItemStack> selectedList = (Boolean)this.merge.getValue() ? combinedStacks : list;
      int listSize = selectedList.size();

      for(int i = 0; i < listSize; ++i) {
         int iX = x + i % 9 * 16 + 11;
         int iY = y + i / 9 * 16 - 11 + 8;
         ItemStack stack = (ItemStack)selectedList.get(i);
         context.drawItem(stack, iX, iY);
         if (Managers.getTextManager().isCustom()) {
            ((IDrawContext)context).drawItemInSlotCFont(mc.textRenderer, stack, iX, iY, (String)null);
         } else {
            context.drawItemInSlot(mc.textRenderer, stack, iX, iY);
         }
      }

   }

   public static class TypeStringEvent extends Event {
      private final String string;
      private final int keyCode;

      
      public String getString() {
         return this.string;
      }

      
      public int getKeyCode() {
         return this.keyCode;
      }

      
      private TypeStringEvent(String string, int keyCode) {
         this.string = string;
         this.keyCode = keyCode;
      }

      
      public static ShulkerPreview.TypeStringEvent of(String string, int keyCode) {
         return new ShulkerPreview.TypeStringEvent(string, keyCode);
      }
   }

   public static class RenderHandledScreenEvent extends Event {
      private final DrawContext context;

      
      public DrawContext getContext() {
         return this.context;
      }

      
      private RenderHandledScreenEvent(DrawContext context) {
         this.context = context;
      }

      
      public static ShulkerPreview.RenderHandledScreenEvent of(DrawContext context) {
         return new ShulkerPreview.RenderHandledScreenEvent(context);
      }

      public static class DrawItem extends ShulkerPreview.RenderHandledScreenEvent {
         private final DefaultedList<Slot> slots;

         public DrawItem(DrawContext context, DefaultedList<Slot> slots) {
            super(context);
            this.slots = slots;
         }

         
         public DefaultedList<Slot> getSlots() {
            return this.slots;
         }
      }
   }
}
