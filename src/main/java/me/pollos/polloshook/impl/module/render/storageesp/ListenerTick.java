package me.pollos.polloshook.impl.module.render.storageesp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerTick extends ModuleListener<StorageESP, TickEvent> {
   public ListenerTick(StorageESP module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (mc.player != null && mc.world != null && ((StorageESP)this.module).timer.passed(50L)) {
         ((StorageESP)this.module).service.submit(() -> {
            ((StorageESP)this.module).tileEntityList = BlockUtil.getTileEntities((Integer)mc.options.getViewDistance().getValue());
         });
         ((StorageESP)this.module).timer.reset();
      }
   }
}
