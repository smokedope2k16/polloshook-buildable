package me.pollos.polloshook.impl.module.other.window;



public enum CornerMode {
   BOX(0),
   ROUND(1),
   SLIM(2);

   private final int mode;

   
   private CornerMode(final int mode) {
      this.mode = mode;
   }

   
   public int getMode() {
      return this.mode;
   }

   // $FF: synthetic method
   private static CornerMode[] $values() {
      return new CornerMode[]{BOX, ROUND, SLIM};
   }
}
