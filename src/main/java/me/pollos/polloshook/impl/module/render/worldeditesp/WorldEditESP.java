package me.pollos.polloshook.impl.module.render.worldeditesp;

import java.awt.Color;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.worldeditesp.mode.AxeMode;
import me.pollos.polloshook.impl.module.render.worldeditesp.mode.RenderMode;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.math.BlockPos;

public class WorldEditESP extends ToggleableModule {
   protected final EnumValue<AxeMode> axe;
   protected final ColorValue firstColor;
   protected final ColorValue secondColor;
   protected final EnumValue<RenderMode> mode;
   protected final Value<Boolean> cross;
   protected final ColorValue fillColor;
   protected final ColorValue lineColor;
   protected BlockPos firstBlock;
   protected BlockPos secondBlock;

   public WorldEditESP() {
      super(new String[]{"WorldEditESP", "worldedit"}, Category.RENDER);
      this.axe = new EnumValue(AxeMode.WOOD, new String[]{"Axe", "ax"});
      this.firstColor = new ColorValue(new Color(15, 127, 255), false, new String[]{"FirstColor", "first", "1st"});
      this.secondColor = new ColorValue(new Color(255, 15, 15), false, new String[]{"SecondColor", "first", "2nd"});
      this.mode = new EnumValue(RenderMode.OUTLINE, new String[]{"RenderMode", "render"});
      this.cross = new Value(false, new String[]{"Cross", "c", "crossed"});
      this.fillColor = (new ColorValue(new Color(255, 0, 255, 120), false, new String[]{"FillColor", "filled"})).setParent(this.mode, RenderMode.OUTLINE, true);
      this.lineColor = (new ColorValue(new Color(255, 0, 255, 255), false, new String[]{"LineColor", "line"})).setParent(this.mode, RenderMode.FILL, true);
      this.offerValues(new Value[]{this.axe, this.firstColor, this.secondColor, this.mode, this.cross, this.fillColor, this.lineColor});
      this.offerListeners(new Listener[]{new ListenerClickBlock(this), new ListenerInteractBlock(this), new ListenerRender(this)});
   }

   protected boolean isValid(Item item) {
      if (item instanceof AxeItem) {
         AxeItem axeItem = (AxeItem)item;
         if (!mc.player.isCreative()) {
            return false;
         } else {
            boolean var10000;
            switch((AxeMode)this.axe.getValue()) {
               case ANY:
                   var10000 = true;
                   break;
               case WOOD:
                   var10000 = axeItem.getMaterial() == ToolMaterials.WOOD;
                   break;
               case STONE:
                   var10000 = axeItem.getMaterial() == ToolMaterials.STONE;
                   break;
               case IRON:
                   var10000 = axeItem.getMaterial() == ToolMaterials.IRON;
                   break;
               case DIAMOND:
                   var10000 = axeItem.getMaterial() == ToolMaterials.DIAMOND;
                   break;
               case NETHERITE:
                   var10000 = axeItem.getMaterial() == ToolMaterials.NETHERITE;
                   break;
               default:
                   throw new MatchException((String)null, (Throwable)null);
               }

            return var10000;
         }
      } else {
         return false;
      }
   }

   public void onWorldLoad() {
      this.firstBlock = null;
      this.secondBlock = null;
   }

   protected void onToggle() {
      this.firstBlock = null;
      this.secondBlock = null;
   }
}
