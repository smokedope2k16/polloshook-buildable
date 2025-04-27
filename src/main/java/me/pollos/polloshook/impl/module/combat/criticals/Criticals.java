package me.pollos.polloshook.impl.module.combat.criticals;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import net.minecraft.util.math.BlockPos;

public class Criticals extends ToggleableModule {
   protected final EnumValue<CriticalsType> mode;
   protected final Value<Boolean> onlyPhase;
   protected final Value<Boolean> motion;
   protected final Value<Boolean> boats;
   protected final NumberValue<Integer> boatAttacks;
   protected final Value<Boolean> mace;
   protected final NumberValue<Float> height;

   public Criticals() {
      super(new String[]{"Criticals", "autocrit", "crits"}, Category.COMBAT);
      this.mode = new EnumValue(CriticalsType.PACKET, new String[]{"Mode"});
      this.onlyPhase = (new Value(true, new String[]{"OnlyPhase", "phase"})).setParent(this.mode, CriticalsType.STRICT);
      this.motion = (new Value(false, new String[]{"StopMotion", "stop"})).setParent(this.mode, CriticalsType.LOW_HOP, true);
      this.boats = new Value(true, new String[]{"Boats", "boat"});
      this.boatAttacks = (new NumberValue(15, 3, 15, new String[]{"Packets", "boatattacks"})).setParent(this.boats);
      this.mace = new Value(false, new String[]{"Mace", "mace32k"});
      this.height = (new NumberValue(1.501F, 0.0F, 100.0F, 0.1F, new String[]{"Height", "h", "factor"})).setParent(this.mace);
      this.offerValues(new Value[]{this.mode, this.onlyPhase, this.motion, this.boats, this.boatAttacks, this.mace, this.height});
      this.offerListeners(new Listener[]{new ListenerAttackEntity(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   protected boolean isBlocked() {
      BlockPos pos = mc.player.getBlockPos().up();
      return !mc.world.getBlockState(pos).blocksMovement();
   }
}
