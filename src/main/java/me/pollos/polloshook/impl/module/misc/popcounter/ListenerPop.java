package me.pollos.polloshook.impl.module.misc.popcounter;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.entity.TotemPopEvent;
import me.pollos.polloshook.impl.manager.friend.Friend;
import me.pollos.polloshook.impl.module.misc.popcounter.mode.PopCounterStyle;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ListenerPop extends ModuleListener<PopCounter, TotemPopEvent> {
   public ListenerPop(PopCounter module) {
      super(module, TotemPopEvent.class);
   }

   public void call(TotemPopEvent event) {
      PlayerEntity entity = event.getPlayer();
      String name = entity.getName().getString();
      boolean isFriend = Managers.getFriendManager().isFriend(entity);
      boolean isSelf = entity == mc.player && (Boolean)((PopCounter)this.module).selfFriend.getValue();
      boolean isHidden = (Boolean)((PopCounter)this.module).hideSelf.getValue() && entity instanceof ClientPlayerEntity;
      int pops = (Integer)Managers.getPopManager().getPopMap().get(name);
      if (Managers.getFriendManager().isFriend(name)) {
         Friend fr = Managers.getFriendManager().getFriend(name);
         if (!fr.getAlias().equals(name)) {
            name = fr.getAlias();
         }
      }

      int color = !isFriend && !isSelf ? Manager.get().getThemeColor().getColor().getRGB() : Formatting.AQUA.getColorValue();
      MutableText formatName = Text.empty().append(Text.literal(isHidden ? "You" : name).formatted(new Formatting[]{Formatting.BOLD, Formatting.GRAY})).append(Text.literal(" ").formatted(Formatting.RESET));
      MutableText ordinalText;
      switch((PopCounterStyle)((PopCounter)this.module).getStyle().getValue()) {
      case CARDINAL:
         ordinalText = formatName.append(Text.literal(isHidden ? "popped " : "has popped ").withColor(color)).append(Text.literal(String.valueOf(pops)).formatted(new Formatting[]{Formatting.BOLD, Formatting.GRAY})).append(Text.literal(pops == 1 ? " totem" : " totems").withColor(color));
         ClientLogger.getLogger().log((Text)ordinalText, -entity.getId());
         break;
      case ORDINAL:
         ordinalText = formatName.append(Text.literal(isHidden ? "have popped your " : "has popped their ").withColor(color)).append(Text.literal(pops + TextUtil.toOrdinal(pops)).formatted(new Formatting[]{Formatting.BOLD, Formatting.GRAY})).append(Text.literal(" totem").withColor(color));
         ClientLogger.getLogger().log((Text)ordinalText, -entity.getId());
      }

   }
}