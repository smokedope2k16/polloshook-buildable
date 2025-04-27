package me.pollos.polloshook.impl.module.combat.autocrystal;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.rotations.RaycastUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.api.minecraft.world.BlockStateHelper;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.targeting.TargetPreset;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;
import me.pollos.polloshook.asm.ducks.entity.IEndCrystalEntity;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.asm.ducks.world.IClientWorld;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.manager.minecraft.combat.EntitiesManager;
import me.pollos.polloshook.impl.manager.minecraft.combat.SafeManager;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.AntiWeakness;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.AutoCrystalTarget;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.AutoSwitch;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.RenderMode;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.SequentialMode;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.TagInfo;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.Timing;
import me.pollos.polloshook.impl.module.combat.autocrystal.util.CrystalPos;
import me.pollos.polloshook.impl.module.combat.autocrystal.util.CrystalRenderPos;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class AutoCrystal extends ToggleableModule {
   private final EnumValue<Timing> timing;
   private final NumberValue<Float> lockDelay;
   private final Value<Boolean> multiTask;
   private final Value<Boolean> whileMining;
   private final Value<Boolean> swing;
   private final Value<Boolean> breakBlocks;
   private final Value<Boolean> rotate;
   private final Value<Boolean> sequential;
   private final EnumValue<SequentialMode> sequentialMode;
   private final NumberValue<Float> sequentialBuffer;
   private final NumberValue<Float> placeRange;
   private final NumberValue<Float> placeWallRange;
   private final NumberValue<Float> placeSpeed;
   private final Value<Boolean> protocolPlace;
   private final Value<Boolean> await;
   private final Value<Boolean> strictDirection;
   private final NumberValue<Float> attackRange;
   private final NumberValue<Float> attackRangeWall;
   private final NumberValue<Float> attackSpeed;
   private final NumberValue<Float> attackDelay;
   private final NumberValue<Integer> hits;
   private final NumberValue<Integer> maxHits;
   private final NumberValue<Integer> ticksExisted;
   private final Value<Boolean> strictRange;
   private final Value<Boolean> antiStuck;
   private final Value<Boolean> boost;
   private final Value<Boolean> inhibit;
   private final EnumValue<AntiWeakness> antiWeakness;
   private final Value<Boolean> eyePos;
   private final TargetValue target;
   private final NumberValue<Float> enemyRange;
   private final NumberValue<Float> minDMG;
   private final NumberValue<Float> maxSelfDMG;
   private final Value<Boolean> suicide;
   private final NumberValue<Float> lethalMlt;
   private final Value<Boolean> armorBreaker;
   private final NumberValue<Integer> armorPercent;
   private final NumberValue<Integer> extrapolation;
   private final EnumValue<AutoSwitch> swap;
   private final Value<Boolean> forceDesync;
   private final NumberValue<Float> altPlaceSpeed;
   private final NumberValue<Float> swapDelay;
   private final EnumValue<RenderMode> renderMode;
   private final NumberValue<Float> renderFactor;
   private final Value<Boolean> renderDamage;
   private final NumberValue<Float> scaleFactor;
   private final Value<Boolean> renderAttacks;
   private final Value<Boolean> renderExtrapolation;
   private final NumberValue<Float> alphaFactor;
   private final EnumValue<TagInfo> tagInfo;
   protected final Value<Boolean> reduceHitbox;
   protected final NumberValue<Integer> threadDelay;
   protected final BlockStateHelper stateHelper;
   private CrystalRenderPos render;
   private final List<CrystalRenderPos> fadePositions;
   private LivingEntity enemy;
   private boolean isUsingOffhand;
   private final ConcurrentHashMap<BlockPos, Long> pendingPlacePositions;
   private final ConcurrentHashMap<BlockPos, Long> confirmedPlacePositions;
   private final ConcurrentHashMap<Integer, Long> inhibitedCrystals;
   private final ConcurrentHashMap<BlockPos, Long> blockedPositions;
   private final ConcurrentHashMap<Integer, Long> crystalDelays;
   private final ConcurrentHashMap<LivingEntity, Double> cacheArmor;
   private final ConcurrentHashMap<LivingEntity, Vec3d> cachePositions;
   private CrystalPos lastCrystalPos;
   private final long pingTimeout;
   private final StopWatch placeTimer;
   private final StopWatch breakTimer;
   private final StopWatch switchTimer;
   private final StopWatch renderTimer;
   private final StopWatch collisionTimer;
   private final StopWatch targetTimer;
   private final StopWatch threadTimer;
   private final StopWatch rotationsTimer;
   private final StopWatch lockTimer;
   private final StopWatch debugTimer;
   private boolean isPlacing;
   private List<LivingEntity> targets;
   private Runnable attack;
   private Runnable place;
   private float[] rotations;
   private float[] breakRotations;
   private float[] placeRotations;
   private CrystalPos placeTarget;
   private EndCrystalEntity crystalTarget;
   private boolean wasEating;
   private boolean skipBreak;
   private boolean skipPlace;
   private int lastSpawnID;
   private String attackLatency;
   private String calculationTime;
   private final List<Long> crystalsPerSecond;
   private int maxCount;
   private final List<BlockPos> attacks;
   private boolean locked;
   public static boolean SWITCH_LOCK = false;

   public AutoCrystal() {
      super(new String[]{"AutoCrystal", "crystalaura", "ca"}, Category.COMBAT);
      this.timing = new EnumValue(Timing.FAST, new String[]{"Timing", "timin"});
      this.lockDelay = (new NumberValue(1.0F, 0.0F, 5.0F, 0.1F, new String[]{"LockTime", "locker"})).setParent(this.timing, Timing.STRICT);
      this.multiTask = new Value(true, new String[]{"MultiTask", "mutlitask"});
      this.whileMining = new Value(true, new String[]{"WhileMining", "whileattacking"});
      this.swing = new Value(true, new String[]{"Swing", "punch"});
      this.breakBlocks = new Value(false, new String[]{"DestroyBlocks", "breakblocks"});
      this.rotate = new Value(true, new String[]{"Rotate", "rotations"});
      this.sequential = (new Value(false, new String[]{"Sequential", "sequence"})).setParent(this.rotate);
      this.sequentialMode = (new EnumValue(SequentialMode.NORMAL, new String[]{"Sequence", "mode", "sequentialmode"})).setParent(this.sequential);
      this.sequentialBuffer = (new NumberValue(1.0F, 0.1F, 5.0F, 0.1F, new String[]{"Buffer", "sequencebuffer", "sequentialbuffer"})).setParent(this.sequential);
      this.placeRange = (new NumberValue(5.0F, 1.0F, 6.0F, 0.1F, new String[]{"PlaceRange", "range"})).withTag("range");
      this.placeWallRange = (new NumberValue(3.0F, 0.0F, 6.0F, 0.1F, new String[]{"PlaceRangeWall", "wallrange"})).withTag("range");
      this.placeSpeed = new NumberValue(20.0F, 0.1F, 20.0F, 0.1F, new String[]{"PlaceSpeed", "placesped"});
      this.protocolPlace = new Value(true, new String[]{"ProtocolPlacement", "protocol"});
      this.await = new Value(true, new String[]{"Await", "awaiting"});
      this.strictDirection = new Value(false, new String[]{"StrictDirection", "strictdir"});
      this.attackRange = (new NumberValue(5.0F, 1.0F, 6.0F, 0.1F, new String[]{"AttackRange", "arange"})).withTag("range");
      this.attackRangeWall = (new NumberValue(3.0F, 0.0F, 6.0F, 0.1F, new String[]{"AttackRangeWall", "awallrange"})).withTag("range");
      this.attackSpeed = new NumberValue(20.0F, 0.1F, 20.0F, 0.1F, new String[]{"AttackSpeed", "attack"});
      this.attackDelay = new NumberValue(0.0F, 0.0F, 5.0F, 0.1F, new String[]{"AttackDelay", "attackdel"});
      this.hits = new NumberValue(1, 1, 3, new String[]{"Hits", "hit"});
      this.maxHits = new NumberValue(2, 1, 10, new String[]{"MaxHits", "maxhit"});
      this.ticksExisted = new NumberValue(0, 0, 10, new String[]{"TicksExisted", "age"});
      this.strictRange = new Value(false, new String[]{"StrictRange", "strictranges"});
      this.antiStuck = new Value(false, new String[]{"AntiStuck", "antistucker"});
      this.boost = new Value(false, new String[]{"Boost", "boosttation"});
      this.inhibit = new Value(true, new String[]{"Inhibit", "inhibt"});
      this.antiWeakness = new EnumValue(AntiWeakness.OFF, new String[]{"AntiWeakness", "weakness"});
      this.eyePos = new Value(false, new String[]{"EyePos", "eyes"});
      this.target = new TargetValue(new TargetPreset(true, false, false, false, false, AutoCrystalTarget.DAMAGE), new String[]{"Targeting", "targets", "enemy"});
      this.enemyRange = (new NumberValue(9.0F, 0.1F, 18.0F, 0.1F, new String[]{"EnemyRange", "enemy"})).withTag("range");
      this.minDMG = new NumberValue(5.0F, 1.0F, 20.0F, 0.1F, new String[]{"MinDMG", "mindamage"});
      this.maxSelfDMG = new NumberValue(11.0F, 0.1F, 20.0F, 0.1F, new String[]{"MaxSelfDMG", "maxselfdamage"});
      this.suicide = new Value(false, new String[]{"Suicide", "suisside", "klemen", "bandhu"});
      this.lethalMlt = new NumberValue(1.0F, 1.0F, 2.5F, 0.1F, new String[]{"LethalMultiplier", "lethalmlt"});
      this.armorBreaker = new Value(false, new String[]{"ArmorBreaker", "breaker"});
      this.armorPercent = (new NumberValue(20, 1, 100, new String[]{"ArmorScale", "scale"})).setParent(this.armorBreaker).withTag("%");
      this.extrapolation = new NumberValue(0, 0, 10, new String[]{"Extrapolation", "interpolation"});
      this.swap = new EnumValue(AutoSwitch.NORMAL, new String[]{"Switch", "swap"});
      this.forceDesync = (new Value(false, new String[]{"ForceDesync", "1.12sync", "resync"})).setParent(this.swap, AutoSwitch.ALT_SWAP);
      this.altPlaceSpeed = (new NumberValue(19.0F, 0.1F, 20.0F, 0.1F, new String[]{"AltPlaceSpeed", "altplacesped"})).setParent(this.swap, AutoSwitch.ALT_SWAP);
      this.swapDelay = (new NumberValue(2.5F, 0.0F, 5.0F, 0.1F, new String[]{"SwapDelay", "switchdelay"})).setParent(this.swap, AutoSwitch.NORMAL);
      this.renderMode = new EnumValue(RenderMode.NORMAL, new String[]{"Render", "rendermode"});
      this.renderFactor = (new NumberValue(1.0F, 0.0F, 5.0F, 0.1F, new String[]{"Factor", "fact", "factoid", "renderfactor"})).setParent(() -> {
         return this.renderMode.getValue() == RenderMode.FADE;
      });
      this.renderDamage = new Value(false, new String[]{"RenderDamage", "damage"});
      this.scaleFactor = (new NumberValue(1.0F, 0.5F, 2.5F, 0.1F, new String[]{"ScaleFactor", "scalefac"})).setParent(this.renderDamage);
      this.renderAttacks = (new Value(false, new String[]{"RenderAttacks", "renderattack"})).setParent(this.renderMode, RenderMode.FADE, true);
      this.renderExtrapolation = new Value(false, new String[]{"RenderExtrapolation", "renderinterp"});
      this.alphaFactor = new NumberValue(1.0F, 0.0F, 3.0F, 0.1F, new String[]{"AlphaFactor", "factoid"});
      this.tagInfo = new EnumValue(TagInfo.TARGET, new String[]{"TagInfo", "tag"});
      this.reduceHitbox = new Value(false, new String[]{"ReduceCrystalBox", "lol"});
      this.threadDelay = (new NumberValue(0, 0, 500, new String[]{"ThreadDelay", "delay"})).withTag("ms");
      this.stateHelper = new BlockStateHelper();
      this.fadePositions = new ArrayList();
      this.isUsingOffhand = false;
      this.pendingPlacePositions = new ConcurrentHashMap();
      this.confirmedPlacePositions = new ConcurrentHashMap();
      this.inhibitedCrystals = new ConcurrentHashMap();
      this.blockedPositions = new ConcurrentHashMap();
      this.crystalDelays = new ConcurrentHashMap();
      this.cacheArmor = new ConcurrentHashMap();
      this.cachePositions = new ConcurrentHashMap();
      this.lastCrystalPos = null;
      this.pingTimeout = 250L;
      this.placeTimer = new StopWatch();
      this.breakTimer = new StopWatch();
      this.switchTimer = new StopWatch();
      this.renderTimer = new StopWatch();
      this.collisionTimer = new StopWatch();
      this.targetTimer = new StopWatch();
      this.threadTimer = new StopWatch();
      this.rotationsTimer = new StopWatch();
      this.lockTimer = new StopWatch();
      this.debugTimer = new StopWatch();
      this.isPlacing = false;
      this.targets = new ArrayList();
      this.attackLatency = null;
      this.calculationTime = null;
      this.crystalsPerSecond = new ArrayList();
      this.maxCount = 0;
      this.attacks = new ArrayList();
      this.locked = false;
      this.offerValues(new Value[]{this.timing, this.lockDelay, this.multiTask, this.whileMining, this.swing, this.breakBlocks, this.rotate, this.sequential, this.sequentialMode, this.sequentialBuffer, this.placeRange, this.placeWallRange, this.placeSpeed, this.altPlaceSpeed, this.protocolPlace, this.strictDirection, this.await, this.attackRange, this.attackRangeWall, this.attackSpeed, this.attackDelay, this.hits, this.maxHits, this.ticksExisted, this.strictRange, this.antiStuck, this.boost, this.inhibit, this.antiWeakness, this.eyePos, this.target, this.enemyRange, this.minDMG, this.maxSelfDMG, this.suicide, this.lethalMlt, this.armorBreaker, this.armorPercent, this.extrapolation, this.swap, this.forceDesync, this.swapDelay, this.renderMode, this.renderFactor, this.renderDamage, this.scaleFactor, this.renderAttacks, this.renderExtrapolation, this.alphaFactor, this.tagInfo, this.reduceHitbox, this.threadDelay});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerRender(this), new ListenerSound(this), new ListenerBlockUpdate(this), new ListenerSpawn(this), new ListenerDestroyEntity(this), new ListenerMine(this), new ListenerDeath(this), new ListenerGameLoop(this)});
   }

   protected String getTag() {
      return this.getTagInfo();
   }

   protected void onToggle() {
      this.reset();
   }

   public void onWorldLoad() {
      this.reset();
   }

   public boolean isMovingLikeAaronBandhu() {
      return false;
   }

   protected void initThreads() {
      Thread autoCrystalThread = this.getMainThread();
      autoCrystalThread.start();
      Thread calcThread = this.getCalcThread();
      calcThread.start();
   }

   public void reset() {
      this.placeTimer.reset();
      this.breakTimer.reset();
      this.switchTimer.reset();
      this.renderTimer.reset();
      this.collisionTimer.reset();
      this.debugTimer.reset();
      this.pendingPlacePositions.clear();
      this.confirmedPlacePositions.clear();
      this.crystalDelays.clear();
      this.render = null;
      this.fadePositions.clear();
      this.enemy = null;
      this.targets.clear();
      this.rotations = null;
      this.placeRotations = null;
      this.breakRotations = null;
      this.crystalTarget = null;
      this.placeTarget = null;
      this.skipBreak = false;
      this.skipPlace = false;
      this.place = null;
      this.attack = null;
      this.calculationTime = "0.00ms";
      this.attackLatency = "0.0";
      this.maxCount = 0;
      this.crystalsPerSecond.clear();
      this.attacks.clear();
      this.isPlacing = false;
      this.cacheArmor.clear();
      this.cachePositions.clear();
      this.lastCrystalPos = null;
      this.stateHelper.clearAllStates();
   }

   public void setLock(boolean lock) {
      this.locked = lock;
      this.lockTimer.reset();
   }

   private Thread getMainThread() {
      Thread autoCrystalThread = new Thread(() -> {
         try {
            if (mc.world != null && mc.player != null && mc.interactionManager != null && !mc.isPaused()) {
               try {
                  this.onThread();
               } catch (Exception var2) {
                  var2.printStackTrace();
               }
            } else {
               this.reset();
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }

      });
      autoCrystalThread.setName("polloshook-AutoCrystalMain");
      return autoCrystalThread;
   }

   private void onThread() {
      if (this.lockTimer.passed((double)((Float)this.lockDelay.getValue() * 100.0F))) {
         this.locked = false;
      }

      if (this.rotationsTimer.passed(250L)) {
         this.placeRotations = null;
         this.breakRotations = null;
      }

      long currentTime = System.currentTimeMillis();
      List<Long> toRemove = new ArrayList();
      Iterator var4 = (new ArrayList(this.crystalsPerSecond)).iterator();

      while(var4.hasNext()) {
         long l = (Long)var4.next();
         if (l + 2000L < currentTime) {
            toRemove.add(l);
         }
      }

      this.crystalsPerSecond.removeAll(toRemove);
      if (this.crystalsPerSecond.size() > this.maxCount) {
         this.maxCount = this.crystalsPerSecond.size();
      }

      if (this.debugTimer.passed(1500L)) {
         if (this.attacks.size() < this.maxCount) {
            --this.maxCount;
         }

         this.debugTimer.reset();
      }

      if (this.targetTimer.passed(500L)) {
         this.enemy = null;
      }

      if (this.renderTimer.passed(this.getExpireFactor())) {
         this.render = null;
         this.renderTimer.reset();
         this.attacks.clear();
      }

      this.isUsingOffhand = mc.player.getOffHandStack().getItem() instanceof EndCrystalItem;
      this.expire();
      if ((!this.locked || this.timing.getValue() != Timing.STRICT) && !EntityUtil.isDead(mc.player) && !this.targets.isEmpty() && (!this.wasEating || !mc.options.useKey.isPressed())) {
         boolean multiTasking = PlayerUtil.isUsingBow() || PlayerUtil.isDrinking() || PlayerUtil.isEating();
         if (!(Boolean)this.multiTask.getValue() && multiTasking) {
            this.placeRotations = null;
            this.breakRotations = null;
            this.wasEating = true;
         } else {
            this.wasEating = false;
            if (!(Boolean)this.whileMining.getValue() && PlayerUtil.isMining()) {
               this.placeRotations = null;
               this.breakRotations = null;
            } else {
               if (this.skipBreak || !this.generateBreakAction(this.crystalTarget)) {
                  this.skipBreak = false;
                  this.generatePlaceAction(this.placeTarget);
               }

               if (this.attack != null || this.place != null) {
                  this.runAttack();
                  this.runPlace();
               }
            }
         }
      } else {
         this.placeRotations = null;
         this.breakRotations = null;
      }
   }

   private Thread getCalcThread() {
      Thread calc = new Thread(() -> {
         try {
            if (mc.world != null && mc.player != null && mc.interactionManager != null && !mc.isPaused() && this.threadTimer.passed((long)(Integer)this.threadDelay.getValue())) {
               this.threadTimer.reset();
               List<LivingEntity> enemies = new ArrayList();
               List<Entity> loadedEntities = new ArrayList(Managers.getEntitiesManager().getEntities());
               if (this.target.getTarget().name().equalsIgnoreCase("DAMAGE")) {
                  enemies = this.target.getTargets((Float)this.enemyRange.getValue(), loadedEntities);
               } else {
                  LivingEntity gayKid = this.target.getEnemy((Float)this.enemyRange.getValue(), loadedEntities);
                  if (gayKid != null) {
                     ((List)enemies).add(gayKid);
                  }
               }

               if (this.isMovingLikeAaronBandhu()) {
                  ((List)enemies).clear();
                  ((List)enemies).add(mc.player);
                  this.enemy = mc.player;
               }

               if (this.enemy != null && ((List)enemies).contains(this.enemy)) {
                  this.targetTimer.reset();
               }

               this.targets = (List)enemies;
               if (!((List)enemies).isEmpty()) {
                  long startTime = System.currentTimeMillis();
                  int crystalSlot = ItemUtil.findHotbarItem(Items.END_CRYSTAL);
                  if (crystalSlot != -1 || this.isUsingOffhand) {
                     this.placeTarget = this.getCrystalPos(new ArrayList((Collection)enemies));
                  }

                  EndCrystalEntity crystal = this.getBestCrystal(loadedEntities, new ArrayList((Collection)enemies));
                  if (crystal != null) {
                     this.crystalTarget = crystal;
                  }

                  float calcTime = (float)(System.currentTimeMillis() - startTime) / 100.0F;
                  Object[] var10002 = new Object[]{calcTime};
                  this.calculationTime = String.format("%.2f", var10002) + "ms";
                  this.cacheEntities((List)enemies);
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

      });
      calc.setName("polloshook-AutoCrystalCalc");
      return calc;
   }

   protected void onEvent(MotionUpdateEvent event) {
      if (event.getStage() == Stage.PRE && (Boolean)this.rotate.getValue()) {
         this.handleRotations(event);
      }

   }

   private void cacheEntities(List<LivingEntity> enemies) {
      List<LivingEntity> entities = new ArrayList(enemies);
      entities.add(mc.player);
      SafeManager SAFE = Managers.getSafeManager();

      LivingEntity entity;
      for(Iterator var4 = entities.iterator(); var4.hasNext(); this.cachePositions.put(entity, entity == mc.player ? Managers.getPositionManager().getVec() : ((ILivingEntity)entity).getServerVec())) {
         entity = (LivingEntity)var4.next();
         if (SAFE.getArmorValues().containsKey(entity)) {
            this.cacheArmor.put(entity, (double)(Integer)SAFE.getArmorValues().get(entity) + (Double)SAFE.getArmorAttributes().get(entity));
         }
      }

   }

   private CrystalPos getCrystalPos(List<LivingEntity> targets) {
      float damage = 0.5F;
      CrystalPos crystalPos = null;
      List<BlockPos> positions = this.getCrystalPositions();
      LivingEntity gayKid = null;
      HashMap<BlockPos, Float> selfDamages = new HashMap();
      Iterator var7 = positions.iterator();

      label84:
      while(var7.hasNext()) {
         BlockPos pos = (BlockPos)var7.next();
         Iterator var9 = targets.iterator();

         while(true) {
            LivingEntity target;
            float enemyHealth;
            float enemyDamage;
            float selfDamage;
            do {
               if (!var9.hasNext()) {
                  continue label84;
               }

               target = (LivingEntity)var9.next();
               enemyHealth = EntityUtil.getHealth(target);
               enemyDamage = CombatUtil.getDamage(target, mc.world, 6.0F, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, (Boolean)this.breakBlocks.getValue(), (Integer)this.extrapolation.getValue(), true);
               selfDamage = 0.0F;
               if (!(Boolean)this.suicide.getValue()) {
                  if (selfDamages.containsKey(pos)) {
                     selfDamage = (Float)selfDamages.get(pos);
                  } else {
                     selfDamage = CombatUtil.getDamage(mc.player, mc.world, 6.0F, (double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, (Boolean)this.breakBlocks.getValue(), true);
                     selfDamages.put(pos, selfDamage);
                  }
               }
            } while(!(enemyDamage >= (Float)this.minDMG.getValue()) && !(enemyDamage >= EntityUtil.getHealth(target)) && !this.isArmorUnderPercent(target, enemyDamage));

            boolean isSuicide = selfDamage > (Float)this.maxSelfDMG.getValue() && this.isLethal(enemyHealth, enemyDamage) || (double)selfDamage + 2.0D >= (double)EntityUtil.getHealth(mc.player);
            boolean invalidDamage = selfDamage >= enemyDamage && this.isLethal(enemyHealth, enemyHealth) || damage >= enemyDamage;
            if (!isSuicide && !invalidDamage && !this.isCollidedByUnknownCrystal(pos, (Boolean)this.antiStuck.getValue())) {
               damage = enemyDamage;
               crystalPos = new CrystalPos(pos, enemyDamage);
               gayKid = target;
            }
         }
      }

      if (crystalPos != null) {
         this.placeRotations = RotationsUtil.getRotationsFacing(crystalPos, Direction.UP);
         if (gayKid != null) {
            this.enemy = (LivingEntity)(this.isMovingLikeAaronBandhu() ? mc.player : gayKid);
         }

         this.targetTimer.reset();
         this.rotationsTimer.reset();
      }

      return crystalPos;
   }

   private boolean isLethal(float health, float damage) {
      return !(damage > health * (Float)this.lethalMlt.getValue());
   }

   protected boolean isCollidedByUnknownCrystal(BlockPos pos) {
      return this.isCollidedByUnknownCrystal(pos, false);
   }

   protected boolean isCollidedByUnknownCrystal(BlockPos pos, boolean kill) {
      if (!this.collisionTimer.passed(250L)) {
         return false;
      } else {
         BlockPos up = pos.up();
         double x = (double)up.getX();
         double y = (double)up.getY();
         double z = (double)up.getZ();
         List<Entity> list = Managers.getEntitiesManager().getAnyCollidingEntities(this.getCrystalBoxFromVec(new Vec3d(x, y, z)));
         Iterator var11 = list.iterator();

         EndCrystalEntity crystal;
         boolean confirmed;
         boolean rangeCheck;
         do {
            do {
               do {
                  Entity entity;
                  do {
                     do {
                        do {
                           if (!var11.hasNext()) {
                              return false;
                           }

                           entity = (Entity)var11.next();
                        } while(entity == null);
                     } while(EntityUtil.isDead(entity));
                  } while(!(entity instanceof EndCrystalEntity));

                  crystal = (EndCrystalEntity)entity;
               } while(this.crystalTarget != null && this.crystalTarget.equals(crystal));
            } while(System.currentTimeMillis() - ((IEndCrystalEntity)crystal).getLastAttackTime() < 250L);

            BlockPos entityPos = crystal.getBlockPos();
            Vec3d crystalVec = new Vec3d(crystal.getX(), crystal.getY(), crystal.getZ());
            confirmed = this.pendingPlacePositions.containsKey(entityPos) || this.confirmedPlacePositions.containsKey(entityPos);
            rangeCheck = this.isInAttackWallRange(this.getPlayerPos(), crystalVec, new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 2.7D, (double)pos.getZ() + 0.5D)) && this.isInAttackRange(this.getPlayerPos(), crystalVec);
         } while(confirmed && rangeCheck);

         if (kill && this.fastCrystalCheck(crystal)) {
            this.crystalTarget = crystal;
            this.breakRotations = RotationsUtil.getRotations(crystal.getX(), crystal.getY() - 1.1D, crystal.getZ());
            this.rotationsTimer.reset();
         }

         return true;
      }
   }

   private List<BlockPos> getCrystalPositions() {
      List<BlockPos> crystalPositions = new ArrayList();
      Iterator var2 = BlockUtil.getSphere((float)Math.ceil((double)((Float)this.placeRange.getValue() + 1.0F)), true).iterator();

      while(var2.hasNext()) {
         BlockPos pos = (BlockPos)var2.next();
         if (!this.blockedPositions.containsKey(pos) && BlockUtil.canPlaceCrystal(pos, true, (Boolean)this.protocolPlace.getValue(), (Integer)this.extrapolation.getValue())) {
            Vec3d loc = this.getPlayerPos();
            Vec3d crystalVec = this.getRelativeVecFromCrystal((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D, loc);
            if (this.isInAttackWallRange(loc, crystalVec, new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 2.7D, (double)pos.getZ() + 0.5D)) && this.isInAttackRange(loc, crystalVec) && this.isInPlaceWallRange(pos) && this.isInPlaceRange(PositionUtil.getEyesPos(), pos)) {
               crystalPositions.add(pos);
            }
         }
      }

      return crystalPositions;
   }

   protected void generatePlaceAction(CrystalPos crystalPos) {
      if (this.skipPlace) {
         this.skipPlace = false;
      } else if (crystalPos == null) {
         this.placeTarget = null;
      } else if (this.blockedPositions.containsKey(crystalPos.toPos())) {
         this.placeTarget = null;
      } else if (!(Boolean)this.rotate.getValue() || this.placeRotations != null) {
         int crystalSlot = ItemUtil.findHotbarItem(Items.END_CRYSTAL);
         long altSpeed = (long)(1000.0F - (Float)this.altPlaceSpeed.getValue() * 50.0F);
         boolean useAltSpeed = crystalSlot != -1 && !this.isUsingOffhand && this.swap.getValue() == AutoSwitch.ALT_SWAP;
         long defaultDelay = useAltSpeed ? altSpeed : (long)(1000.0F - (Float)this.placeSpeed.getValue() * 50.0F);
         long delay = this.pendingPlacePositions.containsKey(crystalPos.toPos().up()) && defaultDelay < 50L ? 50L : defaultDelay;
         if (this.placeTimer.passed(delay)) {
            this.placeTimer.reset();
            BlockHitResult result = this.handlePlacement(crystalPos);
            this.place = () -> {
               if (this.placeAction(result, crystalPos.getDamage())) {
                  this.pendingPlacePositions.put(result.getBlockPos().up(), System.currentTimeMillis());
                  CrystalRenderPos renderPos = new CrystalRenderPos(result.getBlockPos(), crystalPos.getDamage());
                  this.render = renderPos;
                  if (!this.isFading(renderPos)) {
                     this.fadePositions.add(renderPos);
                  }

                  this.placeTarget = null;
               }

            };
         }

      }
   }

   protected boolean placeAction(BlockHitResult result, float damage) {
      return this.placeAction(result, damage, false);
   }

   protected boolean placeAction(BlockHitResult result, float damage, boolean forceSilent) {
      int crystalSlot = ItemUtil.findHotbarItem(Items.END_CRYSTAL);
      if (crystalSlot == -1 && !this.isUsingOffhand) {
         this.placeRotations = null;
         return false;
      } else {
         this.isPlacing = true;
         ItemStack oldItem = mc.player.getStackInHand(Hand.MAIN_HAND);
         int oldSlot = mc.player.getInventory().selectedSlot;
         if (forceSilent) {
            InventoryUtil.altSwap(crystalSlot);
         } else if (crystalSlot != -1 && this.swap.getValue() != AutoSwitch.OFF && !this.isUsingOffhand) {
            switch((AutoSwitch)this.swap.getValue()) {
            case NORMAL:
               if (this.switchTimer.passed((double)((Float)this.swapDelay.getValue() * 50.0F))) {
                  InventoryUtil.switchToSlot(crystalSlot);
                  this.switchTimer.reset();
               }
               break;
            case SILENT:
               InventoryUtil.switchToSlot(crystalSlot);
               break;
            case ALT_SWAP:
               InventoryUtil.altSwap(crystalSlot);
            }
         }

         ItemStack newItem = mc.player.getStackInHand(Hand.MAIN_HAND);
         Hand hand = this.getHand();
         BlockHitResult res = new BlockHitResult(result.getPos(), result.getSide(), result.getBlockPos(), false);
         PendingUpdateManager pendingUpdateManager = ((IClientWorld)mc.world).getPendingUpdateManager().incrementSequence();
         int i = pendingUpdateManager.getSequence();
         PacketUtil.send(new PlayerInteractBlockC2SPacket(hand, res, i));
         pendingUpdateManager.close();
         if ((Boolean)this.swing.getValue()) {
            PacketUtil.swing(hand);
            EntityUtil.swingClient(hand);
         }

         this.lastCrystalPos = new CrystalPos(result.getBlockPos(), damage);
         ItemStack fakeStack;
         int slot;
         int lastSlot;
         Slot currentSlot;
         Slot swapSlot;
         DefaultedList defaultedList;
         int size;
         ArrayList list;
         Iterator var20;
         Slot slot1;
         ItemStack itemStack;
         ItemStack itemStack2;
         Int2ObjectOpenHashMap int2ObjectMap;
         int j;
         if (forceSilent && !this.isUsingOffhand) {
            if ((Boolean)this.forceDesync.getValue()) {
               fakeStack = new ItemStack(Items.END_CRYSTAL, 64);
               slot = InventoryUtil.hotbarToInventory(crystalSlot);
               lastSlot = InventoryUtil.hotbarToInventory(oldSlot);
               currentSlot = (Slot)mc.player.currentScreenHandler.slots.get(lastSlot);
               swapSlot = (Slot)mc.player.currentScreenHandler.slots.get(slot);
               defaultedList = mc.player.currentScreenHandler.slots;
               size = defaultedList.size();
               list = Lists.newArrayListWithCapacity(size);
               var20 = defaultedList.iterator();

               while(var20.hasNext()) {
                  slot1 = (Slot)var20.next();
                  list.add(slot1.getStack().copy());
               }

               mc.player.currentScreenHandler.onSlotClick(slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
               int2ObjectMap = new Int2ObjectOpenHashMap();

               for(j = 0; j < size; ++j) {
                  itemStack = (ItemStack)list.get(j);
                  itemStack2 = ((Slot)defaultedList.get(j)).getStack();
                  if (!ItemStack.areEqual(itemStack, itemStack2)) {
                     int2ObjectMap.put(j, itemStack2.copy());
                  }
               }

               PacketUtil.send(new ClickSlotC2SPacket(0, mc.player.currentScreenHandler.getRevision(), slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, fakeStack, int2ObjectMap));
               currentSlot.insertStack(oldItem);
               swapSlot.insertStack(newItem);
            } else {
               InventoryUtil.altSwap(crystalSlot);
            }

            this.isPlacing = false;
            return true;
         } else {
            if (this.swap.getValue() != AutoSwitch.OFF && !this.isUsingOffhand) {
               switch((AutoSwitch)this.swap.getValue()) {
               case SILENT:
                  InventoryUtil.switchToSlot(oldSlot);
                  break;
               case ALT_SWAP:
                  if ((Boolean)this.forceDesync.getValue()) {
                     fakeStack = new ItemStack(Items.END_CRYSTAL, 64);
                     slot = InventoryUtil.hotbarToInventory(crystalSlot);
                     lastSlot = InventoryUtil.hotbarToInventory(oldSlot);
                     currentSlot = (Slot)mc.player.currentScreenHandler.slots.get(lastSlot);
                     swapSlot = (Slot)mc.player.currentScreenHandler.slots.get(slot);
                     defaultedList = mc.player.currentScreenHandler.slots;
                     size = defaultedList.size();
                     list = Lists.newArrayListWithCapacity(size);
                     var20 = defaultedList.iterator();

                     while(var20.hasNext()) {
                        slot1 = (Slot)var20.next();
                        list.add(slot1.getStack().copy());
                     }

                     mc.player.currentScreenHandler.onSlotClick(slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
                     int2ObjectMap = new Int2ObjectOpenHashMap();

                     for(j = 0; j < size; ++j) {
                        itemStack = (ItemStack)list.get(j);
                        itemStack2 = ((Slot)defaultedList.get(j)).getStack();
                        if (!ItemStack.areEqual(itemStack, itemStack2)) {
                           int2ObjectMap.put(j, itemStack2.copy());
                        }
                     }

                     PacketUtil.send(new ClickSlotC2SPacket(0, mc.player.currentScreenHandler.getRevision(), slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, fakeStack, int2ObjectMap));
                     currentSlot.insertStack(oldItem);
                     swapSlot.insertStack(newItem);
                  } else {
                     InventoryUtil.altSwap(crystalSlot);
                  }
               }
            }

            this.isPlacing = false;
            return true;
         }
      }
   }

   protected boolean generateBreakAction(EndCrystalEntity crystal) {
      if (crystal == null) {
         this.crystalTarget = null;
         return false;
      } else if ((Boolean)this.rotate.getValue() && this.breakRotations == null) {
         return false;
      } else if ((Boolean)this.inhibit.getValue() && this.inhibitedCrystals.containsKey(crystal.getId())) {
         return false;
      } else if ((float)(System.currentTimeMillis() - ((IEndCrystalEntity)crystal).getSpawnTime()) < 100.0F * (Float)this.attackDelay.getValue()) {
         return true;
      } else if (((IEndCrystalEntity)crystal).getExactTicksExisted() < (float)(Integer)this.ticksExisted.getValue()) {
         return true;
      } else if (((IEndCrystalEntity)crystal).getHitsSinceLastAttack() >= 1 && System.currentTimeMillis() - ((IEndCrystalEntity)crystal).getLastAttackTime() < this.getExpireFactor()) {
         return false;
      } else {
         if (this.breakTimer.passed((double)(1000.0F - (Float)this.attackSpeed.getValue() * 50.0F))) {
            this.breakTimer.reset();
            this.attack = () -> {
               this.breakAction(crystal);
               if (!this.attacks.contains(crystal.getBlockPos())) {
                  this.attacks.add(crystal.getBlockPos());
               }

               Iterator var2 = Managers.getEntitiesManager().getEntities().iterator();

               while(var2.hasNext()) {
                  Entity e = (Entity)var2.next();
                  if (e instanceof EndCrystalEntity) {
                     EndCrystalEntity nearbyCrystal = (EndCrystalEntity)e;
                     if (!(nearbyCrystal.squaredDistanceTo(crystal) <= (double)MathUtil.square(6.0F)) && nearbyCrystal != crystal) {
                        this.updateHits(nearbyCrystal);
                     }
                  }
               }

            };
         }

         return true;
      }
   }

   protected void breakAction(EndCrystalEntity crystal) {
      for(int i = 0; i < (Integer)this.hits.getValue(); ++i) {
         this.kill(crystal);
      }

   }

   private void kill(EndCrystalEntity crystal) {
      int oldSlot = mc.player.getInventory().selectedSlot;
      int weaknessSlot = -1;
      if (this.antiWeakness.getValue() != AntiWeakness.OFF) {
         if (!CombatUtil.canBreakWeakness(true) && (weaknessSlot = CombatUtil.findAntiWeakness()) == -1) {
            return;
         }

         if (weaknessSlot != -1) {
            InventoryUtil.switchToSlot(weaknessSlot);
         }
      }

      Hand hand = this.getHand();
      if ((Boolean)this.swing.getValue()) {
         PacketUtil.swing(hand);
         EntityUtil.swingClient(hand);
      }

      PlayerInteractEntityC2SPacket hitPacket = PacketUtil.attackPacket(crystal.getId());
      PacketUtil.send(hitPacket);
      if (this.timing.getValue() == Timing.FAST) {
         this.confirmedPlacePositions.remove(new BlockPos(crystal.getBlockPos()));
      }

      if (weaknessSlot != -1 && this.antiWeakness.getValue() == AntiWeakness.SILENT) {
         InventoryUtil.switchToSlot(oldSlot);
      }

      this.updateHits(crystal);
      this.inhibitedCrystals.put(crystal.getId(), System.currentTimeMillis());
      this.crystalTarget = null;
   }

   protected void updateHits(EndCrystalEntity crystal) {
      IEndCrystalEntity enderCrystal = (IEndCrystalEntity)crystal;
      if (System.currentTimeMillis() - enderCrystal.getLastAttackTime() > this.getExpireFactor()) {
         enderCrystal.setHitsSinceLastAttack(1);
      } else {
         enderCrystal.setHitsSinceLastAttack(enderCrystal.getHitsSinceLastAttack() + 1);
      }

      enderCrystal.setLastAttackTime(System.currentTimeMillis());
   }

   protected Hand getHand() {
      return this.isUsingOffhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
   }

   protected boolean isArmorUnderPercent(LivingEntity living, float damage) {
      if ((Boolean)this.armorBreaker.getValue() && living instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)living;
         if (damage < 1.0F) {
            return false;
         } else {
            for(int i = 3; i >= 0; --i) {
               ItemStack stack = (ItemStack)player.getInventory().armor.get(i);
               if (stack.isEmpty()) {
                  return true;
               }

               if (ItemUtil.getDamageInPercent(stack) < (double)(Integer)this.armorPercent.getValue()) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private void handleRotations(MotionUpdateEvent event) {
      float beginYaw = event.getYaw();
      float beginPitch = event.getPitch();
      EntitiesManager ENTITIES = Managers.getEntitiesManager();
      BlockPos place = this.placeTarget == null ? null : this.placeTarget.toPos();
      if (this.placeRotations != null && this.breakRotations == null) {
         this.setRotations(this.placeRotations, event);
      } else if (this.placeRotations == null && this.breakRotations != null) {
         this.setRotations(this.breakRotations, event);
      } else if (this.placeRotations != null) {
         if ((Boolean)this.sequential.getValue()) {
            if (this.crystalTarget != null && (float)(System.currentTimeMillis() - ((IEndCrystalEntity)this.crystalTarget).getSpawnTime()) > 100.0F * (Float)this.sequentialBuffer.getValue() && this.inhibitedCrystals.containsKey(this.crystalTarget.getId())) {
               switch((SequentialMode)this.sequentialMode.getValue()) {
               case STRICT:
                  this.setRotations(this.breakRotations, event);
                  this.skipPlace = true;
                  break;
               case OLD:
                  if (place != null && place.equals(this.lastCrystalPos.toPos()) && this.crystalTarget != null && ENTITIES.getAnyCollidingEntities(new Box(place.add(0, 1, 0))).contains(this.crystalTarget) && this.crystalTarget.getBlockPos().down().equals(place)) {
                     this.setRotations(this.placeRotations, event);
                  } else if (this.crystalTarget != null && !this.crystalTarget.getBlockPos().down().equals(place)) {
                     this.setRotations(this.breakRotations, event);
                     this.skipPlace = true;
                  } else {
                     this.setRotations(this.placeRotations, event);
                  }
                  break;
               case NORMAL:
                  if (place != null && this.lastCrystalPos != null && place.equals(this.lastCrystalPos.toPos()) && this.crystalTarget != null && ENTITIES.getAnyCollidingEntities(new Box(place.add(0, 1, 0))).contains(this.crystalTarget) && this.crystalTarget.getBlockPos().down().equals(place)) {
                     this.setRotations(this.placeRotations, event);
                  } else if (this.crystalTarget != null && !this.crystalTarget.getBlockPos().down().equals(place)) {
                     this.setRotations(this.placeRotations, event);
                  } else {
                     this.setRotations(this.breakRotations, event);
                     this.skipBreak = true;
                  }
               }
            } else {
               this.setRotations(this.placeRotations, event);
            }
         } else if (this.lastCrystalPos != null && place != null && place.equals(this.lastCrystalPos.toPos()) && this.crystalTarget != null && ENTITIES.getAnyCollidingEntities(new Box(place.add(0, 1, 0))).contains(this.crystalTarget) && this.crystalTarget.getBlockPos().down().equals(place)) {
            this.setRotations(this.placeRotations, event);
         } else if (this.crystalTarget != null && !this.crystalTarget.getBlockPos().down().equals(place)) {
            this.setRotations(this.breakRotations, event);
            this.skipPlace = true;
         } else {
            this.setRotations(this.placeRotations, event);
         }
      }

      float yaw = event.getYaw();
      float pitch = event.getPitch();
      if ((beginPitch != pitch || beginYaw != event.getYaw()) && (this.placeRotations != null || this.breakRotations != null)) {
         float[] curRots = new float[]{Managers.getRotationManager().getServerYaw(), Managers.getRotationManager().getServerPitch()};
         float[] theseRots = new float[]{yaw, pitch};
         if (RotationsUtil.hastRotatedYaw(theseRots, curRots, 0.5F)) {
            this.rotations = new float[]{yaw, pitch};
            return;
         }

         yaw = this.easeTo(theseRots[0]);
         event.setYaw(yaw);
      }

      this.rotations = new float[]{yaw, pitch};
   }

   protected void setRotations(float[] rotations, MotionUpdateEvent event) {
      if (rotations != null) {
         Managers.getRotationManager().setRotations(rotations, event);
      }

   }

   protected BlockHitResult handlePlacement(BlockPos pos) {
      Vec3d playerPos = PositionUtil.getEyesPos();
      BlockHitResult result = this.rayTracePlacement(pos, playerPos, false);
      if (result == null) {
         result = this.rayTracePlacement(pos, playerPos, true);
      }

      if (result == null) {
         result = new BlockHitResult(new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D), Direction.UP, pos, false);      }

      return result;
   }

   private EndCrystalEntity getBestCrystal(List<Entity> entities, List<LivingEntity> targets) {
      float damage = 0.5F;
      EndCrystalEntity attack = null;
      LivingEntity gayKid = null;
      HashMap<BlockPos, Float> selfDamages = new HashMap();
      Iterator var7 = this.getValidCrystals(entities).iterator();

      while(true) {
         label92:
         while(var7.hasNext()) {
            EndCrystalEntity crystal = (EndCrystalEntity)var7.next();
            if (this.confirmedPlacePositions.containsKey(crystal.getBlockPos()) && this.lastCrystalPos.toPos().equals(crystal.getBlockPos().down())) {
               damage = this.lastCrystalPos.getDamage();
               attack = crystal;
               gayKid = null;
               this.targetTimer.reset();
            } else if (this.confirmedPlacePositions.isEmpty() || attack == null) {
               Iterator var9 = targets.iterator();

               while(true) {
                  LivingEntity target;
                  float enemyHealth;
                  float enemyDamage;
                  float selfDamage;
                  do {
                     if (!var9.hasNext()) {
                        continue label92;
                     }

                     target = (LivingEntity)var9.next();
                     enemyHealth = EntityUtil.getHealth(target);
                     enemyDamage = CombatUtil.getDamage(target, mc.world, 6.0F, crystal.getX(), crystal.getY(), crystal.getZ(), (Boolean)this.breakBlocks.getValue(), (Integer)this.extrapolation.getValue(), true);
                     selfDamage = 0.0F;
                     if (!(Boolean)this.suicide.getValue()) {
                        if (selfDamages.containsKey(crystal.getBlockPos())) {
                           selfDamage = (Float)selfDamages.get(crystal.getBlockPos());
                        } else {
                           selfDamage = CombatUtil.getDamage(mc.player, mc.world, 6.0F, crystal.getX(), crystal.getY(), crystal.getZ(), (Boolean)this.breakBlocks.getValue(), true);
                           selfDamages.put(crystal.getBlockPos(), selfDamage);
                        }
                     }
                  } while(!(enemyDamage >= (Float)this.minDMG.getValue()) && !(enemyDamage >= EntityUtil.getHealth(target)) && !this.isArmorUnderPercent(target, enemyDamage));

                  boolean isSuicide = selfDamage > (Float)this.maxSelfDMG.getValue() && this.isLethal(enemyHealth, enemyDamage) || (double)selfDamage + 2.0D >= (double)EntityUtil.getHealth(mc.player);
                  boolean invalidDamage = selfDamage >= enemyDamage && this.isLethal(enemyHealth, enemyHealth) || damage >= enemyDamage;
                  if (!isSuicide && !invalidDamage) {
                     damage = enemyDamage;
                     attack = crystal;
                     gayKid = target;
                  }
               }
            }
         }

         if (attack != null) {
            this.breakRotations = RotationsUtil.getRotations(attack.getX(), attack.getY() - 1.1D, attack.getZ());
            if (gayKid != null) {
               this.enemy = (LivingEntity)(this.isMovingLikeAaronBandhu() ? mc.player : gayKid);
            }

            this.targetTimer.reset();
            this.rotationsTimer.reset();
         }

         return attack;
      }
   }

   private boolean fastCrystalCheck(EndCrystalEntity crystal) {
      float selfDamage = CombatUtil.getDamage(mc.player, mc.world, 6.0F, crystal.getX(), crystal.getY(), crystal.getZ(), (Boolean)this.breakBlocks.getValue(), true);
      if ((double)selfDamage + 2.0D >= (double)EntityUtil.getHealth(mc.player)) {
         return false;
      } else {
         Vec3d loc = this.getPlayerPos();
         Vec3d crystalVec = this.getRelativeVecFromCrystal(crystal.getX(), crystal.getY(), crystal.getZ(), loc);
         if (!this.isInAttackWallRange(loc, crystalVec, new Vec3d(crystalVec.getX(), crystal.getY() + 1.7D, crystalVec.getZ()))) {
            return false;
         } else {
            return !this.isInAttackRange(loc, crystalVec) ? false : this.isValidCrystalTarget(crystal);
         }
      }
   }

   private List<EndCrystalEntity> getValidCrystals(Collection<Entity> entities) {
      List<EndCrystalEntity> crystals = new ArrayList();
      List<Entity> list = new ArrayList(entities);
      Vec3d loc = this.getPlayerPos();
      Iterator var5 = list.iterator();

      while(true) {
         Entity entity;
         EndCrystalEntity crystal;
         Vec3d crystalVec;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var5.hasNext()) {
                              return crystals;
                           }

                           entity = (Entity)var5.next();
                        } while(entity == null);
                     } while(EntityUtil.isDead(entity));
                  } while(!(entity instanceof EndCrystalEntity));

                  crystal = (EndCrystalEntity)entity;
                  crystalVec = this.getRelativeVecFromCrystal(crystal.getX(), crystal.getY(), crystal.getZ(), loc);
               } while(!this.isInAttackWallRange(loc, crystalVec, new Vec3d(entity.getX(), entity.getY() + 1.7D, entity.getZ())));
            } while(!this.isInAttackRange(loc, crystalVec));
         } while(!this.isValidCrystalTarget(crystal) && (Boolean)this.await.getValue());

         crystals.add((EndCrystalEntity)entity);
      }
   }

   private boolean isValidCrystalTarget(EndCrystalEntity crystal) {
      return ((IEndCrystalEntity)crystal).getHitsSinceLastAttack() < (Integer)this.maxHits.getValue() || System.currentTimeMillis() - ((IEndCrystalEntity)crystal).getLastAttackTime() >= 250L;
   }

   private BlockHitResult rayTracePlacement(BlockPos pos, Vec3d playerPos, boolean throughWalls) {
      if (throughWalls && (Boolean)this.strictDirection.getValue()) {
         return new BlockHitResult(new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 1.0D, (double)pos.getZ() + 0.5D), Direction.UP, pos, false);
      } else {
         Direction bestFacing = null;
         Vec3d bestVec = null;
         double bestDistance = 999.0D;
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction facing = var8[var10];
            Vec3d vec = new Vec3d((double)pos.getX() + 0.5D + (double)facing.getVector().getX() * 0.5D, (double)pos.getY() + 0.5D + (double)facing.getVector().getY() * 0.5D, (double)pos.getZ() + 0.5D + (double)facing.getVector().getZ() * 0.5D);
            if (throughWalls) {
               double distance = playerPos.distanceTo(vec);
               if (bestFacing == null || !(distance > bestDistance)) {
                  bestFacing = facing;
                  bestVec = vec;
                  bestDistance = distance;
               }
            } else {
               RaycastContext raycastContext = new RaycastContext(playerPos, vec, ShapeType.COLLIDER, FluidHandling.NONE, mc.player);
               BlockHitResult result = mc.world.raycast(raycastContext);
               if (result != null && result.getType() == Type.BLOCK && result.getBlockPos().equals(pos)) {
                  double distance = playerPos.distanceTo(result.getPos());
                  if (bestFacing == null || !(distance > bestDistance)) {
                     bestFacing = result.getSide();
                     bestVec = result.getPos();
                     bestDistance = distance;
                  }
               }
            }
         }

         if (bestFacing != null) {
            return new BlockHitResult(bestVec, bestFacing, pos, false);
         } else {
            return null;
         }
      }
   }

   private void runAttack() {
      if (this.attack != null) {
         this.attack.run();
         this.attack = null;
      }

   }

   private void runPlace() {
      if (this.place != null && !SWITCH_LOCK) {
         this.place.run();
         this.place = null;
      }

   }

   protected void removeAllPending(BlockPos pos) {
      Iterator var2 = this.pendingPlacePositions.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<BlockPos, Long> entry = (Entry)var2.next();
         if (entry.getKey() == pos) {
            this.pendingPlacePositions.remove(entry.getKey());
         }
      }

   }

   private void expire() {
      Iterator var1 = this.pendingPlacePositions.entrySet().iterator();

      Entry entry;
      while(var1.hasNext()) {
         entry = (Entry)var1.next();
         if (System.currentTimeMillis() - (Long)entry.getValue() > this.getExpireFactor() && this.lastCrystalPos.toPos() != ((BlockPos)entry.getKey()).down()) {
            this.pendingPlacePositions.remove(entry.getKey());
         }
      }

      var1 = this.confirmedPlacePositions.entrySet().iterator();

      while(var1.hasNext()) {
         entry = (Entry)var1.next();
         if (System.currentTimeMillis() - (Long)entry.getValue() > this.getExpireFactor() && this.lastCrystalPos.toPos() != ((BlockPos)entry.getKey()).down()) {
            this.confirmedPlacePositions.remove(entry.getKey());
         }
      }

      var1 = this.inhibitedCrystals.entrySet().iterator();

      while(var1.hasNext()) {
         entry = (Entry)var1.next();
         if (System.currentTimeMillis() - (Long)entry.getValue() > this.getExpireFactor()) {
            this.inhibitedCrystals.remove(entry.getKey());
         }
      }

      var1 = this.blockedPositions.entrySet().iterator();

      while(var1.hasNext()) {
         entry = (Entry)var1.next();
         if (System.currentTimeMillis() - (Long)entry.getValue() > 100L) {
            this.blockedPositions.remove(entry.getKey());
         }
      }

   }

   protected Vec3d getPlayerPos() {
      return (Boolean)this.eyePos.getValue() ? PositionUtil.getEyesPos() : mc.player.getPos();
   }

   protected boolean isInPlaceRange(Vec3d from, BlockPos pos) {
      Vec3d vec = new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
      double distance = from.squaredDistanceTo(vec);
      return distance < (double)MathUtil.square((Float)this.placeRange.getValue());
   }

   protected boolean isInPlaceWallRange(BlockPos pos) {
      Vec3d vec = new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
      double distance = PositionUtil.getEyesPos().squaredDistanceTo(vec);
      return distance < (double)MathUtil.square(Math.min((Float)this.placeWallRange.getValue(), (Float)this.placeRange.getValue()));
   }

   protected boolean isInAttackRange(Vec3d from, Vec3d vec) {
      double distance = from.squaredDistanceTo(vec);
      return distance < (double)MathUtil.square((Float)this.attackRange.getValue());
   }

   protected boolean isInAttackWallRange(Vec3d from, Vec3d vec, Vec3d wallVec) {
      if (RaycastUtil.hasLineOfSight(wallVec)) {
         return true;
      } else {
         double distance = from.squaredDistanceTo(vec);
         return distance < (double)MathUtil.square(Math.min((Float)this.attackRangeWall.getValue(), (Float)this.attackRange.getValue()));
      }
   }

   private float easeTo(float target) {
      float serverYaw = Managers.getRotationManager().getServerYaw();
      float yawChange = RotationsUtil.normalizeAngle(target - serverYaw);
      float yawChangeFactor = Math.abs(yawChange) / 180.0F;
      float maxYawChange = 50.0F * yawChangeFactor;
      if (Math.abs(yawChange) >= maxYawChange) {
         yawChange = yawChange > 0.0F ? maxYawChange : -maxYawChange;
      }

      return serverYaw + yawChange;
   }

   protected Box getCrystalBoxFromVec(Vec3d vec3d) {
      return new Box(vec3d.x - 1.0D, vec3d.y, vec3d.z - 1.0D, vec3d.x + 1.0D, vec3d.y + 2.0D, vec3d.z + 1.0D);
   }

   protected Vec3d getRelativeVecFromCrystal(double x, double y, double z, Vec3d playerVec) {
      return (Boolean)this.strictRange.getValue() ? new Vec3d(x, y, z) : MathUtil.getVecRelativeToPlayer(x, y, z, playerVec, 1.0D, 2.0D);
   }

   protected String reFormat(String lol) {
      if (lol.endsWith("0") && lol.length() > 3) {
         lol = lol.substring(0, lol.length() - 1);
      }

      return lol;
   }

   protected void calcLastAttack(long time) {
      float calc = (float)(System.currentTimeMillis() - time);
      String lol = String.format("%.2f", (double)calc / 100.0D);
      this.attackLatency = this.reFormat(lol);
   }

   private String getTagInfo() {
      String tag = null;
      String enemyTag = null;
      if (this.enemy != null) {
         LivingEntity var4 = this.enemy;
         if (var4 instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)var4;
            enemyTag = player.getName().getString();
         } else {
            enemyTag = this.enemy.getType().getName().getString();
         }
      }

      switch((TagInfo)this.tagInfo.getValue()) {
      case ATTACK:
         tag = this.attackLatency;
         break;
      case FUTURE:
         tag = String.format("%s, %s", this.attackLatency, this.maxCount);
         break;
      case BETA:
         tag = String.format("%s, %s, %s", this.calculationTime, this.attackLatency, this.maxCount);
         break;
      case FULL:
         tag = String.format("%s%s, %s, %s", enemyTag == null ? "" : enemyTag + ", ", this.calculationTime, this.attackLatency, this.maxCount);
         break;
      case TARGET:
         if (enemyTag != null) {
            tag = enemyTag;
         }
      }

      return tag;
   }

   protected boolean isFading(CrystalRenderPos render) {
      Iterator var2 = (new ArrayList(this.fadePositions)).iterator();

      CrystalRenderPos renderPos;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         renderPos = (CrystalRenderPos)var2.next();
      } while(!renderPos.getPos().equals(render.getPos()));

      return true;
   }

   private long getExpireFactor() {
      float tpsFactor = (20.0F - Managers.getTpsManager().getCurrentTps()) * 100.0F;
      float attackDelayFactor = (Float)this.attackDelay.getValue() * 100.0F;
      return (long)(250.0F + tpsFactor + attackDelayFactor);
   }

   protected float getMindDMG() {
      return (Float)this.minDMG.getValue();
   }

   protected float getMaxSelfDamage() {
      return (Float)this.maxSelfDMG.getValue();
   }

   protected boolean isBreakingBlocks() {
      return (Boolean)this.breakBlocks.getValue();
   }

   protected boolean isSuicide() {
      return (Boolean)this.suicide.getValue();
   }

   public boolean isReduced() {
      return (Boolean)this.reduceHitbox.getValue();
   }

   
   public EnumValue<Timing> getTiming() {
      return this.timing;
   }

   
   public NumberValue<Float> getLockDelay() {
      return this.lockDelay;
   }

   
   public Value<Boolean> getMultiTask() {
      return this.multiTask;
   }

   
   public Value<Boolean> getWhileMining() {
      return this.whileMining;
   }

   
   public Value<Boolean> getSwing() {
      return this.swing;
   }

   
   public Value<Boolean> getBreakBlocks() {
      return this.breakBlocks;
   }

   
   public Value<Boolean> getRotate() {
      return this.rotate;
   }

   
   public Value<Boolean> getSequential() {
      return this.sequential;
   }

   
   public EnumValue<SequentialMode> getSequentialMode() {
      return this.sequentialMode;
   }

   
   public NumberValue<Float> getSequentialBuffer() {
      return this.sequentialBuffer;
   }

   
   public NumberValue<Float> getPlaceRange() {
      return this.placeRange;
   }

   
   public NumberValue<Float> getPlaceWallRange() {
      return this.placeWallRange;
   }

   
   public NumberValue<Float> getPlaceSpeed() {
      return this.placeSpeed;
   }

   
   public Value<Boolean> getProtocolPlace() {
      return this.protocolPlace;
   }

   
   public Value<Boolean> getAwait() {
      return this.await;
   }

   
   public Value<Boolean> getStrictDirection() {
      return this.strictDirection;
   }

   
   public NumberValue<Float> getAttackRange() {
      return this.attackRange;
   }

   
   public NumberValue<Float> getAttackRangeWall() {
      return this.attackRangeWall;
   }

   
   public NumberValue<Float> getAttackSpeed() {
      return this.attackSpeed;
   }

   
   public NumberValue<Float> getAttackDelay() {
      return this.attackDelay;
   }

   
   public NumberValue<Integer> getHits() {
      return this.hits;
   }

   
   public NumberValue<Integer> getMaxHits() {
      return this.maxHits;
   }

   
   public NumberValue<Integer> getTicksExisted() {
      return this.ticksExisted;
   }

   
   public Value<Boolean> getStrictRange() {
      return this.strictRange;
   }

   
   public Value<Boolean> getAntiStuck() {
      return this.antiStuck;
   }

   
   public Value<Boolean> getBoost() {
      return this.boost;
   }

   
   public Value<Boolean> getInhibit() {
      return this.inhibit;
   }

   
   public EnumValue<AntiWeakness> getAntiWeakness() {
      return this.antiWeakness;
   }

   
   public Value<Boolean> getEyePos() {
      return this.eyePos;
   }

   
   public TargetValue getTarget() {
      return this.target;
   }

   
   public NumberValue<Float> getEnemyRange() {
      return this.enemyRange;
   }

   
   public NumberValue<Float> getMinDMG() {
      return this.minDMG;
   }

   
   public NumberValue<Float> getMaxSelfDMG() {
      return this.maxSelfDMG;
   }

   
   public Value<Boolean> getSuicide() {
      return this.suicide;
   }

   
   public NumberValue<Float> getLethalMlt() {
      return this.lethalMlt;
   }

   
   public Value<Boolean> getArmorBreaker() {
      return this.armorBreaker;
   }

   
   public NumberValue<Integer> getArmorPercent() {
      return this.armorPercent;
   }

   
   public NumberValue<Integer> getExtrapolation() {
      return this.extrapolation;
   }

   
   public EnumValue<AutoSwitch> getSwap() {
      return this.swap;
   }

   
   public Value<Boolean> getForceDesync() {
      return this.forceDesync;
   }

   
   public NumberValue<Float> getAltPlaceSpeed() {
      return this.altPlaceSpeed;
   }

   
   public NumberValue<Float> getSwapDelay() {
      return this.swapDelay;
   }

   
   public EnumValue<RenderMode> getRenderMode() {
      return this.renderMode;
   }

   
   public NumberValue<Float> getRenderFactor() {
      return this.renderFactor;
   }

   
   public Value<Boolean> getRenderDamage() {
      return this.renderDamage;
   }

   
   public NumberValue<Float> getScaleFactor() {
      return this.scaleFactor;
   }

   
   public Value<Boolean> getRenderAttacks() {
      return this.renderAttacks;
   }

   
   public Value<Boolean> getRenderExtrapolation() {
      return this.renderExtrapolation;
   }

   
   public NumberValue<Float> getAlphaFactor() {
      return this.alphaFactor;
   }

   
   public Value<Boolean> getReduceHitbox() {
      return this.reduceHitbox;
   }

   
   public NumberValue<Integer> getThreadDelay() {
      return this.threadDelay;
   }

   
   public BlockStateHelper getStateHelper() {
      return this.stateHelper;
   }

   
   public CrystalRenderPos getRender() {
      return this.render;
   }

   
   public List<CrystalRenderPos> getFadePositions() {
      return this.fadePositions;
   }

   
   public LivingEntity getEnemy() {
      return this.enemy;
   }

   
   public boolean isUsingOffhand() {
      return this.isUsingOffhand;
   }

   
   public ConcurrentHashMap<BlockPos, Long> getPendingPlacePositions() {
      return this.pendingPlacePositions;
   }

   
   public ConcurrentHashMap<BlockPos, Long> getConfirmedPlacePositions() {
      return this.confirmedPlacePositions;
   }

   
   public ConcurrentHashMap<Integer, Long> getInhibitedCrystals() {
      return this.inhibitedCrystals;
   }

   
   public ConcurrentHashMap<BlockPos, Long> getBlockedPositions() {
      return this.blockedPositions;
   }

   
   public ConcurrentHashMap<Integer, Long> getCrystalDelays() {
      return this.crystalDelays;
   }

   
   public ConcurrentHashMap<LivingEntity, Double> getCacheArmor() {
      return this.cacheArmor;
   }

   
   public ConcurrentHashMap<LivingEntity, Vec3d> getCachePositions() {
      return this.cachePositions;
   }

   
   public CrystalPos getLastCrystalPos() {
      return this.lastCrystalPos;
   }

   
   public long getPingTimeout() {
      Objects.requireNonNull(this);
      return 250L;
   }

   
   public StopWatch getPlaceTimer() {
      return this.placeTimer;
   }

   
   public StopWatch getBreakTimer() {
      return this.breakTimer;
   }

   
   public StopWatch getSwitchTimer() {
      return this.switchTimer;
   }

   
   public StopWatch getRenderTimer() {
      return this.renderTimer;
   }

   
   public StopWatch getCollisionTimer() {
      return this.collisionTimer;
   }

   
   public StopWatch getTargetTimer() {
      return this.targetTimer;
   }

   
   public StopWatch getThreadTimer() {
      return this.threadTimer;
   }

   
   public StopWatch getRotationsTimer() {
      return this.rotationsTimer;
   }

   
   public StopWatch getLockTimer() {
      return this.lockTimer;
   }

   
   public StopWatch getDebugTimer() {
      return this.debugTimer;
   }

   
   public boolean isPlacing() {
      return this.isPlacing;
   }

   
   public List<LivingEntity> getTargets() {
      return this.targets;
   }

   
   public Runnable getAttack() {
      return this.attack;
   }

   
   public Runnable getPlace() {
      return this.place;
   }

   
   public float[] getRotations() {
      return this.rotations;
   }

   
   public float[] getBreakRotations() {
      return this.breakRotations;
   }

   
   public float[] getPlaceRotations() {
      return this.placeRotations;
   }

   
   public CrystalPos getPlaceTarget() {
      return this.placeTarget;
   }

   
   public EndCrystalEntity getCrystalTarget() {
      return this.crystalTarget;
   }

   
   public boolean isWasEating() {
      return this.wasEating;
   }

   
   public boolean isSkipBreak() {
      return this.skipBreak;
   }

   
   public boolean isSkipPlace() {
      return this.skipPlace;
   }

   
   public int getLastSpawnID() {
      return this.lastSpawnID;
   }

   
   public String getAttackLatency() {
      return this.attackLatency;
   }

   
   public String getCalculationTime() {
      return this.calculationTime;
   }

   
   public List<Long> getCrystalsPerSecond() {
      return this.crystalsPerSecond;
   }

   
   public int getMaxCount() {
      return this.maxCount;
   }

   
   public List<BlockPos> getAttacks() {
      return this.attacks;
   }

   
   public boolean isLocked() {
      return this.locked;
   }

   
   public void setRender(CrystalRenderPos render) {
      this.render = render;
   }

   
   public void setEnemy(LivingEntity enemy) {
      this.enemy = enemy;
   }

   
   public void setPlaceTarget(CrystalPos placeTarget) {
      this.placeTarget = placeTarget;
   }

   
   public void setSkipBreak(boolean skipBreak) {
      this.skipBreak = skipBreak;
   }

   
   public void setSkipPlace(boolean skipPlace) {
      this.skipPlace = skipPlace;
   }

   
   public void setLastSpawnID(int lastSpawnID) {
      this.lastSpawnID = lastSpawnID;
   }

   
   public void setAttackLatency(String attackLatency) {
      this.attackLatency = attackLatency;
   }

   
   public void setCalculationTime(String calculationTime) {
      this.calculationTime = calculationTime;
   }

   
   public void setMaxCount(int maxCount) {
      this.maxCount = maxCount;
   }
}
