package me.pollos.polloshook.impl.module.render.freecam;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.asm.ducks.entity.IClientPlayerEntity;
import me.pollos.polloshook.impl.module.render.freecam.entity.FreecamEntity;
import me.pollos.polloshook.impl.module.render.freecam.mode.FreecamInteractMode;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Freecam extends ToggleableModule {
   protected final EnumValue<FreecamInteractMode> interact;
   protected final Value<Boolean> rotate;
   protected final NumberValue<Float> horizontal;
   protected final NumberValue<Float> vertical;
   protected FreecamEntity render;
   protected Input input;

   public Freecam() {
      super(new String[]{"Freecam", "freecamera", "camera", "freelook"}, Category.RENDER);
      this.interact = new EnumValue(FreecamInteractMode.CAMERA, new String[]{"Interact", "mode", "raytrace", "raycast"});
      this.rotate = new Value(false, new String[]{"Rotate", "rots", "rotations"});
      this.horizontal = new NumberValue(0.5F, 0.1F, 3.5F, 0.1F, new String[]{"Horizontal", "h", "horizontally"});
      this.vertical = new NumberValue(0.5F, 0.1F, 3.5F, 0.1F, new String[]{"Vertical", "v", "vertically"});
      this.offerValues(new Value[]{this.interact, this.rotate, this.horizontal, this.vertical});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerHand(this), new ListenerCrosshair(this), new ListenerRaycast(this), new ListenerMotion(this), new ListenerCaveCulling(this), new ListenerTurnHead(this), new ListenerUseEntity(this), new ListenerTick(this), new ListenerTickInput(this)});
   }

   protected void onEnable() {
      if (mc.player != null && mc.world != null) {
         if (mc.player.input instanceof KeyboardInput) {
            mc.player.input = new Input();
         }

         this.input = new KeyboardInput(mc.options);
         IClientPlayerEntity accessPlayer = (IClientPlayerEntity)mc.player;
         this.render = new FreecamEntity(mc, mc.world, mc.getNetworkHandler(), mc.player.getStatHandler(), mc.player.getRecipeBook(), accessPlayer.getLastSneaking(), accessPlayer.getLastSprinting());
         this.render.copyPositionAndRotation(mc.player);
         this.render.setBoundingBox(mc.player.getBoundingBox());
         this.setPositionBB(this.render);
         mc.setCameraEntity(this.render);
      } else {
         this.toggle();
      }
   }

   protected void onDisable() {
      if (mc.player != null && mc.world != null) {
         ClientPlayerEntity playerSP = mc.player;
         Input input = mc.player.input;
         if (input != null && input.getClass() == Input.class) {
            mc.executeTask(() -> {
               playerSP.input = new KeyboardInput(mc.options);
            });
         }

         mc.setCameraEntity(mc.player);
      }
   }

   public void onWorldLoad() {
      if (this.isEnabled()) {
         this.setEnabled(false);
      }

   }

   public void setPositionBB(Entity entity) {
      Box bb = entity.getBoundingBox();
      Vec3d vec = new Vec3d((bb.minX + bb.maxX) / 2.0D, bb.minY, (bb.minZ + bb.maxZ) / 2.0D);
      entity.setPosition(vec);
   }

   
   public FreecamEntity getRender() {
      return this.render;
   }

   
   public Input getInput() {
      return this.input;
   }

   public static class TickInputEvent extends Event {
      
      private TickInputEvent() {
      }

      
      public static Freecam.TickInputEvent create() {
         return new Freecam.TickInputEvent();
      }
   }

   public static class FindCrosshairEvent extends Event {
      Entity entity;

      
      public Entity getEntity() {
         return this.entity;
      }

      
      public void setEntity(Entity entity) {
         this.entity = entity;
      }

      
      private FindCrosshairEvent(Entity entity) {
         this.entity = entity;
      }

      
      public static Freecam.FindCrosshairEvent of(Entity entity) {
         return new Freecam.FindCrosshairEvent(entity);
      }
   }

   public static class EntityTurnHeadEvent extends Event {
      Entity entity;
      boolean lockYaw;

      
      public Entity getEntity() {
         return this.entity;
      }

      
      public boolean isLockYaw() {
         return this.lockYaw;
      }

      
      public void setEntity(Entity entity) {
         this.entity = entity;
      }

      
      public void setLockYaw(boolean lockYaw) {
         this.lockYaw = lockYaw;
      }

      
      private EntityTurnHeadEvent(Entity entity, boolean lockYaw) {
         this.entity = entity;
         this.lockYaw = lockYaw;
      }

      
      public static Freecam.EntityTurnHeadEvent of(Entity entity, boolean lockYaw) {
         return new Freecam.EntityTurnHeadEvent(entity, lockYaw);
      }
   }
}
