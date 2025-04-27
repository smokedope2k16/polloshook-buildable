package me.pollos.polloshook.impl.module.misc.pvpinfo;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;

public class PvPInfo extends ToggleableModule {
   protected final Value<Boolean> pearls = new Value(false, new String[]{"Pearls", "pearlnotify"});
   protected final Value<Boolean> direction;
   protected final Value<Boolean> potions;
   protected final Value<Boolean> left;
   protected final List<StatusEffect> effects;

   public PvPInfo() {
      super(new String[]{"PvPInfo", "potinfo", "pearlinfo", "pvpnofications"}, Category.MISC);
      this.direction = (new Value(false, new String[]{"Direction", "dir", "d"})).setParent(this.pearls);
      this.potions = new Value(false, new String[]{"Potions", "pots", "p"});
      this.left = (new Value(false, new String[]{"Left", "l"})).setParent(this.potions);
      this.effects = Arrays.asList((StatusEffect)StatusEffects.SPEED.value(), (StatusEffect)StatusEffects.STRENGTH.value(), (StatusEffect)StatusEffects.RESISTANCE.value());
      this.offerValues(new Value[]{this.pearls, this.direction, this.potions, this.left});
      this.offerListeners(new Listener[]{new ListenerPearl(this), new ListenerPotion(this), new ListenerRemove(this)});
   }
}