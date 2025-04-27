package me.pollos.polloshook.impl.manager.minecraft.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.impl.events.entity.EntityWorldEvent;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class EntitiesManager extends SubscriberImpl implements Minecraftable {
   private volatile List<PlayerEntity> players = Collections.emptyList();
   private volatile List<Entity> entities = Collections.emptyList();
   private long lastUpdate = 0L;

   public EntitiesManager() {
      this.listeners.add(new Listener<GameLoopEvent>(GameLoopEvent.class) {
         public void call(GameLoopEvent event) {
            if (mc.world == null) {
               EntitiesManager.this.setLists(Collections.emptyList(), Collections.emptyList());
            } else if (System.currentTimeMillis() - EntitiesManager.this.lastUpdate >= 50L) {
               EntitiesManager.this.setLists(EntitiesManager.this.toList(mc.world.getEntities()), new ArrayList(mc.world.getPlayers()));
            }

         }
      });
      this.listeners.add(new Listener<EntityWorldEvent.Add>(EntityWorldEvent.Add.class) {
         public void call(EntityWorldEvent.Add event) {
            Entity entity = event.getEntity();
            if (entity != null) {
               List<Entity> entityList = new ArrayList(EntitiesManager.this.entities);
               List<PlayerEntity> playerEntities = new ArrayList(EntitiesManager.this.players);
               entityList.add(entity);
               if (entity instanceof PlayerEntity) {
                  PlayerEntity player = (PlayerEntity)entity;
                  playerEntities.add(player);
               }

               EntitiesManager.this.setLists(entityList, playerEntities);
               EntitiesManager.this.lastUpdate = System.currentTimeMillis();
            }
         }
      });
      this.listeners.add(new Listener<EntityWorldEvent.Remove>(EntityWorldEvent.Remove.class) {
         public void call(EntityWorldEvent.Remove event) {
            Entity entity = event.getEntity();
            if (entity != null) {
               List<Entity> entityList = new ArrayList(EntitiesManager.this.entities);
               List<PlayerEntity> playerEntities = new ArrayList(EntitiesManager.this.players);
               entityList.remove(entity);
               if (entity instanceof PlayerEntity) {
                  PlayerEntity player = (PlayerEntity)entity;
                  playerEntities.remove(player);
               }

               EntitiesManager.this.setLists(entityList, playerEntities);
               EntitiesManager.this.lastUpdate = System.currentTimeMillis();
            }
         }
      });
   }

   private List<Entity> toList(Iterable<Entity> entities) {
      List<Entity> target = new ArrayList();
      Iterator entityIterator = entities.iterator();

      while(entityIterator.hasNext()) {
         target.add((Entity)entityIterator.next());
      }

      return target;
   }

   private void setLists(List<Entity> loadedEntities, List<PlayerEntity> playerEntities) {
      this.entities = loadedEntities;
      this.players = playerEntities;
   }

   public List<Entity> getAnyCollidingEntities(Box bb) {
      List<Entity> colliding = new ArrayList();
      List<Entity> current = new ArrayList(this.entities);
      current.forEach((entity) -> {
         if (entity.getBoundingBox().intersects(bb)) {
            colliding.add(entity);
         }

      });
      return colliding;
   }

   
   public List<PlayerEntity> getPlayers() {
      return this.players;
   }

   
   public List<Entity> getEntities() {
      return this.entities;
   }

   
   public long getLastUpdate() {
      return this.lastUpdate;
   }
}
