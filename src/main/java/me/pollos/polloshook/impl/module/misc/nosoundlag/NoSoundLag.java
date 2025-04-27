package me.pollos.polloshook.impl.module.misc.nosoundlag;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional; 
import java.util.stream.Collectors;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.nosoundlag.util.CountedSound;
import me.pollos.polloshook.impl.module.misc.nosoundlag.util.NoSoundMode;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class NoSoundLag extends ToggleableModule {
   protected final EnumValue<NoSoundMode> mode;
   protected final NumberValue<Integer> threshold;
   protected final NumberValue<Float> factor;
   protected final ArrayList<CountedSound> sounds;
   protected final List<RegistryEntry<SoundEvent>> packetSounds;

   public NoSoundLag() {
      super(new String[]{"NoSoundLag", "nosoundlagger"}, Category.MISC);
      this.mode = new EnumValue(NoSoundMode.PACKET, new String[]{"Mode", "mod"});
      this.threshold = (new NumberValue(250, 100, 1000, new String[]{"Threshold", "thresh"})).setParent(this.mode, NoSoundMode.SPAM);
      this.factor = (new NumberValue(1.5F, 1.0F, 10.0F, 0.25F, new String[]{"Factor", "f"})).setParent(this.mode, NoSoundMode.SPAM);
      this.sounds = new ArrayList();

      this.packetSounds = Arrays.asList(
            SoundEvents.ENTITY_GENERIC_DRINK,
            SoundEvents.ENTITY_GENERIC_EAT,
            SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
            SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
            SoundEvents.ENTITY_GENERIC_HURT,
            SoundEvents.ENTITY_PLAYER_HURT,
            SoundEvents.ENTITY_PLAYER_DEATH,
            SoundEvents.ENTITY_GENERIC_SPLASH
      ).stream()
       .map(soundEvent -> Registries.SOUND_EVENT.getEntry(soundEvent.getId()))
       .filter(Optional::isPresent) 
       .map(Optional::get) 
       .collect(Collectors.toList());


      this.offerValues(new Value[]{this.mode, this.threshold, this.factor});
      this.offerListeners(new Listener[]{ new ListenerPacket(this), new ListenerSound(this), new ListenerUpdate(this) }); 
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   protected float getSoundDuration(Identifier identifier) {
      try {
         Optional<Resource> resourceOptional = mc.getResourceManager().getResource(identifier);
         Resource resource = resourceOptional.orElse(null);

         if (resource != null) {
            InputStream stream = resource.getInputStream();

            float var8;
            label54: {
               try {
                  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
                  long frames = audioInputStream.getFrameLength();
                  float frameRate = audioInputStream.getFormat().getFrameRate();
                  if (frameRate > 0.0F) {
                     var8 = (float)frames / frameRate;
                     break label54;
                  }
               } catch (Throwable var10) {
                  if (stream != null) {
                     try {
                        stream.close();
                     } catch (Throwable var9) {
                        var10.addSuppressed(var9);
                     }
                  }
                  throw var10;
               } finally { 
                  if (stream != null) {
                      stream.close();
                  }
               }

               return 1000.0F; 
            }

            return var8;
         }
      } catch (Exception var11) {
         ClientLogger.getLogger().error("Unable to get duration for sound %s: %s".formatted(new Object[]{identifier.getPath(), var11.getMessage()}));
         var11.printStackTrace();
      }

      return 1000.0F;
   }

   public static class PlaySoundEvent extends Event {
      private final SoundInstance instance;

      public SoundInstance getInstance() {
         return this.instance;
      }

      private PlaySoundEvent(SoundInstance instance) {
         this.instance = instance;
      }

      public static NoSoundLag.PlaySoundEvent of(SoundInstance instance) {
         return new NoSoundLag.PlaySoundEvent(instance);
      }
   }
}