package me.pollos.polloshook.impl.gui.click.component.values;


import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.minecraft.render.utils.Dots;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.KeybindValue;
import net.minecraft.client.gui.DrawContext;

public class KeybindComponent extends ValueComponent<Keybind, KeybindValue> {
   private final KeybindValue keybindValue;
   private boolean binding;
   protected final Module module;

   public KeybindComponent(Module module, KeybindValue keybindValue, Rectangle rect, float offsetX, float offsetY) {
      super(keybindValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), keybindValue);
      this.keybindValue = keybindValue;
      this.module = module;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, (double)(this.getFinishedX() + 5.0F), (double)(this.getFinishedY() + 1.0F), (double)(this.getWidth() - 10.0F), (double)(this.getHeight() - 2.0F));
      Render2DMethods.drawRect(context, this.commonRenderRectangle(), hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
      Managers.getTextManager().drawString((DrawContext)context, this.isBinding() ? "Press keyCodec key" + Dots.get3Dots() : this.getKeybindValue().getLabel() + ": " + (((Keybind)this.getKeybindValue().getValue()).getKey() == -1 ? "None" : KeyboardUtil.getKeyNameFromNumber(((Keybind)this.getKeybindValue().getValue()).getKey(), false).toUpperCase()), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      super.keyPressed(keyCode, scanCode, modifiers);
      if (this.isBinding()) {
         if (KeyboardUtil.REMOVE_BINDS_LIST.contains(keyCode)) {
            this.getKeybindValue().setValue(Keybind.noKeyBind());
            this.setBinding(false);
            return;
         }

         Keybind kb = new Keybind(keyCode) {
            public void onKeyPress() {
               Module var2 = KeybindComponent.this.module;
               if (var2 instanceof ToggleableModule) {
                  ToggleableModule toggleableModule = (ToggleableModule)var2;
                  toggleableModule.toggle();
               }

            }
         };
         this.getKeybindValue().setValue(kb);
         this.setBinding(false);
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)(this.getFinishedX() + 5.0F), (double)(this.getFinishedY() + 1.0F), (double)(this.getWidth() - 10.0F), (double)(this.getHeight() - 2.0F));
      if (hovered && button == 0) {
         this.click();
         this.setBinding(!this.isBinding());
         return false;
      } else {
         if (this.isBinding()) {
            if (button == -1 || button == 0 || button == 1) {
               return false;
            }

            Keybind bind = new Keybind(button) {
               public void onKeyPress() {
                  Module var2 = KeybindComponent.this.module;
                  if (var2 instanceof ToggleableModule) {
                     ToggleableModule toggleableModule = (ToggleableModule)var2;
                     toggleableModule.toggle();
                  }

               }
            };
            this.getKeybindValue().setValue(bind);
            this.setBinding(false);
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return super.mouseReleased(mouseX, mouseY, button);
   }

   
   public KeybindValue getKeybindValue() {
      return this.keybindValue;
   }

   
   public boolean isBinding() {
      return this.binding;
   }

   
   public Module getModule() {
      return this.module;
   }

   
   public void setBinding(boolean binding) {
      this.binding = binding;
   }
}
