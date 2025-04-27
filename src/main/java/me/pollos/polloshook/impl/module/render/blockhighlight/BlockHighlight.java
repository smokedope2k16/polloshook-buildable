package me.pollos.polloshook.impl.module.render.blockhighlight;

import java.awt.Color;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class BlockHighlight extends ToggleableModule {
   protected final Value<Boolean> crosshairInfo = new Value(false, new String[]{"CrosshairInfo", "crosshairinfo"});
   protected final Value<Boolean> noBlockOutline = new Value(false, new String[]{"NoBlockOutline", "removeblockoutline"});
   protected final NumberValue<Float> lineWidth;
   protected final Value<Boolean> fill;
   protected final ColorValue fillColor;
   protected final ColorValue outlineColor;

   public BlockHighlight() {
      super(new String[]{"BlockHighlight", "highlight"}, Category.RENDER);
      this.lineWidth = (new NumberValue(1.0F, 1.0F, 4.0F, 0.1F, new String[]{"LineWidth", "width"})).setParent(this.noBlockOutline, true);
      this.fill = (new Value(false, new String[]{"Fill", "f"})).setParent(this.noBlockOutline, true);
      this.fillColor = (new ColorValue(new Color(255, 255, 255, 45), false, new String[]{"FillColor", "fcolor"})).setParent(this.fill);
      this.outlineColor = (new ColorValue(new Color(-1), false, new String[]{"OutlineColor", "outlinecolor"})).setParent(this.noBlockOutline, true);
      this.offerValues(new Value[]{this.crosshairInfo, this.noBlockOutline, this.lineWidth, this.fill, this.fillColor, this.outlineColor});
      this.offerListeners(new Listener[]{new ListenerRender(this), new ListenerBlockOutline(this)});
   }

   protected String getTag() {
      if (!(Boolean)this.crosshairInfo.getValue()) {
         return null;
      } else if (!PlayerUtil.isNull() && mc.crosshairTarget != null) {
         HitResult var2;
         switch(mc.crosshairTarget.getType()) {
         case MISS:
            return "Air";
         case BLOCK:
            var2 = mc.crosshairTarget;
            if (var2 instanceof BlockHitResult) {
               BlockHitResult bhr = (BlockHitResult)var2;
               BlockPos lookingAt = bhr.getBlockPos();
               if (lookingAt != null) {
                  Block block = BlockUtil.getBlock(lookingAt);
                  if (block != null) {
                     return block.getName().getString();
                  }
               }
            }
            break;
         case ENTITY:
            var2 = mc.crosshairTarget;
            if (var2 instanceof EntityHitResult) {
               EntityHitResult ehr = (EntityHitResult)var2;
               Entity entity = ehr.getEntity();
               if (entity != null) {
                  String label = entity.getDisplayName() == null ? entity.getName().getString() : entity.getDisplayName().getString();
                  return TextUtil.removeColor(label);
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
