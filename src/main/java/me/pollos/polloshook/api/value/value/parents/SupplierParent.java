package me.pollos.polloshook.api.value.value.parents;

import java.util.function.Supplier;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;

public class SupplierParent extends Parent {
   private final Supplier<Boolean> supplier;

   public SupplierParent(Supplier<Boolean> supplier, boolean opposite) {
      super(opposite);
      this.supplier = supplier;
   }

   public boolean isVisible() {
      if (this.opposite) {
         return !(Boolean)this.supplier.get();
      } else {
         return (Boolean)this.supplier.get();
      }
   }
}
