package me.pollos.polloshook.impl.module.other.capes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.impl.events.render.GetCapeTextureEvent;
import me.pollos.polloshook.impl.module.other.capes.mode.CapeMode;
import me.pollos.polloshook.impl.module.other.capes.mode.SelfCapeMode;
import me.pollos.polloshook.impl.module.other.capes.util.impl.CapeEntry;
import me.pollos.polloshook.impl.module.other.capes.util.impl.CapeRegistry;
import me.pollos.polloshook.impl.module.other.capes.util.impl.CapeUtil;
import me.pollos.polloshook.impl.module.other.irc.IrcModule;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ListenerCape extends ModuleListener<Capes, GetCapeTextureEvent> {
   private final CapeRegistry registry = new CapeRegistry();

   public ListenerCape(Capes module) {
      super(module, GetCapeTextureEvent.class);
   }

   public void call(GetCapeTextureEvent event) {
      if (mc.player != null) {
         Entity entity = event.getEntity();
         if (entity != null) {
            Identifier[] id = this.getCapeFromPlayer(entity);
            if (id != null) {
               event.setIdentifiers(id);
            }
         }
      }
   }

   private Identifier[] getCapeFromPlayer(Entity entity) {
      if (entity instanceof ClientPlayerEntity && ((Capes)this.module).self.getValue() != SelfCapeMode.AUTO && ((Capes)this.module).capes.getValue() == CapeMode.CUSTOM) {
         CapeEntry entry = CapeUtil.getEntryFromLabel(((SelfCapeMode)((Capes)this.module).self.getValue()).name().toLowerCase().replace("_", ""), this.registry.getEntries());
         if (entry != null) {
            Identifier identifier = CapeUtil.getIdentifier(entry);
            return this.split(identifier);
         }
      }

      if (!(entity instanceof PlayerEntity)) {
         return null;
      } else {
         String displayName = EntityUtil.getName(entity);
         switch((CapeMode)((Capes)this.module).capes.getValue()) {
         case CUSTOM:
            CapeEntry entry = CapeUtil.getEntryFromUUID(entity.getUuid(), this.registry.getEntries());
            if (entry != null) {
               Identifier identifier = CapeUtil.getIdentifier(entry);
               return this.split(identifier);
            }

            if (!Managers.getIrcManager().isClientUser(displayName) && !(entity instanceof ClientPlayerEntity)) {
               break;
            }

            return this.split(IrcModule.IRC_CAPE_IDENTIFIER);
         case GENSHIN:
            boolean isCaped = Managers.getIrcManager().isClientUser(displayName) || entity instanceof ClientPlayerEntity || CapeUtil.getEntryFromUUID(entity.getUuid(), this.registry.getEntries()) != null;
            if (isCaped) {
               String hash = Integer.toString(Math.abs(entity.hashCode()));
               int index = 0;
               char[] var6 = hash.toCharArray();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  char c = var6[var8];
                  int digit = Character.getNumericValue(c);
                  if (digit < 4) {
                     index = digit;
                     break;
                  }
               }

               return this.split((Identifier)this.registry.getGenshinIdentifiers().get(index));
            }
         }

         return null;
      }
   }

   private List<Identifier[]> split(List<Identifier> i) {
      List<Identifier[]> identifiers = new ArrayList();
      Iterator var3 = i.iterator();

      while(var3.hasNext()) {
         Identifier identifier = (Identifier)var3.next();
         identifiers.add(new Identifier[]{identifier, identifier});
      }

      return identifiers;
   }

   private Identifier[] split(Identifier i) {
      return new Identifier[]{i, i};
   }
}
