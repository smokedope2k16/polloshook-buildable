package me.pollos.polloshook.impl.module.render.extratab;

import java.awt.Color;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.render.tablist.TabNameEvent;
import me.pollos.polloshook.impl.manager.friend.Friend;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ListenerName extends ModuleListener<ExtraTab, TabNameEvent> {
   public ListenerName(ExtraTab module) {
      super(module, TabNameEvent.class);
   }

   public void call(TabNameEvent event) {
      String string = event.getString();
      String name = TextUtil.removeColor(string);
      boolean irc = Managers.getIrcManager().isClientUser(string);
      boolean appendChicken = (Boolean)((ExtraTab)this.module).irc.getValue() && irc;
      string = appendChicken ? "\ud83d\udc14" + string : string;
      if (string != null) {
         boolean self = this.containsName(mc.player.getName().getString(), string) && (Boolean)((ExtraTab)this.module).self.getValue();
         boolean friend = this.isFriend(name) && (Boolean)((ExtraTab)this.module).friends.getValue();
         if (friend) {
            this.handle(event, string, ((ExtraTab)this.module).getFriendColor());
            return;
         }

         if (self) {
            this.handle(event, string, ((ExtraTab)this.module).getSelfColor());
            return;
         }

         if ((Boolean)((ExtraTab)this.module).noColoredNames.getValue()) {
            this.handle(event, string, new Color(-1));
            return;
         }

         if (appendChicken) {
            event.setText(Text.empty().append("\ud83d\udc14").append(event.getString()));
            event.setCanceled(true);
         }
      }

   }

   private void handle(TabNameEvent event, String string, Color color) {
      MutableText appendText = Text.empty();
      Text name = Text.literal(TextUtil.removeColor(string));
      Text text = appendText.append(name).withColor(color.getRGB());
      event.setText(text);
      event.setCanceled(true);
   }

   private boolean isFriend(String name) {
      Iterator var2 = Managers.getFriendManager().getFriends().iterator();

      Friend friend;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         friend = (Friend)var2.next();
      } while(!this.containsName(name, friend.getLabel()) && !this.containsName(name, friend.getAlias()));

      return true;
   }

   private boolean containsName(String fullName, String string) {
      Matcher pattern = Pattern.compile("(?<!<)\\elementCodec" + Pattern.quote(fullName) + "\\elementCodec(?!>)").matcher(string);
      return pattern.find();
   }
}
