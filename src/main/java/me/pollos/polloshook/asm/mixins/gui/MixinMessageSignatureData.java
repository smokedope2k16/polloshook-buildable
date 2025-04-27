package me.pollos.polloshook.asm.mixins.gui;


import me.pollos.polloshook.asm.ducks.gui.chat.IMessageSignatureData;
import net.minecraft.network.message.MessageSignatureData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({MessageSignatureData.class})
public class MixinMessageSignatureData implements IMessageSignatureData {
   @Unique
   private int id = 0;

   
   public int getId() {
      return this.id;
   }

   
   public void setId(int id) {
      this.id = id;
   }
}
