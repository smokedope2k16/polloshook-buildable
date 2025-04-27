package me.pollos.polloshook.api.value.value.parents;

import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;

public class BooleanParent extends Parent {
   protected Value<Boolean> parent;

   public BooleanParent(Value<Boolean> parent, boolean opposite) {
      super(opposite);
      this.parent = parent;
   }

   public boolean isVisible() {
      if (!this.parent.getParent().isVisible()) {
         return false;
      } else {
         return this.opposite != (Boolean)this.parent.getValue();
      }
   }
}
