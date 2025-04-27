package me.pollos.polloshook.impl.module.other.hud.elements.draggable.textradar.util;


import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public record TextRadarEntry(String label, float distance, float health, int pops) implements Minecraftable {
   
   public TextRadarEntry(String label, float distance, float health, int pops) {
      if (label == null) {
         throw new NullPointerException("label is marked non-null but is null");
      } else {
         this.label = label;
         this.distance = distance;
         this.health = health;
         this.pops = pops;
      }
   }

   public static TextRadarEntry of(PlayerEntity player) {
      if (player == null) {
         throw new NullPointerException("player is marked non-null but is null");
      } else {
         String label = player.getName().getString();
         float distance = player.distanceTo(mc.player);
         float health = EntityUtil.getHealth(player);
         int pops = (Integer)Managers.getPopManager().getPopMap().getOrDefault(player.getName().getString(), 0);
         return new TextRadarEntry(label, distance, health, pops);
      }
   }

   public String build(boolean showDistance, boolean showHealth, boolean showPops) {
      String distance = TextRadarComponents.getDistanceComp(this.distance()).getString();
      String label = TextRadarComponents.getLabelComp(this.label()).getString();
      String health = TextRadarComponents.getHealthComp(this.health()).getString();
      String pops = TextRadarComponents.getPopsComp(this.pops()).getString();
      StringBuilder sb = new StringBuilder();
      if (showDistance) {
         sb.append(distance).append(" ");
      }

      sb.append(label);
      if (showHealth || showPops) {
         sb.append(Formatting.BOLD).append(" (");
         if (showHealth) {
            sb.append(health);
         }

         if (showPops && !pops.equalsIgnoreCase("")) {
            if (showHealth) {
               sb.append(", ");
            }

            sb.append(pops);
         }

         sb.append(Formatting.BOLD).append(")");
      }

      return sb.toString();
   }

   public String label() {
      return this.label;
   }

   public float distance() {
      return this.distance;
   }

   public float health() {
      return this.health;
   }

   public int pops() {
      return this.pops;
   }
}
