package me.pollos.polloshook.impl.module.render.trajectories;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.function.Predicate;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;

public class ListenerRender extends ModuleListener<Trajectories, RenderEvent> {
   public ListenerRender(Trajectories module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      float delta = event.getTickDelta();
      MatrixStack matrix = event.getMatrixStack();
      Matrix4f posMatrix = matrix.peek().getPositionMatrix();
      ItemStack stack = mc.player.getMainHandStack();
      if (this.canThrow(stack)) {
         if ((Boolean)((Trajectories)this.module).requireActiveHand.getValue() && stack.getItem() instanceof BowItem && !mc.player.isUsingItem()) {
            return;
         }

         if (stack.getItem() instanceof TridentItem) {
            int j = EnchantUtil.getLevel(Enchantments.RIPTIDE, stack);
            if (j > 0 && mc.player.isTouchingWaterOrRain()) {
               return;
            }
         }

         double yVelo = mc.player.isOnGround() ? 0.0D : mc.player.getVelocity().y;
         float initialVelocity = (float)(((double)this.getThrowSpeedForProjectile(stack) + Math.abs(yVelo)) * (double)((Boolean)((Trajectories)this.module).roundUp.getValue() ? 35.0F : 34.6F));
         Entity renderEntity = mc.player;
         Vec3d eyePos = renderEntity.getEyePos();
         Vec3d right = Vec3d.fromPolar((float)(Integer)((Trajectories)this.module).offsetPitch.getValue(), renderEntity.getYaw() + (float)(Integer)((Trajectories)this.module).offset.getValue()).multiply(0.14000000059604645D);
         Vec3d lookDirection = Vec3d.fromPolar(MathHelper.wrapDegrees(renderEntity.getPitch() + this.getRoll(stack)), renderEntity.getYaw());
         Vec3d lerped = new Vec3d(MathHelper.lerp((double)delta, renderEntity.prevX, renderEntity.getX()), MathHelper.lerp((double)delta, renderEntity.prevY, renderEntity.getY()), MathHelper.lerp((double)delta, renderEntity.prevZ, renderEntity.getZ()));
         Vec3d offset = renderEntity.getPos().subtract(lerped);
         Vec3d velocity = lookDirection.multiply((double)initialVelocity).multiply(0.20000000298023224D);
         Vec3d prevPoint = Vec3d.ZERO.add(eyePos).subtract(offset).add(right);
         Vec3d landingVec = null;
         RenderSystem.enableBlend();
         RenderSystem.enableDepthTest();
         Tessellator tessellator = RenderSystem.renderThreadTesselator();
         RenderSystem.setShader(GameRenderer::getPositionProgram);
         BufferBuilder bufferBuilder = tessellator.begin(DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION);
         Color color = ((Trajectories)this.module).color.getColor();
         RenderMethods.color(color.getRGB());
         Color entityColor = null;

         Vec3d nextPoint;
         Vec3d pos2;
         for(int iteration = 0; iteration < 150; ++iteration) {
            nextPoint = prevPoint.add(velocity.multiply(0.10000000149011612D));
            pos2 = Interpolation.interpolateVec(prevPoint);
            bufferBuilder.vertex(posMatrix, (float)pos2.x, (float)pos2.y, (float)pos2.z);
            RaycastContext context = new RaycastContext(prevPoint, nextPoint, ShapeType.OUTLINE, FluidHandling.NONE, mc.player);
            BlockHitResult result = mc.world.raycast(context);
            if (result.getType() != Type.MISS) {
               landingVec = result.getPos();
               Vec3d interp = Interpolation.interpolateVec(landingVec);
               bufferBuilder.vertex(posMatrix, (float)interp.x, (float)interp.y, (float)interp.z);
               break;
            }

            Box box = new Box(prevPoint, nextPoint);
            Predicate<Entity> predicate = (e) -> {
               return !e.isSpectator() && e.canHit();
            };
            EntityHitResult entityResult = ProjectileUtil.raycast(mc.player, prevPoint, nextPoint, box, predicate, 4096.0D);
            Vec3d interp;
            if (entityResult != null && entityResult.getType() != Type.MISS) {
               landingVec = entityResult.getPos();
               entityColor = ((Trajectories)this.module).entityColor.getColor();
               RenderMethods.color(entityColor.getRGB());
               interp = Interpolation.interpolateVec(landingVec);
               bufferBuilder.vertex(posMatrix, (float)interp.x, (float)interp.y, (float)interp.z);
               break;
            }

            interp = Interpolation.interpolateVec(nextPoint);
            bufferBuilder.vertex(posMatrix, (float)interp.x, (float)interp.y, (float)interp.z);
            prevPoint = nextPoint;
            velocity = velocity.multiply(0.99D).add(0.0D, this.throwableGravity(stack.getItem()), 0.0D);
         }

         BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
         RenderMethods.resetColor();
         RenderSystem.enableDepthTest();
         RenderSystem.disableBlend();
         if (landingVec != null) {
            float size = 0.15F;
            nextPoint = landingVec.add((double)(-size), (double)(-size), (double)(-size));
            pos2 = landingVec.add((double)size, (double)size, (double)size);
            Box box = new Box(nextPoint.x, nextPoint.y, nextPoint.z, pos2.x, pos2.y, pos2.z);
            matrix.push();
            RenderMethods.enable3D();
            MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
            Framebuffer framebuffer = mc.getFramebuffer();
            MSAAFramebuffer.start(smoothBuffer, framebuffer);
            Color boxColor = entityColor != null ? entityColor : color;
            RenderMethods.drawBox(matrix, Interpolation.interpolateAxis(box), ColorUtil.changeAlpha(boxColor, 25));
            RenderMethods.drawOutlineBox(matrix, Interpolation.interpolateAxis(box), ColorUtil.changeAlpha(boxColor, 175), 1.3F);
            MSAAFramebuffer.end(smoothBuffer, framebuffer);
            RenderMethods.disable3D();
            matrix.pop();
         }
      }

   }

   private boolean canThrow(ItemStack stack) {
      Item item = stack.getItem();
      return item instanceof BowItem || item instanceof SnowballItem || item instanceof EggItem || item instanceof ExperienceBottleItem || item instanceof ThrowablePotionItem || item instanceof TridentItem || item instanceof EnderPearlItem || item instanceof CrossbowItem;
   }

   private float getRoll(ItemStack stack) {
      if ((Boolean)((Trajectories)this.module).expTest.getValue()) {
         Item item = stack.getItem();
         if (item instanceof ThrowablePotionItem || item instanceof ExperienceBottleItem) {
            return -20.0F;
         }
      }

      return 0.0F;
   }

   private float getThrowSpeedForProjectile(ItemStack stack) {
      Item item = stack.getItem();
      if (item instanceof BowItem) {
         return !(Boolean)((Trajectories)this.module).requireActiveHand.getValue() && !mc.player.isUsingItem() ? 1.5F : 1.5F * BowItem.getPullProgress(mc.player.getItemUseTime());
      } else if (item instanceof CrossbowItem) {
         ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent)stack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
         return !chargedProjectilesComponent.isEmpty() && chargedProjectilesComponent.getProjectiles().stream().anyMatch((d) -> {
            return d.getItem() instanceof FireworkRocketItem;
         }) ? 1.6F : 3.15F;
      } else if (!(item instanceof SnowballItem) && !(item instanceof EggItem) && !(item instanceof EnderPearlItem)) {
         if (item instanceof ExperienceBottleItem) {
            return 0.7F;
         } else if (item instanceof ThrowablePotionItem) {
            return 0.5F;
         } else {
            return item instanceof TridentItem ? 2.0F : 1.5F;
         }
      } else {
         return 1.5F;
      }
   }

   private double throwableGravity(Item item) {
      if (item instanceof ExperienceBottleItem && (Boolean)((Trajectories)this.module).expTest.getValue()) {
         return -0.33000001311302185D;
      } else if (item instanceof CrossbowItem) {
         CrossbowItem crossbowItem = (CrossbowItem)item;
         ItemStack projectile = RangedWeaponItem.getHeldProjectile(mc.player, crossbowItem.getHeldProjectiles());
         return projectile.getItem() instanceof FireworkRocketItem && CrossbowItem.isCharged(mc.player.getMainHandStack()) ? 0.0D : -0.12999999523162842D;
      } else {
         return item == Items.TRIDENT ? -0.04500000178813934D : -0.12999999523162842D;
      }
   }
}
