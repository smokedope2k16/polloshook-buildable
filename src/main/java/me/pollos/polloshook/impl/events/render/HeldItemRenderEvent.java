package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class HeldItemRenderEvent extends Event {
   private final Hand hand;
   private final MatrixStack matrix;

   
   public Hand getHand() {
      return this.hand;
   }

   
   public MatrixStack getMatrix() {
      return this.matrix;
   }

   
   public HeldItemRenderEvent(Hand hand, MatrixStack matrix) {
      this.hand = hand;
      this.matrix = matrix;
   }
}
