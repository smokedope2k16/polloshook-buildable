package me.pollos.polloshook.api.event.events;

public class StageEvent extends Event {
   private final Stage stage;

   public StageEvent(Stage stage) {
      this.stage = stage;
   }

   public Stage getStage() {
      return this.stage;
   }
}
