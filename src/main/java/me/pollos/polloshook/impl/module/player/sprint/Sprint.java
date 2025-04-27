package me.pollos.polloshook.impl.module.player.sprint;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.player.sprint.mode.SprintMode;

public class Sprint extends ToggleableModule {
   protected final EnumValue<SprintMode> mode;
   protected final Value<Boolean> rotate;
   protected final Value<Boolean> swim;

   public Sprint() {
      super(new String[]{"Sprint", "autosprint"}, Category.PLAYER);
      this.mode = new EnumValue(SprintMode.LEGIT, new String[]{"Mode", "mod"});
      this.rotate = (new Value(false, new String[]{"Rotations", "rotate", "r"})).setParent(this.mode, SprintMode.RAGE);
      this.swim = new Value(false, new String[]{"Swim", "swimmer"});
      this.offerValues(new Value[]{this.mode, this.rotate, this.swim});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerSprint(this), new ListenerMotion(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   public boolean isRage() {
      return this.isEnabled() && this.mode.getValue() == SprintMode.RAGE;
   }

   public boolean canSprint() {
      return !mc.player.isSneaking() && !mc.player.horizontalCollision && (float)mc.player.getHungerManager().getFoodLevel() >= 6.0F && (!mc.player.isSubmergedInWater() || (Boolean)this.swim.getValue());
   }

   protected boolean doReturn() {
      Sprint.SprintEvent sprintEvent = new Sprint.SprintEvent();
      sprintEvent.dispatch();
      return sprintEvent.isCanceled();
   }

   
   public EnumValue<SprintMode> getMode() {
      return this.mode;
   }

   
   public Value<Boolean> getRotate() {
      return this.rotate;
   }

   
   public Value<Boolean> getSwim() {
      return this.swim;
   }

   public static class SprintEvent extends Event {
   }
}
