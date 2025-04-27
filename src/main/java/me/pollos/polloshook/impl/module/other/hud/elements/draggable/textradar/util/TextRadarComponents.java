package me.pollos.polloshook.impl.module.other.hud.elements.draggable.textradar.util;

import me.pollos.polloshook.api.managers.Managers;
import net.minecraft.util.Formatting;

public class TextRadarComponents {
   public static TextRadarComponents.TextRadarComponent getLabelComp(String label) {
      return new TextRadarComponents.TextRadarComponent() {
         public String getString() {
            String var10000 = String.valueOf(this.getColor());
            return var10000 + label;
         }

         private Formatting getColor() {
            return Managers.getFriendManager().isFriend(label) ? Formatting.AQUA : Formatting.RESET;
         }
      };
   }

   public static TextRadarComponents.TextRadarComponent getHealthComp(float health) {
      return new TextRadarComponents.TextRadarComponent() {
         public String getString() {
            String var10000 = String.valueOf(this.getColor());
            return var10000 + String.format("%.1f", health);
         }

         private Formatting getColor() {
            if (health > 16.0F) {
               return Formatting.GREEN;
            } else if (health > 12.0F) {
               return Formatting.WHITE;
            } else if (health > 8.0F) {
               return Formatting.GOLD;
            } else {
               return health > 5.0F ? Formatting.RED : Formatting.RED;
            }
         }
      };
   }

   public static TextRadarComponents.TextRadarComponent getPopsComp(int pops) {
      return new TextRadarComponents.TextRadarComponent() {
         public String getString() {
            return pops <= 0 ? "" : String.valueOf(this.getColor()) + "-" + pops;
         }

         private Formatting getColor() {
            if (pops <= 6) {
               switch(pops) {
               case 1:
               case 2:
                  return Formatting.GREEN;
               case 3:
               case 4:
                  return Formatting.WHITE;
               case 5:
               case 6:
                  return Formatting.GOLD;
               }
            }

            return Formatting.RED;
         }
      };
   }

   public static TextRadarComponents.TextRadarComponent getDistanceComp(float distance) {
      return new TextRadarComponents.TextRadarComponent() {
         public String getString() {
            String var10000 = String.valueOf(Formatting.BOLD);
            return var10000 + "[" + String.valueOf(this.getColor()) + String.format("%.1fm", distance) + String.valueOf(Formatting.BOLD) + "]";
         }

         private Formatting getColor() {
            if (distance >= 25.0F) {
               return Formatting.GREEN;
            } else if (distance < 25.0F && distance >= 16.0F) {
               return Formatting.GOLD;
            } else {
               return distance < 16.0F && distance >= 8.0F ? Formatting.WHITE : Formatting.RED;
            }
         }
      };
   }

   abstract static class TextRadarComponent {
      abstract String getString();
   }
}