package me.pollos.polloshook.impl.gui.click;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.config.modules.ModuleConfig;
import me.pollos.polloshook.impl.gui.click.component.Component;
import me.pollos.polloshook.impl.gui.click.component.ConfigComponent;
import me.pollos.polloshook.impl.gui.click.component.ModuleComponent;
import me.pollos.polloshook.impl.gui.click.component.values.ColorComponent;
import me.pollos.polloshook.impl.gui.click.component.values.KeybindComponent;
import me.pollos.polloshook.impl.gui.click.component.values.StringComponent;
import me.pollos.polloshook.impl.gui.click.frame.CategoryFrame;
import me.pollos.polloshook.impl.gui.click.frame.Frame;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen implements Minecraftable {
   private final ArrayList<Frame> frames = new ArrayList<>(); 

   public ClickGuiScreen() {
      super(Text.of("Pollos's GUI"));
   }

   @Override 
   public void init() {
      this.frames.clear(); 
      int x = 2;
      int y = 2;

      for (Category category : Category.values()) {
         if (category != Category.ELEMENTS) {
            this.frames.add(new CategoryFrame(this, category.getLabel(), (float)x, (float)y, 100.0F, 16.0F) {
               @Override 
               public void init() {
                  this.getComponents().clear();
                  float offsetY = this.getHeight() + 1.0F;
                  List<Module> moduleList = Managers.getModuleManager().getModulesSorted();

                  for (Module module : moduleList) {
                     if (module.getCategory().equals(category)) {
                        this.getComponents().add(new ModuleComponent(module, this.getPosX(), this.getPosY(), 0.0F, offsetY, this.getWidth(), 14.0F));
                        offsetY += 14.0F;
                     }
                  }

                  super.init();
               }
            });
            x += 102;
         }
      }

      if (ClickGUI.get().getConfigs().getValue()) {
         this.initConfigs((float)x, (float)y);
      }

      this.frames.forEach(Frame::init);
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      Render2DMethods.drawGradientRect(context, 0.0F, 0.0F, (float)mc.getWindow().getScaledWidth(), (float)mc.getWindow().getScaledHeight(), false, (new Color(25, 25, 25, 55)).getRGB(), (new Color(0, 0, 0, 120)).getRGB());
      this.frames.forEach((frame) -> {
         frame.render(context, mouseX, mouseY, delta);
      });
   }

   @Override 
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      this.frames.forEach((frame) -> {
         frame.keyPressed(keyCode, scanCode, modifiers);
      });
      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   @Override 
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.frames.forEach((frame) -> {
         frame.mouseClicked(mouseX, mouseY, button);
      });
      return super.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int releaseButton) {
      this.frames.forEach((frame) -> {
         frame.mouseReleased(mouseX, mouseY, releaseButton);
      });
      return super.mouseReleased(mouseX, mouseY, releaseButton);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.frames.forEach((frame) -> {
         frame.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      });
      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override 
   public boolean charTyped(char chr, int modifiers) {
      this.frames.forEach((frame) -> {
         frame.charTyped(chr, modifiers);
      });
      return super.charTyped(chr, modifiers);
   }

   @Override 
   public boolean shouldPause() {
      return false;
   }

   @Override 
   public void close() {
      super.close();
      this.frames.forEach((frame) -> {
         for (Component comp : frame.getComponents()) {
            if (comp instanceof ModuleComponent moduleComponent) { 
               for (Component component : moduleComponent.getComponents()) {
                  if (component instanceof KeybindComponent keybindComponent) { 
                     keybindComponent.setBinding(false);
                  }

                  if (component instanceof StringComponent stringComponent) { 
                     stringComponent.setListening(false);
                  }
               }
            }
         }
      });
   }

   public void onGuiOpened() {
      this.frames.forEach((frame) -> {
         for (Component comp : frame.getComponents()) {
            if (comp instanceof ModuleComponent moduleComponent) {
               for (Component component : moduleComponent.getComponents()) {
                  if (component instanceof ColorComponent colorComponent) { 
                     float[] hsb = Color.RGBtoHSB(colorComponent.getColorValue().getColor().getRed(), colorComponent.getColorValue().getColor().getGreen(), colorComponent.getColorValue().getColor().getBlue(), (float[])null);
                     colorComponent.setHue(hsb[0]);
                     colorComponent.setSaturation(hsb[1]);
                     colorComponent.setBrightness(hsb[2]);
                     colorComponent.setAlpha((float)colorComponent.getColorValue().getColor().getAlpha() / 255.0F);
                  }
               }
            }
         }
      });
   }

   public void initConfigs(float x, float y) {
      if (!Managers.getConfigManager().getModuleConfigs().isEmpty()) {
         try {
            this.addProfilesCategory(x, y);
         } catch (Exception var4) {
            ClientLogger.getLogger().error(Arrays.toString(var4.getStackTrace()));
         }
      }

      this.frames.forEach((f) -> {
         if (f instanceof CategoryFrame && f.getLabel().equalsIgnoreCase("Configs")) {
            List<String> added = new ArrayList<>(); 
            f.getComponents().removeIf((component) -> {
               if (component instanceof ConfigComponent configComponent) { 
                  if (!configComponent.getModuleConfig().getFile().exists()) {
                     return true;
                  }
               }

               String name = component.getLabel();
               if (added.contains(name)) {
                  return true;
               } else {
                  added.add(name);
                  return false;
               }
            });
         }
      });
   }

   public void addProfilesCategory(float x, float y) {
      Frame frame = new CategoryFrame(this, "Configs", x, y, 100.0F, 16.0F) {
         @Override
         public void init() {
            this.getComponents().clear();
            float offsetY = this.getHeight() + 1.0F;

            for (ModuleConfig config : Managers.getConfigManager().getModuleConfigs()) {
               this.getComponents().add(new ConfigComponent(config, config.getLabel(), this.getPosX(), this.getPosY(), 0.0F, offsetY, this.getWidth(), 14.0F));
               offsetY += 14.0F;
            }

            super.init();
         }
      };
      this.frames.add(frame);
   }

   public ArrayList<Frame> getFrames() {
      return this.frames;
   }
}