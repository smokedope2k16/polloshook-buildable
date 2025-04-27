package me.pollos.polloshook.impl.module.render.esp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.esp.mode.OutlineMode;
import me.pollos.polloshook.impl.module.render.esp.util.ChorusPos;
import me.pollos.polloshook.impl.module.render.esp.util.RenderStack;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<ESP, RenderEvent> {
   public ListenerRender(ESP module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      this.loopChorus();
      List<Entity> entities = new ArrayList(Managers.getEntitiesManager().getEntities());
      this.renderLines(event, entities);
      this.renderNametags(event, entities);
   }

   private void renderLines(RenderEvent event, List<Entity> entities) {
      MatrixStack matrix = event.getMatrixStack();
      matrix.push();
      RenderMethods.enable3D();
      MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
      Framebuffer framebuffer = mc.getFramebuffer();
      MSAAFramebuffer.start(smoothBuffer, framebuffer);
      Iterator var6 = entities.iterator();

      while(true) {
         Box interpolatedBox;
         boolean friend;
         while(true) {
            Entity entity;
            PlayerEntity player;
            do {
               do {
                  do {
                     do {
                        if (!var6.hasNext()) {
                           MSAAFramebuffer.end(smoothBuffer, framebuffer);
                           RenderMethods.disable3D();
                           matrix.pop();
                           return;
                        }

                        entity = (Entity)var6.next();
                     } while(!Interpolation.isVisible(Interpolation.interpolateAxis(entity.getVisibilityBoundingBox()), event));

                     Vec3d vec = Interpolation.interpolateEntity(entity);
                     interpolatedBox = Interpolation.getInterpolatedBox(entity, vec);
                     if (this.isValidEntity(entity)) {
                        boolean var10000;
                        label54: {
                           if (entity instanceof EnderPearlEntity) {
                              EnderPearlEntity enderPearl = (EnderPearlEntity)entity;
                              if (enderPearl.getOwner() != null && Managers.getFriendManager().isFriend(enderPearl.getOwner().getName().getString())) {
                                 var10000 = true;
                                 break label54;
                              }
                           }

                           var10000 = false;
                        }

                        boolean isFriend = var10000;
                        this.render(matrix, interpolatedBox, isFriend ? ColorUtil.changeAlpha(Colours.get().getFriendColor(), ((ESP)this.module).color.getColor().getAlpha()) : ((ESP)this.module).color.getColor());
                     }
                  } while(!(entity instanceof PlayerEntity));

                  player = (PlayerEntity)entity;
               } while(!(Boolean)((ESP)this.module).players.getValue());
            } while(EntityUtil.isDead(player));

            friend = Managers.getFriendManager().isFriend(player);
            boolean mcPlayer = entity == mc.player;
            if ((Boolean)((ESP)this.module).self.getValue() && mcPlayer) {
               if (mc.options.getPerspective() == Perspective.FIRST_PERSON) {
                  continue;
               }
               break;
            } else if (!mcPlayer) {
               break;
            }
         }

         this.render(matrix, interpolatedBox.shrink(0.05000000074505806D, 0.05000000074505806D, 0.05000000074505806D), friend ? ColorUtil.changeAlpha(Colours.get().getFriendColor(), ((ESP)this.module).color.getColor().getAlpha()) : ((ESP)this.module).color.getColor());
      }
   }

   private void renderNametags(RenderEvent event, List<Entity> entities) {
      MatrixStack matrix = event.getMatrixStack();
      if ((Boolean)((ESP)this.module).itemNametags.getValue()) {
         this.renderItemNametags(event, entities);
      }

      Iterator var4 = entities.iterator();

      while(var4.hasNext()) {
         Entity entity = (Entity)var4.next();
         if (Interpolation.isVisible(Interpolation.interpolateAxis(entity.getVisibilityBoundingBox()), event)) {
            Vec3d vec = Interpolation.interpolateEntity(entity);
            if (entity instanceof EnderPearlEntity) {
               EnderPearlEntity pearlEntity = (EnderPearlEntity)entity;
               if ((Boolean)((ESP)this.module).pearlNametags.getValue() && pearlEntity.getOwner() != null) {
                  this.drawTag(matrix, pearlEntity.getOwner().getName().getString(), vec.x, vec.y, vec.z, Interpolation.getMcPlayerInterpolation());
               }
            }
         }
      }

      if ((Boolean)((ESP)this.module).chorus.getValue() && !((ESP)this.module).chorusFruits.isEmpty()) {
         List<ChorusPos> safeCopy = new ArrayList(((ESP)this.module).chorusFruits);
         List<ChorusPos> badFruits = new ArrayList();
         Iterator var19 = safeCopy.iterator();

         while(var19.hasNext()) {
            ChorusPos cf = (ChorusPos)var19.next();
            Vec3d pos = cf.getVec();
            double x = pos.getX() - Interpolation.getRenderPosX();
            double y = pos.getY() - Interpolation.getRenderPosY();
            double z = pos.getZ() - Interpolation.getRenderPosZ();
            int alpha = (int)ColorUtil.fade((double)cf.getTime(), (double)((Float)((ESP)this.module).fadeTime.getValue() * 1000.0F + 50.0F));
            Color color = ColorUtil.changeAlpha(((ESP)this.module).nametagsColor.getColor(), alpha);
            if (alpha == 0) {
               badFruits.add(cf);
            } else {
               this.drawTag(matrix, cf.getLabel(), x, y, z, color, Interpolation.getMcPlayerInterpolation());
            }
         }

         ((ESP)this.module).chorusFruits.addAll(badFruits);
      }

   }

   protected void drawTag(MatrixStack matrix, String displayTag, double x, double y, double z, Vec3d mcPlayerInterpolation) {
      this.drawTag(matrix, displayTag, x, y, z, ((ESP)this.module).nametagsColor.getColor(), mcPlayerInterpolation);
   }

   protected void drawTag(MatrixStack matrix, String displayTag, double x, double y, double z, Color color, Vec3d mcPlayerInterpolation) {
      double xDist = mcPlayerInterpolation.x - x;
      double yDist = mcPlayerInterpolation.y - y;
      double zDist = mcPlayerInterpolation.z - z;
      float dist = MathHelper.sqrt((float)(xDist * xDist + yDist * yDist + zDist * zDist));
      double s = 0.0018D + (double)(MathUtil.fixedNametagScaling(0.3F) * dist);
      if (dist <= 8.0F) {
         s = 0.0245D;
      }

      int textWidth = (int)(Managers.getTextManager().getWidth(displayTag) / 2.0F);
      matrix.push();
      matrix.translate((double)((float)x), y, (double)((float)z));
      matrix.multiply(mc.getEntityRenderDispatcher().getRotation());
      matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrix.scale((float)(-s), (float)(-s), (float)(-s));
      Managers.getTextManager().drawString(matrix, displayTag, (double)(-textWidth), (double)(-Managers.getTextManager().getHeight() + 1), color.getRGB());
      matrix.pop();
   }

   private void render(MatrixStack matrix, Box box, Color color) {
      Color outlineColor = ColorUtil.changeAlpha(color, (Integer)((ESP)this.module).outlineAlpha.getValue());
      Color fillColor = ColorUtil.changeAlpha(color, (Integer)((ESP)this.module).fillAlpha.getValue());
      switch((OutlineMode)((ESP)this.module).mode.getValue()) {
      case BOTH:
         RenderMethods.drawBox(matrix, box, fillColor);
         RenderMethods.drawOutlineBox(matrix, box, outlineColor, (Float)((ESP)this.module).lineWidth.getValue());
         break;
      case FILL:
         RenderMethods.drawBox(matrix, box, fillColor);
         break;
      case OUTLINE:
         RenderMethods.drawOutlineBox(matrix, box, outlineColor, (Float)((ESP)this.module).lineWidth.getValue());
      }

   }

   private boolean isValidEntity(Entity entity) {
      boolean valid = entity instanceof ItemEntity && (Boolean)((ESP)this.module).items.getValue();
      if (entity instanceof ExperienceBottleEntity && (Boolean)((ESP)this.module).xpBottles.getValue()) {
         valid = true;
      }

      if (entity instanceof EnderPearlEntity && (Boolean)((ESP)this.module).enderPearls.getValue()) {
         valid = true;
      }

      return valid;
   }

   private void loopChorus() {
      List<ChorusPos> toRemove = new ArrayList();
      List<ChorusPos> positions = new ArrayList(((ESP)this.module).chorusFruits);
      Iterator var3 = positions.iterator();

      while(var3.hasNext()) {
         ChorusPos cPos = (ChorusPos)var3.next();
         if ((float)(System.currentTimeMillis() - cPos.getTime()) > (Float)((ESP)this.module).fadeTime.getValue() * 1000.0F) {
            toRemove.add(cPos);
         }
      }

      ((ESP)this.module).chorusFruits.removeAll(toRemove);
   }
   
   ItemEntity itemEntity;

   private void renderItemNametags(RenderEvent event, List<Entity> entities) {
      MatrixStack matrix = event.getMatrixStack();
      Set<Entity> processed = new HashSet();
      List<RenderStack> renderStacks = new ArrayList();
      Iterator var6 = entities.iterator();

      while(true) {
         Entity entity;
         do {
            do {
               do {
                  if (!var6.hasNext()) {
                     double yOffset = 0.0D;

                     String displayName;
                     for(Iterator var17 = renderStacks.iterator(); var17.hasNext(); yOffset += 0.25D + (double)Managers.getTextManager().getHeight(displayName) / 100.0D) {
                        RenderStack renderStack = (RenderStack)var17.next();
                        ItemStack stack = renderStack.getStack();
                        int count = renderStack.getCount();
                        Vec3d vec = Interpolation.interpolateEntity(renderStack.getEntity());
                        String var10000 = stack.getName().getString();
                        displayName = var10000 + " x" + count;
                        double finalYOffset = (Boolean)((ESP)this.module).stack.getValue() ? yOffset : 0.0D;
                        this.drawTag(matrix, displayName, vec.x, vec.y + finalYOffset, vec.z, Interpolation.getMcPlayerInterpolation());
                     }

                     return;
                  }

                  entity = (Entity)var6.next();
               } while(!(entity instanceof ItemEntity));

               itemEntity = (ItemEntity)entity;
            } while(processed.contains(entity));
         } while(!Interpolation.isVisible(Interpolation.interpolateAxis(itemEntity.getVisibilityBoundingBox()), event));

         if (!(Boolean)((ESP)this.module).merge.getValue()) {
            ItemStack stack = itemEntity.getStack();
            renderStacks.add(new RenderStack(stack.copy(), entity, stack.getCount()));
         } else {
            List<ItemEntity> collidedItems = entities.stream().filter((e) -> {
               return e instanceof ItemEntity && !processed.contains(e);
            }).map((e) -> {
               return (ItemEntity)e;
            }).filter((e) -> {
               return e.getBoundingBox().expand(0.5D, 0.5D, 0.5D).intersects(itemEntity.getBoundingBox());
            }).toList();
            Map<Item, RenderStack> mapped = new HashMap();
            Iterator var11 = collidedItems.iterator();

            while(var11.hasNext()) {
               ItemEntity collided = (ItemEntity)var11.next();
               ItemStack stack = collided.getStack();
               Item item = stack.getItem();
               mapped.putIfAbsent(item, new RenderStack(stack.copy(), entity, 0));
               RenderStack renderStack = (RenderStack)mapped.get(item);
               renderStack.getStack().setCount(1);
               renderStack.increase(stack.getCount());
            }

            renderStacks.addAll(mapped.values());
            processed.addAll(collidedItems);
         }

         processed.add(itemEntity);
      }
   }
}
