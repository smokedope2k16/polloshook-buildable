package me.pollos.polloshook.impl.module.render.chams;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.chams.util.ChamsType;
import me.pollos.polloshook.impl.module.render.chams.util.EntityRenderRunnable;
import me.pollos.polloshook.impl.module.render.chams.util.TotemPopPlayer;
import me.pollos.polloshook.impl.module.render.skeleton.util.CacheConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Chams extends ToggleableModule {
   protected final Value<Boolean> simplePlayers = new Value(false, new String[]{"SimplePlayers", "normalplayers"});
   protected final EnumValue<ChamsType> players;
   protected final EnumValue<ChamsType> crystals;
   protected final NumberValue<Float> lineWidth;
   protected final Value<Boolean> totems;
   protected final Value<Boolean> selfTotem;
   protected final NumberValue<Float> alphaFactor;
   protected final NumberValue<Float> fadeTime;
   protected final NumberValue<Float> elevate;
   protected final NumberValue<Float> bounceSpeed;
   protected final Value<Boolean> legacyHeight;
   protected final NumberValue<Float> rotateSpeed;
   protected final Value<Boolean> blendDamage;
   protected final Value<Boolean> damage;
   protected final ColorValue damageColor;
   protected final Value<Boolean> xqz;
   protected final ColorValue visibleColor;
   protected final ColorValue xqzColor;
   protected final ColorValue wireColor;
   protected final ColorValue totemColor;
   public static NativeImageBackedTexture cancer;
   protected ArrayList<TotemPopPlayer> popped;
   protected final List<EntityRenderRunnable> renderings;
   protected final ConcurrentHashMap<PlayerEntity, CacheConsumerProvider> vertexes;
   protected boolean stop;

   public Chams() {
      super(new String[]{"Chams", "chammies", "charms"}, Category.RENDER);
      this.players = (new EnumValue(ChamsType.WIRE_FRAME, new String[]{"Players", "player"})).setParent(this.simplePlayers, true);
      this.crystals = new EnumValue(ChamsType.WIRE_FRAME, new String[]{"Crystals", "crystal"});
      this.lineWidth = (new NumberValue(1.0F, 1.0F, 5.0F, 0.1F, new String[]{"LineWidth", "width", "width"})).setParent(() -> {
         return this.getLineWidthSupplierParent().isVisible();
      });
      this.totems = new Value(false, new String[]{"Totems", "tots", "totempops"});
      this.selfTotem = (new Value(false, new String[]{"SelfTotem", "selfpop"})).setParent(this.totems);
      this.alphaFactor = (new NumberValue(0.6F, 0.1F, 1.0F, 0.1F, new String[]{"AlphaFactor", "factor"})).setParent(this.totems);
      this.fadeTime = (new NumberValue(1.0F, 0.1F, 5.0F, 0.1F, new String[]{"FadeTime", "fade"})).setParent(this.totems);
      this.elevate = (new NumberValue(0.0F, -10.0F, 10.0F, 0.1F, new String[]{"Elevate", "elevator", "heaven", "hell"})).setParent(this.totems);
      this.bounceSpeed = new NumberValue(1.0F, 0.0F, 5.0F, 0.1F, new String[]{"BounceSpeed", "speed"});
      this.legacyHeight = new Value(false, new String[]{"LegacyHeight", "oldheight"});
      this.rotateSpeed = new NumberValue(1.0F, 0.0F, 5.0F, 0.1F, new String[]{"RotateSpeed", "rotatespeeds"});
      this.blendDamage = new Value(false, new String[]{"BlendDamage", "blenddmg"});
      this.damage = new Value(false, new String[]{"Damage", "damage", "dmg"});
      this.damageColor = (new ColorValue(new Color(255, 0, 0, 191), false, new String[]{"DamageColor", "dmgcolor"})).setParent(this.damage);
      this.xqz = (new Value(false, new String[]{"XQZ", "xqc", "walls"})).setParent(() -> {
         return this.getFillEnabledParent().isVisible();
      });
      this.visibleColor = (new ColorValue(new Color(255, 0, 255, 85), false, new String[]{"VisibleColor", "visiblecolor"})).setParent(() -> {
         return this.getFillEnabledParent().isVisible();
      });
      this.xqzColor = (new ColorValue(new Color(150, 0, 255, 125), false, new String[]{"XQZColor", "xqzcolor"})).setParent(this.xqz);
      this.wireColor = (new ColorValue(new Color(-1), false, new String[]{"WireColor", "wirecolour"})).setParent(() -> {
         return this.getWireFrameSupplierParent().isVisible();
      });
      this.totemColor = (new ColorValue(new Color(0, 255, 247, 125), false, new String[]{"TotemColor", "totempopcolor"})).setParent(this.totems);
      this.popped = new ArrayList();
      this.renderings = new ArrayList();
      this.vertexes = new ConcurrentHashMap();
      this.stop = false;
      this.offerValues(new Value[]{this.simplePlayers, this.players, this.crystals, this.lineWidth, this.totems, this.selfTotem, this.alphaFactor, this.elevate, this.fadeTime, this.bounceSpeed, this.legacyHeight, this.rotateSpeed, this.blendDamage, this.damage, this.damageColor, this.xqz, this.visibleColor, this.xqzColor, this.wireColor, this.totemColor});
      this.offerListeners(new Listener[]{new ListenerPreLivingEntityRender(this), new ListenerPreEndCrystalRender(this), new ListenerPostEndCrystalRender(this), new ListenerTotemPop(this), new ListenerRender(this), new ListenerLogout(this)});
      this.damageColor.addObserver((o) -> {
         this.damageColor(this.isEnabled() ? this.getDamage() : -1308622593);
      });
   }

   protected void onToggle() {
      this.renderings.clear();
      this.vertexes.clear();
      this.damageColor(-1308622593);
   }

   protected void damageColor(int rgba) {
      if (cancer != null) {
         NativeImage nativeImage = cancer.getImage();

         for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {
               if (i < 8) {
                  nativeImage.setColor(j, i, rgba);
               } else {
                  int k = (int)((1.0F - (float)j / 15.0F * 0.75F) * 255.0F);
                  nativeImage.setColor(j, i, k << 24 | 16777215);
               }
            }
         }

         RenderSystem.activeTexture(33985);
         cancer.bindTexture();
         nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, true, false, false);
         RenderSystem.activeTexture(33984);
      }
   }

   protected int getDamage() {
      return (Boolean)this.damage.getValue() && this.isEnabled() ? this.mixColor(this.damageColor.getColor()) : -1308622593;
   }

   protected int mixColor(Color color) {
      int r = color.getRed();
      int g = color.getGreen();
      int elementCodec = color.getBlue();
      int keyCodec = 255 - color.getAlpha();
      return (keyCodec << 24) + (elementCodec << 16) + (g << 8) + r;
   }

   protected Color getVisibleColor(Entity entity) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)entity;
         if (Managers.getFriendManager().isFriend(player)) {
            return this.normalize(entity, ColorUtil.changeAlpha(Colours.get().getFriendColor(), this.visibleColor.getColor().getAlpha()));
         }
      }

      return this.normalize(entity, this.visibleColor.getColor());
   }

   protected Color getWireColor(Entity entity) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)entity;
         if (Managers.getFriendManager().isFriend(player)) {
            return this.normalize(entity, ColorUtil.changeAlpha(Colours.get().getFriendColor(), this.wireColor.getColor().getAlpha()));
         }
      }

      return this.normalize(entity, this.wireColor.getColor());
   }

   protected Color getXQZColor(Entity entity) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)entity;
         if (Managers.getFriendManager().isFriend(player)) {
            return this.normalize(entity, ColorUtil.changeAlpha(Colours.get().getFriendColor(), this.xqzColor.getColor().getAlpha()));
         }
      }

      return this.normalize(entity, this.xqzColor.getColor());
   }

   protected Color normalize(Entity entity, Color color) {
      if (entity instanceof LivingEntity) {
         LivingEntity livingEntity = (LivingEntity)entity;
         if ((Boolean)this.blendDamage.getValue()) {
            boolean flag = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
            if (flag) {
               return ColorUtil.blend(color, this.getDamageColor());
            }
         }
      }

      return color;
   }

   public Color getDamageColor() {
      return (Boolean)this.damage.getValue() ? this.damageColor.getColor() : new Color(1.0F, 0.0F, 0.0F, 0.3F);
   }

   protected final SupplierParent getFillEnabledParent() {
      return new SupplierParent(() -> {
         return this.players.getValue() == ChamsType.BOTH || this.players.getValue() == ChamsType.FILL || this.crystals.getValue() == ChamsType.BOTH || this.crystals.getValue() == ChamsType.FILL;
      }, false);
   }

   protected final SupplierParent getWireFrameSupplierParent() {
      return new SupplierParent(() -> {
         return this.players.getValue() == ChamsType.BOTH || this.players.getValue() == ChamsType.WIRE_FRAME || this.crystals.getValue() == ChamsType.BOTH || this.crystals.getValue() == ChamsType.WIRE_FRAME;
      }, false);
   }

   protected final SupplierParent getLineWidthSupplierParent() {
      return new SupplierParent(() -> {
         return this.getWireFrameSupplierParent().isVisible() || (Boolean)this.totems.getValue();
      }, false);
   }

   
   public Value<Boolean> getSimplePlayers() {
      return this.simplePlayers;
   }

   
   public void setStop(boolean stop) {
      this.stop = stop;
   }
}
