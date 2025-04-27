package me.pollos.polloshook.impl.module.movement.fly;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class Fly extends ToggleableModule {
   protected final NumberValue<Float> horizontal = (new NumberValue(5.0F, 0.1F, 10.0F, 0.1F, new String[]{"Horizontal", "horizontally", "h"})).setNoLimit(true);
   protected final NumberValue<Float> vertical = (new NumberValue(8.0F, 0.1F, 10.0F, 0.1F, new String[]{"Vertical", "vertically", "v"})).setNoLimit(true);
   protected final Value<Boolean> spoofGround = new Value(false, new String[]{"SpoofGround", "spoof"});
   protected final Value<Boolean> glide = new Value(false, new String[]{"Glide", "g", "glider"});
   protected final NumberValue<Float> factor;
   protected final Value<Boolean> damage;
   protected final Value<Boolean> antiKick;

   public Fly() {
      super(new String[]{"Fly", "flight", "autofly"}, Category.MOVEMENT);
      this.factor = (new NumberValue(0.03126F, 0.001F, 0.5F, 0.01F, new String[]{"Factor", "glidespeed", "gfactor"})).setParent(this.glide);
      this.damage = new Value(false, new String[]{"Damage", "dmg", "d"});
      this.antiKick = new Value(false, new String[]{"AntiKick", "nokick"});
      this.offerValues(new Value[]{this.horizontal, this.vertical, this.spoofGround, this.glide, this.factor, this.damage, this.antiKick});
      this.offerListeners(new Listener[]{new ListenerMove(this), new ListenerUpdate(this), new ListenerPacket(this)});
   }

   protected void onEnable() {
      if (mc.player != null && mc.world != null) {
         if ((Boolean)this.damage.getValue() && mc.player.isOnGround()) {
            for(int i = 0; i <= 81; ++i) {
               PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.0625D, mc.player.getZ(), false);
               PacketUtil.move(mc.player.getX(), mc.player.getY(), mc.player.getZ(), i == 81);
            }
         }

      }
   }

   protected void onDisable() {
      if (mc.player != null) {
         mc.player.setVelocity(0.0D, 0.0D, 0.0D);
      }
   }
}
