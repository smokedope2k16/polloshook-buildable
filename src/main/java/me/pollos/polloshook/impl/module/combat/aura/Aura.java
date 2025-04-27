package me.pollos.polloshook.impl.module.combat.aura;

import java.util.ArrayList;
import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.targeting.TargetPreset;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;
import me.pollos.polloshook.asm.ducks.entity.IPlayerEntity;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.aura.modes.Location;
import me.pollos.polloshook.impl.module.combat.aura.modes.SwordMode;
import me.pollos.polloshook.impl.module.combat.aura.modes.Targeting;
import me.pollos.polloshook.impl.module.combat.aura.modes.TpsSync;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.MaceItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class Aura extends ToggleableModule {
   protected final TargetValue enemyFinding;
   protected final Value<Boolean> eyesPos;
   protected final NumberValue<Float> range;
   protected final NumberValue<Float> wallRange;
   protected final NumberValue<Float> speed;
   protected final Value<Boolean> respawnDisable;
   protected final EnumValue<Targeting> targeting;
   protected final Value<Boolean> rotate;
   protected final Value<Boolean> waitForCrit;
   protected final EnumValue<SwordMode> sword;
   protected final Value<Boolean> maceLagback;
   protected final NumberValue<Float> health;
   protected final EnumValue<TpsSync> tpsSync;
   protected final EnumValue<Location> bone;
   protected final Value<Boolean> stopShield;
   protected final Value<Boolean> disableShield;
   protected final Value<Boolean> protocol;
   protected final Value<Boolean> render;
   protected float[] rotations;
   protected LivingEntity target;
   protected List<LivingEntity> targets;

   public Aura() {
      super(new String[]{"Aura", "killaura", "ka"}, Category.COMBAT);
      this.enemyFinding = new TargetValue(TargetPreset.DEFAULT, new String[]{"Enemy", "e", "ent"});
      this.eyesPos = new Value(false, new String[]{"EyesPos", "eyepos", "eye"});
      this.range = (new NumberValue(6.0F, 1.0F, 6.0F, 0.1F, new String[]{"Range", "rang", "r"})).withTag("range");
      this.wallRange = (new NumberValue(3.0F, 1.0F, 6.0F, 0.1F, new String[]{"WallRange", "walldistance"})).withTag("range");
      this.speed = new NumberValue(1.0F, 0.0F, 1.0F, 0.1F, new String[]{"Speed", "sped", "s"});
      this.respawnDisable = new Value(false, new String[]{"RespawnDisable", "respawn", "disableondeath"});
      this.targeting = new EnumValue(Targeting.SINGLE, new String[]{"Targeting", "t", "target"});
      this.rotate = new Value(false, new String[]{"Rotations", "rotate"});
      this.waitForCrit = new Value(false, new String[]{"WaitForCrit", "critpause"});
      this.sword = new EnumValue(SwordMode.REQUIRE, new String[]{"HeldItem", "weapon"});
      this.maceLagback = (new Value(false, new String[]{"MaceLagback", "macelag"})).setParent(this.sword, SwordMode.NONE, true);
      this.health = (new NumberValue(20.0F, 1.0F, 36.0F, 0.5F, new String[]{"LagbackHealth", "laghealth"})).setParent(this.maceLagback);
      this.tpsSync = new EnumValue(TpsSync.LATEST, new String[]{"TPSSync", "sync"});
      this.bone = new EnumValue(Location.BODY, new String[]{"Bone", "bodypart"});
      this.stopShield = new Value(false, new String[]{"StopShield", "block"});
      this.disableShield = new Value(false, new String[]{"DisableShield", "shielddisable"});
      this.protocol = new Value(false, new String[]{"Protocol", "prot"});
      this.render = new Value(true, new String[]{"Render", "r", "renderer"});
      this.targets = new ArrayList();
      this.offerValues(new Value[]{this.enemyFinding, this.eyesPos, this.range, this.wallRange, this.speed, this.respawnDisable, this.targeting, this.rotate, this.sword, this.maceLagback, this.tpsSync, this.bone, this.waitForCrit, this.stopShield, this.disableShield, this.protocol, this.render});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerRespawn(this), new ListenerRender(this)});
   }

   protected void onToggle() {
      this.target = null;
      this.targets.clear();
   }

   protected String getTag() {
      return this.targeting.getStylizedName();
   }

   protected void attack(float factor) {
      if (this.target != null) {
         boolean stopShield = (Boolean)this.stopShield.getValue() && PlayerUtil.isUsingShield();
         if (stopShield) {
            mc.player.stopUsingItem();
         }

         LivingEntity var4 = this.target;
         if (var4 instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)var4;
            if (PlayerUtil.isUsingShield(player) && (Boolean)this.disableShield.getValue()) {
               int axeSlot = ItemUtil.getHotbarSlotByClass(AxeItem.class);
               int lastSlot = mc.player.getInventory().selectedSlot;
               InventoryUtil.switchToSlot(axeSlot);
               boolean sprinting = mc.player.isSprinting();
               if (!sprinting) {
                  PacketUtil.send(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));
               }

               mc.interactionManager.attackEntity(mc.player, this.target);
               mc.player.swingHand(Hand.MAIN_HAND);
               if (!sprinting) {
                  PacketUtil.send(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));
               }

               InventoryUtil.switchToSlot(lastSlot);
               return;
            }
         }

         double floorDistance = mc.world.raycast(new RaycastContext(mc.player.getPos(), new Vec3d(mc.player.getX(), (double)mc.world.getBottomY(), mc.player.getZ()), ShapeType.COLLIDER, FluidHandling.NONE, mc.player)).getPos().distanceTo(mc.player.getPos());
         boolean mace = mc.player.getMainHandStack().getItem() instanceof MaceItem && this.target.hurtTime <= 0;
         if (!mace || !(floorDistance > (double)Math.max(this.targets.size(), 2))) {
            float fl = !mace || !(this.predictAttackDamage() > EntityUtil.getHealth(this.target)) && !(mc.player.fallDistance > 5.0F) ? (Float)this.speed.getValue() : 0.0F;
            if (mc.player.getAttackCooldownProgress(factor) >= fl) {
               mc.interactionManager.attackEntity(mc.player, this.target);
               mc.player.swingHand(Hand.MAIN_HAND);
               if (this.targeting.getValue() == Targeting.SWITCH) {
                  if (this.targets.size() > 1) {
                     this.targets.remove(this.target);
                     this.target = null;
                  }

                  this.targets.remove(this.target);
                  this.target = null;
               }
            }

            if (stopShield) {
               mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }

            if (mace && (Boolean)this.maceLagback.getValue() && !mc.player.isOnGround() && this.predictAttackDamage() >= (Float)this.health.getValue()) {
               PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.1D, mc.player.getZ(), false);
            }

            this.rotations = null;
         }
      }
   }

   protected float predictAttackDamage() {
      float f = (float)mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
      DamageSource damageSource = mc.player.getDamageSources().playerAttack(mc.player);
      float g = ((IPlayerEntity)mc.player).$getDamageAgainst(this.target, f, damageSource) - f;
      float h = mc.player.getAttackCooldownProgress(0.5F);
      f *= 0.2F + h * h * 0.8F;
      g *= h;
      f += mc.player.getMainHandStack().getItem().getBonusAttackDamage(this.target, f, damageSource);
      if (this.canCrit()) {
         f *= 1.5F;
      }

      return f + g;
   }

   protected boolean canCrit() {
      return mc.player.fallDistance > 0.0F && !mc.player.isOnGround() && !mc.player.isClimbing() && !mc.player.isTouchingWater() && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS) && !mc.player.hasVehicle() && !mc.player.isSprinting();
   }

   protected void setRotations(MotionUpdateEvent event) {
      if (this.rotations != null) {
         Managers.getRotationManager().setRotations(this.rotations, event);
      }

   }

   protected boolean canAttack() {
      LivingEntity var3 = this.target;
      boolean shield;
      if (var3 instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)var3;
         shield = PlayerUtil.isUsingShield(player) && (Boolean)this.disableShield.getValue() && ItemUtil.getHotbarSlotByClass(AxeItem.class) != -1;
      } else {
         shield = false;
      }

      switch((SwordMode)this.sword.getValue()) {
      case SWITCH:
         if (shield) {
            return true;
         } else {
            int swordSlot = ItemUtil.getHotbarSlotByClass(SwordItem.class);
            int maceSlot = ItemUtil.getHotbarSlotByClass(MaceItem.class);
            if (swordSlot != -1) {
               InventoryUtil.switchToSlot(swordSlot);
            } else {
               if (maceSlot == -1) {
                  return false;
               }

               InventoryUtil.switchToSlot(maceSlot);
            }

            return true;
         }
      case REQUIRE:
         if (shield) {
            return true;
         }

         return mc.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof SwordItem || mc.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MaceItem;
      default:
         return false;
      }
   }

   
   public LivingEntity getTarget() {
      return this.target;
   }
}