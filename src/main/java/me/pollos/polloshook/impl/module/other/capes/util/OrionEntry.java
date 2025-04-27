package me.pollos.polloshook.impl.module.other.capes.util;

import java.util.List;
import me.pollos.polloshook.impl.module.other.capes.util.impl.CapeEntry;

public class OrionEntry extends CapeEntry {
   public OrionEntry() {
      super("orion");
   }

   public List<String> getUUIDs() {
      return List.of("66268245-cd4e-4133-b2cf-3acc33af0fda", "aba69ca1-6ae2-4d39-9905-bc137b181ac6", "30ee51c8-c0ef-42d6-816d-88b601110ddd");
   }

   public String getIdentifier() {
      return "orioncape.png";
   }
}
