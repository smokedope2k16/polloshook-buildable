package me.pollos.polloshook.api.value.value.targeting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumUtil;
import me.pollos.polloshook.impl.module.combat.aura.modes.Target;
import me.pollos.polloshook.impl.module.player.blink.Blink;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class TargetValue extends Value<TargetPreset> implements Minecraftable {
   private boolean targetPlayers;
   private boolean targetMonsters;
   private boolean targetFriendlies;
   private boolean ignoreInvis;
   private boolean ignoreNaked;
   private Enum<?> target;

   public TargetValue(TargetPreset preset, String... aliases) {
      super(preset, aliases);
      this.targetPlayers = preset.isTargetPlayers();
      this.targetMonsters = preset.isTargetMonsters();
      this.targetFriendlies = preset.isTargetFriendlies();
      this.ignoreInvis = preset.isIgnoreInvis();
      this.ignoreNaked = preset.isIgnoreNaked();
      this.target = preset.getTarget();
   }

   public TargetPreset getValue() {
      return new TargetPreset(this.targetPlayers, this.targetMonsters, this.targetFriendlies, this.ignoreInvis, this.ignoreNaked, this.target);
   }

   public String returnValue(String[] args) {
      if (args.length > 2) {
         String var2 = args[2].toUpperCase();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1999561828:
            if (var2.equals("NAKEDS")) {
               var3 = 11;
            }
            break;
         case -1932423455:
            if (var2.equals("PLAYER")) {
               var3 = 1;
            }
            break;
         case -1827576431:
            if (var2.equals("TARGET")) {
               var3 = 19;
            }
            break;
         case -1763381819:
            if (var2.equals("INNOCENTS")) {
               var3 = 8;
            }
            break;
         case -1512213064:
            if (var2.equals("IGNOREINVISIBLES")) {
               var3 = 13;
            }
            break;
         case -1270864247:
            if (var2.equals("FRIENDLIES")) {
               var3 = 5;
            }
            break;
         case -1114380306:
            if (var2.equals("IGNORENAKEDS")) {
               var3 = 9;
            }
            break;
         case -594356695:
            if (var2.equals("IGNOREINVIS")) {
               var3 = 14;
            }
            break;
         case -590137083:
            if (var2.equals("IGNORENAKED")) {
               var3 = 10;
            }
            break;
         case -382834268:
            if (var2.equals("PRIORITY")) {
               var3 = 17;
            }
            break;
         case -138953257:
            if (var2.equals("ANIMALS")) {
               var3 = 6;
            }
            break;
         case 77:
            if (var2.equals("M")) {
               var3 = 21;
            }
            break;
         case 2372003:
            if (var2.equals("MODE")) {
               var3 = 20;
            }
            break;
         case 2590522:
            if (var2.equals("TYPE")) {
               var3 = 22;
            }
            break;
         case 69825723:
            if (var2.equals("INVIS")) {
               var3 = 16;
            }
            break;
         case 74045335:
            if (var2.equals("NAKED")) {
               var3 = 12;
            }
            break;
         case 224415122:
            if (var2.equals("PLAYERS")) {
               var3 = 0;
            }
            break;
         case 463053785:
            if (var2.equals("MONSTERS")) {
               var3 = 2;
            }
            break;
         case 482684601:
            if (var2.equals("HOSTILES")) {
               var3 = 4;
            }
            break;
         case 1658659430:
            if (var2.equals("INVISIBLES")) {
               var3 = 15;
            }
            break;
         case 1954599866:
            if (var2.equals("MONSTER")) {
               var3 = 3;
            }
            break;
         case 1971028113:
            if (var2.equals("TARGETING")) {
               var3 = 18;
            }
            break;
         case 2122025868:
            if (var2.equals("NEUTRALS")) {
               var3 = 7;
            }
         }

         switch(var3) {
         case 0:
         case 1:
            this.setTargetPlayers(!this.targetPlayers);
            return String.format("Toggling TargetPlayers %s", !this.targetPlayers ? "off" : "on");
         case 2:
         case 3:
         case 4:
            this.setTargetMonsters(!this.targetMonsters);
            return String.format("Toggling TargetMonsters %s", !this.targetMonsters ? "off" : "on");
         case 5:
         case 6:
         case 7:
         case 8:
            this.setTargetFriendlies(!this.targetFriendlies);
            return String.format("Toggling TargetFriendlies %s", !this.targetFriendlies ? "off" : "on");
         case 9:
         case 10:
         case 11:
         case 12:
            this.setIgnoreNaked(!this.ignoreNaked);
            return String.format("Toggling IgnoreNakeds %s", !this.ignoreNaked ? "off" : "on");
         case 13:
         case 14:
         case 15:
         case 16:
            this.setIgnoreInvis(!this.ignoreInvis);
            return String.format("Toggling IgnoreInvisibles %s", !this.ignoreInvis ? "off" : "on");
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
            Enum<?> entry = EnumUtil.fromString(this.target, args[2]);
            if (entry instanceof Target) {
               Target t = (Target)entry;
               this.target = t;
               return "Set Targeting mode to %s".formatted(new Object[]{t.name()});
            }

            return "Try again next time bucko";
         }
      }

      return "%s (%s, %s, %s, %s, %s, %s, %s)".formatted(new Object[]{this.getLabel(), this.getStringForBool(this.targetPlayers, "Players"), this.getStringForBool(this.targetMonsters, "Monsters"), this.getStringForBool(this.targetFriendlies, "Friendlies"), this.getStringForBool(this.ignoreInvis, "IgnoreInvisibles"), this.getStringForBool(this.ignoreNaked, "IgnoreNakeds"), "Targeting " + this.target.name().toUpperCase()});
   }

   private String getStringForBool(boolean bool, String label) {
      Formatting color = bool ? Formatting.GREEN : Formatting.RED;
      String var10000 = String.valueOf(color);
      return var10000 + label + String.valueOf(Formatting.GRAY);
   }

   public void setValue(TargetPreset value) {
      this.setTargetPlayers(value.isTargetPlayers());
      this.setTargetMonsters(value.isTargetMonsters());
      this.setTargetFriendlies(value.isTargetFriendlies());
      this.setIgnoreInvis(value.isIgnoreInvis());
      this.setIgnoreNaked(value.isIgnoreNaked());
      this.setTarget(value.getTarget());
      super.setValue(value);
   }

   public List<LivingEntity> getTargets(float targetRange, Collection<Entity> entities) {
      List<LivingEntity> targets = new ArrayList();
      Iterator var4 = entities.iterator();

      while(var4.hasNext()) {
         Entity entity = (Entity)var4.next();
         if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            if (this.isValidEntityStatus(living) && this.isValidEntityType(living) && !(entity.distanceTo(mc.player) >= targetRange)) {
               targets.add(living);
            }
         }
      }

      return targets;
   }

   public LivingEntity getEnemy(float targetRange, Collection<Entity> entities) {
      LivingEntity finalEntity = null;
      float last = Float.MAX_VALUE;
      Iterator var5 = entities.iterator();

      while(var5.hasNext()) {
         Entity entity = (Entity)var5.next();
         if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            if (this.isValidEntityStatus(living) && this.isValidEntityType(living) && !(entity.distanceTo(mc.player) >= targetRange)) {
               String var9 = this.target.name().toUpperCase();
               byte var10 = -1;
               switch(var9.hashCode()) {
               case 87631:
                  if (var9.equals("YAW")) {
                     var10 = 0;
                  }
                  break;
               case 62548255:
                  if (var9.equals("ARMOR")) {
                     var10 = 2;
                  }
                  break;
               case 1071086581:
                  if (var9.equals("DISTANCE")) {
                     var10 = 1;
                  }
                  break;
               case 2127033948:
                  if (var9.equals("HEALTH")) {
                     var10 = 3;
                  }
               }

               Vec3d pos;
               float var10000;
               switch(var10) {
               case 0:
                  pos = mc.player.getRotationVec(1.0F);
                  Vec3d entityVec = living.getPos().subtract(mc.player.getPos()).normalize();
                  double angle = Math.acos(pos.dotProduct(entityVec));
                  var10000 = (float)Math.toDegrees(angle);
                  break;
               case 1:
                  pos = mc.player.getPos();
                  var10000 = (float)living.squaredDistanceTo(pos.x, pos.y, pos.z);
                  break;
               case 2:
                  var10000 = EntityUtil.getArmor(living);
                  break;
               case 3:
                  var10000 = EntityUtil.getHealth(living);
                  break;
               default:
                  var10000 = -mc.player.distanceTo(living);
               }

               float comp = var10000;
               if (comp < last) {
                  finalEntity = living;
                  last = comp;
               }
            }
         }
      }

      return finalEntity;
   }

   public boolean isValidEntityStatus(LivingEntity entity) {
      if (entity == null) {
         return false;
      } else {
         if (entity instanceof FakePlayerEntity) {
            FakePlayerEntity fk = (FakePlayerEntity)entity;
            if (fk.getLabel().equalsIgnoreCase(Blink.FAKE_PLAYER_LABEL)) {
               return false;
            }
         }

         if (entity.isInvisible() && this.ignoreInvis) {
            return false;
         } else {
            if (entity instanceof PlayerEntity) {
               PlayerEntity player = (PlayerEntity)entity;
               if (Managers.getFriendManager().isFriend(player)) {
                  return false;
               }

               if (!TargetUtil.hasArmor(player) && this.ignoreNaked) {
                  return false;
               }
            }

            return !EntityUtil.isDead(entity) && entity != mc.player;
         }
      }
   }

   public boolean isValidEntityType(LivingEntity entity) {
      if (entity instanceof PlayerEntity && this.targetPlayers) {
         return true;
      } else if (TargetUtil.isFriendly(entity) && this.targetFriendlies) {
         return true;
      } else {
         return TargetUtil.isMonster(entity) && this.targetMonsters;
      }
   }

   
   public boolean isTargetPlayers() {
      return this.targetPlayers;
   }

   
   public boolean isTargetMonsters() {
      return this.targetMonsters;
   }

   
   public boolean isTargetFriendlies() {
      return this.targetFriendlies;
   }

   
   public boolean isIgnoreInvis() {
      return this.ignoreInvis;
   }

   
   public boolean isIgnoreNaked() {
      return this.ignoreNaked;
   }

   
   public Enum<?> getTarget() {
      return this.target;
   }

   
   public void setTargetPlayers(boolean targetPlayers) {
      this.targetPlayers = targetPlayers;
   }

   
   public void setTargetMonsters(boolean targetMonsters) {
      this.targetMonsters = targetMonsters;
   }

   
   public void setTargetFriendlies(boolean targetFriendlies) {
      this.targetFriendlies = targetFriendlies;
   }

   
   public void setIgnoreInvis(boolean ignoreInvis) {
      this.ignoreInvis = ignoreInvis;
   }

   
   public void setIgnoreNaked(boolean ignoreNaked) {
      this.ignoreNaked = ignoreNaked;
   }

   
   public void setTarget(Enum<?> target) {
      this.target = target;
   }
}
