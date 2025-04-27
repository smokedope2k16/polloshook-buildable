package me.pollos.polloshook.impl.gui.click.component.values;


import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.minecraft.render.utils.Dots;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.StringValue;
import net.minecraft.client.gui.DrawContext;

public class StringComponent extends ValueComponent<String, StringValue> {
   private final StringValue stringValue;
   public boolean isListening;
   private StringComponent.CurrentString currentString = new StringComponent.CurrentString("");

   public StringComponent(StringValue stringValue, Rectangle rect, float offsetX, float offsetY) {
      super(stringValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), stringValue);
      this.stringValue = stringValue;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, (double)(this.getFinishedX() + 1.0F), (double)(this.getFinishedY() + 1.0F), (double)(this.getWidth() - 2.0F), (double)(this.getHeight() - 2.0F));
      if (hovered) {
         Render2DMethods.drawRect(context, this.commonRenderRectangle(), 1714631475);
      }

      Render2DMethods.drawRect(context, this.commonRenderRectangle(), this.getColor().getRGB());
      String string = this.isListening ? this.currentString.string() : this.getStringValue().getLabel() + ": " + (String)this.getStringValue().getValue();
      if (this.isListening && !this.currentString.string().endsWith(".")) {
         string = string + Dots.get3Dots();
      }

      this.stringValue.setTypingStr(this.currentString.string());
      Managers.getTextManager().drawString((DrawContext)context, string, (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      super.keyPressed(keyCode, scanCode, modifiers);
      if (this.isListening) {
         if (keyCode == 256) {
            return;
         }

         if (keyCode == 257) {
            this.enterString();
            this.setListening(false);
         } else if (keyCode == 259) {
            this.setString(removeLastChar(this.currentString.string()));
         } else if (KeyboardUtil.isPasting()) {
            try {
               String var10001 = this.currentString.string();
               this.setString(var10001 + mc.keyboard.getClipboard());
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         }
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)(this.getFinishedX() + 1.0F), (double)(this.getFinishedY() + 1.0F), (double)(this.getWidth() - 2.0F), (double)(this.getHeight() - 2.0F));
      if (hovered && button == 0) {
         this.click();
         this.toggle();
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean charTyped(char chr, int modifiers) {
      super.charTyped(chr, modifiers);
      if (this.isListening) {
         String var10001 = this.currentString.string();
         this.setString(var10001 + chr);
      }
      return true;
   }

   private void enterString() {
      if (this.currentString.string().isEmpty()) {
         this.getStringValue().setValue((String)this.getStringValue().getDefaultValue());
      } else {
         this.getStringValue().setValue(this.currentString.string());
      }

      this.setString("");
   }

   public void toggle() {
      this.isListening = !this.isListening;
   }

   public void setString(String newString) {
      if (((StringValue)this.value).isTypingObserver() && !TextUtil.isNullOrEmpty(newString)) {
         ((StringValue)this.value).setValue(newString);
      }

      this.currentString = new StringComponent.CurrentString(newString);
   }

   public static String removeLastChar(String str) {
      String output = "";
      if (str != null && !str.isEmpty()) {
         output = str.substring(0, str.length() - 1);
      }

      return output;
   }

   
   public StringValue getStringValue() {
      return this.stringValue;
   }

   
   public boolean isListening() {
      return this.isListening;
   }

   
   public StringComponent.CurrentString getCurrentString() {
      return this.currentString;
   }

   
   public void setListening(boolean isListening) {
      this.isListening = isListening;
   }

   
   public void setCurrentString(StringComponent.CurrentString currentString) {
      this.currentString = currentString;
   }

   public static record CurrentString(String string) {
      public CurrentString(String string) {
         this.string = string;
      }

      public String string() {
         return this.string;
      }
   }
}
