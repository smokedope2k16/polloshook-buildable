package me.pollos.polloshook.api.module;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.interfaces.Toggleable;
import me.pollos.polloshook.api.minecraft.render.anim.Animation;
import me.pollos.polloshook.api.minecraft.render.anim.DecelerateAnimation;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;

public class ToggleableModule extends Module implements Toggleable {
   private boolean enabled;
   private Keybind keybind = Keybind.noKeyBind();
   private final Animation animation = new DecelerateAnimation(250, 1.0D);

   public ToggleableModule(String[] aliases, Category category) {
      super(aliases, category);
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
      if (this.isEnabled()) {
         this.enabled = true;
         this.onEnable();
         this.registerListeners();
      } else {
         this.enabled = false;
         this.unregisterListeners();
         this.onDisable();
      }

      this.onToggle();
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
      this.onToggle();
   }

   protected void onToggle() {
   }

   protected void onEnable() {
   }

   protected void onDisable() {
   }

   private void registerListeners() {
      this.getListeners().forEach((listener) -> {
         PollosHook.getEventBus().register(listener);
      });
   }

   private void unregisterListeners() {
      this.getListeners().forEach((listener) -> {
         PollosHook.getEventBus().unregister(listener);
      });
   }

   public String getInfo() {
      return "No info";
   }
   
   public boolean isEnabled() {
      return this.enabled;
   }

   public Keybind getKeybind() {
      return this.keybind;
   }

   public Animation getAnimation() {
      return this.animation;
   }

   public String toString() {
      String var10000 = super.toString();
      return "ToggleableModule(super=" + var10000 + ", enabled=" + this.isEnabled() + ", keybind=" + String.valueOf(this.getKeybind()) + ", animation=" + String.valueOf(this.getAnimation()) + ")";
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ToggleableModule)) {
         return false;
      } else {
         ToggleableModule other = (ToggleableModule)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else if (this.isEnabled() != other.isEnabled()) {
            return false;
         } else {
            label40: {
               Object this$keybind = this.getKeybind();
               Object other$keybind = other.getKeybind();
               if (this$keybind == null) {
                  if (other$keybind == null) {
                     break label40;
                  }
               } else if (this$keybind.equals(other$keybind)) {
                  break label40;
               }

               return false;
            }

            Object this$animation = this.getAnimation();
            Object other$animation = other.getAnimation();
            if (this$animation == null) {
               if (other$animation != null) {
                  return false;
               }
            } else if (!this$animation.equals(other$animation)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof ToggleableModule;
   }

   public int hashCode() {
      int result = super.hashCode();
      result = result * 59 + (this.isEnabled() ? 79 : 97);
      Object $keybind = this.getKeybind();
      result = result * 59 + ($keybind == null ? 43 : $keybind.hashCode());
      Object $animation = this.getAnimation();
      result = result * 59 + ($animation == null ? 43 : $animation.hashCode());
      return result;
   }

   public void setKeybind(Keybind keybind) {
      this.keybind = keybind;
   }
}