package me.pollos.polloshook.impl.module.player.reach;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class Reach extends ToggleableModule {
   protected final NumberValue<Float> entities = new NumberValue(0.5F, 0.0F, 2.0F, 0.1F, new String[]{"Entities", "entity", "ent"});
   protected final NumberValue<Float> blocks = new NumberValue(0.5F, 0.0F, 2.0F, 0.1F, new String[]{"Blocks", "elementCodec", "blocky"});
   protected final NumberValue<Float> hitbox = new NumberValue(0.0F, 0.0F, 3.0F, 0.1F, new String[]{"HitboxExtend", "hitboxextension", "hitbox"});

   public Reach() {
      super(new String[]{"Reach", "reaching"}, Category.PLAYER);
      this.offerValues(new Value[]{this.entities, this.blocks, this.hitbox});
      this.offerListeners(new Listener[]{new ListenerReach(this), new ListenerHitbox(this)});
   }

   public static class HitboxEvent extends Event {
      private float add = 0.0F;

      
      public float getAdd() {
         return this.add;
      }

      
      public void setAdd(float add) {
         this.add = add;
      }
   }

   public static class ReachEvent extends Event {
      private final boolean block;
      private float add = 0.0F;

      
      public boolean isBlock() {
         return this.block;
      }

      
      public float getAdd() {
         return this.add;
      }

      
      public void setAdd(float add) {
         this.add = add;
      }

      
      public ReachEvent(boolean block) {
         this.block = block;
      }
   }
}
