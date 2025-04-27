package me.pollos.polloshook.asm.mixins.network;

import com.mojang.authlib.GameProfile;
import java.util.function.Supplier;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.misc.GetSkinEvent;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerListEntry.class})
public class MixinPlayerListEntry {
   @Shadow
   @Final
   private GameProfile profile;
   @Shadow
   @Final
   private Supplier<SkinTextures> texturesSupplier;
   @Unique
   private static final SkinTextures[] SLIMS = new SkinTextures[]{slim("textures/entity/player/slim/alex.png"), slim("textures/entity/player/slim/ari.png"), slim("textures/entity/player/slim/efe.png"), slim("textures/entity/player/slim/kai.png"), slim("textures/entity/player/slim/makena.png"), slim("textures/entity/player/slim/noor.png"), slim("textures/entity/player/slim/steve.png"), slim("textures/entity/player/slim/sunny.png"), slim("textures/entity/player/slim/zuri.png")};
   @Unique
   private static final SkinTextures[] WIDES = new SkinTextures[]{wide("textures/entity/player/wide/alex.png"), wide("textures/entity/player/wide/ari.png"), wide("textures/entity/player/wide/efe.png"), wide("textures/entity/player/wide/kai.png"), wide("textures/entity/player/wide/makena.png"), wide("textures/entity/player/wide/noor.png"), wide("textures/entity/player/wide/steve.png"), wide("textures/entity/player/wide/sunny.png"), wide("textures/entity/player/wide/zuri.png")};

   @Inject(
   method = {"getSkinTextures"}, 
   at = {@At("HEAD")}, 
   cancellable = true
)
private void getSkinTexturesHook(CallbackInfoReturnable<SkinTextures> cir) {
   GetSkinEvent getSkinEvent = new GetSkinEvent(this.profile);
   PollosHook.getEventBus().dispatch(getSkinEvent);
   
   if (getSkinEvent.isCanceled()) {
      Model model = ((SkinTextures)this.texturesSupplier.get()).model();
      int hashCode = Math.abs(this.profile.hashCode());

      String toString = String.valueOf(hashCode);
      int i = Character.getNumericValue(toString.charAt(0));
      
      if (i == 0 || i > 9) {
         i = 2;
      }
      
      if (model == Model.SLIM) {
         cir.setReturnValue(SLIMS[i % SLIMS.length]);
      } else {
         cir.setReturnValue(WIDES[i % WIDES.length]);
      }
   }
}

   @Unique
   private static SkinTextures slim(String texture) {
      return new SkinTextures(Identifier.of(texture), (String)null, (Identifier)null, (Identifier)null, Model.SLIM, true);
   }

   @Unique
   private static SkinTextures wide(String texture) {
      return new SkinTextures(Identifier.of(texture), (String)null, (Identifier)null, (Identifier)null, Model.WIDE, true);
   }
}