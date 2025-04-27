package me.pollos.polloshook.impl.module.combat.idpredict;

import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.thread.ThreadUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class IDPredict extends ToggleableModule {
   protected final NumberValue<Integer> offset = new NumberValue(1, 1, 10, new String[]{"Offset", "off"});
   protected final NumberValue<Integer> packets = new NumberValue(1, 1, 10, new String[]{"Packets", "packetconut"});
   protected final NumberValue<Integer> delay = (new NumberValue(0, 0, 500, new String[]{"Delay", "del", "d"})).withTag("ms");
   protected final Value<Boolean> swing = new Value(false, new String[]{"Swing", "swin", "punch"});
   protected final NumberValue<Integer> coolDown = (new NumberValue(0, 0, 500, new String[]{"Cooldown", "downtime"})).withTag("ms");
   private static final ScheduledExecutorService THREAD = ThreadUtil.newDaemonScheduledExecutor("ID-Predict");
   protected final StopWatch timer = new StopWatch();
   protected final StopWatch attackTimer = new StopWatch();
   protected int attacks = 0;
   private int highestID;
   private boolean updated;
   private int slot;
   private boolean ignored;

   public IDPredict() {
      super(new String[]{"IDPredict", "booster", "godmodule"}, Category.COMBAT);
      this.offerValues(new Value[]{this.offset, this.packets, this.delay, this.swing, this.coolDown});
      this.offerListeners(new Listener[]{new ListenerTick(this), new ListenerPlace(this), new ListenerEntitySpawn(this), new ListenerExperienceOrb(this), new ListenerSwitch(this)});
   }

   protected String getTag() {
      return "" + this.attacks;
   }

   public void onShutdown() {
      THREAD.shutdown();
   }

   protected void onToggle() {
      this.setUpdated(false);
      this.highestID = 0;
      this.ignored = false;
   }

   public void onWorldLoad() {
      this.setUpdated(false);
      this.highestID = 0;
      this.ignored = false;
   }

   public void update() {
      int highest = this.highestID;
      Iterator var2 = mc.world.getEntities().iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if (entity.getId() > highest) {
            highest = entity.getId();
         }
      }

      if (highest > this.highestID) {
         this.highestID = highest;
      }
   }

   protected void attack(int idOffset, int packets, int sleep) {
      if (sleep <= 0) {
         this.attackPackets(idOffset, packets);
      } else {
         THREAD.schedule(() -> {
            this.update();
            this.attackPackets(idOffset, packets);
         }, (long)sleep, TimeUnit.MILLISECONDS);
      }
   }

   private void attackPackets(int idOffset, int packets) {
      for(int i = 0; i < packets; ++i) {
         int id = this.highestID + idOffset + i;
         Entity entity = mc.world.getEntityById(id);
         if (entity == null || entity instanceof EndCrystalEntity) {
            PlayerInteractEntityC2SPacket packet = PacketUtil.attackPacket(id);
            PacketUtil.send(packet);
            if ((Boolean)this.swing.getValue()) {
               PacketUtil.swing();
            }
         }
      }
   }

   protected void checkID(int id) {
      if (id > this.highestID) {
         this.highestID = id;
      }
   }

   public boolean isUpdated() {
      return this.updated;
   }

   public void setUpdated(boolean updated) {
      this.updated = updated;
   }

   public int getSlot() {
      return this.slot;
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }

   public boolean isIgnored() {
      return this.ignored;
   }

   public void setIgnored(boolean ignored) {
      this.ignored = ignored;
   }
}
