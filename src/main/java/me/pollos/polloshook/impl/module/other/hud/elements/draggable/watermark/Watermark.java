package me.pollos.polloshook.impl.module.other.hud.elements.draggable.watermark;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class Watermark extends DraggableHUDModule {
   private final Value<Boolean> customMark = new Value(false, new String[]{"CustomMark", "customwatermark"});
   private final StringValue mark;
   private final Value<Boolean> version;
   private final Value<Boolean> whiteVersion;
   private final Value<Boolean> sha;
   private final Value<Boolean> date;
   private final Value<Boolean> gray;

   public Watermark() {
      super(new String[]{"Watermark", "logo"});
      this.mark = (new StringValue("machara.mx", new String[]{"Mark", "watermark", "text"})).setParent(this.customMark);
      this.version = new Value(true, new String[]{"Version", "v", "ver"});
      this.whiteVersion = (new Value(false, new String[]{"White", "whiteversion"})).setParent(this.version);
      this.sha = (new Value(false, new String[]{"GitSHA", "githubsha", "hash"})).setParent(this.version);
      this.date = new Value(false, new String[]{"Date", "today", "thisminute"});
      this.gray = (new Value(false, new String[]{"GrayDate", "gray"})).setParent(this.date);
      this.offerValues(new Value[]{this.customMark, this.mark, this.version, this.whiteVersion, this.sha, this.date, this.gray});
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX(2.0F);
      this.setTextY(2.0F);
      this.setTextHeight((float)Managers.getTextManager().getHeight());
      this.setTextWidth(this.getWidth(this.getFullString()));
   }

   public void draw(DrawContext context) {
      this.setTextHeight((float)Managers.getTextManager().getHeight());
      this.setTextWidth(this.getWidth(this.getFullString()));
      this.drawText(context, this.getFullString(), (int)this.getTextX(), (int)this.getTextY());
   }

   private String getFullString() {
      String watermark = (Boolean)this.customMark.getValue() ? (String)this.mark.getValue() : "polloshook";
      String var10000 = this.getVersion();
      String versionString = var10000 + this.getSHA() + this.getDate();
      return watermark + versionString;
   }

   private String getVersion() {
      if (!(Boolean)this.version.getValue()) {
         return "";
      } else {
         return (Boolean)this.whiteVersion.getValue() ? " " + String.valueOf(Formatting.GRAY) + "v2.8.5" : " v2.8.5";
      }
   }

   private String getDate() {
      if (!(Boolean)this.date.getValue()) {
         return "";
      } else {
         LocalDateTime now = LocalDateTime.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
         String var10000;
         if ((Boolean)this.gray.getValue()) {
            var10000 = String.valueOf(Formatting.BOLD);
            return " " + var10000 + now.format(formatter);
         } else {
            var10000 = now.format(formatter);
            return " " + var10000;
         }
      }
   }

   private String getSHA() {
      if ((Boolean)this.sha.getValue() && (Boolean)this.version.getValue()) {
         if (!"421997cc5c6d96ff46cd700602c8565d0de2ca04".equalsIgnoreCase("UNKNOWN") && !"2025-01-30T16:09:52Z".equalsIgnoreCase("UNKNOWN")) {
            String sha = "421997cc5c6d96ff46cd700602c8565d0de2ca04".substring(0, 10);
            return String.format("+%s.%s", this.Xuancheng__CNPvP_500PING(), sha);
         } else {
            return "-beta";
         }
      } else {
         return "";
      }
   }

   private String Xuancheng__CNPvP_500PING() {
      int number = 11;
  
      try {
          String GIT_DATE = "2025-01-30T16:08:53Z".replace("Z", "").replace("T", "");
          String BUILD_DATE = "2025-01-30T16:09:52Z".replace("Z", "").replace("T", "");
          String s = GIT_DATE.substring(GIT_DATE.length() - 2);
          String s1 = GIT_DATE.substring(BUILD_DATE.length() - 2);
          int i = Integer.parseInt(s);
          int i2 = Integer.parseInt(s1);
          number = (int)((double)(i + i2) * 1.48D);
      } catch (Exception var8) {
          var8.printStackTrace();
      }
  
      return "" + number;
  }
}
