package me.pollos.polloshook.impl.module.movement.jesus;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.impl.events.block.CollisionShapeEvent;
import me.pollos.polloshook.impl.module.movement.fly.Fly;
import me.pollos.polloshook.impl.module.movement.jesus.mode.JesusMode;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class ListenerCollide extends SafeModuleListener<Jesus, CollisionShapeEvent> {
   public ListenerCollide(Jesus module) {
      super(module, CollisionShapeEvent.class);
   }

   public void safeCall(CollisionShapeEvent event) {
      BlockState state = event.getState();
      Entity entity = mc.player.hasVehicle() ? mc.player.getVehicle() : mc.player;
      if (event.getEntity() == entity) {
         if (((Jesus)this.module).mode.getValue() != JesusMode.DOLPHIN && !((Fly)Managers.getModuleManager().get(Fly.class)).isEnabled() && !PlayerUtil.isSpectator() && !mc.player.input.sneaking && !state.getFluidState().isEmpty() && !PlayerUtil.isInLiquid()) {
            if (state.getBlock() instanceof FluidBlock && EntityUtil.isAboveWater((Entity)entity)) {
               VoxelShape shape = this.adjustShape(((JesusMode)((Jesus)this.module).mode.getValue()).getShape());
               event.setShape(shape);
               event.setCanceled(true);
            }

         }
      }
   }

   private VoxelShape adjustShape(VoxelShape shape) {
      return mc.player.hasVehicle() ? VoxelShapes.cuboid(new Box(0.0D, 0.0D, 0.0D, 1.0D, 0.949999988079071D, 1.0D)) : shape;
   }
}
