package me.pollos.polloshook.impl.module.other.font;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.other.font.util.FontArgument;
import me.pollos.polloshook.impl.module.other.font.util.FontStyle;

public class CustomFont extends CommandModule {
   private final StringValue fontName = new StringValue("Verdana", new String[]{"Name", "fontname"});
   private final EnumValue<FontStyle> fontStyle;
   private final NumberValue<Integer> fontSize;
   private final Value<Boolean> shadow;
   private final Value<Boolean> smoothFont;
   protected final List<String> fonts;

   public CustomFont() {
      super(new String[]{"Font", "cfont", "customfont"}, Category.OTHER, new String[]{"SetFont", "font", "putfont"}, new FontArgument("[font]"));
      this.fontStyle = new EnumValue(FontStyle.PLAIN, new String[]{"Style", "fontstyle"});
      this.fontSize = new NumberValue(18, 12, 24, new String[]{"Size", "fontsize"});
      this.shadow = new Value(true, new String[]{"Shadow", "shadows"});
      this.smoothFont = new Value(false, new String[]{"SmoothFont", "smooth", "w"});
      this.fonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
      this.offerValues(new Value[]{this.fontName, this.fontStyle, this.fontSize, this.shadow, this.smoothFont});
      this.registerObservers();
   }

   protected void onEnable() {
      this.setFont();
      Managers.getTextManager().setCustom(true);
   }

   protected void onDisable() {
      Managers.getTextManager().setCustom(false);
   }

   public String onCommand(String[] args) {
      if (args.length == 1) {
         return "nigga";
      } else {
         String args1 = args[1].replace("_", " ");
         Iterator var3 = this.getFonts().iterator();

         String str;
         do {
            if (!var3.hasNext()) {
               return "No font with name %s".formatted(new Object[]{args1});
            }

            str = (String)var3.next();
         } while(!args1.equalsIgnoreCase(str));

         this.fontName.setValue(str);
         this.setFont();
         return "Set font to %s".formatted(new Object[]{args1});
      }
   }

   private void registerObservers() {
      Iterator var1 = this.getValues().iterator();

      while(var1.hasNext()) {
         Value<?> value = (Value)var1.next();
         if (!value.equals(this.shadow)) {
            value.addObserver((e) -> {
               if (!e.getValue().equals(e.getSetting().getValue())) {
                  PollosHookThread.SCHEDULED_EXECUTOR.schedule(this::setFont, 50L, TimeUnit.MILLISECONDS);
               }

            });
         }
      }

   }

   private void setFont() {
      Managers.getTextManager().set((String)this.fontName.getValue(), ((FontStyle)this.fontStyle.getValue()).getFontStyle(), (float)((Integer)this.fontSize.getValue() - 9));
   }

   public float getShadowLength() {
      return (Boolean)this.shadow.getValue() ? 1.0F : 0.4F;
   }

   
   public Value<Boolean> getSmoothFont() {
      return this.smoothFont;
   }

   
   public List<String> getFonts() {
      return this.fonts;
   }
}
