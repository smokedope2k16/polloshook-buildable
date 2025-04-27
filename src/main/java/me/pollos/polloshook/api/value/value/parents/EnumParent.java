package me.pollos.polloshook.api.value.value.parents;

import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;

public class EnumParent extends Parent {
   private final EnumValue<?> parent;
   private final Enum<?> target;

   public EnumParent(EnumValue<?> parent, Enum<?> target, boolean opposite) {
      super(opposite);
      this.parent = parent;
      this.target = target;
   }

   public boolean isVisible() {
      if (((Enum)this.parent.getValue()).getClass() != this.target.getClass()) {
         throw new RuntimeException("Parent enumclass [%s] is not equal to the target enumclass [%s]".formatted(new Object[]{((Enum)this.parent.getValue()).toString(), this.target.getClass().toString()}));
      } else if (!this.parent.getParent().isVisible()) {
         return false;
      } else {
         Enum<?> value = (Enum)this.parent.getValue();
         if (this.opposite) {
            return value != this.target;
         } else {
            return value == this.target;
         }
      }
   }
}
