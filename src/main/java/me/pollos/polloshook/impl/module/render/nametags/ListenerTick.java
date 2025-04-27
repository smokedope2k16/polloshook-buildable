package me.pollos.polloshook.impl.module.render.nametags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerTick extends ModuleListener<Nametags, TickEvent> {
   public ListenerTick(Nametags module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (mc.world != null) {
         List<PlayerEntity> entityList = new ArrayList();
         Iterator var3 = (new ArrayList(mc.world.getPlayers())).iterator();

         while(true) {
            PlayerEntity player;
            do {
               do {
                  do {
                     do {
                        if (!var3.hasNext()) {
                           entityList.sort(Comparator.comparing((e) -> {
                              return Interpolation.getCameraPos().distanceTo(e.getPos());
                           }));
                           Collections.reverse(entityList);
                           ((Nametags)this.module).playerEntities = entityList;
                           return;
                        }

                        player = (PlayerEntity)var3.next();
                     } while(player == null);
                  } while(EntityUtil.isDead(player));
               } while((Boolean)((Nametags)this.module).getSelf().getValue() && mc.options.getPerspective() == Perspective.FIRST_PERSON && player == Interpolation.getRenderEntity());
            } while(!(Boolean)((Nametags)this.module).getSelf().getValue() && player == Interpolation.getRenderEntity());

            entityList.add(player);
         }
      }
   }
}
