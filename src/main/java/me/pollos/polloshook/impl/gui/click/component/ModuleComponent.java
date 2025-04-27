package me.pollos.polloshook.impl.gui.click.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.KeybindValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.toggleable.block.BlockListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;
import me.pollos.polloshook.impl.gui.click.component.values.BlockListComponent;
import me.pollos.polloshook.impl.gui.click.component.values.BooleanComponent;
import me.pollos.polloshook.impl.gui.click.component.values.ColorComponent;
import me.pollos.polloshook.impl.gui.click.component.values.EnumComponent;
import me.pollos.polloshook.impl.gui.click.component.values.ItemListComponent;
import me.pollos.polloshook.impl.gui.click.component.values.KeybindComponent;
import me.pollos.polloshook.impl.gui.click.component.values.NumberComponent;
import me.pollos.polloshook.impl.gui.click.component.values.StringComponent;
import me.pollos.polloshook.impl.gui.click.component.values.TargetComponent;
import me.pollos.polloshook.impl.gui.click.component.values.ValueComponent;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.other.colours.util.SettingColorComponent;
import net.minecraft.client.gui.DrawContext;

public class ModuleComponent extends Component {
   private final Module module;
   private final ArrayList<Component> components = new ArrayList();

   public ModuleComponent(Module module, float posX, float posY, float offsetX, float offsetY, float width, float height) {
      super(module.getLabel(), posX, posY, offsetX, offsetY, width, height);
      this.module = module;
   }

   public void init() {
      this.getComponents().clear();
      float offY = this.getHeight();
      Rectangle rectangle = new Rectangle(this.getFinishedX(), this.getFinishedY(), this.getWidth(), 14.0F);
      if (!this.getModule().getValues().isEmpty()) {
         Iterator var3 = this.getModule().getValues().iterator();

         while(var3.hasNext()) {
            Value<?> value = (Value)var3.next();
            if (value.getValue() instanceof Boolean) {
               this.getComponents().add(new BooleanComponent((Value<Boolean>) value, rectangle, 0.0F, offY));
               offY += 14.0F;
            } else if (value instanceof KeybindValue) {
               KeybindValue keybindValue = (KeybindValue)value;
               this.getComponents().add(new KeybindComponent(this.getModule(), keybindValue, rectangle, 0.0F, offY));
               offY += 14.0F;
            } else if (value instanceof NumberValue) {
               NumberValue<?> numberValue = (NumberValue)value;
               this.getComponents().add(new NumberComponent((NumberValue<Number>) numberValue, rectangle, 0.0F, offY));
               offY += 14.0F;
            } else if (value instanceof EnumValue) {
               EnumValue<?> enumValue = (EnumValue)value;
               this.getComponents().add(new EnumComponent(enumValue, rectangle, 0.0F, offY));
               offY += 14.0F;
            } else if (value instanceof ColorValue) {
               ColorValue colorValue = (ColorValue)value;
               if (this.module instanceof Colours) {
                  this.getComponents().add(new SettingColorComponent(colorValue, rectangle, 0.0F, offY));
               } else {
                  this.getComponents().add(new ColorComponent(colorValue, rectangle, 0.0F, offY));
               }

               offY += 14.0F;
            } else if (value instanceof StringValue) {
               StringValue stringValue = (StringValue)value;
               this.getComponents().add(new StringComponent(stringValue, rectangle, 0.0F, offY));
               offY += 14.0F;
            } else if (value instanceof TargetValue) {
               TargetValue targetValue = (TargetValue)value;
               this.getComponents().add(new TargetComponent(targetValue, rectangle, 0.0F, offY));
               offY += 14.0F;
            } else if (value instanceof BlockListValue) {
               BlockListValue blockListValue = (BlockListValue)value;
               this.getComponents().add(new BlockListComponent(blockListValue, rectangle, 0.0F, offY));
               offY += 14.0F;
            }

            if (value instanceof ItemListValue) {
               ItemListValue itemListValue = (ItemListValue)value;
               this.getComponents().add(new ItemListComponent(itemListValue, rectangle, 0.0F, offY));
               offY += 14.0F;
            }
         }
      }

      Module var13 = this.getModule();
      if (var13 instanceof ToggleableModule) {
         ToggleableModule toggleableModule = (ToggleableModule)var13;
         if (!this.getModule().getCategory().getLabel().equalsIgnoreCase("Other") && !this.getModule().getCategory().getLabel().equalsIgnoreCase("Elements")) {
            KeybindValue keybindValue = new KeybindValue(toggleableModule.getKeybind(), new String[]{"Keybind", "key", "bind", "elementCodec"});
            keybindValue.addObserver((event) -> {
               ((ToggleableModule)this.getModule()).setKeybind((Keybind)event.getValue());
            });
            this.getComponents().add(new KeybindComponent(this.getModule(), keybindValue, rectangle, 0.0F, offY));
         }
      }

      this.getComponents().forEach(Component::init);
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
      this.getComponents().forEach((component) -> {
         component.moved(this.getFinishedX(), this.getFinishedY());
      });
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, (double)this.getFinishedX(), (double)this.getFinishedY(), (double)this.getWidth(), (double)this.getHeight());
      boolean enabled = !(this.module instanceof ToggleableModule) || ((ToggleableModule)this.module).isEnabled();
      Rectangle moduleRectangle = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 0.5F, this.getFinishedX() + this.getWidth() - 1.0F, this.getFinishedY() + this.getHeight() - 0.5F);
      if ((Boolean)ClickGUI.get().getFutureBox().getValue()) {
         Render2DMethods.drawGradientRect(context, moduleRectangle, false, 553648127, 285212671);
      }

      if (hovered && !enabled) {
         Render2DMethods.drawRect(context, moduleRectangle, 1714631475);
      }

      Color color = hovered ? this.getColor().darker() : this.getColor();
      if (enabled) {
         Render2DMethods.drawRect(context, moduleRectangle, color.getRGB());
      }

      if ((Boolean)ClickGUI.get().getColorByModule().getValue() && !enabled) {
         Render2DMethods.drawRect(context, moduleRectangle, ColorUtil.changeAlpha(color, 30).getRGB());
      }

      Managers.getTextManager().drawString(context, this.getLabel(), (double)((int)(this.getFinishedX() + 4.0F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), enabled ? -1 : -5592406);
      if (this.isExtended()) {
         Iterator var9 = this.getComponents().iterator();

         while(var9.hasNext()) {
            Component component = (Component)var9.next();
            if (component instanceof BlockListComponent) {
               BlockListComponent blockListComponent = (BlockListComponent)component;
               blockListComponent.updateVisibility();
            }

            if (component instanceof ItemListComponent) {
               ItemListComponent itemListComponent = (ItemListComponent)component;
               itemListComponent.updateVisibility();
            }

            if (component instanceof ValueComponent && ((ValueComponent)component).getValue().getParent().isVisible()) {
               component.render(context, mouseX, mouseY, delta);
            }
         }
      }

      this.updatePositions();
   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      super.keyPressed(keyCode, scanCode, modifiers);
      if (this.isExtended()) {
         Iterator var4 = this.getComponents().iterator();

         while(var4.hasNext()) {
            Component component = (Component)var4.next();
            if (component instanceof ValueComponent && ((ValueComponent)component).getValue().getParent().isVisible()) {
               component.keyPressed(keyCode, scanCode, modifiers);
            }
         }
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)this.getFinishedX(), (double)this.getFinishedY(), (double)this.getWidth(), (double)this.getHeight());
      if (hovered) {
         switch(button) {
         case 0:
            click();
            if (module instanceof ToggleableModule) {
               ((ToggleableModule)module).toggle();
            }
            break;
         case 1:
            if (!this.getComponents().isEmpty()) {
               this.click();
               this.getComponents().forEach((c) -> {
                  if (c.isExtended()) {
                     c.setExtended(false);
                  }

               });
               this.setExtended(!this.isExtended());
            }
            break;
         case 2:
            this.module.setDrawn(!this.module.isDrawn());
         }
      }

      if (this.isExtended()) {
         Iterator var7 = this.getComponents().iterator();

         while(var7.hasNext()) {
            Component component = (Component)var7.next();
            if (component instanceof ValueComponent && ((ValueComponent)component).getValue().getParent().isVisible()) {
               component.mouseClicked(mouseX, mouseY, button);
            }
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.isExtended()) {
         Iterator var6 = this.getComponents().iterator();

         while(var6.hasNext()) {
            Component component = (Component)var6.next();
            if (component instanceof ValueComponent && ((ValueComponent)component).getValue().getParent().isVisible()) {
               component.mouseReleased(mouseX, mouseY, button);
            }
         }
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean charTyped(char chr, int modifiers) {
      super.charTyped(chr, modifiers);
      if (this.isExtended()) {
         Iterator var3 = this.getComponents().iterator();

         while(var3.hasNext()) {
            Component component = (Component)var3.next();
            if (component instanceof ValueComponent && ((ValueComponent)component).getValue().getParent().isVisible()) {
               component.charTyped(chr, modifiers);
            }
         }
      }
      return true;
   }

   private void updatePositions() {
      float offsetY = this.getHeight();
      Iterator var2 = this.getComponents().iterator();

      while(var2.hasNext()) {
         Component component = (Component)var2.next();
         if (component instanceof ValueComponent && ((ValueComponent)component).getValue().getParent().isVisible()) {
            component.setOffsetY(offsetY);
            component.moved(this.getPosX(), this.getPosY());
            offsetY += component.getHeight();
         }
      }

   }

   public Color getColor() {
      return (Boolean)ClickGUI.get().getColorByModule().getValue() ? ColorUtil.changeAlpha(this.module.getModuleColor(), 95) : super.getColor();
   }

   
   public Module getModule() {
      return this.module;
   }

   
   public ArrayList<Component> getComponents() {
      return this.components;
   }
}
