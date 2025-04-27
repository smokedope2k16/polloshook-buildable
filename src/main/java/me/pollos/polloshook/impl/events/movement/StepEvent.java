package me.pollos.polloshook.impl.events.movement;


import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.events.StageEvent;
import net.minecraft.util.math.Box;

public class StepEvent extends StageEvent {
   private final Box bb;
   private float height;

   public StepEvent(Stage stage, Box box, float height) {
      super(stage);
      this.height = height;
      this.bb = box;
   }

   public void setHeight(float height) {
      if (this.getStage() == Stage.PRE) {
         this.height = height;
      }

   }

   public Box getBB() {
      return this.bb;
   }

   
   public float getHeight() {
      return this.height;
   }
}
