package me.pollos.polloshook.impl.module.combat.projectilemanip;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

public class ProjectileManip extends ToggleableModule {
   protected final Value<Boolean> eggs = new Value(false, new String[]{"Eggs", "egg", "chicken"});
   protected final Value<Boolean> snowballs = new Value(false, new String[]{"Snowballs", "balls", "elementCodec"});
   protected final Value<Boolean> trident = new Value(false, new String[]{"Trident", "t", "pitchfork"});
   protected final Value<Boolean> pearls = new Value(false, new String[]{"Pearls", "p", "enderpearls"});
   protected final Value<Boolean> arrows = new Value(false, new String[]{"Arrows", "keyCodec", "bow"});
   protected final NumberValue<Float> power = new NumberValue(5.0F, 0.0F, 15.0F, 0.1F, new String[]{"Power", "p"});
   protected final NumberValue<Float> delay = (new NumberValue(5.0F, 0.1F, 15.0F, 0.1F, new String[]{"Delay", "d", "del"})).withTag("second");
   protected final StopWatch timer = new StopWatch();

   public ProjectileManip() {
      super(new String[]{"ProjectileManip", "projectilemanipulation", "fastprojectile"}, Category.COMBAT);
      this.offerValues(new Value[]{this.eggs, this.snowballs, this.trident, this.pearls, this.arrows, this.power, this.delay});
      this.offerListeners(new Listener[]{new ListenerUseItem(this), new ListenerAction(this)});
   }

   protected String getTag() {
      return this.timer.passed((double)((Float)this.delay.getValue() * 1000.0F)) ? "Ready" : "%.1fs".formatted(new Object[]{(float)this.timer.getTime() / 1000.0F});
   }

   protected void send(float power) {
      float offset = 1.0E-5F;
      PacketUtil.sprint(true);

      for(int i = 0; (float)i < power * 50.0F; ++i) {
         PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() - (double)offset, mc.player.getZ(), true));
         PacketUtil.send(new PositionAndOnGround(mc.player.getX(), mc.player.getY() + (double)offset, mc.player.getZ(), false));
      }

      this.timer.reset();
   }

   protected boolean isValidItemBothHands() {
      return this.isValidItem(mc.player.getMainHandStack().getItem()) || this.isValidItem(mc.player.getOffHandStack().getItem());
   }

   private boolean isValidItem(Item item) {
      return (Boolean)this.eggs.getValue() && item == Items.EGG || (Boolean)this.snowballs.getValue() && item == Items.SNOWBALL || (Boolean)this.trident.getValue() && item == Items.TRIDENT || (Boolean)this.pearls.getValue() && item == Items.ENDER_PEARL || (Boolean)this.arrows.getValue() && (item == Items.BOW || item == Items.CROSSBOW);
   }
}