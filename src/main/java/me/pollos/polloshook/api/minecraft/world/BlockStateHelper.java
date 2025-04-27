package me.pollos.polloshook.api.minecraft.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.Nullable;

public class BlockStateHelper implements Minecraftable, IBlockStateHelper {
   private final Map<BlockPos, BlockState> states;
   private final Supplier<WorldAccess> world;

   public BlockStateHelper() {
      this(new HashMap<>());
   }

   public BlockStateHelper(Supplier<WorldAccess> world) {
      this(new HashMap<>(), world);
   }

   public BlockStateHelper(Map<BlockPos, BlockState> stateMap) {
      this(stateMap, () -> mc.world);
   }

   public BlockStateHelper(Map<BlockPos, BlockState> states, Supplier<WorldAccess> world) {
      this.states = states;
      this.world = world;
   }

   public Map<BlockPos, BlockState> getStates() {
      return this.states;
   }

   @Override
   public BlockState getBlockState(BlockPos pos) {
      BlockState state = this.states.get(pos);
      return state == null ? this.world.get().getBlockState(pos) : state;
   }

   @Override
   public FluidState getFluidState(BlockPos pos) {
      return null;
   }

   public void addBlockState(BlockPos pos, BlockState state) {
      this.states.putIfAbsent(pos.toImmutable(), state);
   }

   public void delete(BlockPos pos) {
      this.states.remove(pos);
   }

   public void clearAllStates() {
      this.states.clear();
   }

   @Override
   public BlockEntity getBlockEntity(BlockPos pos) {
      return this.world.get().getBlockEntity(pos);
   }

   @Override
   public int getLightLevel(BlockPos pos, int lightValue) {
      return this.world.get().getLightLevel(pos, lightValue);
   }

   @Override
   public boolean isAir(BlockPos pos) {
      return this.getBlockState(pos).isAir();
   }

   @Nullable
   @Override
   public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
      return null;
   }

   @Override
   public int getTopY(Type heightmap, int x, int z) {
      return 0;
   }

   @Override
   public int getAmbientDarkness() {
      return 0;
   }

   @Override
   public BiomeAccess getBiomeAccess() {
      return null;
   }

   @Override
   public RegistryEntry<Biome> getBiome(BlockPos pos) {
      return this.world.get().getBiome(pos);
   }

   @Override
   public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
      return null;
   }

   @Override
   public boolean isClient() {
      return false;
   }

   @Override
   public int getSeaLevel() {
      return 0;
   }

   @Override
   public DimensionType getDimension() {
      return null;
   }

   @Override
   public DynamicRegistryManager getRegistryManager() {
      return null;
   }

   @Override
   public FeatureSet getEnabledFeatures() {
      return null;
   }

   public ClientWorld getClientWorld() {
      return mc.world;
   }

   @Override
   public long getTickOrder() {
      return 0L;
   }

   @Override
   public QueryableTickScheduler<Block> getBlockTickScheduler() {
      return null;
   }

   @Override
   public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
      return null;
   }

   @Override
   public WorldProperties getLevelProperties() {
      return null;
   }

   @Override
   public LocalDifficulty getLocalDifficulty(BlockPos pos) {
      return null;
   }

   @Nullable
   @Override
   public MinecraftServer getServer() {
      return null;
   }

   @Override
   public ChunkManager getChunkManager() {
      return null;
   }

   @Override
   public Random getRandom() {
      return null;
   }

   @Override
   public void playSound(@Nullable PlayerEntity except, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
   }

   @Override
   public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
   }

   @Override
   public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
   }

   @Override
   public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, Emitter emitter) {
   }

   @Override
   public float getBrightness(Direction direction, boolean shaded) {
      return 0.0F;
   }

   @Override
   public LightingProvider getLightingProvider() {
      return null;
   }

   @Override
   public WorldBorder getWorldBorder() {
      return null;
   }

   @Override
   public List<Entity> getOtherEntities(@Nullable Entity except, Box box, Predicate<? super Entity> predicate) {
      return null;
   }

   @Override
   public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
      return null;
   }

   @Override
   public List<? extends PlayerEntity> getPlayers() {
      return null;
   }

   @Override
   public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
      return false;
   }

   @Override
   public boolean removeBlock(BlockPos pos, boolean move) {
      return false;
   }

   @Override
   public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
      return false;
   }

   @Override
   public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
      return false;
   }

   @Override
   public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
      return false;
   }
}