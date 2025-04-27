package me.pollos.polloshook.impl.module.movement.elytrafly;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.elytrafly.mode.ElytraFlyMode;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraFly extends ToggleableModule {
   protected final EnumValue<ElytraFlyMode> mode;
   protected final Value<Boolean> antiKick;
   protected final Value<Boolean> autoStart;
   protected final Value<Boolean> stopInWater;
   protected final Value<Boolean> stopOnGround;
   protected final NumberValue<Float> hSpeed;
   protected final Value<Boolean> vertically;
   protected final NumberValue<Float> vSpeed;
   protected final NumberValue<Integer> factor;
   protected final NumberValue<Float> delay;
   protected final Value<Boolean> pitchLock;
   protected final NumberValue<Integer> pitch;
   private boolean china;
   protected final StopWatch startTimer;
   protected final StopWatch fireworkTimer;

   public ElytraFly() {
      super(new String[]{"ElytraFly", "e+", "efly", "elytra+"}, Category.MOVEMENT);
      this.mode = new EnumValue(ElytraFlyMode.CONTROL, new String[]{"Mode", "type"});
      this.antiKick = (new Value(false, new String[]{"AntiKick", "fall"})).setParent(this.mode, ElytraFlyMode.CONTROL);
      this.autoStart = new Value(false, new String[]{"AutoStart", "start"});
      this.stopInWater = (new Value(true, new String[]{"StopInWater", "water"})).setParent(() -> {
         return this.mode.getValue() == ElytraFlyMode.BOOST || this.mode.getValue() == ElytraFlyMode.CONTROL;
      });
      this.stopOnGround = (new Value(false, new String[]{"StopOnGround", "stopground"})).setParent(() -> {
         return this.mode.getValue() == ElytraFlyMode.BOOST || this.mode.getValue() == ElytraFlyMode.CONTROL;
      });
      this.hSpeed = (new NumberValue(16.0F, 0.1F, 100.0F, 0.1F, new String[]{"Horizontal", "horiz"})).setParent(this.mode, ElytraFlyMode.CONTROL);
      this.vertically = (new Value(true, new String[]{"FlyVertically", "vertically", "vertical"})).setParent(this.mode, ElytraFlyMode.CONTROL);
      this.vSpeed = (new NumberValue(12.0F, 0.1F, 100.0F, 0.1F, new String[]{"Vertical", "vertically"})).setParent(this.vertically);
      this.factor = (new NumberValue(15, 1, 30, new String[]{"Factor", "factoid"})).setParent(this.mode, ElytraFlyMode.BOOST);
      this.delay = (new NumberValue(1.0F, 1.0F, 10.0F, 0.1F, new String[]{"FireworkDelay", "delay"})).setParent(this.mode, ElytraFlyMode.FIREWORK).withTag("second");
      this.pitchLock = (new Value(false, new String[]{"LockPitch", "pitchlock", "lock"})).setParent(() -> {
         return this.mode.getValue() == ElytraFlyMode.BOUNCE || this.mode.getValue() == ElytraFlyMode.CONTROL;
      });
      this.pitch = (new NumberValue(75, 0, 90, new String[]{"Pitch", "p", "pitc"})).withTag("degree").setParent(() -> {
         return this.mode.getValue() == ElytraFlyMode.BOUNCE && (Boolean)this.pitchLock.getValue();
      });
      this.startTimer = new StopWatch();
      this.fireworkTimer = new StopWatch();
      this.offerValues(new Value[]{this.mode, this.antiKick, this.autoStart, this.stopInWater, this.stopOnGround, this.hSpeed, this.vertically, this.vSpeed, this.factor, this.delay, this.pitchLock, this.pitch});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerMove(this), new ListenerPacket(this), new ListenerKey(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   protected void onToggle() {
      Managers.getTimerManager().set(1.0F);
   }

   public boolean isElytra() {
      return this.isValidElytra() && mc.player.isFallFlying();
   }

   protected boolean isValidElytra() {
      ItemStack stack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
      return stack.getItem() == Items.ELYTRA && ElytraItem.isUsable(stack);
   }


   public EnumValue<ElytraFlyMode> getMode() {
      return this.mode;
   }


   public boolean isChina() {
      return this.china;
   }


   public void setChina(boolean china) {
      this.china = china;
   }
}