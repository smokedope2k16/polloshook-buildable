package me.pollos.polloshook.impl.module.combat.autolog;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class AutoLog extends ToggleableModule {
   protected final Value<Boolean> totems = new Value(false, new String[]{"Totems", "t"});
   protected final NumberValue<Integer> totemCount;
   protected final NumberValue<Float> health;
   protected final Value<Boolean> onEnter;
   protected final Value<Boolean> fallDistance;
   protected final NumberValue<Float> fallDistanceCount;
   protected final Value<Boolean> kick;

   public AutoLog() {
      super(new String[]{"AutoLog", "autologout", "logout"}, Category.COMBAT);
      this.totemCount = (new NumberValue(3, 0, 16, new String[]{"TotemCount", "tc", "totemamount"})).setParent(this.totems);
      this.health = new NumberValue(16.0F, 1.0F, 20.0F, 0.5F, new String[]{"Health", "h", "hp"});
      this.onEnter = new Value(false, new String[]{"OnEnter", "enter", "e"});
      this.fallDistance = new Value(false, new String[]{"FallDistance", "falldamage", "fd"});
      this.fallDistanceCount = (new NumberValue(5.0F, 0.0F, 20.0F, 0.1F, new String[]{"FallDistanceCount", "falldistancec", "falldistanceamount", "fdc"})).setParent(this.fallDistance);
      this.kick = new Value(false, new String[]{"Kick", "illegal", "bad"});
      this.offerValues(new Value[]{this.totems, this.totemCount, this.health, this.onEnter, this.fallDistance, this.fallDistanceCount, this.kick});
      this.offerListeners(new Listener[]{new ListenerUpdate(this)});
   }

   protected String getTag() {
      return String.format("%.1f", this.health.getValue());
   }

   protected void leave(String string) {
      if ((Boolean)this.kick.getValue()) {
         PacketUtil.send(new UpdateSelectedSlotC2SPacket(-1));
      }

      NetworkUtil.disconnectFromServer(string);
      this.toggle();
   }
}
