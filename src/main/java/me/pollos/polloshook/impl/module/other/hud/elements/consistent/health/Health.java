package me.pollos.polloshook.impl.module.other.hud.elements.consistent.health;

import java.util.Comparator;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.targeting.TargetUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Health extends HUDModule {
   protected final Value<Boolean> renderEnemyHP = new Value(false, new String[]{"RenderEnemyHP", "renderenemyhealth", "enemyhealth"});
   protected final NumberValue<Float> range;
   protected final Value<Boolean> outline;
   protected final NumberValue<Float> crystalRange;
   protected final NumberValue<Float> yOffset;

   public Health() {
      super(new String[]{"Health", "hp", "heal"});
      this.range = (new NumberValue(12.0F, 0.0F, 18.0F, 0.1F, new String[]{"Range", "r"})).setParent(this.renderEnemyHP).withTag("range");
      this.outline = new Value(false, new String[]{"Outline", "line"});
      this.crystalRange = (new NumberValue(12.0F, 1.0F, 18.0F, 0.1F, new String[]{"CrystalRange", "crystalr"})).withTag("range");
      this.yOffset = new NumberValue(1.0F, 0.0F, 10.0F, 0.1F, new String[]{"YFactor", "yoffset"});
      this.offerValues(new Value[]{this.renderEnemyHP, this.range, this.outline, this.crystalRange, this.yOffset});
   }

   public void draw(DrawContext context) {
      if (!PlayerUtil.isSpectator()) {
         this.renderText(context, mc.player);
         if ((Boolean)this.renderEnemyHP.getValue()) {
            PlayerEntity enemy = TargetUtil.getEnemySimple((double)MathUtil.square((Float)this.range.getValue()));
            if (enemy != null) {
               this.renderText(context, enemy, 10);
            }
         }

      }
   }

   private void renderText(DrawContext context, PlayerEntity player) {
      this.renderText(context, player, 0);
   }

   private void renderText(DrawContext context, PlayerEntity player, int offset) {
      String string = String.valueOf((int)EntityUtil.getHealth(player));
      int width = (int)(((float)context.getScaledWindowWidth() - this.getWidth(string)) / 2.0F);
      int scaledWidth = (int)((float)context.getScaledWindowHeight() / 2.0F);
      int factor = (int)(5.0F * (Float)this.yOffset.getValue());
      int height = scaledWidth + factor + offset;
      if ((Boolean)this.outline.getValue()) {
         Managers.getTextManager().drawString((DrawContext)context, string, (double)(width + 1), (double)height, 0);
         Managers.getTextManager().drawString((DrawContext)context, string, (double)(width - 1), (double)height, 0);
         Managers.getTextManager().drawString((DrawContext)context, string, (double)width, (double)(height + 1), 0);
         Managers.getTextManager().drawString((DrawContext)context, string, (double)width, (double)(height - 1), 0);
      }

      EndCrystalEntity nearestCrystal = mc.world.getOtherEntities(player, player.getBoundingBox().expand((double)(Float)this.crystalRange.getValue(), 6.0D, (double)(Float)this.crystalRange.getValue())).stream().filter((entity) -> {
         return entity instanceof EndCrystalEntity;
      }).map((entity) -> {
         return (EndCrystalEntity)entity;
      }).min(Comparator.comparingDouble((entity) -> {
         return entity.squaredDistanceTo(player);
      })).orElse((EndCrystalEntity)null); 
      
      float health = nearestCrystal == null ? EntityUtil.getHealth(player) : EntityUtil.getHealth(player) - CombatUtil.getDamage(player, mc.world, nearestCrystal);
      Managers.getTextManager().drawString(context, string, (double)width, (double)height, this.getColorForText(health));
   }

   private int getColorForText(float health) {
      if (health >= 16.0F) {
         return 5635925;
      } else {
         return health < 16.0F && health >= 8.0F ? 16777045 : 16733525;
      }
   }

   public boolean isEnemyAlive() {
      PlayerEntity enemy = TargetUtil.getEnemySimple((double)MathUtil.square((Float)this.range.getValue()));
      return (Boolean)this.renderEnemyHP.getValue() && enemy != null;
   }

   
   public NumberValue<Float> getYOffset() {
      return this.yOffset;
   }
}