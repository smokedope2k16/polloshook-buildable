package me.pollos.polloshook.impl.module.render.norender;

import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.entity.Entity;

public class ListenerTick extends SafeModuleListener<NoRender, TickEvent> {
   public ListenerTick(NoRender module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      ((NoRender)this.module).getNoRenderList().clear();
      Iterator var2 = mc.world.getEntities().iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if ((Boolean)((NoRender)this.module).getEntities().getValue() && Interpolation.getRenderEntity().squaredDistanceTo(entity) > (double)MathUtil.square((Float)((NoRender)this.module).getEntityDistance().getValue())) {
            ((NoRender)this.module).getNoRenderList().add(entity);
         }
      }

   }
}
