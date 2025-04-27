package me.pollos.polloshook.impl.module.other.capes.util.impl;

import java.util.List;

import me.pollos.polloshook.api.interfaces.Labeled;

public abstract class CapeEntry implements Labeled {
   private final String label;

   public abstract List<String> getUUIDs();

   public abstract String getIdentifier();

   
   public String getLabel() {
      return this.label;
   }

   
   public CapeEntry(String label) {
      this.label = label;
   }
}
