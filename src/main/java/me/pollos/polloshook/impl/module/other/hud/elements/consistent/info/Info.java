package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.other.fastlatency.FastLatency;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.mode.DurabilityMode;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.mode.StatusEffectMode;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.RomanNumber;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util.status.impl.StatusEffectPreset;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.util.Formatting;
import net.minecraft.world.biome.Biome;

public class Info extends HUDModule {
   private final Value<Boolean> sortByLength = new Value(false, new String[]{"SortByLength", "sort", "sorting"});
   private final Value<Boolean> fps = new Value(true, new String[]{"FPS", "frames"});
   private final Value<Boolean> ping = new Value(true, new String[]{"Ping", "latency"});
   private final Value<Boolean> kmh = new Value(true, new String[]{"KMH", "speed"});
   private final Value<Boolean> tps = new Value(true, new String[]{"TPS", "tickspersecond"});
   private final Value<Boolean> serverWorldTPS;
   private final Value<Boolean> averageTPS;
   private final EnumValue<DurabilityMode> durability;
   private final Value<Boolean> biome;
   private final Value<Boolean> time;
   private final Value<Boolean> serverBrand;
   private final Value<Boolean> advancedTime;
   private final StringValue formatter;
   private final Value<Boolean> statusEffects;
   private final EnumValue<StatusEffectMode> preset;
   private final Value<Boolean> renderIcon;
   private final Value<Boolean> right;
   protected final Value<Boolean> btc;
   protected double price;
   protected final StopWatch timer;

   public Info() {
      super(new String[]{"Info", "informations"});
      this.serverWorldTPS = (new Value(true, new String[]{"ServerWorldTPS", "serverworld"})).setParent(this.tps);
      this.averageTPS = (new Value(false, new String[]{"AverageTPS", "factor"})).setParent(this.tps);
      this.durability = new EnumValue(DurabilityMode.OFF, new String[]{"Durability", "dura"});
      this.biome = new Value(false, new String[]{"Biome", "biomename"});
      this.time = new Value(false, new String[]{"Time", "t"});
      this.serverBrand = new Value(false, new String[]{"ServerBrand", "brand"});
      this.advancedTime = (new Value(false, new String[]{"CustomTime", "advanced"})).setParent(this.time);
      this.formatter = (new StringValue("hh:mm", new String[]{"Format", "f"})).setParent(this.advancedTime);
      this.statusEffects = new Value(false, new String[]{"StatusEffects", "potioneffects", "potions", "potionhud"});
      this.preset = (new EnumValue(StatusEffectMode.GRAY, new String[]{"Preset", "p", "type"})).setParent(this.statusEffects);
      this.renderIcon = (new Value(false, new String[]{"RenderIcon", "icon"})).setParent(this.statusEffects);
      this.right = (new Value(false, new String[]{"Right", "r"})).setParent(this.renderIcon);
      this.btc = new Value(false, new String[]{"BTCPrice", "bitcoin", "btc"});
      this.price = 0.0D;
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.sortByLength, this.fps, this.ping, this.biome, this.durability, this.kmh, this.tps, this.serverWorldTPS, this.averageTPS, this.time, this.advancedTime, this.formatter, this.serverBrand, this.statusEffects, this.preset, this.renderIcon, this.right, this.btc});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
   }

   public void draw(DrawContext context) {
      int y = 10;
      int offset = this.isChatOpened ? 24 : 10;
      int width = context.getScaledWindowWidth();
      int height = context.getScaledWindowHeight();
      String text;
      if ((Boolean)this.statusEffects.getValue()) {
         for(Iterator var6 = mc.player.getStatusEffects().iterator(); var6.hasNext(); offset += y) {
            StatusEffectInstance effect = (StatusEffectInstance)var6.next();
            int amplifier = effect.getAmplifier() + 1;
            text = RomanNumber.toRoman(amplifier);
            StatusEffectPreset effectPreset = ((StatusEffectMode)this.preset.getValue()).getPreset();
            String duration = this.getPotionDurationString(effect);
            String name = ((StatusEffect)effect.getEffectType().value()).getName().getString();
            String built = effectPreset.build().replace("<time>", duration).replace("<amp>", text);
            String potionString = effectPreset.isNoAmpFlag() ? name + built : name + " " + text + built;
            int potionColor = ((StatusEffect)effect.getEffectType().value()).getColor();
            RegistryEntry<StatusEffect> statusEffect = effect.getEffectType();
            Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(statusEffect);
            int textX = (int)((float)width - this.getWidth(potionString) - 2.0F);
            int spriteY = height - offset + 6;
            if ((Boolean)this.renderIcon.getValue()) {
               if ((Boolean)this.right.getValue()) {
                  context.drawSprite(width - 12, spriteY - 7, 0, 10, 10, sprite);
                  this.drawText(context, potionString, (int)((float)width - this.getWidth(potionString) - 16.0F), height - offset, potionColor, true);
               } else {
                  context.drawSprite(textX - 12, spriteY - 7, 0, 10, 10, sprite);
                  this.drawText(context, potionString, (int)((float)width - this.getWidth(potionString) - 2.0F), height - offset, potionColor, true);
               }
            } else {
               this.drawText(context, potionString, (int)((float)width - this.getWidth(potionString) - 2.0F), height - offset, potionColor, true);
            }
         }
      }

      List<String> texts = new ArrayList();
      String timeStr;
      if ((Boolean)this.btc.getValue()) {
         timeStr = "$BTC: %s%.1f".formatted(new Object[]{Formatting.GRAY, this.price});
         texts.add(timeStr);
      }

      String var10000;
      if ((Boolean)this.serverBrand.getValue()) {
         var10000 = String.valueOf(Formatting.GRAY);
         timeStr = "ServerBrand: " + var10000 + mc.getNetworkHandler().getBrand();
         texts.add(timeStr);
      }

      if ((Boolean)this.biome.getValue()) {
         var10000 = String.valueOf(Formatting.GRAY);
         timeStr = "Biome: " + var10000 + this.formatBiome(this.getBiomeString(mc.world.getBiome(Interpolation.getRenderEntity().getBlockPos())));
         texts.add(timeStr);
      }

      if ((Boolean)this.kmh.getValue()) {
         double x = mc.player.getX() - mc.player.prevX;
         double z = mc.player.getZ() - mc.player.prevZ;
         double dist = Math.sqrt(x * x + z * z) / 1000.0D;
         double div = 1.388888888888889E-5D;
         float timer = Managers.getTimerManager().getTimer();
         double speed = dist / div * (double)timer;
         var10000 = String.valueOf(Formatting.GRAY);
         String kmhStr = "Speed: " + var10000 + String.format("%.2f", speed) + "km/h";
         texts.add(kmhStr);
      }

      String fpsStr;
      if ((Boolean)this.time.getValue()) {
         if ((Boolean)this.advancedTime.getValue()) {
            fpsStr = (String)this.formatter.getValue();

            try {
               DateTimeFormatter dtf = DateTimeFormatter.ofPattern(fpsStr);
               timeStr = "Time: " + String.valueOf(Formatting.GRAY) + LocalDateTime.now().format(dtf);
            } catch (Exception var20) {
               timeStr = String.valueOf(Formatting.RED) + "Invalid Formatting :(";
               String link = "https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html";

               Style copyStyle = Style.EMPTY.withClickEvent(new ClickEvent(Action.OPEN_URL, link));
               Text text2 = Text.of("%sInvalid formatting %s[%sClick to open guide%s]".formatted(new Object[]{Formatting.RED, Formatting.BOLD, Formatting.GRAY, Formatting.BOLD})).copy().setStyle(copyStyle);
               ClientLogger.getLogger().log((Text)text2);
            }
         } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm");
            timeStr = "Time: " + String.valueOf(Formatting.GRAY) + LocalDateTime.now().format(dtf);
         }

         texts.add(timeStr);
      }

      if (this.durability.getValue() != DurabilityMode.OFF) {
         ItemStack stack = mc.player.getMainHandStack();
         if (stack.isDamageable()) {
            fpsStr = "Durability: " + String.valueOf(Formatting.GRAY);
            switch((DurabilityMode)this.durability.getValue()) {
            case PERCENT:
               var10000 = fpsStr + "%.1f%%".formatted(new Object[]{ItemUtil.getDamageInPercent(mc.player.getMainHandStack())});
               break;
            case VALUE:
               var10000 = fpsStr + (stack.getMaxDamage() - stack.getDamage());
               break;
            case OFF:
               var10000 = null;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }

            text = var10000;
            texts.add(text);
         }
      }

      boolean singlePlayer = !((FastLatency)Managers.getModuleManager().get(FastLatency.class)).isEnabled() && mc.isInSingleplayer();
      if ((Boolean)this.ping.getValue() && !singlePlayer) {
         var10000 = String.valueOf(Formatting.GRAY);
         fpsStr = "Ping: " + var10000 + NetworkUtil.getPing() + "ms";
         texts.add(fpsStr);
      }

      if ((Boolean)this.tps.getValue()) {
         fpsStr = this.getTpsString();
         texts.add(fpsStr);
      }

      if ((Boolean)this.fps.getValue()) {
         var10000 = String.valueOf(Formatting.GRAY);
         fpsStr = "FPS: " + var10000 + Manager.get().getFpsCalcThread().getFpsCount();
         texts.add(fpsStr);
      }

      if ((Boolean)this.sortByLength.getValue()) {
         texts.sort(Comparator.comparingInt((s) -> {
            return TextUtil.removeColor(s).length();
         }));
         Collections.reverse(texts);
      }

      for(Iterator var31 = texts.iterator(); var31.hasNext(); offset += y) {
         text = (String)var31.next();
         this.drawText(context, text, (int)((float)width - this.getWidth(text) - 2.0F), height - offset);
      }

   }

   private String getTpsString() {
      float tpsFactor = (Boolean)this.serverWorldTPS.getValue() ? Managers.getTpsManager().getServerWorldTPS() : Managers.getTpsManager().getTps();
      String var10000 = String.valueOf(Formatting.GRAY);
      String tpsStr = "TPS: " + var10000 + String.format("%.2f", tpsFactor);
      if ((Boolean)this.averageTPS.getValue()) {
         String avgTpsStr = "%s%.2f".formatted(new Object[]{Formatting.GRAY, Managers.getTpsManager().getCurrentTps()});
         return tpsStr + " " + String.valueOf(Formatting.BOLD) + "(" + avgTpsStr + String.valueOf(Formatting.BOLD) + ")";
      } else {
         return tpsStr;
      }
   }

   private String getPotionDurationString(StatusEffectInstance effect) {
      String str = StatusEffectUtil.getDurationText(effect, 1.0F, mc.world.getTickManager().getTickRate()).getString();
      String inf = Text.translatable("effect.duration.infinite").getString();
      str = str.replace(inf, "**:**");
      return str.length() == 5 && str.startsWith("0") ? str.substring(1) : str;
   }

   public String formatBiome(String biomeName) {
      String formattedName = biomeName.replace("minecraft:", "");
      formattedName = formattedName.replace("_", " ");
      String[] words = formattedName.split(" ");
      StringBuilder capitalizedName = new StringBuilder();
      String[] var5 = words;
      int var6 = words.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String word = var5[var7];
         if (!word.isEmpty()) {
            capitalizedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase()).append(" ");
         }
      }

      return capitalizedName.toString().trim();
   }

   private String getBiomeString(RegistryEntry<Biome> biome) {
      return (String)biome.getKeyOrValue().map((biomeKey) -> {
         return biomeKey.getValue().toString();
      }, (biome_) -> {
         return "[unregistered " + String.valueOf(biome_) + "]";
      });
   }
}