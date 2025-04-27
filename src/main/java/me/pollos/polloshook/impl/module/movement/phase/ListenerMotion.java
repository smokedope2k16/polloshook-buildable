package me.pollos.polloshook.impl.module.movement.phase;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.movement.phase.util.PhaseMode;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class ListenerMotion extends ModuleListener<Phase, MotionUpdateEvent> {
   public ListenerMotion(Phase module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (event.getStage() == Stage.PRE && ((Phase)this.module).mode.getValue() == PhaseMode.PEARL) {
         int lastSlot = mc.player.getInventory().selectedSlot;
         int pearlSlot = ItemUtil.findHotbarItem(Items.ENDER_PEARL);
         if ((Boolean)((Phase)this.module).checkEntities.getValue() && !Managers.getEntitiesManager().getAnyCollidingEntities(Managers.getPositionManager().getBB()).isEmpty()) {
            return;
         }

         if (pearlSlot == -1) {
            ((Phase)this.module).toggle();
            return;
         }

         float[] oldRots = new float[]{mc.player.getYaw(), mc.player.getPitch()};
         PacketUtil.send(new LookAndOnGround((float)this.getYaw(), (float)(Integer)((Phase)this.module).pitch.getValue(), mc.player.isOnGround()));
         InventoryUtil.switchToSlot(pearlSlot);
         ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
            return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
         });
         PacketUtil.swing();
         InventoryUtil.switchToSlot(lastSlot);
         PacketUtil.send(new LookAndOnGround(oldRots[0], oldRots[1], mc.player.isOnGround()));
         ((Phase)this.module).toggle();
      }

   }

   private int getYaw() {
      return (int)Math.round(RotationsUtil.getYaw(new Vec3d(Math.floor(mc.player.getX()) + 0.5D, 0.0D, Math.floor(mc.player.getZ()) + 0.5D))) + 180;
   }
}