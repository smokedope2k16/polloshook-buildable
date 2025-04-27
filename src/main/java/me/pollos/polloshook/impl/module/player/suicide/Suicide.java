package me.pollos.polloshook.impl.module.player.suicide;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.player.suicide.ac.SuicideAutoCrystal;
import me.pollos.polloshook.impl.module.player.suicide.mode.SuicideMode;

public class Suicide extends ToggleableModule {
   protected final EnumValue<SuicideMode> mode;
   protected final Value<Boolean> throwOut;
   protected final Value<Boolean> armor;
   private final SuicideAutoCrystal AUTO_CRYSTAL;

   public Suicide() {
      super(new String[]{"Suicide", "/kill", "death"}, Category.PLAYER);
      this.mode = new EnumValue(SuicideMode.CRYSTAL, new String[]{"Suicide", "suicidal"});
      this.throwOut = (new Value(false, new String[]{"ThrowOut", "throw"})).setParent(this.mode, SuicideMode.CRYSTAL);
      this.armor = (new Value(false, new String[]{"Armor", "armour"})).setParent(this.throwOut);
      this.AUTO_CRYSTAL = new SuicideAutoCrystal();
      this.offerValues(new Value[]{this.mode, this.throwOut, this.armor});
      this.offerListeners(new Listener[]{new ListenerDeath(this), new ListenerMotion(this)});
   }

   protected void onEnable() {
      this.AUTO_CRYSTAL.setEnabled(true);
   }

   protected void onDisable() {
      this.AUTO_CRYSTAL.setEnabled(false);
   }

   public boolean isCrystal() {
      return this.mode.getValue() == SuicideMode.CRYSTAL && this.isEnabled();
   }
}
