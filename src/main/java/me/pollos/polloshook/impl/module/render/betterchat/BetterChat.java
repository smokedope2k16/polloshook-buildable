package me.pollos.polloshook.impl.module.render.betterchat;

import java.awt.Color;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.other.manager.util.ClientBrackets;
import me.pollos.polloshook.impl.module.render.betterchat.mode.Language;

public class BetterChat extends ToggleableModule {
   protected final Value<Boolean> portals = new Value(true, new String[]{"Portals", "portalchat", "port"});
   protected final Value<Boolean> antiClear = new Value(false, new String[]{"AntiClear", "noclear", "serverclear"});
   protected final Value<Boolean> antiScroll = new Value(false, new String[]{"AntiScroll", "noscroll", "scroll"});
   protected final Value<Boolean> infinite = new Value(true, new String[]{"Infinite", "inf", "infinity"});
   protected final Value<Boolean> nameHighlight = new Value(false, new String[]{"NameHighlight", "highlight"});
   protected final Value<Boolean> sound;
   protected final Value<Boolean> colored;
   protected final ColorValue color;
   protected final Value<Boolean> copy;
   protected final Value<Boolean> translate;
   protected final EnumValue<Language> targetLanguage;
   protected final Value<Boolean> clearBackground;
   protected final Value<Boolean> clearTextBox;
   protected final Value<Boolean> noIndicator;
   protected final NumberValue<Integer> offset;
   protected final Value<Boolean> timeStamps;
   protected final EnumValue<ClientBrackets> bracket;
   protected final ColorValue bracketColor;
   protected final ColorValue timeColor;

   public BetterChat() {
      super(new String[]{"BetterChat", "chat", "chattweaks", "chattimestamps"}, Category.RENDER);
      this.sound = (new Value(false, new String[]{"Sound", "ding"})).setParent(this.nameHighlight);
      this.colored = (new Value(false, new String[]{"Colored", "colorname"})).setParent(this.nameHighlight);
      this.color = (new ColorValue(new Color(0, 255, 255), true, new String[]{"Color", "c"})).setParent(this.colored);
      this.copy = new Value(false, new String[]{"CopyOnClick", "copy"});
      this.translate = new Value(false, new String[]{"TranslateOnClick", "translate"});
      this.targetLanguage = (new EnumValue(Language.ENGLISH, new String[]{"TargetLanguage", "language", "lang", "targ"})).setParent(this.translate);
      this.clearBackground = new Value(false, new String[]{"ClearBackground", "clearbg", "background"});
      this.clearTextBox = new Value(false, new String[]{"ClearTextBox", "clearchatbox", "clear"});
      this.noIndicator = new Value(false, new String[]{"NoIndicator", "removeindicator"});
      this.offset = (new NumberValue(-2, -3, 3, new String[]{"Offset", "off", "offx", "x"})).setParent(this.noIndicator);
      this.timeStamps = new Value(true, new String[]{"Timestamps", "time"});
      this.bracket = (new EnumValue(ClientBrackets.BRACKET, new String[]{"Brackets", "bracket"})).setParent(this.timeStamps);
      this.bracketColor = (new ColorValue(new Color(11184810), false, new String[]{"BracketColor", "bracketcolour"})).setParent(this.timeStamps);
      this.timeColor = (new ColorValue(new Color(16777215), false, new String[]{"TimeColor", "timecolour"})).setParent(this.timeStamps);
      this.offerValues(new Value[]{this.portals, this.antiClear, this.antiScroll, this.infinite, this.nameHighlight, this.sound, this.colored, this.color, this.copy, this.translate, this.targetLanguage, this.clearBackground, this.clearTextBox, this.noIndicator, this.offset, this.timeStamps, this.bracket, this.bracketColor, this.timeColor});
      this.offerListeners(new Listener[]{new ListenerChat(this), new ListenerChatMouseClick(this)});
   }

   public boolean getHighlight() {
      return this.isEnabled() && (Boolean)this.nameHighlight.getValue();
   }

   public boolean getSound() {
      return this.getHighlight() && (Boolean)this.sound.getValue();
   }

   public Color getColor() {
      return this.color.getColor();
   }

   public boolean getInfinite() {
      return this.isEnabled() && (Boolean)this.infinite.getValue();
   }

   public boolean getNoIndicator() {
      return this.isEnabled() && (Boolean)this.noIndicator.getValue();
   }

   public boolean getClearTextBox() {
      return this.isEnabled() && (Boolean)this.clearTextBox.getValue();
   }

   
   public Value<Boolean> getPortals() {
      return this.portals;
   }

   
   public Value<Boolean> getAntiClear() {
      return this.antiClear;
   }

   
   public Value<Boolean> getAntiScroll() {
      return this.antiScroll;
   }

   
   public Value<Boolean> getNameHighlight() {
      return this.nameHighlight;
   }

   
   public Value<Boolean> getColored() {
      return this.colored;
   }

   
   public Value<Boolean> getCopy() {
      return this.copy;
   }

   
   public Value<Boolean> getTranslate() {
      return this.translate;
   }

   
   public EnumValue<Language> getTargetLanguage() {
      return this.targetLanguage;
   }

   
   public Value<Boolean> getClearBackground() {
      return this.clearBackground;
   }

   
   public NumberValue<Integer> getOffset() {
      return this.offset;
   }

   
   public Value<Boolean> getTimeStamps() {
      return this.timeStamps;
   }

   
   public EnumValue<ClientBrackets> getBracket() {
      return this.bracket;
   }

   
   public ColorValue getBracketColor() {
      return this.bracketColor;
   }

   
   public ColorValue getTimeColor() {
      return this.timeColor;
   }
}
