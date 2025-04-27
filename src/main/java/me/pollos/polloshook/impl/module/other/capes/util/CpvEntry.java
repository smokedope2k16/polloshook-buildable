package me.pollos.polloshook.impl.module.other.capes.util;

import java.util.List;
import me.pollos.polloshook.impl.module.other.capes.util.impl.CapeEntry;

public class CpvEntry extends CapeEntry {
   public CpvEntry() {
      super("cpv");
   }

   public List<String> getUUIDs() {
      return List.of("75aa6787-f7de-456b-9074-8dce5a611100", "b8e7bc40-0982-477b-8368-6b619963eb04");
   }

   public String getIdentifier() {
      return "cpvcape.png";
   }
}
