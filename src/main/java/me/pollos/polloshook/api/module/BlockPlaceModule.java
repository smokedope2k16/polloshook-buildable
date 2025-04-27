package me.pollos.polloshook.api.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.entity.CombatUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.minecraft.render.RenderPosition;
import me.pollos.polloshook.api.minecraft.rotations.StrictDirection;
import me.pollos.polloshook.api.minecraft.world.BlockStateHelper;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.asm.ducks.world.IClientWorld;
import me.pollos.polloshook.impl.events.item.FinishUsingItemEvent;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.events.render.RenderRotationsEvent;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.Timing;
import me.pollos.polloshook.impl.module.combat.holefill.HoleFill;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerEntity;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class BlockPlaceModule extends ToggleableModule {
   protected final EnumValue<Timing> timing;
   protected final Value<Boolean> rotations;
   protected final Value<Boolean> swing;
   protected final Value<Boolean> strictDirection;
   protected final Value<Boolean> attack;
   protected final NumberValue<Float> placeRange;
   protected final NumberValue<Integer> blocks;
   protected final NumberValue<Integer> placeDelay;
   protected final Value<Boolean> jumpDisable;
   protected final Value<Boolean> chorusDisable;
   protected final Value<Boolean> render;
   protected final Value<Boolean> altSwap;
   protected final BlockStateHelper stateHelper;
   private final List<BlockPos> awaiting;
   private final List<Packet<?>> packets;
   private final List<RenderPosition> renderBlocks;
   private PlayerInteractEntityC2SPacket attackingPacket;
   private EndCrystalEntity lastAttacked;
   private final StopWatch attackTimer;
   private final Map<BlockPos, Long> placed;
   private double enablePosY;
   private int blocksPlaced;
   private int burstBlocks;
   private int slot;
   private final StopWatch timer;
   private final StopWatch burstTimer;
   private final StopWatch jumpTimer;

   public BlockPlaceModule(String[] aliases, Category category) {
      super(aliases, category);
      this.timing = new EnumValue(Timing.FAST, new String[]{"Timing", "t"});
      this.rotations = new Value(false, new String[]{"Rotations", "rotate"});
      this.swing = new Value(true, new String[]{"Swing", "punch"});
      this.strictDirection = new Value(false, new String[]{"StrictDirection", "strictdir", "strict"});
      this.attack = new Value(true, new String[]{"Attack"});
      this.placeRange = (new NumberValue(4.5F, 0.1F, 6.0F, 0.1F, new String[]{"PlaceRange", "range"})).withTag("range");
      this.blocks = new NumberValue(5, 1, 20, new String[]{"Blocks", "bps", "blocks/tick"});
      this.placeDelay = new NumberValue(2, 0, 10, new String[]{"PlaceDelay", "placedel", "delay"});
      this.jumpDisable = new Value(true, new String[]{"JumpDisable", "disableonjump"});
      this.chorusDisable = new Value(false, new String[]{"ChorusDisable", "chorusfruitdisable"});
      this.render = new Value(false, new String[]{"Render", "renderplaced"});
      this.altSwap = new Value(false, new String[]{"AltSwap", "altswitch", "alternativeswap"});
      this.stateHelper = new BlockStateHelper();
      this.awaiting = new ArrayList();
      this.packets = new ArrayList();
      this.renderBlocks = new ArrayList();
      this.attackTimer = new StopWatch();
      this.placed = new HashMap();
      this.blocksPlaced = 0;
      this.burstBlocks = 0;
      this.timer = new StopWatch();
      this.burstTimer = new StopWatch();
      this.jumpTimer = new StopWatch();
      this.offerValues(new Value[]{this.timing, this.rotations, this.swing, this.strictDirection, this.attack, this.placeRange, this.blocks, this.placeDelay, this.chorusDisable, this.jumpDisable, this.render});
      Listener<PacketEvent.Receive<PlayerPositionLookS2CPacket>> teleportListener = new Listener<PacketEvent.Receive<PlayerPositionLookS2CPacket>>(PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class) {
         public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
            if (mc.player != null) {
               double packetY = ((PlayerPositionLookS2CPacket)event.getPacket()).getY();
               if (packetY > BlockPlaceModule.this.enablePosY) {
                  BlockPlaceModule.this.enablePosY = packetY;
                  BlockPlaceModule.this.jumpTimer.reset();
               }

            }
         }
      };
      Listener<PacketEvent.Receive<PlaySoundS2CPacket>> soundListener = new Listener<PacketEvent.Receive<PlaySoundS2CPacket>>(PacketEvent.Receive.class, PlaySoundS2CPacket.class) {
         public void call(PacketEvent.Receive<PlaySoundS2CPacket> event) {
            if (mc.world != null) {
               PlaySoundS2CPacket packet = (PlaySoundS2CPacket)event.getPacket();
               if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound().equals(SoundEvents.ENTITY_GENERIC_EXPLODE)) {
                  Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
                  mc.executeSync(() -> {
                     Iterator var2 = mc.world.getEntities().iterator();

                     while(var2.hasNext()) {
                        Entity entity = (Entity)var2.next();
                        if (entity instanceof EndCrystalEntity) {
                           EndCrystalEntity crystal = (EndCrystalEntity)entity;
                           if (entity.squaredDistanceTo(pos.x, pos.y, pos.z) <= (double)MathUtil.square(11.0F)) {
                              if (BlockPlaceModule.this.lastAttacked != null && crystal == BlockPlaceModule.this.lastAttacked) {
                                 BlockPlaceModule.this.attackTimer.setTime(10000L);
                                 BlockPlaceModule.this.lastAttacked = null;
                              }

                              entity.kill();
                           }
                        }
                     }

                  });
               }

            }
         }
      };
      Listener<FinishUsingItemEvent> finishUsingItemEventListener = new Listener<FinishUsingItemEvent>(FinishUsingItemEvent.class) {
         public void call(FinishUsingItemEvent event) {
            ItemStack stack = event.getStack();
            LivingEntity entity = event.getEntity();
            if (entity == mc.player && stack.getItem() instanceof ChorusFruitItem && (Boolean)BlockPlaceModule.this.chorusDisable.getValue()) {
               BlockPlaceModule.this.setEnabled(false);
            }

         }
      };
      Listener<RenderEvent> renderEventListener = new Listener<RenderEvent>(RenderEvent.class) {
         public void call(RenderEvent event) {
            if ((Boolean)BlockPlaceModule.this.render.getValue()) {
               if (!BlockPlaceModule.this.renderBlocks.isEmpty()) {
                  MatrixStack stack = event.getMatrixStack();
                  stack.push();
                  RenderMethods.enable3D();
                  MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
                  Framebuffer framebuffer = mc.getFramebuffer();
                  MSAAFramebuffer.start(smoothBuffer, framebuffer);
                  List<RenderPosition> removedBlocks = new ArrayList();
                  Iterator var6 = BlockPlaceModule.this.renderBlocks.iterator();

                  while(var6.hasNext()) {
                     RenderPosition renderPos = (RenderPosition)var6.next();
                     BlockPos pos = renderPos.getPos();
                     int alpha = (int)ColorUtil.fade((double)renderPos.getTime(), (double)Manager.get().getFadeTime());
                     if (alpha == 0) {
                        removedBlocks.add(renderPos);
                     } else {
                        Box box = Interpolation.interpolatePos(pos);
                        RenderMethods.drawBox(stack, box, ColorUtil.changeAlpha(Manager.get().getBlocksColor(), alpha / 4));
                        RenderMethods.drawOutlineBox(stack, box, ColorUtil.changeAlpha(Manager.get().getBlocksColor(), alpha), 1.3F);
                     }
                  }

                  BlockPlaceModule.this.removeRenderPositions(removedBlocks);
                  MSAAFramebuffer.end(smoothBuffer, framebuffer);
                  RenderMethods.disable3D();
                  stack.pop();
               }
            }
         }
      };
      this.offerListeners(new Listener[]{teleportListener, finishUsingItemEventListener, soundListener});
      PollosHook.getEventBus().register(renderEventListener);
      this.initializeAltSwap();
   }


   protected void onEnable() {
      this.packets.clear();
      this.renderBlocks.clear();
      this.blocksPlaced = 0;
      this.clear();
      if (mc.player != null) {
         this.enablePosY = mc.player.getY();
      }
   }

   protected void clear() {
   }

   protected int getSlot() {
      return ItemUtil.getHotbarItemSlot(Items.OBSIDIAN);
   }

   public void onEvent(List<BlockPos> blocks, MotionUpdateEvent event) {
      if (event.getStage() == Stage.PRE) {
         this.blocksPlaced = 0;
         if (this.update()) {
            this.placeBlocks(blocks);
         }

         AutoCrystal.SWITCH_LOCK = true;
         this.execute();
         AutoCrystal.SWITCH_LOCK = false;
      } else {
         this.stateHelper.clearAllStates();
         this.awaiting.clear();
      }

   }

   private boolean update() {
      if (this.updatePlaced()) {
         return false;
      } else {
         long delay = (Integer)this.placeDelay.getValue() == 0 ? 5L : (long)(Integer)this.placeDelay.getValue() * 50L;
         if (!this.timer.passed(delay)) {
            return false;
         } else {
            this.setEnablePosY();
            this.findSlot();
            return true;
         }
      }
   }

   private boolean updatePlaced() {
      int delay = this.timing.getValue() == Timing.STRICT ? 150 : 50;
      this.placed.entrySet().removeIf((entry) -> {
         return System.currentTimeMillis() - (Long)entry.getValue() >= (long)delay;
      });
      return false;
   }

   private void placeBlocks(List<BlockPos> blockList) {
      if (blockList != null && !blockList.isEmpty()) {
         Iterator var2 = BlockUtil.sortByPlacing(blockList, mc.world).iterator();

         while(var2.hasNext()) {
            BlockPos pos = (BlockPos)var2.next();
            if (!this.placed.containsKey(pos) && this.stateHelper.getBlockState(pos).getBlock().getDefaultState().isReplaceable()) {
               this.placeBlock(pos);
            }
         }

      }
   }

   private void placeBlock(BlockPos pos) {
      if (mc.world != null && mc.player != null) {
         if (!this.awaiting.contains(pos)) {
            if (this.slot != -1) {
               if (!(PositionUtil.getEyesPos().squaredDistanceTo(pos.toCenterPos()) > (double)MathUtil.square((Float)this.placeRange.getValue()))) {
                  Direction facing = this.getFacing(pos);
                  if (facing != null) {
                     if (this.burstTimer.passed(500L)) {
                        this.burstBlocks = 0;
                     }

                     int maxBlocks = (Integer)this.blocks.getValue();
                     if (this.burstBlocks >= (Integer)this.blocks.getValue() && this.timing.getValue() == Timing.STRICT) {
                        maxBlocks = 1;
                     }

                     if (!Managers.getPositionManager().isOnGround() && this.timing.getValue() == Timing.STRICT) {
                        this.burstBlocks = (Integer)this.blocks.getValue();
                        this.burstTimer.reset();
                        maxBlocks = 1;
                     }

                     if (this.blocksPlaced < maxBlocks) {
                        if (this.canPlace(pos)) {
                           if (this.attackingPacket == null && (Boolean)this.attack.getValue()) {
                              this.crystalCheck(pos);
                           }

                           this.placeBlock(pos, facing, Vec3d.ofCenter(pos));
                           this.stateHelper.addBlockState(pos, this.getState());
                           this.renderPos(pos);
                           this.addCallback(pos);
                           this.awaiting.add(pos);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   protected void placeBlock(BlockPos pos, Direction facing, Vec3d center) {
      float[] vecRots = BlockUtil.getVecRotations(center);
      if ((Boolean)this.rotations.getValue()) {
         PlayerMoveC2SPacket rotsPacket = PacketUtil.getRotate(vecRots, Managers.getPositionManager().isOnGround());
         this.packets.add(rotsPacket);
      }

      BlockPos neighbor = pos.offset(facing);
      Vec3d newCenter = center.add((double)facing.getOffsetX() * 0.5D, (double)facing.getOffsetY() * 0.5D, (double)facing.getOffsetZ() * 0.5D);
      BlockHitResult hitResult = new BlockHitResult(newCenter, facing.getOpposite(), neighbor, false);
      PendingUpdateManager pendingUpdateManager = ((IClientWorld)mc.world).getPendingUpdateManager().incrementSequence();
      int i = pendingUpdateManager.getSequence();
      PlayerInteractBlockC2SPacket place = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, i);
      pendingUpdateManager.close();
      this.packets.add(place);
      if ((Boolean)this.swing.getValue()) {
         this.packets.add(new HandSwingC2SPacket(Hand.MAIN_HAND));
      }

      ++this.blocksPlaced;
      ++this.burstBlocks;
      if (this.burstBlocks != (Integer)this.blocks.getValue()) {
         this.burstTimer.reset();
      }

   }

   protected void execute() {
      if (!this.packets.isEmpty()) {
         ((AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class)).setLock(true);
         int lastSlot = mc.player.getInventory().selectedSlot;
         if (this.attackingPacket != null) {
            this.attackTimer.reset();
            PacketUtil.swing();
            PacketUtil.send(this.attackingPacket);
            this.attackingPacket = null;
         }

         if ((Boolean)this.altSwap.getValue()) {
            InventoryUtil.altSwap(this.slot);
         } else {
            InventoryUtil.switchToSlot(this.slot);
         }

         PacketUtil.sneak(true);
         List<Packet<?>> packetsToRemove = new ArrayList();
         Iterator var3 = this.packets.iterator();

         while(var3.hasNext()) {
            Packet<?> packet = (Packet)var3.next();
            if (packet instanceof PlayerMoveC2SPacket) {
               PlayerMoveC2SPacket rotation = (PlayerMoveC2SPacket)packet;
               PollosHook.getEventBus().dispatch(new RenderRotationsEvent(rotation.getYaw(mc.player.getYaw()), rotation.getPitch(mc.player.getPitch())));
            }

            PacketUtil.send(packet);
            packetsToRemove.add(packet);
         }

         this.packets.removeAll(packetsToRemove);
         if ((Boolean)this.swing.getValue()) {
            EntityUtil.swingClient(Hand.MAIN_HAND);
         }

         this.timer.reset();
         PacketUtil.sneak(false);
         if ((Boolean)this.altSwap.getValue()) {
            InventoryUtil.altSwap(this.slot);
         } else {
            InventoryUtil.switchToSlot(lastSlot);
         }
      }

   }

   private void crystalCheck(BlockPos pos) {
      if (this.attackTimer.passed(25L * (long)(Integer)this.placeDelay.getValue() == 0L ? 1L : (long)(Integer)this.placeDelay.getValue())) {
         PlayerInteractEntityC2SPacket attackPacket = null;
         double distance = 3.4028234663852886E38D;
         Iterator var5 = mc.world.getOtherEntities((Entity)null, new Box(pos)).iterator();

         while(var5.hasNext()) {
            Entity entity = (Entity)var5.next();
            if (entity instanceof EndCrystalEntity && !(PositionUtil.getEyesPos().squaredDistanceTo(entity.getPos()) > (double)MathUtil.square((Float)this.placeRange.getValue() + 1.0F))) {
               Vec3d playerPos = mc.player.getPos();
               double dist = entity.squaredDistanceTo(playerPos.x, playerPos.y, playerPos.z);
               if (dist < distance) {
                  distance = dist;
                  attackPacket = PacketUtil.attackPacket(entity);
                  this.lastAttacked = (EndCrystalEntity)entity;
               }
            }
         }

         if (attackPacket != null) {
            int oldSlot = mc.player.getInventory().selectedSlot;
            if (!CombatUtil.canBreakWeakness(true)) {
               int weaknessSlot;
               if ((weaknessSlot = CombatUtil.findAntiWeakness()) != -1) {
                  this.attackTimer.reset();
                  InventoryUtil.switchToSlot(weaknessSlot);
                  PacketUtil.send(attackPacket);
                  if ((Boolean)this.swing.getValue()) {
                     mc.player.swingHand(Hand.MAIN_HAND);
                  }

                  InventoryUtil.switchToSlot(oldSlot);
               }

            } else {
               this.attackingPacket = attackPacket;
            }
         }
      }
   }

   private void initializeAltSwap() {
      if (this instanceof HoleFill) {
         this.getValues().add(this.altSwap);
      }

   }

   private Direction getFacing(BlockPos pos) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         BlockPos offset = pos.offset(direction);
         if ((!(Boolean)this.strictDirection.getValue() || StrictDirection.strictDirectionCheck(offset, direction.getOpposite(), this.stateHelper.getClientWorld())) && this.isValidPlacePos(offset)) {
            return direction;
         }
      }

      return null;
   }

   private boolean isValidPlacePos(BlockPos pos) {
      if (!mc.world.getWorldBorder().contains(pos)) {
         return false;
      } else {
         BlockState offsetState = this.getState(pos);
         return !offsetState.isAir() && offsetState.getFluidState().isEmpty();
      }
   }

   private void renderPos(BlockPos pos) {
      if (mc.world.getBlockState(pos).getBlock() instanceof AirBlock) {
         RenderPosition remove = null;
         Iterator var3 = (new ArrayList(this.renderBlocks)).iterator();

         while(var3.hasNext()) {
            RenderPosition renderPos = (RenderPosition)var3.next();
            if (renderPos.getPos() == pos) {
               remove = renderPos;
            }
         }

         if (remove != null) {
            this.renderBlocks.remove(remove);
         }

         RenderPosition renderPos = new RenderPosition(pos);
         this.renderBlocks.add(renderPos);
      }
   }

   private void removeRenderPositions(List<RenderPosition> positions) {
      if (!positions.isEmpty()) {
         Iterator var2 = positions.iterator();

         while(var2.hasNext()) {
            RenderPosition renderPos = (RenderPosition)var2.next();
            this.renderBlocks.remove(renderPos);
         }

      }
   }

   protected boolean canPlace(BlockPos pos) {
      if (!World.isValid(pos)) {
         return false;
      } else if (!this.getState(pos).isReplaceable()) {
         return false;
      } else {
         VoxelShape voxelShape = this.getState(pos).getCollisionShape(mc.world, pos, ShapeContext.absent());
         Box blockBB = new Box(pos);
         Iterator var4 = mc.world.getOtherEntities((Entity)null, blockBB).iterator();
         if (var4.hasNext()) {
            Entity entity = (Entity)var4.next();
            if (this.getState() != Blocks.COBWEB.getDefaultState()) {
               if (!(entity instanceof EndCrystalEntity) && !EntityUtil.PLACEABLE_ENTITES.contains(entity.getClass())) {
                  Box bb = entity.getBoundingBox();
                  bb.shrink(-1.0E-7D, -1.0E-7D, -1.0E-7D);
                  return bb.intersects(blockBB);
               }

               return voxelShape.isEmpty();
            }
         }

         var4 = this.getFacings(pos).iterator();

         BlockState state;
         Direction side;
         do {
            if (!var4.hasNext()) {
               return voxelShape.isEmpty();
            }

            side = (Direction)var4.next();
            state = this.getState(pos.offset(side));
            if (state == null) {
               state = BlockUtil.getState(pos.offset(side));
            }
         } while(state.getBlock().getDefaultState().getCollisionShape(mc.world, pos.offset(side)) != VoxelShapes.fullCube());

         return true;
      }
   }

   private List<Direction> getFacings(BlockPos pos) {
      ArrayList<Direction> validFacings = new ArrayList();
      Direction[] var3 = Direction.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Direction side = var3[var5];
         BlockPos neighbour = pos.offset(side);
         BlockState state = this.getState(neighbour);
         if (state == null) {
            state = BlockUtil.getState(neighbour);
         }

         if (state.getBlock().getDefaultState().getCollisionShape(mc.world, neighbour) == VoxelShapes.fullCube() && !state.getBlock().getDefaultState().isReplaceable()) {
            validFacings.add(side);
         }
      }

      return validFacings;
   }

   public void addCallback(BlockPos pos) {
      Managers.getBlocksManager().addCallback(pos, (s) -> {
         mc.execute(() -> {
            this.placed.remove(pos);
         });
      });
      this.placed.put(pos, System.currentTimeMillis());
   }

   private void setEnablePosY() {
      if (this.jumpTimer.passed(250L)) {
         this.enablePosY = mc.player.getY();
         this.jumpTimer.reset();
      }

   }

   public boolean handleJump(BlockPlaceModule module) {
      if (module.getValues().contains(this.jumpDisable) && mc.player.getY() > module.enablePosY + 0.25D && (Boolean)module.jumpDisable.getValue()) {
         module.setEnabled(false);
         return true;
      } else {
         return false;
      }
   }

   private void findSlot() {
      this.slot = this.getSlot();
   }

   private BlockState getState(BlockPos pos) {
      return this.stateHelper.getBlockState(pos);
   }

   private BlockState getState() {
      return InventoryUtil.getStack(this.slot).getItem() == Items.COBWEB ? Blocks.COBWEB.getDefaultState() : (InventoryUtil.getStack(this.slot).getItem() == Items.ENDER_CHEST ? Blocks.ENDER_CHEST.getDefaultState() : Blocks.OBSIDIAN.getDefaultState());   }

   public boolean fastIntersectionCheck(BlockPos pos, Entity entity) {
      if (!EntityUtil.isDead(entity) && !(entity instanceof EndCrystalEntity) && !(entity instanceof ArrowEntity) && !(entity instanceof ExperienceOrbEntity) && !(entity instanceof ItemEntity)) {
         Box bb = entity.getBoundingBox();
         if (!(entity instanceof FakePlayerEntity) && entity instanceof LivingEntity) {
            bb = ((ILivingEntity)entity).getServerBoundingBox();
         }

         bb.expand(-1.0E-7D);
         return PositionUtil.voxelShapeIntersect(bb, new Box(pos));
      } else {
         return false;
      }
   }

   public boolean fastIntersectionCheck(BlockPos pos, List<Entity> entities) {
      Iterator var3 = entities.iterator();

      Entity entity;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         entity = (Entity)var3.next();
      } while(entity == null || !this.fastIntersectionCheck(pos, entity));

      return true;
   }

   
   public String toString() {
      String var10000 = super.toString();
      return "BlockPlaceModule(super=" + var10000 + ", timing=" + String.valueOf(this.timing) + ", rotations=" + String.valueOf(this.rotations) + ", swing=" + String.valueOf(this.swing) + ", strictDirection=" + String.valueOf(this.strictDirection) + ", attack=" + String.valueOf(this.attack) + ", placeRange=" + String.valueOf(this.getPlaceRange()) + ", blocks=" + String.valueOf(this.blocks) + ", placeDelay=" + String.valueOf(this.placeDelay) + ", jumpDisable=" + String.valueOf(this.jumpDisable) + ", chorusDisable=" + String.valueOf(this.chorusDisable) + ", render=" + String.valueOf(this.render) + ", altSwap=" + String.valueOf(this.altSwap) + ", stateHelper=" + String.valueOf(this.stateHelper) + ", awaiting=" + String.valueOf(this.awaiting) + ", packets=" + String.valueOf(this.getPackets()) + ", renderBlocks=" + String.valueOf(this.renderBlocks) + ", attackingPacket=" + String.valueOf(this.attackingPacket) + ", lastAttacked=" + String.valueOf(this.lastAttacked) + ", attackTimer=" + String.valueOf(this.attackTimer) + ", placed=" + String.valueOf(this.getPlaced()) + ", enablePosY=" + this.enablePosY + ", blocksPlaced=" + this.blocksPlaced + ", burstBlocks=" + this.burstBlocks + ", slot=" + this.getSlot() + ", timer=" + String.valueOf(this.timer) + ", burstTimer=" + String.valueOf(this.burstTimer) + ", jumpTimer=" + String.valueOf(this.jumpTimer) + ")";
   }

   
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BlockPlaceModule)) {
         return false;
      } else {
         BlockPlaceModule other = (BlockPlaceModule)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (!super.equals(o)) {
            return false;
         } else if (Double.compare(this.enablePosY, other.enablePosY) != 0) {
            return false;
         } else if (this.blocksPlaced != other.blocksPlaced) {
            return false;
         } else if (this.burstBlocks != other.burstBlocks) {
            return false;
         } else if (this.getSlot() != other.getSlot()) {
            return false;
         } else {
            Object this$timing = this.timing;
            Object other$timing = other.timing;
            if (this$timing == null) {
               if (other$timing != null) {
                  return false;
               }
            } else if (!this$timing.equals(other$timing)) {
               return false;
            }

            Object this$rotations = this.rotations;
            Object other$rotations = other.rotations;
            if (this$rotations == null) {
               if (other$rotations != null) {
                  return false;
               }
            } else if (!this$rotations.equals(other$rotations)) {
               return false;
            }

            label285: {
               Object this$swing = this.swing;
               Object other$swing = other.swing;
               if (this$swing == null) {
                  if (other$swing == null) {
                     break label285;
                  }
               } else if (this$swing.equals(other$swing)) {
                  break label285;
               }

               return false;
            }

            label278: {
               Object this$strictDirection = this.strictDirection;
               Object other$strictDirection = other.strictDirection;
               if (this$strictDirection == null) {
                  if (other$strictDirection == null) {
                     break label278;
                  }
               } else if (this$strictDirection.equals(other$strictDirection)) {
                  break label278;
               }

               return false;
            }

            label271: {
               Object this$attack = this.attack;
               Object other$attack = other.attack;
               if (this$attack == null) {
                  if (other$attack == null) {
                     break label271;
                  }
               } else if (this$attack.equals(other$attack)) {
                  break label271;
               }

               return false;
            }

            label264: {
               Object this$placeRange = this.getPlaceRange();
               Object other$placeRange = other.getPlaceRange();
               if (this$placeRange == null) {
                  if (other$placeRange == null) {
                     break label264;
                  }
               } else if (this$placeRange.equals(other$placeRange)) {
                  break label264;
               }

               return false;
            }

            Object this$blocks = this.blocks;
            Object other$blocks = other.blocks;
            if (this$blocks == null) {
               if (other$blocks != null) {
                  return false;
               }
            } else if (!this$blocks.equals(other$blocks)) {
               return false;
            }

            label250: {
               Object this$placeDelay = this.placeDelay;
               Object other$placeDelay = other.placeDelay;
               if (this$placeDelay == null) {
                  if (other$placeDelay == null) {
                     break label250;
                  }
               } else if (this$placeDelay.equals(other$placeDelay)) {
                  break label250;
               }

               return false;
            }

            Object this$jumpDisable = this.jumpDisable;
            Object other$jumpDisable = other.jumpDisable;
            if (this$jumpDisable == null) {
               if (other$jumpDisable != null) {
                  return false;
               }
            } else if (!this$jumpDisable.equals(other$jumpDisable)) {
               return false;
            }

            Object this$chorusDisable = this.chorusDisable;
            Object other$chorusDisable = other.chorusDisable;
            if (this$chorusDisable == null) {
               if (other$chorusDisable != null) {
                  return false;
               }
            } else if (!this$chorusDisable.equals(other$chorusDisable)) {
               return false;
            }

            Object this$render = this.render;
            Object other$render = other.render;
            if (this$render == null) {
               if (other$render != null) {
                  return false;
               }
            } else if (!this$render.equals(other$render)) {
               return false;
            }

            label222: {
               Object this$altSwap = this.altSwap;
               Object other$altSwap = other.altSwap;
               if (this$altSwap == null) {
                  if (other$altSwap == null) {
                     break label222;
                  }
               } else if (this$altSwap.equals(other$altSwap)) {
                  break label222;
               }

               return false;
            }

            label215: {
               Object this$stateHelper = this.stateHelper;
               Object other$stateHelper = other.stateHelper;
               if (this$stateHelper == null) {
                  if (other$stateHelper == null) {
                     break label215;
                  }
               } else if (this$stateHelper.equals(other$stateHelper)) {
                  break label215;
               }

               return false;
            }

            Object this$awaiting = this.awaiting;
            Object other$awaiting = other.awaiting;
            if (this$awaiting == null) {
               if (other$awaiting != null) {
                  return false;
               }
            } else if (!this$awaiting.equals(other$awaiting)) {
               return false;
            }

            label201: {
               Object this$packets = this.getPackets();
               Object other$packets = other.getPackets();
               if (this$packets == null) {
                  if (other$packets == null) {
                     break label201;
                  }
               } else if (this$packets.equals(other$packets)) {
                  break label201;
               }

               return false;
            }

            Object this$renderBlocks = this.renderBlocks;
            Object other$renderBlocks = other.renderBlocks;
            if (this$renderBlocks == null) {
               if (other$renderBlocks != null) {
                  return false;
               }
            } else if (!this$renderBlocks.equals(other$renderBlocks)) {
               return false;
            }

            label187: {
               Object this$attackingPacket = this.attackingPacket;
               Object other$attackingPacket = other.attackingPacket;
               if (this$attackingPacket == null) {
                  if (other$attackingPacket == null) {
                     break label187;
                  }
               } else if (this$attackingPacket.equals(other$attackingPacket)) {
                  break label187;
               }

               return false;
            }

            Object this$lastAttacked = this.lastAttacked;
            Object other$lastAttacked = other.lastAttacked;
            if (this$lastAttacked == null) {
               if (other$lastAttacked != null) {
                  return false;
               }
            } else if (!this$lastAttacked.equals(other$lastAttacked)) {
               return false;
            }

            label173: {
               Object this$attackTimer = this.attackTimer;
               Object other$attackTimer = other.attackTimer;
               if (this$attackTimer == null) {
                  if (other$attackTimer == null) {
                     break label173;
                  }
               } else if (this$attackTimer.equals(other$attackTimer)) {
                  break label173;
               }

               return false;
            }

            label166: {
               Object this$placed = this.getPlaced();
               Object other$placed = other.getPlaced();
               if (this$placed == null) {
                  if (other$placed == null) {
                     break label166;
                  }
               } else if (this$placed.equals(other$placed)) {
                  break label166;
               }

               return false;
            }

            label159: {
               Object this$timer = this.timer;
               Object other$timer = other.timer;
               if (this$timer == null) {
                  if (other$timer == null) {
                     break label159;
                  }
               } else if (this$timer.equals(other$timer)) {
                  break label159;
               }

               return false;
            }

            label152: {
               Object this$burstTimer = this.burstTimer;
               Object other$burstTimer = other.burstTimer;
               if (this$burstTimer == null) {
                  if (other$burstTimer == null) {
                     break label152;
                  }
               } else if (this$burstTimer.equals(other$burstTimer)) {
                  break label152;
               }

               return false;
            }

            Object this$jumpTimer = this.jumpTimer;
            Object other$jumpTimer = other.jumpTimer;
            if (this$jumpTimer == null) {
               if (other$jumpTimer != null) {
                  return false;
               }
            } else if (!this$jumpTimer.equals(other$jumpTimer)) {
               return false;
            }

            return true;
         }
      }
   }

   
   protected boolean canEqual(Object other) {
      return other instanceof BlockPlaceModule;
   }

   
   public int hashCode() {
      int result = super.hashCode();
      long $enablePosY = Double.doubleToLongBits(this.enablePosY);
      result = result * 59 + (int)($enablePosY >>> 32 ^ $enablePosY);
      result = result * 59 + this.blocksPlaced;
      result = result * 59 + this.burstBlocks;
      result = result * 59 + this.getSlot();
      Object $timing = this.timing;
      result = result * 59 + ($timing == null ? 43 : $timing.hashCode());
      Object $rotations = this.rotations;
      result = result * 59 + ($rotations == null ? 43 : $rotations.hashCode());
      Object $swing = this.swing;
      result = result * 59 + ($swing == null ? 43 : $swing.hashCode());
      Object $strictDirection = this.strictDirection;
      result = result * 59 + ($strictDirection == null ? 43 : $strictDirection.hashCode());
      Object $attack = this.attack;
      result = result * 59 + ($attack == null ? 43 : $attack.hashCode());
      Object $placeRange = this.getPlaceRange();
      result = result * 59 + ($placeRange == null ? 43 : $placeRange.hashCode());
      Object $blocks = this.blocks;
      result = result * 59 + ($blocks == null ? 43 : $blocks.hashCode());
      Object $placeDelay = this.placeDelay;
      result = result * 59 + ($placeDelay == null ? 43 : $placeDelay.hashCode());
      Object $jumpDisable = this.jumpDisable;
      result = result * 59 + ($jumpDisable == null ? 43 : $jumpDisable.hashCode());
      Object $chorusDisable = this.chorusDisable;
      result = result * 59 + ($chorusDisable == null ? 43 : $chorusDisable.hashCode());
      Object $render = this.render;
      result = result * 59 + ($render == null ? 43 : $render.hashCode());
      Object $altSwap = this.altSwap;
      result = result * 59 + ($altSwap == null ? 43 : $altSwap.hashCode());
      Object $stateHelper = this.stateHelper;
      result = result * 59 + ($stateHelper == null ? 43 : $stateHelper.hashCode());
      Object $awaiting = this.awaiting;
      result = result * 59 + ($awaiting == null ? 43 : $awaiting.hashCode());
      Object $packets = this.getPackets();
      result = result * 59 + ($packets == null ? 43 : $packets.hashCode());
      Object $renderBlocks = this.renderBlocks;
      result = result * 59 + ($renderBlocks == null ? 43 : $renderBlocks.hashCode());
      Object $attackingPacket = this.attackingPacket;
      result = result * 59 + ($attackingPacket == null ? 43 : $attackingPacket.hashCode());
      Object $lastAttacked = this.lastAttacked;
      result = result * 59 + ($lastAttacked == null ? 43 : $lastAttacked.hashCode());
      Object $attackTimer = this.attackTimer;
      result = result * 59 + ($attackTimer == null ? 43 : $attackTimer.hashCode());
      Object $placed = this.getPlaced();
      result = result * 59 + ($placed == null ? 43 : $placed.hashCode());
      Object $timer = this.timer;
      result = result * 59 + ($timer == null ? 43 : $timer.hashCode());
      Object $burstTimer = this.burstTimer;
      result = result * 59 + ($burstTimer == null ? 43 : $burstTimer.hashCode());
      Object $jumpTimer = this.jumpTimer;
      result = result * 59 + ($jumpTimer == null ? 43 : $jumpTimer.hashCode());
      return result;
   }

   
   public NumberValue<Float> getPlaceRange() {
      return this.placeRange;
   }

   
   public List<Packet<?>> getPackets() {
      return this.packets;
   }

   
   public Map<BlockPos, Long> getPlaced() {
      return this.placed;
   }



}
