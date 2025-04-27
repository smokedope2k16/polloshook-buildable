package me.pollos.polloshook.impl.module.misc.deathcoordslog;

import java.awt.Color;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import me.pollos.polloshook.impl.module.misc.deathcoordslog.util.DeathWaypoint;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;

public class ListenerScreen extends SafeModuleListener<DeathCoordsLog, ScreenEvent> {
   public ListenerScreen(DeathCoordsLog module) {
      super(module, ScreenEvent.class);
   }

   public void safeCall(ScreenEvent event) {
      if (event.getScreen() instanceof DeathScreen) {
         String deathPosition = mc.player.getBlockPos().toShortString();
         MutableText mut = Text.empty();
         ClickEvent clickEvent = new ClickEvent(Action.COPY_TO_CLIPBOARD, deathPosition);
         HoverEvent hoverEvent = new HoverEvent(net.minecraft.text.HoverEvent.Action.SHOW_TEXT, Text.literal(String.valueOf(Formatting.GRAY) + "Click to copy to clipboard"));
         MutableText deathText = Text.literal(" You died at " + deathPosition).setStyle(Style.EMPTY.withColor((new Color(TextColor.fromFormatting(Formatting.RED).getRgb())).getRGB()).withClickEvent(clickEvent).withHoverEvent(hoverEvent));
         mut.append(deathText);
         TextUtil.printWithID(ClientLogger.getLogger().getAlert().copy().append(mut), -1);
         ((DeathCoordsLog)this.module).waypointList.add(new DeathWaypoint(mc.player.getPos(), System.currentTimeMillis(), mc.world.getDimension()));
      }

   }
}