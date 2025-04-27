package me.pollos.polloshook.impl.module.render.modelchanger;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.render.modelchanger.mode.Hands;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class ListenerPushMatrix extends ModuleListener<ModelChanger, ModelChanger.PushItemMatrixEvent> {
   public ListenerPushMatrix(ModelChanger module) {
      super(module, ModelChanger.PushItemMatrixEvent.class);
   }

   public void call(ModelChanger.PushItemMatrixEvent event) {
      MatrixStack matrix = event.getMatrices();
      PlayerEntity player = mc.player;
      if (player != null) {
         if ((Boolean)((ModelChanger)this.module).hands.getValue() || !player.getStackInHand(event.getHand()).isEmpty()) {
            if ((Boolean)((ModelChanger)this.module).eating.getValue() || !player.isUsingItem() || player.getItemUseTimeLeft() <= 0 || player.getActiveHand() != event.getHand()) {
               switch((Hands)((ModelChanger)this.module).onlyHand.getValue()) {
               case OFFHAND:
                  if (event.getHand() == Hand.MAIN_HAND) {
                     return;
                  }
                  break;
               case MAINHAND:
                  if (event.getHand() == Hand.OFF_HAND) {
                     return;
                  }
               }

               matrix.scale((Float)((ModelChanger)this.module).scaleX.getValue(), (Float)((ModelChanger)this.module).scaleY.getValue(), (Float)((ModelChanger)this.module).scaleZ.getValue());
            }
         }
      }
   }
}
