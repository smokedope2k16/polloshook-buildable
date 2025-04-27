package me.pollos.polloshook.impl.module.combat.blocker;

import java.util.concurrent.ConcurrentHashMap;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.util.math.BlockPos;

public class Blocker extends BlockPlaceModule {
   protected final Value<Boolean> replacer = new Value(false, new String[]{"Replacer", "replace"});
   protected final Value<Boolean> fullReplace = new Value(false, new String[]{"FullReplace", "fullreplacement"});
   protected final Value<Boolean> ignoreAir = new Value(false, new String[]{"IgnoreAir", "air"});
   protected final Value<Boolean> below = new Value(false, new String[]{"Below", "lower"});
   protected final Value<Boolean> onlyInHole = new Value(false, new String[]{"OnlyInHole", "onlyhole"});
   protected final Value<Boolean> blockUpdate = new Value(false, new String[]{"AltPacket", "alternativepacket"});
   protected ConcurrentHashMap<BlockPos, Long> minePositions = new ConcurrentHashMap();

   public Blocker() {
      super(new String[]{"Blocker", "anticity", "betterblocker"}, Category.COMBAT);
      this.offerValues(new Value[]{this.replacer, this.fullReplace, this.ignoreAir, this.below, this.onlyInHole, this.blockUpdate});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerAnimation(this), new ListenerBlockChange(this)});
   }
}
