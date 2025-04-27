package me.pollos.polloshook.asm.mixins.util;

import java.io.InputStream;
import java.util.List;
import me.pollos.polloshook.api.minecraft.render.utils.IconUtil;
import net.minecraft.client.util.Icons;
import net.minecraft.resource.InputSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Icons.class})
public abstract class MixinIcons {
   @Inject(
      method = {"getIcons"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getIconsHook(CallbackInfoReturnable<List<InputSupplier<InputStream>>> info) {
      info.setReturnValue(IconUtil.getAllPngResources());
   }
}
