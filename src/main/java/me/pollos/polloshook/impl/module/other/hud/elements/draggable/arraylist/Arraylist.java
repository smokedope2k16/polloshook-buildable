package me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.anim.Animation;
import me.pollos.polloshook.api.minecraft.render.anim.AnimationDirection;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.mode.BracketColor;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.mode.EffectHUDMode;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.mode.OrderingMode;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class Arraylist extends DraggableHUDModule {
   protected final EnumValue<OrderingMode> ordering;
   protected final EnumValue<EffectHUDMode> effectHUD;
   protected final EnumValue<BracketColor> bracketColor;
   protected final NumberValue<Integer> yOffset;
   protected final Value<Boolean> hashColor;
   protected final Value<Boolean> useHeight;
   protected List<Module> modules;
   protected final StopWatch updateTimer;

   public Arraylist() {
      super(new String[]{"Arraylist", "activemodules"});
      this.ordering = new EnumValue(OrderingMode.LENGTH, new String[]{"Ordering", "order"});
      this.effectHUD = new EnumValue(EffectHUDMode.HIDE, new String[]{"EffectHUD", "statuseffects"});
      this.bracketColor = new EnumValue(BracketColor.GRAY, new String[]{"BracketColor", "bracketc"});
      this.yOffset = new NumberValue(0, 0, 1000, new String[]{"YOffset", "ylevel"});
      this.hashColor = new Value(false, new String[]{"HashColor", "hashcodecolor"});
      this.useHeight = new Value(false, new String[]{"UseHeight", "height"});
      this.updateTimer = new StopWatch();
      this.offerValues(new Value[]{this.ordering, this.effectHUD, this.bracketColor, this.yOffset, this.hashColor, this.useHeight});
      PollosHook.getEventBus().register(new ListenerGameLoop(this));
   }

   public boolean isRightToLeftRendering() {
      return true;
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX((float)mc.getWindow().getScaledWidth());
      this.setTextY(2.0F);
      this.setTextHeight(9.0F);
      this.setTextWidth(20.0F);
   }

   public void onWorldLoad() {
      if (this.modules == null) {
         this.modules = new ArrayList(Managers.getModuleManager().getModules());
      }

   }

   public void draw(DrawContext context) {
      if (this.modules == null) {
         this.modules = new ArrayList(Managers.getModuleManager().getModules());
      } else {
         boolean top = (float)context.getScaledWindowHeight() / 2.0F > this.getTextY();
         int offset = 0;
         NoRender NO_RENDER = (NoRender)Managers.getModuleManager().get(NoRender.class);
         if (this.effectHUD.getValue() == EffectHUDMode.MOVE && !mc.player.getStatusEffects().isEmpty()) {
            offset += 27;
         }

         if ((Boolean)NO_RENDER.getEffectTooltip().getValue()) {
            offset = 0;
         }

         int arraylistOffset = 2 + offset;
         List<Float> widths = new ArrayList();
         ArrayList moduleList = new ArrayList(this.modules);

         try {
            if (top) {
               moduleList.sort(Comparator.comparing((modulex) -> {
                  return this.getWidth(this.formatTag((Module) modulex));
               }, Collections.reverseOrder()));
            } else {
               moduleList.sort(Comparator.comparing((modulex) -> {
                  return this.getWidth(this.formatTag((Module) modulex));
               }));
            }
         } catch (Exception var15) {
            var15.printStackTrace();
         }

         Iterator var8 = moduleList.iterator();

         while(true) {
            Module module;
            ToggleableModule toggle;
            Animation moduleAnimation;
            String fullLabel;
            do {
               do {
                  do {
                     if (!var8.hasNext()) {
                        if (!top && !(Boolean)this.getSetPos().getValue()) {
                           int maxY = context.getScaledWindowHeight() - 2;
                           int bottomEdge = (int)(this.getTextY() + (float)arraylistOffset);
                           if (maxY - bottomEdge <= 24) {
                              this.setTextY(this.getTextY() + (float)maxY - (float)bottomEdge + 1.0F);
                           }
                        }

                        float max;
                        try {
                           max = widths.isEmpty() ? this.getWidth("hello") : (Float)Collections.max(widths);
                        } catch (NoSuchElementException var14) {
                           max = this.getWidth("hello");
                        }

                        this.setTextWidth(max);
                        this.setTextHeight((float)(arraylistOffset - 2));
                        return;
                     }

                     module = (Module)var8.next();
                  } while(module.isDrawn());
               } while(!(module instanceof ToggleableModule));

               toggle = (ToggleableModule)module;
               toggle.setDisplayLabel(toggle.getDisplayLabel());
               moduleAnimation = toggle.getAnimation();
               fullLabel = this.formatTag(module);
            } while(!toggle.isEnabled() && moduleAnimation.finished(AnimationDirection.BACKWARDS));

            int offsetY = (Boolean)this.getSetPos().getValue() ? arraylistOffset : (int)((float)arraylistOffset + this.getTextY());
            if ((Boolean)this.hashColor.getValue()) {
               this.drawText(context, fullLabel, moduleAnimation.getX() - 2, offsetY, module.getModuleColor().getRGB(), true);
            } else {
               this.drawText(context, fullLabel, moduleAnimation.getX() - 2, offsetY);
            }

            widths.add(this.getWidth(fullLabel) + 2.0F);
            arraylistOffset += (Boolean)this.useHeight.getValue() ? Managers.getTextManager().getHeight(fullLabel) : 10;
         }
      }
   }

   protected void animate() {
      if (this.modules != null) {
         switch((OrderingMode)this.ordering.getValue()) {
         case LENGTH:
            this.modules.sort((mod1, mod2) -> {
               return (int)(this.getWidth(this.formatTag(mod2)) - this.getWidth(this.formatTag(mod1)));
            });
            break;
         case ABC:
            this.modules.sort(Comparator.comparing(Module::getDisplayLabel));
         }

         Iterator var1 = this.modules.iterator();

         while(var1.hasNext()) {
            Module module = (Module)var1.next();
            if (module instanceof ToggleableModule) {
               ToggleableModule toggle = (ToggleableModule)module;
               Animation moduleAnimation = toggle.getAnimation();
               moduleAnimation.setDirection(toggle.isEnabled() ? AnimationDirection.FORWARDS : AnimationDirection.BACKWARDS);
               String fullLabel = this.formatTag(module);
               int x = (int)(this.getTextX() - this.getWidth(fullLabel));
               x += (int)Math.abs((moduleAnimation.getOutput() - 1.0D) * (double)this.getWidth(fullLabel));
               moduleAnimation.setX(x);
            }
         }

      }
   }

   protected String formatTag(Module module) {
      String tag = module.getFullTag();
      return !TextUtil.isNullOrEmpty(tag) ? this.bracket(module, tag) : module.getDisplayLabel();
   }

   private String bracket(Module mod, String tag) {
      Formatting bracket = ((BracketColor)this.bracketColor.getValue()).getFormatting();
      return String.format("%s %s[%s%s%s]", mod.getDisplayLabel(), bracket, Formatting.GRAY, tag, bracket);
   }

   
   public EnumValue<OrderingMode> getOrdering() {
      return this.ordering;
   }

   
   public EnumValue<EffectHUDMode> getEffectHUD() {
      return this.effectHUD;
   }

   
   public EnumValue<BracketColor> getBracketColor() {
      return this.bracketColor;
   }

   
   public NumberValue<Integer> getYOffset() {
      return this.yOffset;
   }

   
   public Value<Boolean> getHashColor() {
      return this.hashColor;
   }

   
   public Value<Boolean> getUseHeight() {
      return this.useHeight;
   }

   
   public List<Module> getModules() {
      return this.modules;
   }

   
   public StopWatch getUpdateTimer() {
      return this.updateTimer;
   }
}
