package me.pollos.polloshook.impl.module.other.capes.util;

import java.util.List;
import me.pollos.polloshook.impl.module.other.capes.util.impl.CapeEntry;

public class PollosEntry extends CapeEntry {
   public PollosEntry() {
      super("pollos");
   }

   public List<String> getUUIDs() {
      return List.of("ca93ab7f-8bd6-4651-a699-1d130c81d0a2", "c1e16a89-360e-4c65-8e0e-80b304a6cfba");
   }

   public String getIdentifier() {
      return "polloscape.png";
   }
}
