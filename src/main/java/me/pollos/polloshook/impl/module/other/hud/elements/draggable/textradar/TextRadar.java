package me.pollos.polloshook.impl.module.other.hud.elements.draggable.textradar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.textradar.util.TextRadarEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public class TextRadar extends DraggableHUDModule {
   protected final Value<Boolean> health = new Value(true, new String[]{"Health", "hp", "<3"});
   protected final Value<Boolean> distance = new Value(true, new String[]{"Distance", "dist", "reach"});
   protected final Value<Boolean> pops = new Value(true, new String[]{"Pops", "totempops"});

   public TextRadar() {
      super(new String[]{"TextRadar", "radar"});
      this.offerValues(new Value[]{this.health, this.distance, this.pops});
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX(2.0F);
      this.setTextY(50.0F);
      this.setTextHeight(10.0F);
      this.setTextWidth(10.0F);
   }

   public void draw(DrawContext context) {
      List<TextRadarEntry> entries = new ArrayList();
      Iterator var3 = mc.world.getPlayers().iterator();

      while(var3.hasNext()) {
         PlayerEntity player = (PlayerEntity)var3.next();
         if (player != null && player != mc.player && !EntityUtil.isDead(player)) {
            entries.add(TextRadarEntry.of(player));
         }
      }

      if (!entries.isEmpty()) {
         int offset = 10;
         int longest = 10;

         for(Iterator var5 = entries.iterator(); var5.hasNext(); offset += 10) {
            TextRadarEntry e = (TextRadarEntry)var5.next();
            String build = e.build((Boolean)this.distance.getValue(), (Boolean)this.health.getValue(), (Boolean)this.pops.getValue());
            this.drawText(context, build, (int)this.getTextX(), (int)(this.getTextY() + (float)offset));
            longest = (int)Math.max((float)longest, this.getWidth(build));
         }

         this.setTextWidth((float)longest);
         this.setTextHeight((float)offset);
      }
   }
}
