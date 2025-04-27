package me.pollos.polloshook.impl.module.combat.autoexp;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.combat.autoexp.mode.WasteMatch;

public class AutoExp extends ToggleableModule {
   protected final NumberValue<Float> delay = new NumberValue(1.5F, 0.1F, 10.0F, 0.25F, new String[]{"Delay", "del", "d"});
   protected final NumberValue<Integer> packets = new NumberValue(1, 1, 10, new String[]{"Packets", "strength", "exp"});
   protected final Value<Boolean> noWaste = new Value(false, new String[]{"NoWaste", "antiwaste"});
   protected final Value<Boolean> onlyIfFullArmor;
   protected final EnumValue<WasteMatch> match;
   protected final NumberValue<Integer> maxPercent;
   protected final Value<Boolean> rotate;
   protected final Value<Boolean> down;
   protected final Value<Boolean> silent;
   protected final Value<Boolean> strict;
   protected final Value<Boolean> allowInInv;
   protected boolean sending;
   protected final StopWatch timer;

   public AutoExp() {
      super(new String[]{"AutoEXP", "instantexp", "exp"}, Category.COMBAT);
      this.onlyIfFullArmor = (new Value(false, new String[]{"OnlyIfFullArmor", "onlyiffullgear", "armor"})).setParent(this.noWaste);
      this.match = (new EnumValue(WasteMatch.ALL, new String[]{"Match", "m"})).setParent(this.noWaste);
      this.maxPercent = (new NumberValue(90, 0, 100, new String[]{"MaxPercent", "max%", "max"})).setParent(this.noWaste).withTag("%");
      this.rotate = new Value(false, new String[]{"Rotate", "rotations"});
      this.down = new Value(false, new String[]{"Down", "rotatedown", "lookdowm"});
      this.silent = new Value(true, new String[]{"Silent", "silentswap"});
      this.strict = new Value(false, new String[]{"Strict", "ghostfix"});
      this.allowInInv = new Value(true, new String[]{"AllowInInventory", "allowininv", "inv", "inventory"});
      this.sending = false;
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.delay, this.packets, this.noWaste, this.onlyIfFullArmor, this.match, this.maxPercent, this.rotate, this.down, this.silent, this.strict, this.allowInInv});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerInteract(this), new ListenerUseItem(this), new ListenerDeath(this)});
   }
}
