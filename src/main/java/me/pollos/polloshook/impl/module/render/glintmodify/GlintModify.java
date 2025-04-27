package me.pollos.polloshook.impl.module.render.glintmodify;

import java.awt.Color;

import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class GlintModify extends ToggleableModule {
   protected final Value<Boolean> armour = new Value(false, new String[]{"Armor", "armour"});
   private final ColorValue color = new ColorValue(new Color(-1), false, new String[]{"Color", "c"});
   private final Value<Boolean> rotateAngle = new Value(false, new String[]{"RotateAngle", "angl"});
   private final NumberValue<Float> angle;
   private final NumberValue<Float> entityScale;
   private final NumberValue<Float> glintScale;
   public static boolean china;

   public GlintModify() {
      super(new String[]{"GlintModify", "colorenchant", "enchantcol", "colorglint"}, Category.RENDER);
      this.angle = (new NumberValue(0.17453292F, 0.0F, 10.0F, 0.001F, new String[]{"Angle", "keyCodec"})).setParent(this.rotateAngle);
      this.entityScale = new NumberValue(0.16F, 0.0F, 1.0F, 0.01F, new String[]{"EntityScale", "entitys", "entity"});
      this.glintScale = new NumberValue(8.0F, 0.0F, 16.0F, 0.1F, new String[]{"GlintScale", "glints", "glint"});
      this.offerValues(new Value[]{this.armour, this.color, this.rotateAngle, this.angle, this.entityScale, this.glintScale});
   }

   
   public Value<Boolean> getArmour() {
      return this.armour;
   }

   
   public ColorValue getColor() {
      return this.color;
   }

   
   public Value<Boolean> getRotateAngle() {
      return this.rotateAngle;
   }

   
   public NumberValue<Float> getAngle() {
      return this.angle;
   }

   
   public NumberValue<Float> getEntityScale() {
      return this.entityScale;
   }

   
   public NumberValue<Float> getGlintScale() {
      return this.glintScale;
   }
}
