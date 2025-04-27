package me.pollos.polloshook.impl.gui.editor.element;

import java.awt.Color;

import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.impl.module.other.hud.HUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PollosElement extends Screen {
   private final DraggableHUDModule draggable;
   private boolean dragging;
   private int dragX;
   private int dragY;
   private final StopWatch lastMoveTimer = new StopWatch();

   public PollosElement(DraggableHUDModule draggable) {
      super(Text.of(draggable.getLabel()));
      this.draggable = draggable;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.draggable.setHovered(Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, (double)this.draggable.getFixedX((int)this.draggable.getTextX() - 7), (double)(this.draggable.getTextY() - 7.0F), (double)(this.draggable.getTextWidth() + 14.0F), (double)(this.draggable.getTextHeight() + 14.0F)));
      this.draggable.setDragging(this.dragging && this.draggable.isHovered());
      if ((float)mouseX - this.draggable.getTextX() != (float)this.dragX || (float)mouseY - this.draggable.getTextY() != (float)this.dragY) {
         this.lastMoveTimer.reset();
      }

      if (this.draggable.isHovered() && this.dragging) {
         if (this.lastMoveTimer.passed((double)((Float)HUD.get().getSnapDelay().getValue() * 50.0F)) && this.draggable.snapToHudPosition(context)) {
            this.dragging = false;
            this.lastMoveTimer.reset();
            return;
         }

         this.draggable.setPosition(DraggableHUDModule.HudPosition.CUSTOM);
         DraggableHUDModule.DragComponentEvent event = new DraggableHUDModule.DragComponentEvent(this.draggable, context, mouseX - this.dragX, mouseY - this.dragY);
         event.dispatch();
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.dragging = button == 0;
      if (this.dragging && this.draggable.isHovered()) {
         this.dragX = (int)(mouseX - (double)this.draggable.getTextX());
         this.dragY = (int)(mouseY - (double)this.draggable.getTextY());
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0 && this.dragging) {
         this.dragging = false;
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.draggable.isHovered()) {
         switch(keyCode) {
         case 262:
            this.draggable.setTextX(this.draggable.getTextX() + 3.0F);
            break;
         case 263:
            this.draggable.setTextX(this.draggable.getTextX() - 3.0F);
            break;
         case 264:
            this.draggable.setTextY(this.draggable.getTextY() + 3.0F);
            break;
         case 265:
            this.draggable.setTextY(this.draggable.getTextY() - 3.0F);
         }

         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   private void drawSnapPreview(DrawContext context) {
      int screenWidth = context.getScaledWindowWidth();
      int screenHeight = context.getScaledWindowHeight();
      int centerX = screenWidth / 2;
      int centerY = screenHeight / 2;
      int offset = 1;
      int color = Color.PINK.getRGB();
      if (this.draggable.getPosition() != null) {
         switch(this.draggable.getPosition()) {
         case TOP:
            context.fill(0, 0, screenWidth, offset, color);
            break;
         case BOTTOM:
            context.fill(0, screenHeight - offset, screenWidth, screenHeight, color);
            break;
         case LEFT:
            context.fill(0, 0, offset, screenHeight, color);
            break;
         case RIGHT:
            context.fill(screenWidth - offset, 0, screenWidth, screenHeight, color);
            break;
         case CENTER:
         case MIDDLE_CENTER:
            context.fill(0, centerY, screenWidth, centerY, color);
            context.fill(centerX, 0, centerX, screenHeight, color);
            break;
         case TOP_LEFT:
            context.fill(0, 0, screenWidth, offset, color);
            context.fill(0, 0, offset, screenHeight, color);
            break;
         case TOP_RIGHT:
            context.fill(0, 0, screenWidth, offset, color);
            context.fill(screenWidth - offset, 0, screenWidth, screenHeight, color);
            break;
         case BOTTOM_LEFT:
            context.fill(0, screenHeight - offset, screenWidth, screenHeight, color);
            context.fill(0, 0, offset, screenHeight, color);
            break;
         case BOTTOM_RIGHT:
            context.fill(0, screenHeight - offset, screenWidth, screenHeight, color);
            context.fill(screenWidth - offset, 0, screenWidth, screenHeight, color);
            break;
         case TOP_CENTER:
            context.fill(0, 0, screenWidth, offset, color);
            context.fill(centerX, 0, centerX, screenHeight, color);
            break;
         case BOTTOM_CENTER_L:
         case BOTTOM_CENTER_R:
            context.fill(0, screenHeight - offset, screenWidth, screenHeight, color);
            context.fill(centerX, 0, centerX, screenHeight, color);
            break;
         case MIDDLE_LEFT:
            context.fill(0, 0, offset, screenHeight, color);
            context.fill(0, centerY, screenWidth, centerY, color);
            break;
         case MIDDLE_RIGHT:
            context.fill(screenWidth - offset, 0, screenWidth, screenHeight, color);
            context.fill(0, centerY, screenWidth, centerY, color);
         }

      }
   }

   
   public DraggableHUDModule getDraggable() {
      return this.draggable;
   }
}
