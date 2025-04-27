package me.pollos.polloshook.impl.module.render.shulkerpreview;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.utils.Dots;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.text.TextUtil;
import net.minecraft.client.gui.DrawContext;

public class ListenerRender extends ModuleListener<ShulkerPreview, ShulkerPreview.RenderHandledScreenEvent> {
   protected final StopWatch timer = new StopWatch();

   public ListenerRender(ShulkerPreview module) {
      super(module, ShulkerPreview.RenderHandledScreenEvent.class);
   }

   public void call(ShulkerPreview.RenderHandledScreenEvent event) {
      if ((Boolean)((ShulkerPreview)this.module).search.getValue()) {
         this.handleSearch();
         DrawContext context = event.getContext();
         String text = ((ShulkerPreview)this.module).searching ? (TextUtil.isNullOrEmpty(((ShulkerPreview)this.module).searchTarget) ? Dots.get3Dots() : ((ShulkerPreview)this.module).searchTarget) : "Press CTRL + F To toggle search";
         if (((ShulkerPreview)this.module).paused) {
            text = text + " (Paused)";
         }

         float textWidth = Managers.getTextManager().getWidth(text);
         Managers.getTextManager().drawString(context, text, (double)(((float)context.getScaledWindowWidth() - textWidth) / 2.0F), (double)((float)context.getScaledWindowHeight() / 2.0F - 110.0F), 16777215);
      }

   }

   protected void handleSearch() {
      if (KeyboardUtil.isCTRL() && KeyboardUtil.isPressed(70) && this.timer.passed(250L)) {
         ((ShulkerPreview)this.module).searching = !((ShulkerPreview)this.module).searching;
         ((ShulkerPreview)this.module).paused = false;
         ((ShulkerPreview)this.module).searchTarget = "";
         this.timer.reset();
      }

   }
}
