package me.pollos.polloshook.api.minecraft.render.anim;

public enum AnimationDirection {
   FORWARDS,
   BACKWARDS;

   public AnimationDirection opposite() {
      return this == FORWARDS ? BACKWARDS : FORWARDS;
   }

   // $FF: synthetic method
   private static AnimationDirection[] $values() {
      return new AnimationDirection[]{FORWARDS, BACKWARDS};
   }
}
