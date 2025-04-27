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

public class ListenerRemove extends ModuleListener<PvPInfo, PotionManager.RemoveTrackedEffectEvent> {
   public ListenerRemove(PvPInfo module) {
      super(module, PotionManager.RemoveTrackedEffectEvent.class);
   }

   public void call(PotionManager.RemoveTrackedEffectEvent event) {
      if ((Boolean)((PvPInfo)this.module).potions.getValue()) {
         if (((PvPInfo)this.module).effects.contains(event.getEffects().effect()) && (Boolean)((PvPInfo)this.module).left.getValue()) {
            StatusEffect effect = event.getEffects().effect();
            StatusEffectInstance instance = event.getEffects().instance();
            PlayerEntity player = event.getPlayer();
            String name = player.getName().getString();

            Formatting var10000 = Managers.getFriendManager().isFriend(player) ? Formatting.AQUA : Formatting.RESET;
            if (Managers.getFriendManager().isFriend(name)) {
               Friend fr = Managers.getFriendManager().getFriend(name);
               if (!fr.getAlias().equals(name)) {
                  name = fr.getAlias();
               }
            }

            MutableText label = Text.literal(name).styled((style) -> {
               return style.withBold(true);
            });
            MutableText has = Text.literal(" has ran out of ").styled((style) -> {
               return style.withColor(Manager.get().getThemeColor().getColor().getRGB()).withBold(false);
            });
            MutableText first = label.append(has);
            int amplifier = instance.getAmplifier() + 1;
            String amp = RomanNumber.toRoman(amplifier);
            String var14 = effect.getName().getString();
            MutableText second = Text.literal(var14 + " " + amp).withColor(effect.getColor());
            int id = player.hashCode() + effect.hashCode();
            ClientLogger.getLogger().log((Text)first.append(second), -id);
         }
      }
   }
}