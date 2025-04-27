package me.pollos.polloshook.impl.events.misc;

import java.util.Comparator;

import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.network.PlayerListEntry;

public class SortTabEvent extends Event {
   Comparator<PlayerListEntry> comparator;

   
   public SortTabEvent(Comparator<PlayerListEntry> comparator) {
      this.comparator = comparator;
   }

   
   public Comparator<PlayerListEntry> getComparator() {
      return this.comparator;
   }

   
   public void setComparator(Comparator<PlayerListEntry> comparator) {
      this.comparator = comparator;
   }
}
