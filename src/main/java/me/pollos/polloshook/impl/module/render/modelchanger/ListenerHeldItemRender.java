package me.pollos.polloshook.impl.module.render.modelchanger;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.render.HeldItemRenderEvent;
import me.pollos.polloshook.impl.module.render.modelchanger.mode.Hands;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.joml.Quaternionf;

public class ListenerHeldItemRender extends SafeModuleListener<ModelChanger, HeldItemRenderEvent> {
   public ListenerHeldItemRender(ModelChanger module) {
      super(module, HeldItemRenderEvent.class);
   }

   public void safeCall(HeldItemRenderEvent event) {
      MatrixStack matrix = event.getMatrix();
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

               if ((Float)((ModelChanger)this.module).oldScale.getValue() != 1.0F) {
                  matrix.scale((Float)((ModelChanger)this.module).oldScale.getValue(), (Float)((ModelChanger)this.module).oldScale.getValue(), (Float)((ModelChanger)this.module).oldScale.getValue());
               }

               if (event.getHand() == Hand.MAIN_HAND) {
                  matrix.translate((double)(Float)((ModelChanger)this.module).translateX.getValue() * 0.5D, (double)(Float)((ModelChanger)this.module).translateY.getValue() * 0.5D, (double)(Float)((ModelChanger)this.module).translateZ.getValue() * 0.5D);
               } else {
                  matrix.translate((double)(-(Float)((ModelChanger)this.module).translateX.getValue()) * 0.5D, (double)(Float)((ModelChanger)this.module).translateY.getValue() * 0.5D, (double)(Float)((ModelChanger)this.module).translateZ.getValue() * 0.5D);
               }

               if (event.getHand() == Hand.MAIN_HAND) {
                  matrix.multiply((new Quaternionf()).rotationAxis((Float)((ModelChanger)this.module).rotateX.getValue() / 180.0F, 1.0F, 0.0F, 0.0F));
                  matrix.multiply((new Quaternionf()).rotationAxis((Float)((ModelChanger)this.module).rotateY.getValue() / 180.0F, 0.0F, 1.0F, 0.0F));
                  matrix.multiply((new Quaternionf()).rotationAxis((Float)((ModelChanger)this.module).rotateZ.getValue() / 180.0F, 0.0F, 0.0F, 1.0F));
               } else {
                  matrix.multiply((new Quaternionf()).rotationAxis(-((Float)((ModelChanger)this.module).rotateY.getValue() / 180.0F), 0.0F, 1.0F, 0.0F));
                  matrix.multiply((new Quaternionf()).rotationAxis((Float)((ModelChanger)this.module).rotateX.getValue() / 180.0F, 1.0F, 0.0F, 0.0F));
                  matrix.multiply((new Quaternionf()).rotationAxis(-((Float)((ModelChanger)this.module).rotateZ.getValue() / 180.0F), 0.0F, 0.0F, 1.0F));
               }

            }
         }
      }
   }
}
