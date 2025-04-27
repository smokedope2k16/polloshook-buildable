package me.pollos.polloshook.impl.module.misc.popcounter;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.entity.DeathEvent;
import me.pollos.polloshook.impl.manager.friend.Friend;
import me.pollos.polloshook.impl.module.misc.popcounter.mode.PopCounterStyle;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ListenerDeath extends ModuleListener<PopCounter, DeathEvent> {
   public ListenerDeath(PopCounter module) {
      super(module, DeathEvent.class, 100);
   }

   public void call(DeathEvent event) {
      LivingEntity player = event.getEntity();
      if (player instanceof PlayerEntity) {
         String name = EntityUtil.getName(player);
         if (Managers.getPopManager().getPopMap().containsKey(name)) {
            boolean isFriend = Managers.getFriendManager().isFriend((PlayerEntity)player);
            boolean isSelf = player == mc.player && (Boolean)((PopCounter)this.module).selfFriend.getValue();
            boolean isHidden = (Boolean)((PopCounter)this.module).hideSelf.getValue() && player instanceof ClientPlayerEntity;
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
               ordinalText = formatName.append(Text.literal(" died after popping ").withColor(color)).append(Text.literal(String.valueOf(pops)).formatted(new Formatting[]{Formatting.BOLD, Formatting.GRAY})).append(Text.literal(pops == 1 ? " totem" : " totems").withColor(color));
               ClientLogger.getLogger().log((Text)ordinalText, -player.getId());
               break;
            case ORDINAL:
               ordinalText = formatName.append(Text.literal(isHidden ? " have died after popping your " : " has died after popping their ").withColor(color)).append(Text.literal(pops + TextUtil.toOrdinal(pops)).formatted(new Formatting[]{Formatting.BOLD, Formatting.GRAY})).append(Text.literal(" totem").withColor(color));
               ClientLogger.getLogger().log((Text)ordinalText, -player.getId());
            }
         }
      }

   }
}