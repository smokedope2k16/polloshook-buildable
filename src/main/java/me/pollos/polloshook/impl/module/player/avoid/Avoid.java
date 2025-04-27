package me.pollos.polloshook.impl.module.player.avoid;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;

public class Avoid extends ToggleableModule {
   protected final Value<Boolean> tripwire = new Value(false, new String[]{"Tripwire", "tripwirehook"});
   protected final Value<Boolean> lava = new Value(false, new String[]{"Lava", "burn"});
   protected final Value<Boolean> fire = new Value(false, new String[]{"Fire", "f"});
   protected final Value<Boolean> cactus = new Value(false, new String[]{"Cactus", "cacti"});
   protected final Value<Boolean> voids = new Value(false, new String[]{"Void", "v", "vod"});
   protected final Value<Boolean> legacyVoid;
   protected final Value<Boolean> unloaded;

   public Avoid() {
      super(new String[]{"Avoid", "anticactus", "antifire", "antivoid"}, Category.PLAYER);
      this.legacyVoid = (new Value(false, new String[]{"LegacyVoid", "protocolvoid"})).setParent(this.voids);
      this.unloaded = new Value(false, new String[]{"Unloaded", "unloadedchunks"});
      this.offerValues(new Value[]{this.tripwire, this.lava, this.fire, this.cactus, this.voids, this.legacyVoid, this.unloaded});
      this.offerListeners(new Listener[]{new ListenerCollide(this), new ListenerUpdate(this)});
   }
}
