package me.pollos.polloshook.impl.module.render.skeleton;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.chams.Chams;
import me.pollos.polloshook.impl.module.render.skeleton.util.CacheConsumerProvider;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Skeleton extends ToggleableModule {
   protected final Value<Boolean> self = new Value(false, new String[]{"Self", "sel", "sef"});
   protected final Value<Boolean> damage = new Value(false, new String[]{"Damage", "dmg", "d"});
   protected final NumberValue<Float> lineWidth = new NumberValue(1.0F, 1.0F, 4.0F, 0.1F, new String[]{"LineWidth", "width"});
   protected final ColorValue color = new ColorValue(new Color(-1), true, new String[]{"Color", "colour"});
   protected final ColorValue friendColor = new ColorValue(Colours.get().getFriendColor(), false, new String[]{"FriendColor", "friendcolour"});
   protected final ConcurrentHashMap<PlayerEntity, CacheConsumerProvider> vertexes = new ConcurrentHashMap();

   public Skeleton() {
      super(new String[]{"Skeleton", "skelt"}, Category.RENDER);
      this.offerValues(new Value[]{this.self, this.damage, this.lineWidth, this.color, this.friendColor});
      this.offerListeners(new Listener[]{new ListenerRender(this), new ListenerRenderLivingEntity(this)});
   }

   protected void onToggle() {
      this.vertexes.clear();
   }

   protected void onRender(RenderEvent event) {
      Iterator var2 = mc.world.getPlayers().iterator();

      while(true) {
         AbstractClientPlayerEntity player;
         do {
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        this.vertexes.clear();
                        return;
                     }

                     player = (AbstractClientPlayerEntity)var2.next();
                  } while(player == null);
               } while(EntityUtil.isDead(player));
            } while((Boolean)this.self.getValue() && mc.options.getPerspective() == Perspective.FIRST_PERSON && player == Interpolation.getRenderEntity());
         } while(!(Boolean)this.self.getValue() && player == Interpolation.getRenderEntity());

         CacheConsumerProvider provider = (CacheConsumerProvider)this.vertexes.get(player);
         if (provider != null) {
            MatrixStack matrixStack = event.getMatrixStack();
            matrixStack.push();
            List<Vec3d[]> positions = provider.getCacheConsumer().positions;
            RenderSystem.lineWidth((Float)this.lineWidth.getValue());
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            RenderSystem.defaultBlendFunc();
            BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.LINES);
            boolean bl = this.renderBones(matrixStack, positions, builder, player.getPose() == EntityPose.CROUCHING, this.getColor(player));
            if (bl) {
               BufferRenderer.drawWithGlobalProgram(builder.end());
            }

            matrixStack.pop();
         }
      }
   }

   private Color getColor(PlayerEntity player) {
      Chams CHAMS = (Chams)Managers.getModuleManager().get(Chams.class);
      Color noBlend = Managers.getFriendManager().isFriend(player) ? this.friendColor.getColor() : this.color.getColor();
      boolean flag = player.hurtTime > 0 || player.deathTime > 0;
      return flag && (Boolean)this.damage.getValue() ? ColorUtil.blend(noBlend, CHAMS.getDamageColor()) : noBlend;
   }

   private boolean renderBones(MatrixStack matrixStack, List<Vec3d[]> positions, BufferBuilder builder, boolean sneaking, Color color) {
      Vec3d chest = Vec3d.ZERO;
      Vec3d dick = Vec3d.ZERO;
      boolean rendered = false;
      if (positions.size() >= 36) {
         for(int i = 0; i < 6; ++i) {
            Vec3d boxTop = this.average((Vec3d[])positions.get(i * 6));
            Vec3d boxBottom = this.average((Vec3d[])positions.get(i * 6 + 1));
            Vec3d legBottom;
            switch(i) {
            case 0:
               RenderMethods.drawLine(matrixStack, builder, boxTop.lerp(boxBottom, sneaking ? 0.3D : 0.25D), sneaking ? boxBottom.add(0.0D, 0.05D, 0.0D) : boxBottom, color);
               rendered = true;
               break;
            case 1:
               chest = boxTop.lerp(boxBottom, 0.05D);
               dick = boxTop.lerp(boxBottom, sneaking ? 0.85D : 1.0D);
               RenderMethods.drawLine(matrixStack, builder, boxTop, dick, color);
               break;
            case 2:
            case 3:
               legBottom = boxTop.lerp(boxBottom, 0.05D);
               Vec3d handBottom = boxTop.lerp(boxBottom, 0.9D);
               RenderMethods.drawLine(matrixStack, builder, legBottom, handBottom, color);
               RenderMethods.drawLine(matrixStack, builder, legBottom, chest, color);
               break;
            case 4:
            case 5:
               legBottom = boxTop.lerp(boxBottom, 0.9D);
               RenderMethods.drawLine(matrixStack, builder, boxTop, legBottom, color);
               RenderMethods.drawLine(matrixStack, builder, boxTop, dick, color);
            }
         }
      }

      return rendered;
   }

   private Vec3d average(Vec3d... vecs) {
      Vec3d total = vecs[0];

      for(int i = 1; i < vecs.length; ++i) {
         total = total.add(vecs[i]);
      }

      return total.multiply((double)(1.0F / (float)vecs.length));
   }
}
