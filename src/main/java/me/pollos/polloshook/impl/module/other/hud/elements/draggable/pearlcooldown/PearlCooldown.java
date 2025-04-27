package me.pollos.polloshook.impl.module.other.hud.elements.draggable.pearlcooldown;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.gui.editor.core.PollosHUD;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.pearlcooldown.mode.CooldownMode;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PearlCooldown extends DraggableHUDModule {
   protected final EnumValue<CooldownMode> counting;
   protected final NumberValue<Float> cooldown;
   protected final Value<Boolean> setItemCooldown;
   protected final Value<Boolean> customColor;
   protected final Value<Boolean> displayOthers;
   protected final Value<Boolean> displayFriends;
   protected final Map<PlayerEntity, Long> otherPlayers;
   protected final StopWatch timer;

   public PearlCooldown() {
      super(new String[]{"PearlCooldown", "pearldelay"});
      this.counting = new EnumValue(CooldownMode.COUNT_UP, new String[]{"Counting", "count"});
      this.cooldown = (new NumberValue(15.0F, 1.0F, 30.0F, 0.1F, new String[]{"Cooldown", "down", "syndrome"})).withTag("second");
      this.setItemCooldown = new Value(false, new String[]{"SetItemCooldown", "setcooldown"});
      this.customColor = new Value(false, new String[]{"CustomColor", "customc"});
      this.displayOthers = new Value(false, new String[]{"DisplayOthers", "others"});
      this.displayFriends = (new Value(false, new String[]{"DisplayFriends", "friends"})).setParent(this.displayOthers);
      this.otherPlayers = new HashMap();
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.counting, this.cooldown, this.setItemCooldown, this.customColor, this.displayOthers, this.displayFriends});
      this.offerListeners(new Listener[]{new ListenerCooldown(this), new ListenerPearl(this)});
      this.getSetPos().setValue(false);
      this.getValues().remove(this.getSetPos());
   }

   public void draw(DrawContext context) {
      boolean hud = mc.currentScreen instanceof PollosHUD;
      int y = (int)this.getTextY();
      List<Integer> widths = new ArrayList();
      if (!this.timer.passed((double)((Float)this.cooldown.getValue() * 1000.0F)) || hud) {
         boolean isDown = this.counting.getValue() == CooldownMode.COUNT_DOWN;
         widths.add((int)this.getWidth(this.getString(isDown, this.timer.getTime(), mc.player)));
         this.renderTime(context, mc.player, this.timer.getTime(), y);
      }

      int offset = 10;
      if ((Boolean)this.displayOthers.getValue() && !this.otherPlayers.isEmpty()) {
         Map<PlayerEntity, Long> badEntries = new HashMap();
         Iterator var7 = this.otherPlayers.entrySet().iterator();

         while(true) {
            while(var7.hasNext()) {
               Entry<PlayerEntity, Long> entry = (Entry)var7.next();
               PlayerEntity player = (PlayerEntity)entry.getKey();
               Long time = (Long)entry.getValue();
               if (!((float)(System.currentTimeMillis() - time) >= (Float)this.cooldown.getValue() * 1000.0F) && ((Boolean)this.displayFriends.getValue() || !Managers.getFriendManager().isFriend(player))) {
                  boolean isDown = this.counting.getValue() == CooldownMode.COUNT_DOWN;
                  widths.add((int)this.getWidth(this.getString(isDown, time, player)));
                  this.renderTime(context, player, System.currentTimeMillis() - time, y + offset);
                  offset += 10;
               } else {
                  badEntries.put(player, time);
               }
            }

            this.otherPlayers.entrySet().removeIf((entryx) -> {
               return badEntries.containsKey(entryx.getKey());
            });
            break;
         }
      }

      int width = widths.isEmpty() ? 40 : (Integer)Collections.max(widths);
      this.setTextWidth((float)width);
      this.setTextHeight((float)offset);
   }

   private void renderTime(DrawContext context, PlayerEntity player, long time, int y) {
      boolean isDown = this.counting.getValue() == CooldownMode.COUNT_DOWN;
      int FINAL_COLOR = (new Color(1.0F, 0.4117647F, 0.7058824F)).getRGB();
      this.drawText(context, this.getString(isDown, time, player), (int)this.getTextX(), y, FINAL_COLOR, !(Boolean)this.customColor.getValue());
   }

   private String down(float time) {
      float remainingTime = ((Float)this.cooldown.getValue() * 1000.0F - time) / 1000.0F;
      return "%.1fs".formatted(new Object[]{remainingTime});
   }

   private String up(float time) {
      float elapsedTime = time / 1000.0F;
      return "%.1fs".formatted(new Object[]{elapsedTime});
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX(100.0F);
      this.setTextY(22.0F);
      this.setTextHeight(20.0F);
      this.setTextWidth(20.0F);
   }

   private String getString(boolean isDown, long time, PlayerEntity player) {
      String timeText = isDown ? this.down((float)time) : this.up((float)time);
      String selfText = "Ender Pearl cooldown %s (%s)".formatted(new Object[]{isDown ? "expires in" : "for", timeText});
      String otherText = "%s's %s Ender Pearl cooldown %s (%s)".formatted(new Object[]{player.getName().getString(), isDown ? "" : "has", isDown ? "expires in" : "for", timeText});
      return player instanceof ClientPlayerEntity ? selfText : otherText;
   }
}
