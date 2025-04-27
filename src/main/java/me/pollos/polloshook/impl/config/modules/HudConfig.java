package me.pollos.polloshook.impl.config.modules;


import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.module.hud.HUDModule;

public class HudConfig {
   private final String label;
   private final boolean enabled;
   private int x;
   private int y;
   private int width;
   private int height;
   private DraggableHUDModule.HudPosition part;

   public HudConfig(HUDModule module) {
      this.label = module.getLabel();
      this.enabled = module.isEnabled();
      if (module instanceof DraggableHUDModule) {
         DraggableHUDModule drag = (DraggableHUDModule)module;
         this.x = (int)drag.getTextX();
         this.y = (int)drag.getTextY();
         this.width = (int)drag.getTextWidth();
         this.height = (int)drag.getTextHeight();
         this.part = drag.getPosition();
      }

   }

   
   public String getLabel() {
      return this.label;
   }

   
   public boolean isEnabled() {
      return this.enabled;
   }

   
   public int getX() {
      return this.x;
   }

   
   public int getY() {
      return this.y;
   }

   
   public int getWidth() {
      return this.width;
   }

   
   public int getHeight() {
      return this.height;
   }

   
   public DraggableHUDModule.HudPosition getPart() {
      return this.part;
   }
}
