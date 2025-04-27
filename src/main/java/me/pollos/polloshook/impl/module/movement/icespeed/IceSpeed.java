package me.pollos.polloshook.impl.module.movement.icespeed;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.asm.mixins.block.IAbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class IceSpeed extends ToggleableModule {
   protected final NumberValue<Float> speed = new NumberValue(0.5F, 0.1F, 1.0F, 0.1F, new String[]{"Speed", "sped"});
   public static final List<Block> ICE_BLOCKS;

   public IceSpeed() {
      super(new String[]{"IceSpeed", "icesped"}, Category.MOVEMENT);
      this.offerValues(new Value[]{this.speed});
      this.offerListeners(new Listener[]{new ListenerUpdate(this)});
   }

   protected void onToggle() {
      ICE_BLOCKS.forEach((elementCodec) -> {
         IAbstractBlock access = (IAbstractBlock)elementCodec;
         access.setSlipperiness(0.98F);
         if (elementCodec.equals(Blocks.BLUE_ICE)) {
            access.setSlipperiness(0.989F);
         }

      });
   }

   static {
      ICE_BLOCKS = Arrays.asList(Blocks.ICE, Blocks.PACKED_ICE, Blocks.FROSTED_ICE, Blocks.BLUE_ICE);
   }
}