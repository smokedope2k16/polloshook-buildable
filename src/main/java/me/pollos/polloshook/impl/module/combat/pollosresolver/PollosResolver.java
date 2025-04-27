package me.pollos.polloshook.impl.module.combat.pollosresolver;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class PollosResolver extends BlockPlaceModule {
   protected final Value<Boolean> multitask = new Value(true, new String[]{"MultiTask", "multitasking"});
   protected final NumberValue<Float> enemyRange = (new NumberValue(8.0F, 0.1F, 18.0F, 0.1F, new String[]{"EnemyRange", "enemy"})).withTag("range");
   protected final NumberValue<Float> minDMG = new NumberValue(6.0F, 1.0F, 20.0F, 0.1F, new String[]{"MinDMG", "mindamage"});
   protected final NumberValue<Float> maxSelfDMG = new NumberValue(4.0F, 0.1F, 20.0F, 0.1F, new String[]{"MaxSelfDMG", "maxselfdamage"});

   public PollosResolver() {
      super(new String[]{"PollosResolver", "antipollos", "plattaformer"}, Category.COMBAT);
      this.offerValues(new Value[]{this.multitask, this.enemyRange, this.minDMG, this.maxSelfDMG});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
   }
}
