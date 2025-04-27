package me.pollos.polloshook.impl.module.player.suicide.ac;

import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;

public class SuicideAutoCrystal extends AutoCrystal {
   public SuicideAutoCrystal() {
      this.initThreads();
      this.getMaxSelfDMG().setNoLimit(true);
      this.getMaxSelfDMG().setValue((Float) 999.0F);
      this.getSuicide().setValue(true);
   }

   public boolean isMovingLikeAaronBandhu() {
      return true;
   }
}
