package me.pollos.polloshook.impl.module.render.extratab;

import java.util.Comparator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.misc.SortTabEvent;
import me.pollos.polloshook.impl.module.render.extratab.mode.SortingMode;
import net.minecraft.client.network.PlayerListEntry;

public class ListenerSort extends ModuleListener<ExtraTab, SortTabEvent> {
   public ListenerSort(ExtraTab module) {
      super(module, SortTabEvent.class);
   }

   public void call(SortTabEvent event) {
      Comparator var10000;
      switch((SortingMode)((ExtraTab)this.module).sorting.getValue()) {
      case VANILLA:
         var10000 = null;
         break;
      case PING:
         var10000 = Comparator.comparingLong(PlayerListEntry::getLatency);
         break;
      case FRIENDS:
         var10000 = (info1, info2) -> {
            return Boolean.compare(Managers.getFriendManager().isFriend((String) info2), Managers.getFriendManager().isFriend((String) info1));
         };
         break;
      case IRC:
         var10000 = (info1, info2) -> {
            return Boolean.compare(Managers.getIrcManager().isClientUser(((PlayerListEntry) info2).getProfile().getName()), Managers.getIrcManager().isClientUser(((PlayerListEntry) info1).getProfile().getName()));
         };
         break;
      case ABC:
         var10000 = Comparator.comparing((info) -> {
            return ((PlayerListEntry) info).getProfile().getName();
         }, String::compareToIgnoreCase);
         break;
      case LENGTH:
         var10000 = Comparator.comparingInt((info) -> {
            return ((PlayerListEntry) info).getProfile().getName().length();
         });
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      Comparator<PlayerListEntry> comparator = var10000;
      if (comparator != null) {
         event.setComparator(comparator);
         event.setCanceled(true);
      }

   }
}
