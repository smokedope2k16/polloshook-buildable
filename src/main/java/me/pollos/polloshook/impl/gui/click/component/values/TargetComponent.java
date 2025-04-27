package me.pollos.polloshook.impl.gui.click.component.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.constant.EnumUtil;
import me.pollos.polloshook.api.value.value.targeting.TargetPreset;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;
import me.pollos.polloshook.impl.gui.click.component.Component;
import net.minecraft.client.gui.DrawContext;

public class TargetComponent extends ValueComponent<TargetPreset, TargetValue> {
   private final TargetValue targetValue;
   private final ArrayList<Component> components = new ArrayList();
   private final List<String> booleanLabels = Arrays.asList("TargetPlayers", "TargetMonsters", "TargetFriendlies", "IgnoreInvis", "IgnoreNaked");
   private final String targetLabel = "Priority";
   private final float goHomeLilNigga2 = 14.0F;

   public TargetComponent(TargetValue enemyFindingValue, Rectangle rect, float offsetX, float offsetY) {
      super(enemyFindingValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), enemyFindingValue);
      this.targetValue = enemyFindingValue;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
      this.components.forEach((c) -> {
         c.moved(this.getFinishedX(), this.getFinishedY());
      });
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      Rectangle rect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, 12.0F);
      boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, rect);
      if (this.isExtended()) {
         this.components.forEach((c) -> {
            c.render(context, mouseX, mouseY, delta);
         });
         this.setHeight((float)(14 + this.components.size() * 14));
      } else {
         this.setHeight(14.0F);
      }

      Render2DMethods.drawRect(context, this.commonRenderRectangle(), hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
      Managers.getTextManager().drawString((DrawContext)context, this.isExtended() ? "-" : "+", (double)((int)(this.getFinishedX() + 90.0F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
      String label = this.getLabel();
      Managers.getTextManager().drawString((DrawContext)context, label, (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      Rectangle rect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, 12.0F);
      boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, rect);
      if (hovered && button == 1) {
         this.setExtended(!this.isExtended());
         this.click();
      }

      if (this.isExtended()) {
         this.components.forEach((c) -> {
            c.mouseClicked(mouseX, mouseY, button);
         });
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   protected Rectangle commonRenderRectangle() {
      return new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + 14.0F - 0.5F);
   }

   public void setExtended(boolean extended) {
      this.components.clear();
      this.initEnums();
      super.setExtended(extended);
   }

   private void initEnums() {
      float offY = 14.0F;

      for(Iterator var2 = this.booleanLabels.iterator(); var2.hasNext(); offY += 14.0F) {
         String str = (String)var2.next();
         this.components.add(this.createBooleanChild(str, this.getFinishedX(), this.getFinishedY(), this.getWidth(), offY));
      }

      this.components.add(this.createEnumComponent(this.getFinishedX(), this.getFinishedY(), this.getWidth(), offY));
   }

   private Component createBooleanChild(String label, float finishedX, float finishedY, float width, float offY) {
      return new Component(label, finishedX, finishedY, 0.0F, offY, width, 14.0F) {
         public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
            Rectangle rect = new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + this.getHeight() - 0.5F);
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, hoveringRect);
            boolean bool = TargetComponent.this.getBooleanSettingFromLabel(label);
            if (hovered && !bool) {
               Render2DMethods.drawRect(context, rect, 1714631475);
            }

            if (bool) {
               Render2DMethods.drawRect(context, rect, hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
            }

            Managers.getTextManager().drawString((DrawContext)context, this.getLabel(), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
         }

         public void moved(float posX, float posY) {
            super.moved(posX, posY);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            if (Render2DMethods.mouseWithinBounds(mouseX, mouseY, hoveringRect)) {
               TargetComponent.this.setBooleanSettingFromLabel(label, !TargetComponent.this.getBooleanSettingFromLabel(label));
               this.click();
            }

            return super.mouseClicked(mouseX, mouseY, button);
         }
      };
   }

   private Component createEnumComponent(float finishedX, float finishedY, float width, float offY) {
      return new Component("Priority", finishedX, finishedY, 0.0F, offY, width, 14.0F) {
         public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
            Rectangle rect = new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + this.getHeight() - 0.5F);
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, hoveringRect);
            Render2DMethods.drawRect(context, rect, hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
            String var10000 = this.getLabel();
            String label = var10000 + ": " + TextUtil.getFixedName(TargetComponent.this.getTargetValue().getTarget().name());
            Managers.getTextManager().drawString((DrawContext)context, label, (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, hoveringRect);
            if (hovered) {
               if (button == 0) {
                  this.click();
                  TargetComponent.this.getTargetValue().setTarget(EnumUtil.next(TargetComponent.this.getTargetValue().getTarget()));
               } else if (button == 1) {
                  this.click();
                  TargetComponent.this.getTargetValue().setTarget(EnumUtil.previous(TargetComponent.this.getTargetValue().getTarget()));
               }
            }

            return super.mouseClicked(mouseX, mouseY, button);
         }
      };
   }

   private boolean getBooleanSettingFromLabel(String label) {
      TargetValue enemyFindingValue = this.getTargetValue();
      String var3 = label.toLowerCase();
      byte var4 = -1;
      switch(var3.hashCode()) {
      case -1976302486:
         if (var3.equals("targetmonsters")) {
            var4 = 1;
         }
         break;
      case -440096038:
         if (var3.equals("targetfriendlies")) {
            var4 = 2;
         }
         break;
      case 145726209:
         if (var3.equals("targetplayers")) {
            var4 = 0;
         }
         break;
      case 1547649609:
         if (var3.equals("ignoreinvis")) {
            var4 = 3;
         }
         break;
      case 1551869221:
         if (var3.equals("ignorenaked")) {
            var4 = 4;
         }
      }

      boolean var10000;
      switch(var4) {
      case 0:
         var10000 = enemyFindingValue.isTargetPlayers();
         break;
      case 1:
         var10000 = enemyFindingValue.isTargetMonsters();
         break;
      case 2:
         var10000 = enemyFindingValue.isTargetFriendlies();
         break;
      case 3:
         var10000 = enemyFindingValue.isIgnoreInvis();
         break;
      case 4:
         var10000 = enemyFindingValue.isIgnoreNaked();
         break;
      default:
         var10000 = false;
      }

      return var10000;
   }

   private void setBooleanSettingFromLabel(String label, boolean bool) {
      TargetValue enemyFindingValue = this.getTargetValue();
      String var4 = label.toLowerCase();
      byte var5 = -1;
      switch(var4.hashCode()) {
      case -1976302486:
         if (var4.equals("targetmonsters")) {
            var5 = 1;
         }
         break;
      case -440096038:
         if (var4.equals("targetfriendlies")) {
            var5 = 2;
         }
         break;
      case 145726209:
         if (var4.equals("targetplayers")) {
            var5 = 0;
         }
         break;
      case 1547649609:
         if (var4.equals("ignoreinvis")) {
            var5 = 3;
         }
         break;
      case 1551869221:
         if (var4.equals("ignorenaked")) {
            var5 = 4;
         }
      }

      switch(var5) {
      case 0:
         enemyFindingValue.setTargetPlayers(bool);
         break;
      case 1:
         enemyFindingValue.setTargetMonsters(bool);
         break;
      case 2:
         enemyFindingValue.setTargetFriendlies(bool);
         break;
      case 3:
         enemyFindingValue.setIgnoreInvis(bool);
         break;
      case 4:
         enemyFindingValue.setIgnoreNaked(bool);
      }

   }

   
   public TargetValue getTargetValue() {
      return this.targetValue;
   }

   
   public ArrayList<Component> getComponents() {
      return this.components;
   }

   
   public List<String> getBooleanLabels() {
      return this.booleanLabels;
   }

   
   public String getTargetLabel() {
      Objects.requireNonNull(this);
      return "Priority";
   }

   
   public float getGoHomeLilNigga2() {
      Objects.requireNonNull(this);
      return 14.0F;
   }
}
