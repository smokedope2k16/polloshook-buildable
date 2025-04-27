package me.pollos.polloshook.impl.module.render.shader;

import java.awt.Color;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.asm.ducks.world.IGameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class Shader extends ToggleableModule {
   protected final Value<Boolean> players = new Value(true, new String[]{"Players", "people"});
   protected final Value<Boolean> crystals = new Value(true, new String[]{"Crystals", "endcrystals"});
   protected final Value<Boolean> items = new Value(false, new String[]{"Items", "i"});
   protected final Value<Boolean> pearls = new Value(false, new String[]{"Pearls", "pp"});
   protected final Value<Boolean> animals = new Value(false, new String[]{"Animals", "keyCodec"});
   protected final Value<Boolean> monsters = new Value(false, new String[]{"Monsters", "m"});
   protected final Value<Boolean> others = new Value(false, new String[]{"Others", "other"});
   protected final Value<Boolean> hand = new Value(false, new String[]{"Hand", "hands"});
   protected final Value<Boolean> limitDistance = new Value(false, new String[]{"LimitDistance", "limitd"});
   protected final NumberValue<Float> distance;
   protected final NumberValue<Float> lineWith;
   protected final Value<Boolean> rainbow;
   protected final Value<Boolean> fillRainbow;
   protected final NumberValue<Float> speed;
   protected final NumberValue<Float> lightness;
   protected final NumberValue<Float> saturation;
   protected final NumberValue<Float> factor;
   protected final ColorValue color;
   protected boolean renderHand;

   public Shader() {
      super(new String[]{"Shader", "shaders"}, Category.RENDER);
      this.distance = (new NumberValue(128.0F, 32.0F, 256.0F, 2.0F, new String[]{"Distance", "d", "dist"})).withTag("range").setParent(this.limitDistance);
      this.lineWith = new NumberValue(1.0F, 1.0F, 3.0F, new String[]{"LineWidth", "width"});
      this.rainbow = new Value(false, new String[]{"Rainbow", "rain", "gay", "lgbt", "cpv", "pollos"});
      this.fillRainbow = (new Value(false, new String[]{"FillRainbow", "rainbow"})).setParent(this.rainbow);
      this.speed = (new NumberValue(0.5F, 0.1F, 10.0F, 0.1F, new String[]{"Speed", "sped"})).setParent(this.rainbow);
      this.lightness = (new NumberValue(50.0F, 1.0F, 100.0F, 1.0F, new String[]{"Lightness", "light"})).setParent(this.rainbow);
      this.saturation = (new NumberValue(100.0F, 1.0F, 100.0F, 1.0F, new String[]{"Saturation", "sat"})).setParent(this.rainbow);
      this.factor = (new NumberValue(2.5F, 0.5F, 5.0F, 0.1F, new String[]{"Factor", "f"})).setParent(this.rainbow);
      this.color = new ColorValue(Color.WHITE, true, new String[]{"Color", "colour"});
      this.renderHand = true;
      this.offerValues(new Value[]{this.players, this.crystals, this.items, this.pearls, this.animals, this.monsters, this.others, this.hand, this.limitDistance, this.distance, this.lineWith, this.rainbow, this.fillRainbow, this.speed, this.lightness, this.saturation, this.factor, this.color});
      this.offerListeners(new Listener[]{new ListenerRenderWorld(this), new ListenerHand(this)});
   }

   public void renderHandShader(float delta, Matrix4f matrix4f) {
      if ((Boolean)this.hand.getValue()) {
         Managers.getShaderManager().applyShader(() -> {
            ((IGameRenderer)mc.gameRenderer).renderHandFast(mc.gameRenderer.getCamera(), delta, matrix4f);
         }, (Boolean)this.rainbow.getValue());
      }

   }

   public boolean isValid(Entity entity) {
      if (entity == null) {
         return false;
      } else if (mc.player == null) {
         return false;
      } else if (MathHelper.sqrt((float)Interpolation.getRenderEntity().squaredDistanceTo(entity.getPos())) > (Float)this.distance.getValue() && (Boolean)this.limitDistance.getValue()) {
         return false;
      } else if (entity instanceof PlayerEntity) {
         return (Boolean)this.players.getValue();
      } else if (entity instanceof EndCrystalEntity) {
         return (Boolean)this.crystals.getValue();
      } else if (entity instanceof ItemEntity) {
         return (Boolean)this.items.getValue();
      } else if (entity instanceof EnderPearlEntity) {
         return (Boolean)this.pearls.getValue();
      } else if (!(entity instanceof AnimalEntity) && !(entity instanceof AmbientEntity) && !(entity instanceof WaterCreatureEntity)) {
         if (!(entity instanceof Monster) && !(entity instanceof HostileEntity)) {
            return !(entity instanceof PaintingEntity) && !(entity instanceof ArmorStandEntity) && !(entity instanceof FallingBlockEntity) ? (Boolean)this.others.getValue() : false;
         } else {
            return (Boolean)this.monsters.getValue();
         }
      } else {
         return (Boolean)this.animals.getValue();
      }
   }

   public boolean isRainbow() {
      return (Boolean)this.rainbow.getValue();
   }

   
   public Value<Boolean> getPlayers() {
      return this.players;
   }

   
   public Value<Boolean> getCrystals() {
      return this.crystals;
   }

   
   public Value<Boolean> getItems() {
      return this.items;
   }

   
   public Value<Boolean> getPearls() {
      return this.pearls;
   }

   
   public Value<Boolean> getAnimals() {
      return this.animals;
   }

   
   public Value<Boolean> getMonsters() {
      return this.monsters;
   }

   
   public Value<Boolean> getOthers() {
      return this.others;
   }

   
   public Value<Boolean> getHand() {
      return this.hand;
   }

   
   public Value<Boolean> getLimitDistance() {
      return this.limitDistance;
   }

   
   public NumberValue<Float> getDistance() {
      return this.distance;
   }

   
   public NumberValue<Float> getLineWith() {
      return this.lineWith;
   }

   
   public Value<Boolean> getRainbow() {
      return this.rainbow;
   }

   
   public Value<Boolean> getFillRainbow() {
      return this.fillRainbow;
   }

   
   public NumberValue<Float> getSpeed() {
      return this.speed;
   }

   
   public NumberValue<Float> getLightness() {
      return this.lightness;
   }

   
   public NumberValue<Float> getSaturation() {
      return this.saturation;
   }

   
   public NumberValue<Float> getFactor() {
      return this.factor;
   }

   
   public ColorValue getColor() {
      return this.color;
   }

   
   public boolean isRenderHand() {
      return this.renderHand;
   }
}
