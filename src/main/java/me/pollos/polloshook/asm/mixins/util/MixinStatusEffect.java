package me.pollos.polloshook.asm.mixins.util;

import java.util.HashMap;
import java.util.Map;
import me.pollos.polloshook.impl.module.render.oldpotions.OldPotions;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({StatusEffect.class})
public class MixinStatusEffect {
   @Unique
   private final Map<String, Integer> colors = new HashMap<String, Integer>() {
      {
         this.put("minecraft:speed", 8171462);
         this.put("minecraft:slowness", 5926017);
         this.put("minecraft:haste", 14270531);
         this.put("minecraft:mining_fatigue", 4866583);
         this.put("minecraft:strength", 9643043);
         this.put("minecraft:instant_health", 16262179);
         this.put("minecraft:instant_damage", 4393481);
         this.put("minecraft:jump_boost", 2293580);
         this.put("minecraft:nausea", 5578058);
         this.put("minecraft:regeneration", 13458603);
         this.put("minecraft:fire_resistance", 14981690);
         this.put("minecraft:water_breathing", 3035801);
         this.put("minecraft:invisibility", 8356754);
         this.put("minecraft:blindness", 2039587);
         this.put("minecraft:night_vision", 2039713);
         this.put("minecraft:hunger", 5797459);
         this.put("minecraft:weakness", 4738376);
         this.put("minecraft:poison", 5149489);
         this.put("minecraft:wither", 3484199);
         this.put("minecraft:health_boost", 3484199);
         this.put("minecraft:absorption", 2445989);
         this.put("minecraft:saturation", 16262179);
         this.put("minecraft:glowing", 9740385);
         this.put("minecraft:levitation", 13565951);
         this.put("minecraft:luck", 3381504);
         this.put("minecraft:unluck", 12624973);
         this.put("minecraft:slow_falling", 16773073);
         this.put("minecraft:conduit_power", 1950417);
         this.put("minecraft:dolphins_grace", 8954814);
         this.put("minecraft:bad_omen", 745784);
         this.put("minecraft:hero_of_the_village", 4521796);
         this.put("minecraft:darkness", 2696993);
         this.put("minecraft:resistance", 10044730);
      }
   };

   @Inject(
      method = {"getColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getColorHook(CallbackInfoReturnable<Integer> cir) {
      OldPotions.OldColorEvent event = OldPotions.OldColorEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         Identifier id = Registries.STATUS_EFFECT.getId((StatusEffect)(Object)this);
         if (id == null) {
            return;
         }

         Integer color = (Integer)this.colors.get(id.toString());
         cir.setReturnValue(color);
      }

   }
}
