package me.pollos.polloshook.impl.module.other.capes.util.impl;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.util.Identifier;

public final class CapeUtil {
   public static Identifier getIdentifier(CapeEntry entry) {
      return getIdentifier(entry.getIdentifier());
   }

   public static Identifier getIdentifier(String entry) {
      return Identifier.of("polloshook", "textures/cape/" + entry);
   }

   public static CapeEntry getEntryFromLabel(String label, List<CapeEntry> entries) {
      Iterator var2 = entries.iterator();

      CapeEntry entry;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         entry = (CapeEntry)var2.next();
      } while(!entry.getLabel().equalsIgnoreCase(label));

      return entry;
   }

   public static CapeEntry getEntryFromUUID(UUID uuid, List<CapeEntry> entries) {
      Iterator var2 = entries.iterator();

      while(var2.hasNext()) {
         CapeEntry entry = (CapeEntry)var2.next();
         Iterator var4 = entry.getUUIDs().iterator();

         while(var4.hasNext()) {
            String str = (String)var4.next();
            UUID fromString = UUID.fromString(str);
            if (fromString.equals(uuid)) {
               return entry;
            }
         }
      }

      return null;
   }

   
   private CapeUtil() {
      throw new UnsupportedOperationException("This is keyCodec utility class and cannot be instantiated");
   }
}
