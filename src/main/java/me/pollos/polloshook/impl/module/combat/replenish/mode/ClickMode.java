package me.pollos.polloshook.impl.module.combat.replenish.mode;


import net.minecraft.screen.slot.SlotActionType;

public enum ClickMode {
   SWAP(SlotActionType.SWAP),
   SHIFT_CLICK(SlotActionType.QUICK_MOVE);

   final SlotActionType type;

   
   private ClickMode(final SlotActionType type) {
      this.type = type;
   }

   
   public SlotActionType getType() {
      return this.type;
   }

   // $FF: synthetic method
   private static ClickMode[] $values() {
      return new ClickMode[]{SWAP, SHIFT_CLICK};
   }
}
