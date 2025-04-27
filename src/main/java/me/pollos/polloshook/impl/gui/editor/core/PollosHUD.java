package me.pollos.polloshook.impl.gui.editor.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.impl.gui.click.component.Component;
import me.pollos.polloshook.impl.gui.click.component.ModuleComponent;
import me.pollos.polloshook.impl.gui.click.frame.CategoryFrame;
import me.pollos.polloshook.impl.gui.click.frame.Frame;
import me.pollos.polloshook.impl.gui.editor.element.PollosElement;
import me.pollos.polloshook.impl.module.other.hud.HUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class PollosHUD extends Screen {
   private final ArrayList<Frame> frames = new ArrayList();
   private final ArrayList<PollosElement> elements = new ArrayList();
   private static PollosHUD INSTANCE;

   public PollosHUD() {
      super(Text.empty());
      this.frames.add(new CategoryFrame(INSTANCE, Category.ELEMENTS.getLabel(), 82.0F, 24.0F, 100.0F, 16.0F) {
         public void init() {
            this.getComponents().clear();
            float offsetY = this.getHeight() + 1.0F;

            for(Iterator var2 = Managers.getModuleManager().getHUDModules().iterator(); var2.hasNext(); offsetY += 14.0F) {
               HUDModule module = (HUDModule)var2.next();
               if (module instanceof DraggableHUDModule) {
                  DraggableHUDModule draggableHUDModule = (DraggableHUDModule)module;
                  PollosHUD.this.elements.add(new PollosElement(draggableHUDModule));
               }

               this.getComponents().add(new ModuleComponent(module, this.getPosX(), this.getPosY(), 0.0F, offsetY, this.getWidth(), 14.0F));
            }

            PollosHUD.this.frames.forEach((frame) -> {
               frame.getComponents().sort(Comparator.comparing(Component::getLabel));
            });
            super.init();
         }
      });
      this.frames.forEach(Frame::init);
      INSTANCE = this;
   }

   public static PollosHUD getInstance() {
      return INSTANCE == null ? new PollosHUD() : INSTANCE;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      ((HUD)Managers.getModuleManager().get(HUD.class)).setFalse();
      this.frames.forEach((frame) -> {
         frame.render(context, mouseX, mouseY, delta);
      });
      this.elements.forEach((element) -> {
         element.render(context, mouseX, mouseY, delta);
      });
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.frames.forEach((frames) -> {
         frames.mouseClicked(mouseX, mouseY, button);
      });
      this.elements.forEach((element) -> {
         element.mouseClicked(mouseX, mouseY, button);
      });
      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.frames.forEach((frames) -> {
         frames.mouseReleased(mouseX, mouseY, button);
      });
      this.elements.forEach((element) -> {
         element.mouseReleased(mouseX, mouseY, button);
      });
      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      this.elements.forEach((element) -> {
         element.keyPressed(keyCode, scanCode, modifiers);
      });
      this.frames.forEach((panel) -> {
         panel.keyPressed(keyCode, scanCode, modifiers);
      });
      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean charTyped(char chr, int modifiers) {
      this.frames.forEach((panel) -> {
         panel.charTyped(chr, modifiers);
      });
      return super.charTyped(chr, modifiers);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.frames.forEach((frame) -> {
         frame.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      });
      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean shouldPause() {
      return false;
   }

   
   public ArrayList<Frame> getFrames() {
      return this.frames;
   }

   
   public ArrayList<PollosElement> getElements() {
      return this.elements;
   }
}
