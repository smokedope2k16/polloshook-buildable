package me.pollos.polloshook.impl.module.render.norender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.events.render.CaveCullingEvent;
import me.pollos.polloshook.impl.module.render.norender.util.NoRenderValue;
import net.minecraft.entity.Entity;

public class NoRender extends ToggleableModule {
   private final NoRenderValue<NoRender.PumpkinOverlayEvent> pumpkin = new NoRenderValue(NoRender.PumpkinOverlayEvent.class, true, new String[]{"PumpkinOverlay", "pumpkins", "pumpkinonhead"});
   private final NoRenderValue<NoRender.FireOverlayEvent> fireOverlay = new NoRenderValue(NoRender.FireOverlayEvent.class, true, new String[]{"FireOverlay", "fire"});
   private final NoRenderValue<NoRender.TotemOverlayEvent> totemOverlay = new NoRenderValue(NoRender.TotemOverlayEvent.class, false, new String[]{"TotemOverlay", "totem", "t"});
   private final NoRenderValue<NoRender.SuffocationOverlayEvent> suffocationOverlay = new NoRenderValue(NoRender.SuffocationOverlayEvent.class, true, new String[]{"SuffocationOverlay", "suffocate", "inwall", "suffocation"});
   private final NoRenderValue<NoRender.EffectTooltipEvent> effectTooltip = new NoRenderValue(NoRender.EffectTooltipEvent.class, false, new String[]{"EffectTooltip", "effecttooltips", "effecttooltip"});
   private final NoRenderValue<NoRender.LiquidVisionEvent> liquidVision = new NoRenderValue(NoRender.LiquidVisionEvent.class, true, new String[]{"LiquidVision", "liquidsight", "liquidsee"});
   private final NoRenderValue<NoRender.HeldItemTooltipEvent> heldItemTooltips = new NoRenderValue(NoRender.HeldItemTooltipEvent.class, false, new String[]{"HeldItemTooltips", "tooltips", "helditem"});
   private final NoRenderValue<NoRender.InvisibleEntityEvent> invisibles = new NoRenderValue(NoRender.InvisibleEntityEvent.class, false, new String[]{"Invisibles", "invisible", "invis"});
   private final NoRenderValue<NoRender.ArmorEvent> armor = new NoRenderValue(NoRender.ArmorEvent.class, false, new String[]{"Armor", "armour", "keyCodec"});
   private final NoRenderValue<NoRender.ToastsEvent> toasts = new NoRenderValue(NoRender.ToastsEvent.class, false, new String[]{"Toasts", "achievements", "toastoverlay"});
   private final NoRenderValue<NoRender.BossbarEvent> bossbarOverlay = new NoRenderValue(NoRender.BossbarEvent.class, false, new String[]{"BossbarOverlay", "bossbar", "boss"});
   private final NoRenderValue<NoRender.PortalOverlayEvent> portalOverlay = new NoRenderValue(NoRender.PortalOverlayEvent.class, true, new String[]{"PortalOverlay", "portal", "netherportal"});
   private final NoRenderValue<NoRender.EntityFireEvent> entityFire = new NoRenderValue(NoRender.EntityFireEvent.class, true, new String[]{"EntityFire", "efire", "entityonfire"});
   private final NoRenderValue<NoRender.FogEvent> fog = new NoRenderValue(NoRender.FogEvent.class, false, new String[]{"Fog", "foggy", "nofog"});
   private final NoRenderValue<CaveCullingEvent> caveCulling = new NoRenderValue(CaveCullingEvent.class, false, new String[]{"CaveCulling", "cave", "caves"});
   private final NoRenderValue<NoRender.VinesEvent> vines = new NoRenderValue(NoRender.VinesEvent.class, false, new String[]{"Vines", "vine", "viney"});
   private final NoRenderValue<NoRender.WorldBorderEvent> worldBorder = new NoRenderValue(NoRender.WorldBorderEvent.class, false, new String[]{"WorldBorder", "border", "bordered"});
   private final NoRenderValue<NoRender.EatingParticlesEvent> eatingParticles = new NoRenderValue(NoRender.EatingParticlesEvent.class, false, new String[]{"EatingParticles", "eatingparticle", "eat"});
   private final Value<Boolean> effectParticles = new Value(false, new String[]{"EffectParticles", "effects"});
   private final Value<Boolean> explosion = new Value(false, new String[]{"Explosion", "explode"});
   private final Value<Boolean> dynamite = new Value(false, new String[]{"TNT", "tn", "dynamite", "boom"});
   private final Value<Boolean> sand = new Value(false, new String[]{"FallingBlocks", "gravel", "sand"});
   private final Value<Boolean> smoke = new Value(false, new String[]{"Smoke", "s", "cigarette"});
   private final Value<Boolean> entities = new Value(false, new String[]{"Entities", "entity"});
   private final NumberValue<Float> entityDistance;
   private final List<Entity> noRenderList;

   public NoRender() {
      super(new String[]{"NoRender", "antirender", "render"}, Category.RENDER);
      this.entityDistance = (new NumberValue(16.0F, 1.0F, 64.0F, 1.0F, new String[]{"EntityDistance", "entityrange"})).withTag("range").setParent(this.entities);
      this.noRenderList = new ArrayList();
      this.offerValues(new Value[]{this.pumpkin, this.fireOverlay, this.totemOverlay, this.suffocationOverlay, this.effectTooltip, this.liquidVision, this.heldItemTooltips, this.invisibles, this.armor, this.totemOverlay, this.bossbarOverlay, this.portalOverlay, this.entityFire, this.fog, this.caveCulling, this.vines, this.worldBorder, this.eatingParticles, this.effectParticles, this.explosion, this.dynamite, this.sand, this.smoke, this.entities, this.entityDistance});
      this.offerListeners(new Listener[]{new ListenerTick(this), new ListenerRenderEntity(this), new ListenerParticle(this)});
      this.getValues().forEach((v) -> {
         if (v instanceof NoRenderValue) {
            NoRenderValue<?> noRenderValue = (NoRenderValue)v;
            noRenderValue.registerListener();
         }

      });
      Iterator var1 = Arrays.asList(this.caveCulling, this.vines).iterator();

      while(var1.hasNext()) {
         Value<Boolean> value = (Value)var1.next();
         value.addObserver((o) -> {
            this.reloadWorld();
         });
      }

   }

   private void reloadWorld() {
      if (mc.worldRenderer != null) {
         mc.worldRenderer.reload();
      }
   }

   
   public NoRenderValue<NoRender.PumpkinOverlayEvent> getPumpkin() {
      return this.pumpkin;
   }

   
   public NoRenderValue<NoRender.FireOverlayEvent> getFireOverlay() {
      return this.fireOverlay;
   }

   
   public NoRenderValue<NoRender.TotemOverlayEvent> getTotemOverlay() {
      return this.totemOverlay;
   }

   
   public NoRenderValue<NoRender.SuffocationOverlayEvent> getSuffocationOverlay() {
      return this.suffocationOverlay;
   }

   
   public NoRenderValue<NoRender.EffectTooltipEvent> getEffectTooltip() {
      return this.effectTooltip;
   }

   
   public NoRenderValue<NoRender.LiquidVisionEvent> getLiquidVision() {
      return this.liquidVision;
   }

   
   public NoRenderValue<NoRender.HeldItemTooltipEvent> getHeldItemTooltips() {
      return this.heldItemTooltips;
   }

   
   public NoRenderValue<NoRender.InvisibleEntityEvent> getInvisibles() {
      return this.invisibles;
   }

   
   public NoRenderValue<NoRender.ArmorEvent> getArmor() {
      return this.armor;
   }

   
   public NoRenderValue<NoRender.ToastsEvent> getToasts() {
      return this.toasts;
   }

   
   public NoRenderValue<NoRender.BossbarEvent> getBossbarOverlay() {
      return this.bossbarOverlay;
   }

   
   public NoRenderValue<NoRender.PortalOverlayEvent> getPortalOverlay() {
      return this.portalOverlay;
   }

   
   public NoRenderValue<NoRender.EntityFireEvent> getEntityFire() {
      return this.entityFire;
   }

   
   public NoRenderValue<NoRender.FogEvent> getFog() {
      return this.fog;
   }

   
   public NoRenderValue<CaveCullingEvent> getCaveCulling() {
      return this.caveCulling;
   }

   
   public NoRenderValue<NoRender.VinesEvent> getVines() {
      return this.vines;
   }

   
   public NoRenderValue<NoRender.WorldBorderEvent> getWorldBorder() {
      return this.worldBorder;
   }

   
   public NoRenderValue<NoRender.EatingParticlesEvent> getEatingParticles() {
      return this.eatingParticles;
   }

   
   public Value<Boolean> getEffectParticles() {
      return this.effectParticles;
   }

   
   public Value<Boolean> getExplosion() {
      return this.explosion;
   }

   
   public Value<Boolean> getDynamite() {
      return this.dynamite;
   }

   
   public Value<Boolean> getSand() {
      return this.sand;
   }

   
   public Value<Boolean> getSmoke() {
      return this.smoke;
   }

   
   public Value<Boolean> getEntities() {
      return this.entities;
   }

   
   public NumberValue<Float> getEntityDistance() {
      return this.entityDistance;
   }

   
   public List<Entity> getNoRenderList() {
      return this.noRenderList;
   }

   public static class PumpkinOverlayEvent extends Event {
      
      private PumpkinOverlayEvent() {
      }

      
      public static NoRender.PumpkinOverlayEvent create() {
         return new NoRender.PumpkinOverlayEvent();
      }
   }

   public static class FireOverlayEvent extends Event {
      
      private FireOverlayEvent() {
      }

      
      public static NoRender.FireOverlayEvent create() {
         return new NoRender.FireOverlayEvent();
      }
   }

   public static class TotemOverlayEvent extends Event {
      
      private TotemOverlayEvent() {
      }

      
      public static NoRender.TotemOverlayEvent create() {
         return new NoRender.TotemOverlayEvent();
      }
   }

   public static class SuffocationOverlayEvent extends Event {
      
      private SuffocationOverlayEvent() {
      }

      
      public static NoRender.SuffocationOverlayEvent create() {
         return new NoRender.SuffocationOverlayEvent();
      }
   }

   public static class EffectTooltipEvent extends Event {
      
      private EffectTooltipEvent() {
      }

      
      public static NoRender.EffectTooltipEvent create() {
         return new NoRender.EffectTooltipEvent();
      }
   }

   public static class LiquidVisionEvent extends Event {
      
      private LiquidVisionEvent() {
      }

      
      public static NoRender.LiquidVisionEvent create() {
         return new NoRender.LiquidVisionEvent();
      }
   }

   public static class HeldItemTooltipEvent extends Event {
      
      private HeldItemTooltipEvent() {
      }

      
      public static NoRender.HeldItemTooltipEvent create() {
         return new NoRender.HeldItemTooltipEvent();
      }
   }

   public static class InvisibleEntityEvent extends Event {
      
      private InvisibleEntityEvent() {
      }

      
      public static NoRender.InvisibleEntityEvent create() {
         return new NoRender.InvisibleEntityEvent();
      }
   }

   public static class ArmorEvent extends Event {
      
      private ArmorEvent() {
      }

      
      public static NoRender.ArmorEvent create() {
         return new NoRender.ArmorEvent();
      }
   }

   public static class ToastsEvent extends Event {
      
      private ToastsEvent() {
      }

      
      public static NoRender.ToastsEvent create() {
         return new NoRender.ToastsEvent();
      }
   }

   public static class BossbarEvent extends Event {
      
      private BossbarEvent() {
      }

      
      public static NoRender.BossbarEvent create() {
         return new NoRender.BossbarEvent();
      }
   }

   public static class PortalOverlayEvent extends Event {
      
      private PortalOverlayEvent() {
      }

      
      public static NoRender.PortalOverlayEvent create() {
         return new NoRender.PortalOverlayEvent();
      }
   }

   public static class EntityFireEvent extends Event {
      
      private EntityFireEvent() {
      }

      
      public static NoRender.EntityFireEvent create() {
         return new NoRender.EntityFireEvent();
      }
   }

   public static class FogEvent extends Event {
      
      private FogEvent() {
      }

      
      public static NoRender.FogEvent create() {
         return new NoRender.FogEvent();
      }
   }

   public static class VinesEvent extends Event {
      
      private VinesEvent() {
      }

      
      public static NoRender.VinesEvent create() {
         return new NoRender.VinesEvent();
      }
   }

   public static class WorldBorderEvent extends Event {
      
      private WorldBorderEvent() {
      }

      
      public static NoRender.WorldBorderEvent create() {
         return new NoRender.WorldBorderEvent();
      }
   }

   public static class EatingParticlesEvent extends Event {
      
      private EatingParticlesEvent() {
      }

      
      public static NoRender.EatingParticlesEvent create() {
         return new NoRender.EatingParticlesEvent();
      }
   }
}
