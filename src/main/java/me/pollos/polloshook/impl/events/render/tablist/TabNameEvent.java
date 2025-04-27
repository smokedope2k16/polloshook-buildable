package me.pollos.polloshook.impl.events.render.tablist;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.text.Text;

public class TabNameEvent extends Event {
   private final String string;
   private Text text;

   
   public String getString() {
      return this.string;
   }

   
   public Text getText() {
      return this.text;
   }

   
   public void setText(Text text) {
      this.text = text;
   }

   
   public TabNameEvent(String string) {
      this.string = string;
   }
}
