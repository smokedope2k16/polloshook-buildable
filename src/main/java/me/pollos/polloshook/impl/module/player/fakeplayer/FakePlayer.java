package me.pollos.polloshook.impl.module.player.fakeplayer;

import java.util.ArrayList;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerPosition;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerUtil;
import net.minecraft.client.network.OtherClientPlayerEntity;

public class FakePlayer extends ToggleableModule {
   protected final Value<Boolean> record = new Value(false, new String[]{"Record"});
   protected final Value<Boolean> play = new Value(false, new String[]{"Play"});
   protected final Value<Boolean> damage = new Value(false, new String[]{"Damage", "dmg"});
   protected final int playerId = -291000;
   protected final ArrayList<FakePlayerPosition> playerPositions = new ArrayList();
   protected final StopWatch timer = new StopWatch();

   public FakePlayer() {
      super(new String[]{"FakePlayer", "fake"}, Category.PLAYER);
      this.offerValues(new Value[]{this.record, this.play, this.damage});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerDeath(this), new ListenerExplode(this)});
   }

   protected void clearMotion(OtherClientPlayerEntity player) {
      player.setVelocity(0.0D, 0.0D, 0.0D);
   }

   protected String getTag() {
      return (Boolean)this.record.getValue() ? "Recording" : ((Boolean)this.play.getValue() ? "Playing" : null);
   }

   protected void onEnable() {
      if (PlayerUtil.isNull()) {
         this.setEnabled(false);
      }

      this.play.setValue(false);
      this.record.setValue(false);
      FakePlayerUtil.addFakePlayerToWorld("fakeplayer_module", "yabujin3421", -291000);
   }

   protected void onDisable() {
      this.play.setValue(false);
      this.record.setValue(false);
      this.clearPositions();
      FakePlayerUtil.removeFakePlayerFromWorld("fakeplayer_module", -291000);
   }

   public void onGameJoin() {
      if (this.isEnabled()) {
         this.setEnabled(false);
      }

   }

   public void onShutdown() {
      if (this.isEnabled()) {
         this.setEnabled(false);
      }

   }

   protected OtherClientPlayerEntity getPlayer() {
      return FakePlayerUtil.getPlayer("fakeplayer_module");
   }

   private void clearPositions() {
      if (!this.playerPositions.isEmpty()) {
         this.playerPositions.clear();
      }

   }
}
