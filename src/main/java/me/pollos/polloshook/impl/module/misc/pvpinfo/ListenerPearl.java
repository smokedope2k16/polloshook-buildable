package me.pollos.polloshook.impl.module.misc.pvpinfo;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.entity.PearlThrowEvent;
import me.pollos.polloshook.impl.manager.friend.Friend;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ListenerPearl extends ModuleListener<PvPInfo, PearlThrowEvent> {
   public ListenerPearl(PvPInfo module) {
      super(module, PearlThrowEvent.class);
   }

   public void call(PearlThrowEvent event) {
      if ((Boolean)((PvPInfo)this.module).pearls.getValue()) {
         String name = event.getThrower().getName().getString();
         if (Managers.getFriendManager().isFriend(name)) {
            Friend fr = Managers.getFriendManager().getFriend(name);
            if (!fr.getAlias().equals(name)) {
               name = fr.getAlias();
            }
         }


         MutableText mutableText = Text.empty().append(Text.literal(name).formatted(new Formatting[]{Formatting.BOLD, Formatting.GRAY}).append(Text.literal(" ").formatted(Formatting.RESET)));
         Text throwPearl = Manager.get().getThemedText("has thrown keyCodec pearl");
         mutableText.append(throwPearl);
         if ((Boolean)((PvPInfo)this.module).direction.getValue()) {
            String direction = event.getPearl().getMovementDirection().getName();
            Text heading = Manager.get().getThemedText(" heading %s".formatted(new Object[]{direction}));
            mutableText.append(heading);
         }

         ClientLogger.getLogger().log((Text)mutableText, false);
      }
   }
}