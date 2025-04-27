package me.pollos.polloshook.impl.gui.click.component;

import java.awt.Color;
import java.util.Objects;

import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gui.DrawContext;

public class Component implements Labeled, Minecraftable {
   private final String label;
   private float posX;
   private float posY;
   private float finishedX;
   private float finishedY;
   private float offsetX;
   private float offsetY;
   private float lastPosX;
   private float lastPosY;
   private float width;
   private float height;
   private boolean extended;
   private boolean dragging;
   protected final float DEFAULT_HEIGHT = 14.0F;

   public Component(String label, float posX, float posY, float offsetX, float offsetY, float width, float height) {
      this.label = label;
      this.posX = posX;
      this.posY = posY;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.width = width;
      this.height = height;
      this.finishedX = posX + offsetX;
      this.finishedY = posY + offsetY;
   }

   public void init() {
   }

   public void moved(float posX, float posY) {
      this.setPosX(posX);
      this.setPosY(posY);
      this.setFinishedX(this.getPosX() + this.getOffsetX());
      this.setFinishedY(this.getPosY() + this.getOffsetY());
   }

   public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return true;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
      return true;
   }

   public boolean charTyped(char chr, int modifiers) {
      return true;
   }

   public void click() {
      Render2DMethods.click();
   }

   public Color getColor() {
      return this.getColor(125);
   }

   public Color getColor(int alpha) {
      return ColorUtil.changeAlpha(Colours.get().getColor((Boolean)ClickGUI.get().getHomosexuality().getValue() ? (int)this.finishedY : 0), alpha);
   }

   
   public String getLabel() {
      return this.label;
   }

   
   public float getPosX() {
      return this.posX;
   }

   
   public float getPosY() {
      return this.posY;
   }

   
   public float getFinishedX() {
      return this.finishedX;
   }

   
   public float getFinishedY() {
      return this.finishedY;
   }

   
   public float getOffsetX() {
      return this.offsetX;
   }

   
   public float getOffsetY() {
      return this.offsetY;
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

   
   public float getDEFAULT_HEIGHT() {
      Objects.requireNonNull(this);
      return 14.0F;
   }

   
   public void setPosX(float posX) {
      this.posX = posX;
   }

   
   public void setPosY(float posY) {
      this.posY = posY;
   }

   
   public void setFinishedX(float finishedX) {
      this.finishedX = finishedX;
   }

   
   public void setFinishedY(float finishedY) {
      this.finishedY = finishedY;
   }

   
   public void setOffsetX(float offsetX) {
      this.offsetX = offsetX;
   }

   
   public void setOffsetY(float offsetY) {
      this.offsetY = offsetY;
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

   
   public void setHeight(float height) {
      this.height = height;
   }

   
   public void setExtended(boolean extended) {
      this.extended = extended;
   }

   
   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }
}
