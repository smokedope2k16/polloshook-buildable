package me.pollos.polloshook.impl.module.misc.pvpinfo;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.manager.friend.Friend;
import me.pollos.polloshook.impl.manager.minecraft.combat.potion.PotionManager;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.RomanNumber;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ListenerPotion extends ModuleListener<PvPInfo, PotionManager.StartTrackPlayerEvent> {
   public ListenerPotion(PvPInfo module) {
      super(module, PotionManager.StartTrackPlayerEvent.class);
   }

   public void call(PotionManager.StartTrackPlayerEvent event) {
      if ((Boolean)((PvPInfo)this.module).potions.getValue()) {
         if (((PvPInfo)this.module).effects.contains(event.getEffects().effect())) {
            StatusEffect effect = event.getEffects().effect();
            StatusEffectInstance instance = event.getEffects().instance();
            PlayerEntity player = event.getPlayer();
            String name = player.getName().getString();
            if (Managers.getFriendManager().isFriend(player)) {
               Formatting.AQUA.getColorValue();
            } else {
               Manager.get().getThemeColor().getColor().getRGB();
            }

            if (Managers.getFriendManager().isFriend(name)) {
               Friend fr = Managers.getFriendManager().getFriend(name);
               if (!fr.getAlias().equals(name)) {
                  name = fr.getAlias();
               }
            }

            MutableText label = Text.literal(name).styled((style) -> {
               return style.withBold(true);
            });
            MutableText has = Text.literal(" has ").styled((style) -> {
               return style.withColor(Manager.get().getThemeColor().getColor().getRGB()).withBold(false);
            });
            MutableText first = label.append(has);
            int amplifier = instance.getAmplifier() + 1;
            String amp = RomanNumber.toRoman(amplifier);
            String var10000 = effect.getName().getString();
            Text second = Text.literal(var10000 + " " + amp).withColor(effect.getColor());
            int id = player.hashCode() + effect.hashCode();
            ClientLogger.getLogger().log((Text)first.append(second), -id);
         }
      }
   }
}
