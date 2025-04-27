package me.pollos.polloshook.impl.manager.minecraft.block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.session.telemetry.WorldUnloadedEvent;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.BlockPos;

public class BlocksManager extends SubscriberImpl implements Minecraftable {
   private final Map<BlockPos, Queue<Consumer<BlockState>>> callbacks = new ConcurrentHashMap();
   private final List<BlockPos> waitingQueue = new ArrayList();

   public BlocksManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<BlockUpdateS2CPacket>>(PacketEvent.Receive.class, BlockUpdateS2CPacket.class) {
         public void call(PacketEvent.Receive<BlockUpdateS2CPacket> event) {
            BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket)event.getPacket();
            BlocksManager.this.runProcess(packet.getPos(), packet.getState());
         }
      });
      this.listeners.add(new Listener<PacketEvent.Receive<ExplosionS2CPacket>>(PacketEvent.Receive.class, ExplosionS2CPacket.class) {
         public void call(PacketEvent.Receive<ExplosionS2CPacket> event) {
            ExplosionS2CPacket packet = (ExplosionS2CPacket)event.getPacket();
            Iterator var3 = packet.getAffectedBlocks().iterator();

            while(var3.hasNext()) {
               BlockPos pos = (BlockPos)var3.next();
               BlocksManager.this.runProcess(pos, Blocks.AIR.getDefaultState());
            }

         }
      });
      this.listeners.add(new Listener<WorldUnloadedEvent>(WorldUnloadedEvent.class) {
         public void call(WorldUnloadedEvent event) {
            BlocksManager.this.callbacks.clear();
         }
      });
      this.listeners.add(new Listener<LeaveGameEvent>(LeaveGameEvent.class) {
         public void call(LeaveGameEvent event) {
            BlocksManager.this.callbacks.clear();
         }
      });
      this.listeners.add(new Listener<MotionUpdateEvent>(MotionUpdateEvent.class, Integer.MIN_VALUE) {
         public void call(MotionUpdateEvent event) {
            if (event.getStage() == Stage.POST) {
               BlocksManager.this.waitingQueue.clear();
            }

         }
      });
   }

   public void addCallback(BlockPos pos, Consumer<BlockState> callback) {
      ((Queue)this.callbacks.computeIfAbsent(pos.toImmutable(), (v) -> {
         return new ConcurrentLinkedQueue();
      })).add(callback);
   }

   private void process(BlockPos pos, BlockState state) {
      Queue<Consumer<BlockState>> cbs = (Queue)this.callbacks.remove(pos);
      if (cbs != null) {
         emptyQueue(cbs, (c) -> {
            c.accept(state);
         });
      }

   }

   private void runProcess(BlockPos pos, BlockState state) {
      PollosHookThread.submit(() -> {
         this.process(pos, state);
      });
   }

   public static <T> void emptyQueue(Queue<T> queue, Consumer<T> onPoll) {
      while(!queue.isEmpty()) {
         T polled = queue.poll();
         if (polled != null) {
            onPoll.accept(polled);
         }
      }

   }

   public List<BlockPos> getWaitingQueue() {
      return this.waitingQueue;
   }
}