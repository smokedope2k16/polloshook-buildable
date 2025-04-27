package me.pollos.polloshook.impl.module.other.hud.elements.consistent.hotbarkeys;

import java.awt.Color;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.gui.DrawContext;

public class HotbarKeys extends HUDModule {
   private final NumberValue<Integer> opacity = (new NumberValue(50, 0, 100, new String[]{"Opacity", "opac"})).withTag("%");

   public HotbarKeys() {
      super(new String[]{"HotbarKeys", "hotbarkey"});
      this.offerValues(new Value[]{this.opacity});
   }

   public void draw(DrawContext context) {
      if (!PlayerUtil.isSpectator()) {
         float opac = (float)(Integer)this.opacity.getValue() / 100.0F;
         int alpha = (int)(opac * 255.0F);
         if (alpha != 0) {
            int x = context.getScaledWindowWidth() / 2 - 87;
            int y = context.getScaledWindowHeight() - 18;
            int length = mc.options.hotbarKeys.length;

            for(int i = 0; i < length; ++i) {
               this.drawText(context, mc.options.hotbarKeys[i].getBoundKeyLocalizedText().getString(), x + i * 20, y, ColorUtil.changeAlpha(new Color(-1), alpha).getRGB(), true);
            }

         }
      }
   }
}
