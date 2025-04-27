package me.pollos.polloshook.impl.module.other.hud.elements.draggable.welcomer;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.gui.DrawContext;

public class Welcomer extends DraggableHUDModule {
   protected StringValue text = new StringValue("Hello <Player> :^)", new String[]{"Text", "txt", "welcomer", "message"});

   public Welcomer() {
      super(new String[]{"Welcomer", "welcome"});
      this.offerValues(new Value[]{this.text});
   }

   public void setDefaultPosition(DrawContext context) {
      int width = context.getScaledWindowWidth();
      String welcome = this.getWelcomeString();
      this.setTextX((float)((int)((float)width / 2.0F - this.getWidth(welcome) / 2.0F + 2.0F)));
      this.setTextY(2.0F);
      this.setTextHeight((float)Managers.getTextManager().getHeight());
      this.setTextWidth(this.getWidth(welcome));
   }

   public void draw(DrawContext context) {
      if (!PlayerUtil.isNull()) {
         String welcome = this.getWelcomeString();
         this.setTextHeight((float)Managers.getTextManager().getHeight());
         this.setTextWidth(this.getWidth(welcome));
         this.drawText(context, welcome, (int)this.getTextX(), (int)this.getTextY());
      }
   }

   private String getWelcomeString() {
      return mc.player == null ? "ChachooxGang" : ((String)this.text.getValue()).replaceAll("(?i)<player>", EntityUtil.getName(mc.player));
   }
}
