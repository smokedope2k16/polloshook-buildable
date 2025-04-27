package me.pollos.polloshook.impl.module.other.hud.elements.draggable.clientmessages;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.obj.timedmessage.TimedMessage;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class ClientMessages extends DraggableHUDModule {
   protected final NumberValue<Integer> duration = (new NumberValue(750, 500, 3500, new String[]{"Duration", "dura"})).withTag("ms");
   protected final Value<Boolean> prefix = new Value(false, new String[]{"Prefix", "pre"});
   protected final Value<Boolean> lagbacks = new Value(false, new String[]{"Lagbacks", "lag"});
   protected final Value<Boolean> totemPop = new Value(true, new String[]{"PopCounter", "totems"});
   protected final Value<Boolean> modules = new Value(false, new String[]{"Modules", "mods"});
   protected final ColorValue color = new ColorValue(new Color(-1), false, new String[]{"TextColor", "text", "color"});
   protected final Collection<TimedMessage> messages = new ArrayList();

   public ClientMessages() {
      super(new String[]{"ClientMessages", "clientdebug"});
      this.offerValues(new Value[]{this.duration, this.prefix, this.lagbacks, this.totemPop, this.modules, this.color});
      this.offerListeners(new Listener[]{new ListenerPosLook(this), new ListenerPop(this), new ListenerDeath(this)});
   }

   public void draw(DrawContext context) {
      int offset = 0;
      List<TimedMessage> toRemove = new ArrayList();
      List<TimedMessage> renderMessages = new ArrayList(this.messages);
      List<Integer> widths = new ArrayList();
      Iterator var6 = renderMessages.iterator();

      while(var6.hasNext()) {
         TimedMessage timed = (TimedMessage)var6.next();
         String str = timed.getMessage();
         long time = timed.getDelay();
         long elapsed = System.currentTimeMillis() - time;
         if (elapsed >= (long)(Integer)this.duration.getValue() - 25L) {
            toRemove.add(timed);
         } else {
            int fadeCount = (int)(((long)(Integer)this.duration.getValue() - elapsed) / 10L);
            int alpha;
            if (elapsed < 100L) {
               alpha = (int)(255.0D * ((double)elapsed / 100.0D));
            } else {
               alpha = (int)((float)Math.max(0, Math.min(fadeCount, 10)) * 25.5F);
            }

            alpha = ColorUtil.fixColor(alpha);
            Managers.getTextManager().drawString(context, this.format(str), (double)this.getTextX(), (double)(this.getTextY() + (float)offset), ColorUtil.changeAlpha(this.color.getColor(), alpha).getRGB());
            offset += 10;
            widths.add((int)this.getWidth(this.format(str)));
         }
      }

      this.setTextWidth(widths.isEmpty() ? 25.0F : (float)Collections.max(widths));
      this.setTextHeight((float)offset);
      this.messages.removeAll(toRemove);
   }

   public void displayMessage(String text) {
      this.messages.add(TimedMessage.of(System.currentTimeMillis(), text));
   }

   public void displayModule(String str) {
      if ((Boolean)this.modules.getValue()) {
         this.displayMessage(str);
      }

   }

   protected String format(String str) {
      String s = (Boolean)this.prefix.getValue() ? String.valueOf(Formatting.RESET) + Manager.get().getClientNameStr() + " " : "";
      return s + str;
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX(2.0F);
      this.setTextY(12.0F);
      this.setTextWidth(50.0F);
      this.setTextHeight(50.0F);
   }

   
   public NumberValue<Integer> getDuration() {
      return this.duration;
   }

   
   public Value<Boolean> getPrefix() {
      return this.prefix;
   }

   
   public Value<Boolean> getLagbacks() {
      return this.lagbacks;
   }

   
   public Value<Boolean> getTotemPop() {
      return this.totemPop;
   }

   
   public Value<Boolean> getModules() {
      return this.modules;
   }

   
   public ColorValue getColor() {
      return this.color;
   }

   
   public Collection<TimedMessage> getMessages() {
      return this.messages;
   }
}