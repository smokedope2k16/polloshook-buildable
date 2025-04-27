package me.pollos.polloshook.impl.module.render.crosshair;

import java.awt.Color;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.impl.events.render.Render2DEvent;
import net.minecraft.client.gui.DrawContext;

public class ListenerRender extends ModuleListener<Crosshair, Render2DEvent> {
   public ListenerRender(Crosshair module) {
      super(module, Render2DEvent.class);
   }

   public void call(Render2DEvent event) {
      if ((Boolean)((Crosshair)this.module).thirdPerson.getValue() || mc.options.getPerspective().isFirstPerson()) {
         DrawContext context = event.getContext();
         float centerX = (float)context.getScaledWindowWidth() / 2.0F;
         float centerY = (float)context.getScaledWindowHeight() / 2.0F;
         float dynamicOffset = MovementUtil.isMoving() && (Boolean)((Crosshair)this.module).movementError.getValue() ? (Float)((Crosshair)this.module).factor.getValue() : 0.0F;
         float topY = centerY - (Float)((Crosshair)this.module).gap.getValue() - dynamicOffset;
         float bottomY = centerY + (Float)((Crosshair)this.module).gap.getValue() + dynamicOffset;
         float leftX = centerX - (Float)((Crosshair)this.module).gap.getValue() - dynamicOffset;
         float rightX = centerX + (Float)((Crosshair)this.module).gap.getValue() + dynamicOffset;
         this.drawBordered(context, centerX - (Float)((Crosshair)this.module).width.getValue(), topY - (Float)((Crosshair)this.module).length.getValue(), centerX + (Float)((Crosshair)this.module).width.getValue(), topY, (Float)((Crosshair)this.module).width.getValue(), (Float)((Crosshair)this.module).outlineWidth.getValue(), (Color)((Crosshair)this.module).color.getValue(), (Color)((Crosshair)this.module).outlineColor.getValue());
         this.drawBordered(context, centerX - (Float)((Crosshair)this.module).width.getValue(), bottomY, centerX + (Float)((Crosshair)this.module).width.getValue(), bottomY + (Float)((Crosshair)this.module).length.getValue(), (Float)((Crosshair)this.module).width.getValue(), (Float)((Crosshair)this.module).outlineWidth.getValue(), (Color)((Crosshair)this.module).color.getValue(), (Color)((Crosshair)this.module).outlineColor.getValue());
         this.drawBordered(context, leftX - (Float)((Crosshair)this.module).length.getValue(), centerY - (Float)((Crosshair)this.module).width.getValue(), leftX, centerY + (Float)((Crosshair)this.module).width.getValue(), (Float)((Crosshair)this.module).width.getValue(), (Float)((Crosshair)this.module).outlineWidth.getValue(), (Color)((Crosshair)this.module).color.getValue(), (Color)((Crosshair)this.module).outlineColor.getValue());
         this.drawBordered(context, rightX, centerY - (Float)((Crosshair)this.module).width.getValue(), rightX + (Float)((Crosshair)this.module).length.getValue(), centerY + (Float)((Crosshair)this.module).width.getValue(), (Float)((Crosshair)this.module).width.getValue(), (Float)((Crosshair)this.module).outlineWidth.getValue(), (Color)((Crosshair)this.module).color.getValue(), (Color)((Crosshair)this.module).outlineColor.getValue());
      }
   }

   private void drawBordered(DrawContext context, float x, float y, float width, float height, float thickness, float outlineWidth, Color inside, Color outline) {
      Render2DMethods.drawRect(context, x + outlineWidth, y + outlineWidth, width - outlineWidth, height - outlineWidth, inside.getRGB());
      if ((Boolean)((Crosshair)this.module).outline.getValue()) {
         Render2DMethods.drawRect(context, x, y, x + outlineWidth, height, outline.getRGB());
         Render2DMethods.drawRect(context, x + outlineWidth, y, width, y + outlineWidth, outline.getRGB());
         Render2DMethods.drawRect(context, width - outlineWidth, y, width, height, outline.getRGB());
         Render2DMethods.drawRect(context, x, height - outlineWidth, width, height, outline.getRGB());
      }

   }
}
