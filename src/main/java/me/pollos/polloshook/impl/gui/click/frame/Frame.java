package me.pollos.polloshook.impl.gui.click.frame;

import java.awt.Color;
import java.util.ArrayList;

import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.impl.gui.click.component.Component;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class Frame implements Labeled, Minecraftable {
   private final String label;
   private float posX;
   private float posY;
   private float lastPosX;
   private float lastPosY;
   private float width;
   private final float height;
   private boolean extended;
   private boolean dragging;
   private final ArrayList<Component> components = new ArrayList();
   private int scrollY;

   public Frame(Screen parentScreen, String label, float posX, float posY, float width, float height) {
      this.label = label;
      this.posX = posX;
      this.posY = posY;
      this.width = width;
      this.height = height;
   }

   public void init() {
      this.components.forEach(Component::init);
   }

   public void moved(float posX, float posY) {
      this.components.forEach((component) -> {
         component.moved(posX, posY);
      });
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      if (this.isDragging()) {
         this.setPosX((float)mouseX + this.getLastPosX());
         this.setPosY((float)mouseY + this.getLastPosY());
         this.getComponents().forEach((component) -> {
            component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY());
         });
      }

      if (this.getPosX() < 0.0F) {
         this.setPosX(0.0F);
         this.getComponents().forEach((component) -> {
            component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY());
         });
      }

      if (this.getPosX() + this.getWidth() > (float)context.getScaledWindowWidth()) {
         this.setPosX((float)context.getScaledWindowWidth() - this.getWidth());
         this.getComponents().forEach((component) -> {
            component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY());
         });
      }

      if (this.getPosY() < 0.0F) {
         this.setPosY(0.0F);
         this.getComponents().forEach((component) -> {
            component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY());
         });
      }

      if (this.getPosY() + this.getHeight() > (float)context.getScaledWindowHeight()) {
         this.setPosY((float)context.getScaledWindowHeight() - this.getHeight());
         this.getComponents().forEach((component) -> {
            component.moved(this.getPosX(), this.getPosY() + (float)this.getScrollY());
         });
      }

   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.isExtended()) {
         this.getComponents().forEach((component) -> {
            component.keyPressed(keyCode, scanCode, modifiers);
         });
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), (double)this.getHeight());
      switch(button) {
      case 0:
         if (hovered) {
            this.setDragging(true);
            this.setLastPosX((float)((double)this.getPosX() - mouseX));
            this.setLastPosY((float)((double)this.getPosY() - mouseY));
         }
         break;
      case 1:
         if (hovered) {
            this.setExtended(!this.isExtended());
         }
      }

      return hovered;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isDragging()) {
         this.setDragging(false);
      }

      if (this.isExtended()) {
         this.getComponents().forEach((component) -> {
            component.mouseReleased(mouseX, mouseY, mouseButton);
         });
      }

      return true;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return true;
   }

   public boolean charTyped(char chr, int modifiers) {
      return false;
   }

   public String getLabel() {
      return this.label;
   }

   public Color getColor() {
      Component i_from_ming_xiao_city = new Component("", 0.0F, this.posY, 0.0F, 0.0F, 0.0F, 0.0F);
      return i_from_ming_xiao_city.getColor(180);
   }

   
   public float getPosX() {
      return this.posX;
   }

   
   public float getPosY() {
      return this.posY;
   }

   
   public float getLastPosX() {
      return this.lastPosX;
   }

   
   public float getLastPosY() {
      return this.lastPosY;
   }

   
   public float getWidth() {
      return this.width;
   }

   
   public float getHeight() {
      return this.height;
   }

   
   public boolean isExtended() {
      return this.extended;
   }

   
   public boolean isDragging() {
      return this.dragging;
   }

   
   public ArrayList<Component> getComponents() {
      return this.components;
   }

   
   public int getScrollY() {
      return this.scrollY;
   }

   
   public void setPosX(float posX) {
      this.posX = posX;
   }

   
   public void setPosY(float posY) {
      this.posY = posY;
   }

   
   public void setLastPosX(float lastPosX) {
      this.lastPosX = lastPosX;
   }

   
   public void setLastPosY(float lastPosY) {
      this.lastPosY = lastPosY;
   }

   
   public void setWidth(float width) {
      this.width = width;
   }

   
   public void setExtended(boolean extended) {
      this.extended = extended;
   }

   
   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   
   public void setScrollY(int scrollY) {
      this.scrollY = scrollY;
   }
}
