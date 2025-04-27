package me.pollos.polloshook.impl.module.movement.longjump.type.mode;


import me.pollos.polloshook.impl.module.movement.longjump.type.AlternativeLongJump;
import me.pollos.polloshook.impl.module.movement.longjump.type.CowabungaLongJump;
import me.pollos.polloshook.impl.module.movement.longjump.type.LongJumpType;

public enum LongJumpMode {
   NORMAL(new LongJumpType()),
   ALTERNATIVE(new AlternativeLongJump()),
   COWABUNGA(new CowabungaLongJump());

   private final LongJumpType type;

   public boolean isNotFuckingSatanMode(Enum<?> mode) {
      return mode != COWABUNGA;
   }

   
   public LongJumpType getType() {
      return this.type;
   }

   
   private LongJumpMode(final LongJumpType type) {
      this.type = type;
   }

   // $FF: synthetic method
   private static LongJumpMode[] $values() {
      return new LongJumpMode[]{NORMAL, ALTERNATIVE, COWABUNGA};
   }
}
