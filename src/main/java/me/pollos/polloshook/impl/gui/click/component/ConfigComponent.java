package me.pollos.polloshook.impl.gui.click.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.util.thread.FileUtil;
import me.pollos.polloshook.impl.config.modules.ModuleConfig;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class ConfigComponent extends Component {
   private final ModuleConfig moduleConfig;

   public ConfigComponent(ModuleConfig moduleConfig, String label, float posX, float posY, float offsetX, float offsetY, float width, float height) {
      super(label, posX, posY, offsetX, offsetY, width, height);
      this.moduleConfig = moduleConfig;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
      super.render(context, mouseX, mouseY, partialTicks);
      boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, (double)this.getFinishedX(), (double)this.getFinishedY(), (double)this.getWidth(), (double)this.getHeight());
      boolean enabled;
      if (PollosHook.getCurrentConfig() == null) {
         enabled = false;
      } else {
         enabled = this.moduleConfig.getLabel().replace(".cfg", "").equalsIgnoreCase(PollosHook.getCurrentConfig().getLabel().replace(".cfg", ""));
      }

      Rectangle moduleRectangle = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 0.5F, this.getFinishedX() + this.getWidth() - 1.0F, this.getFinishedY() + this.getHeight() - 0.5F);
      if (hovered && !enabled) {
         Render2DMethods.drawRect(context, moduleRectangle, 1714631475);
      }

      if ((Boolean)ClickGUI.get().getFutureBox().getValue()) {
         Render2DMethods.drawGradientRect(context, moduleRectangle, false, 553648127, 285212671);
      }

      Color color = hovered ? this.getColor().darker() : this.getColor();
      if (enabled) {
         this.moduleConfig.updateLastLoaded();
         Render2DMethods.drawRect(context, moduleRectangle, color.getRGB());
      }

      String fixedLabel = this.getLabel().replace(".cfg", "");
      fixedLabel = fixedLabel.isEmpty() ? fixedLabel : Character.toUpperCase(fixedLabel.charAt(0)) + fixedLabel.substring(1);
      Managers.getTextManager().drawString(context, fixedLabel, (double)((int)(this.getFinishedX() + 4.0F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), enabled ? -1 : -5592406);
      if (hovered) {
         this.renderConfigInfo(context, mouseX, mouseY);
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)this.getFinishedX(), (double)this.getFinishedY(), (double)this.getWidth(), (double)this.getHeight());
      if (hovered && button == 0) {
         if (PollosHook.getCurrentConfig() != null) {
            PollosHook.getCurrentConfig().updateLastLoaded();
            PollosHook.getCurrentConfig().save();
         }

         this.moduleConfig.updateLastLoaded();
         this.moduleConfig.load();
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   private void renderConfigInfo(DrawContext context, int mouseX, int mouseY) {
      FileReader reader = FileUtil.createReader(this.moduleConfig.getFile());
      JsonParser parser = new JsonParser();
      JsonArray array = null;

      try {
         array = (JsonArray)parser.parse(reader);
      } catch (ClassCastException var17) {
         this.moduleConfig.save();
      }

      String time = null;
      JsonObject obj = array.get(0).getAsJsonObject();
      JsonObject creationInfo = obj.getAsJsonObject("creation-info");
      if (this.getModuleConfig().getLastUsedFromFile() != null) {
         time = this.getModuleConfig().getFormatter().format(this.getModuleConfig().getLastUsedFromFile());
      }

      String author = creationInfo.get("author").getAsString();
      if (time != null || author != null) {
         if (time == null) {
            time = "Unknown";
         } else if (author == null) {
            author = "Unknown";
         }

         try {
            LocalDateTime lastUsedTime = LocalDateTime.parse(time, this.getModuleConfig().getFormatter());
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(lastUsedTime, now);
            time = this.formatDuration(duration);
         } catch (Exception var16) {
            time = "Unknown";
         }

         String authorText = "Author: %s".formatted(new Object[]{author});
         String lastSavedText = "Last Used: %s".formatted(new Object[]{time});
         float textWidth = Math.max(Managers.getTextManager().getWidth(authorText), Managers.getTextManager().getWidth(lastSavedText));
         MatrixStack matrix = context.getMatrices();
         matrix.push();
         matrix.translate(0.0F, 0.0F, 1.0F);
         Rectangle rect = new Rectangle((float)(mouseX + 14), (float)(mouseY - 12), (float)(mouseX + 21) + textWidth, (float)(mouseY + 12));
         Render2DMethods.drawBorderedRect(context, rect, 1.0F, -2146562546, this.getColor(255).getRGB());
         Managers.getTextManager().drawString((DrawContext)context, authorText, (double)(mouseX + 18), (double)mouseY, -1);
         Managers.getTextManager().drawString((DrawContext)context, lastSavedText, (double)(mouseX + 18), (double)(mouseY - 10), -1);
         matrix.translate(0.0F, 0.0F, -1.0F);
         matrix.pop();
      }
   }

   private String formatDuration(Duration duration) {
      long days = duration.toDays();
      duration = duration.minusDays(days);
      long hours = duration.toHours();
      duration = duration.minusHours(hours);
      long minutes = duration.toMinutes();
      long months = days / 30L;
      days %= 30L;
      long weeks = days / 7L;
      days %= 7L;
      if (months > 0L) {
         return months == 1L ? "keyCodec month ago" : months + " months ago";
      } else if (weeks > 0L) {
         return weeks == 1L ? "last week" : weeks + " weeks ago";
      } else if (days > 0L) {
         return days == 1L ? "yesterday" : days + " days ago";
      } else if (hours > 0L) {
         return hours == 1L ? "an hour ago" : hours + " hours ago";
      } else if (minutes > 0L) {
         return minutes == 1L ? "keyCodec minute ago" : minutes + " minutes ago";
      } else {
         return "just now";
      }
   }

   
   public ModuleConfig getModuleConfig() {
      return this.moduleConfig;
   }
}
