package me.pollos.polloshook.impl.module.render.modelchanger;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.modelchanger.mode.Hands;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

public class ModelChanger extends ToggleableModule {
   protected final Value<Boolean> eating = new Value(false, new String[]{"Eating", "eat"});
   protected final Value<Boolean> hands = new Value(false, new String[]{"Hands", "hand"});
   protected final EnumValue<Hands> onlyHand;
   protected final NumberValue<Float> oldScale;
   protected final NumberValue<Float> translateX;
   protected final NumberValue<Float> translateY;
   protected final NumberValue<Float> translateZ;
   protected final NumberValue<Float> scaleX;
   protected final NumberValue<Float> scaleY;
   protected final NumberValue<Float> scaleZ;
   protected final NumberValue<Float> rotateX;
   protected final NumberValue<Float> rotateY;
   protected final NumberValue<Float> rotateZ;
   protected final Value<Boolean> modifyAlpha;
   protected final NumberValue<Integer> alpha;

   public ModelChanger() {
      super(new String[]{"ViewModel", "modelchanger", "niggachanger", "viewmod"}, Category.RENDER);
      this.onlyHand = new EnumValue(Hands.BOTH, new String[]{"HandMode", "onlyoffhand"});
      this.oldScale = new NumberValue(1.0F, 0.1F, 1.0F, 0.1F, new String[]{"OldScale"});
      this.translateX = new NumberValue(0.0F, -3.0F, 3.0F, 0.1F, new String[]{"TranslateX", "tx"});
      this.translateY = new NumberValue(0.0F, -3.0F, 3.0F, 0.1F, new String[]{"TranslateY", "ty"});
      this.translateZ = new NumberValue(0.0F, -3.0F, 3.0F, 0.1F, new String[]{"TranslateZ", "tz"});
      this.scaleX = new NumberValue(1.0F, -3.0F, 3.0F, 0.1F, new String[]{"ScaleX", "sx"});
      this.scaleY = new NumberValue(1.0F, -3.0F, 3.0F, 0.1F, new String[]{"ScaleY", "sy"});
      this.scaleZ = new NumberValue(1.0F, -3.0F, 3.0F, 0.1F, new String[]{"ScaleZ", "sz"});
      this.rotateX = new NumberValue(0.0F, -360.0F, 360.0F, 2.0F, new String[]{"RotateX", "rx"});
      this.rotateY = new NumberValue(0.0F, -360.0F, 360.0F, 2.0F, new String[]{"RotateY", "ry"});
      this.rotateZ = new NumberValue(0.0F, -360.0F, 360.0F, 2.0F, new String[]{"RotateZ", "rz"});
      this.modifyAlpha = new Value(false, new String[]{"ModifyAlpha", "keyCodec"});
      this.alpha = (new NumberValue(255, 0, 255, new String[]{"Alpha", "keyCodec"})).setParent(this.modifyAlpha);
      this.offerValues(new Value[]{this.eating, this.onlyHand, this.oldScale, this.translateX, this.translateY, this.translateZ, this.scaleX, this.scaleY, this.scaleZ, this.rotateX, this.rotateY, this.rotateZ, this.modifyAlpha, this.alpha});
      this.offerListeners(new Listener[]{new ListenerHeldItemRender(this), new ListenerPushMatrix(this), new ListenerAlpha(this)});
   }

   public static class ItemAlphaEvent extends Event {
      private int alpha = 255;

      
      public int getAlpha() {
         return this.alpha;
      }

      
      public void setAlpha(int alpha) {
         this.alpha = alpha;
      }

      
      private ItemAlphaEvent() {
      }

      
      public static ModelChanger.ItemAlphaEvent create() {
         return new ModelChanger.ItemAlphaEvent();
      }
   }

   public static class PushItemMatrixEvent extends Event {
      private final MatrixStack matrices;
      private final Hand hand;

      
      public MatrixStack getMatrices() {
         return this.matrices;
      }

      
      public Hand getHand() {
         return this.hand;
      }

      
      private PushItemMatrixEvent(MatrixStack matrices, Hand hand) {
         this.matrices = matrices;
         this.hand = hand;
      }

      
      public static ModelChanger.PushItemMatrixEvent of(MatrixStack matrices, Hand hand) {
         return new ModelChanger.PushItemMatrixEvent(matrices, hand);
      }
   }
}
