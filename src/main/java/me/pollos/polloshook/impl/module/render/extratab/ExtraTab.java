package me.pollos.polloshook.impl.module.render.extratab;

import java.awt.Color;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.extratab.mode.SortingMode;

public class ExtraTab extends ToggleableModule {
   protected final Value<Boolean> self = new Value(false, new String[]{"Self", "selffriend"});
   protected final Value<Boolean> selfRainbow;
   protected final ColorValue selfColor;
   protected final Value<Boolean> friends;
   protected final Value<Boolean> friendRainbow;
   protected final ColorValue friendColor;
   protected final EnumValue<SortingMode> sorting;
   protected final Value<Boolean> noColoredNames;
   protected final Value<Boolean> clear;
   protected final Value<Boolean> noFooter;
   protected final Value<Boolean> noHeader;
   protected final Value<Boolean> irc;

   public ExtraTab() {
      super(new String[]{"ExtraTab", "tabtweaks", "moretab"}, Category.RENDER);
      this.selfRainbow = (new Value(true, new String[]{"SelfRainbow", "selfrainbow"})).setParent(this.self);
      this.selfColor = (new ColorValue(new Color(-1), true, new String[]{"SelfColor", "selfcolor"})).setParent(this.selfRainbow, true);
      this.friends = new Value(true, new String[]{"HighlightFriends", "friends"});
      this.friendRainbow = (new Value(true, new String[]{"FriendRainbow", "rainbowfriends"})).setParent(this.friends);
      this.friendColor = (new ColorValue(new Color(-1), true, new String[]{"FriendColor", "friendcolour", "color"})).setParent(this.friendRainbow, true);
      this.sorting = new EnumValue(SortingMode.VANILLA, new String[]{"Sorting", "sort", "s"});
      this.noColoredNames = new Value(false, new String[]{"NoColoredNames", "nocolornames"});
      this.clear = new Value(false, new String[]{"Clear", "c"});
      this.noFooter = new Value(false, new String[]{"NoFooter", "footer"});
      this.noHeader = new Value(false, new String[]{"NoHeader", "nohead"});
      this.irc = (new Value(false, new String[]{"IrcLogo", "irc"})).setParent(() -> {
         return Managers.getIrcManager().isConnected();
      });
      this.offerValues(new Value[]{this.self, this.selfRainbow, this.selfColor, this.friends, this.friendRainbow, this.friendColor, this.noColoredNames, this.clear, this.noFooter, this.noHeader, this.irc});
      this.offerListeners(new Listener[]{new ListenerLimit(this), new ListenerRenderElement(this), new ListenerName(this), new ListenerSort(this)});
   }

   protected Color getSelfColor() {
      return (Boolean)this.selfRainbow.getValue() ? Colours.get().getRainbow(0) : this.selfColor.getColor();
   }

   protected Color getFriendColor() {
      return (Boolean)this.friendRainbow.getValue() ? Colours.get().getRainbow(0) : this.friendColor.getColor();
   }

   public static class RenderTabElementEvent extends Event {
      private final ExtraTab.RenderTabElementEvent.Element element;

      
      public ExtraTab.RenderTabElementEvent.Element getElement() {
         return this.element;
      }

      
      private RenderTabElementEvent(ExtraTab.RenderTabElementEvent.Element element) {
         this.element = element;
      }

      
      public static ExtraTab.RenderTabElementEvent create(ExtraTab.RenderTabElementEvent.Element element) {
         return new ExtraTab.RenderTabElementEvent(element);
      }

      public static enum Element {
         FOOTER,
         HEADER,
         BACKGROUND;

         // $FF: synthetic method
         private static ExtraTab.RenderTabElementEvent.Element[] $values() {
            return new ExtraTab.RenderTabElementEvent.Element[]{FOOTER, HEADER, BACKGROUND};
         }
      }
   }
}
