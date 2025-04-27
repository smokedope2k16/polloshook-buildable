package me.pollos.polloshook.api.module;

import me.pollos.polloshook.api.interfaces.Labeled;

public enum Category implements Labeled {
   COMBAT("Combat"),
   MISC("Misc"),
   MOVEMENT("Movement"),
   PLAYER("Player"),
   RENDER("Render"),
   OTHER("Other"),
   ELEMENTS("Elements");

   private final String label;

   Category(String label) {
      this.label = label;
   }

   @Override
   public String getLabel() {
      return this.label;
   }
}