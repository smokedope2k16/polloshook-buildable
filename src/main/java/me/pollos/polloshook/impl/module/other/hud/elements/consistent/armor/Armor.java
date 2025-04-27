package me.pollos.polloshook.impl.module.other.hud.elements.consistent.armor;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.asm.ducks.render.IDrawContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ItemStack;

public class Armor extends HUDModule {
   private final Value<Boolean> percent = new Value(false, new String[]{"Percent", "%"});
   private final Value<Boolean> symbol;
   private final Value<Boolean> cFont;

   public Armor() {
      super(new String[]{"Armor", "armour"});
      this.symbol = (new Value(false, new String[]{"Draw%Symbol", "symbol", "%symbol"})).setParent(this.percent);
      this.cFont = (new Value(false, new String[]{"CustomFont", "cfont", "custom"})).setParent(() -> {
         return Managers.getTextManager().isCustom();
      });
      this.offerValues(new Value[]{this.percent, this.symbol, this.cFont});
   }

   public void draw(DrawContext context) {
      int width = context.getScaledWindowWidth() >> 1;
      int height = context.getScaledWindowHeight();
      int i2 = 15;
      int i1 = 3;

      for(int i3 = 3; i3 >= 0; i3 = i1) {
         ItemStack stack = (ItemStack)mc.player.getInventory().armor.get(i1);
         if (!(stack.getItem() instanceof AirBlockItem)) {
            int y = height - this.getArmorY();
            int x = width + i2;
            i2 += 18;
            this.renderArmorPiece(stack, context, x, y);
         }

         --i1;
      }

   }

   private void renderArmorPiece(ItemStack stack, DrawContext context, int x, int y) {
      MatrixStack matrix = context.getMatrices();
      int color = stack.getItem().getItemBarColor(stack);
      RenderSystem.disableDepthTest();
      context.drawItem(stack, x, y);
      if ((Boolean)this.cFont.getValue() && this.cFont.getParent().isVisible()) {
         ((IDrawContext)context).drawItemInSlotCFont(mc.textRenderer, stack, x, y, (String)null);
      } else {
         context.drawItemInSlot(mc.textRenderer, stack, x, y);
      }

      if (stack.isDamageable()) {
         int dmg = (int)ItemUtil.getDamageInPercent(stack);
         matrix.push();
         matrix.scale(0.625F, 0.625F, 0.625F);
         RenderSystem.disableDepthTest();
         if ((Boolean)this.percent.getValue()) {
            if ((Boolean)this.cFont.getValue() && this.cFont.getParent().isVisible()) {
               this.drawText(context, dmg + ((Boolean)this.symbol.getValue() ? "%" : ""), (int)(0.0F + (float)(x + this.bandhu(dmg)) * 1.6F), (int)((float)y * 1.6F - 8.0F), color, true);
            } else {
               context.drawText(mc.textRenderer, dmg + ((Boolean)this.symbol.getValue() ? "%" : ""), (int)(0.0F + (float)(x + this.bandhu(dmg)) * 1.6F), (int)((float)y * 1.6F - 8.0F), color, true);
            }
         }

         matrix.scale(1.0F, 1.0F, 1.0F);
         matrix.pop();
      }

      RenderSystem.enableDepthTest();
   }

   private int bandhu(int percent) {
      if (percent == 100) {
         return 1;
      } else {
         return percent < 10 ? 5 : 3;
      }
   }

   public int getArmorY() {
      int y;
      if (mc.player.getAir() < 300 && !mc.player.isCreative()) {
         y = 65;
      } else if (mc.player.isCreative()) {
         y = mc.player.isRiding() ? 45 : 38;
      } else {
         y = 55;
      }

      return y;
   }
}
