package me.pollos.polloshook.impl.module.movement.longjump;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.asm.ducks.entity.IEntity;
import me.pollos.polloshook.impl.module.movement.longjump.type.mode.LongJumpMode;

public class LongJump extends ToggleableModule {
   protected final EnumValue<LongJumpMode> mode;
   protected final Value<Boolean> mini;
   protected final NumberValue<Float> boost;
   protected final Value<Boolean> autoDisable;

   public LongJump() {
      super(new String[]{"LongJump", "longjumper"}, Category.MOVEMENT);
      this.mode = new EnumValue(LongJumpMode.NORMAL, new String[]{"Mode", "m"});
      this.mini = (new Value(false, new String[]{"MiniJumps", "mini"})).setParent(this.mode, LongJumpMode.COWABUNGA, true);
      this.boost = (new NumberValue(4.0F, 0.1F, 10.0F, 0.1F, new String[]{"Boost", "elementCodec", "factor"})).setParent(this.mode, LongJumpMode.COWABUNGA, true);
      this.autoDisable = new Value(false, new String[]{"AutoDisable", "disable"});
      this.offerValues(new Value[]{this.mode, this.mini, this.boost, this.autoDisable});
      this.offerListeners(new Listener[]{new ListenerMove(this), new ListenerPosLook(this), new ListenerTick(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   protected void onToggle() {
      ((LongJumpMode)this.mode.getValue()).getType().reset();
   }

   protected boolean canLongJump() {
      return !mc.player.isFallFlying() && !mc.player.isCrawling() && !mc.player.isClimbing() && !mc.player.isUsingRiptide() && !((IEntity)mc.player).isInWeb();
   }

   
   public EnumValue<LongJumpMode> getMode() {
      return this.mode;
   }

   
   public Value<Boolean> getMini() {
      return this.mini;
   }

   
   public NumberValue<Float> getBoost() {
      return this.boost;
   }

   
   public Value<Boolean> getAutoDisable() {
      return this.autoDisable;
   }
}
