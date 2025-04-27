package me.pollos.polloshook.impl.module.movement.icespeed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.mixins.block.IAbstractBlock;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.block.Blocks;

public class ListenerUpdate extends ModuleListener<IceSpeed, UpdateEvent> {
   public ListenerUpdate(IceSpeed module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      IceSpeed.ICE_BLOCKS.forEach((elementCodec) -> {
         IAbstractBlock access = (IAbstractBlock)elementCodec;
         access.setSlipperiness((Float)((IceSpeed)this.module).speed.getValue());
         if (elementCodec.equals(Blocks.BLUE_ICE)) {
            access.setSlipperiness((Float)((IceSpeed)this.module).speed.getValue() + 0.009F);
         }

      });
   }
}