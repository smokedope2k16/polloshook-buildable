package me.pollos.polloshook.impl.module.other.capes.util.impl;

import java.util.Arrays;
import java.util.List;

import me.pollos.polloshook.impl.module.other.capes.util.AliEntry;
import me.pollos.polloshook.impl.module.other.capes.util.CN_CuteFoxQWQEntry;
import me.pollos.polloshook.impl.module.other.capes.util.ChachooxEntry;
import me.pollos.polloshook.impl.module.other.capes.util.CpvEntry;
import me.pollos.polloshook.impl.module.other.capes.util.GreenKidEntry;
import me.pollos.polloshook.impl.module.other.capes.util.GwhlEntry;
import me.pollos.polloshook.impl.module.other.capes.util.IvvyEntry;
import me.pollos.polloshook.impl.module.other.capes.util.LenayEntry;
import me.pollos.polloshook.impl.module.other.capes.util.OrionEntry;
import me.pollos.polloshook.impl.module.other.capes.util.PollosEntry;
import me.pollos.polloshook.impl.module.other.capes.util.VakuiEntry;
import net.minecraft.util.Identifier;

public class CapeRegistry {
   private final List<CapeEntry> entries = Arrays.asList(new AliEntry(), new ChachooxEntry(), new CN_CuteFoxQWQEntry(), new GwhlEntry(), new PollosEntry(), new VakuiEntry(), new IvvyEntry(), new OrionEntry(), new LenayEntry(), new CpvEntry(), new GreenKidEntry());
   private final List<String> genshinCapes = Arrays.asList("keqingcape.png", "kiaracape.png", "monacape.png", "hutaocape.png", "faruzancape.png");
   private final List<Identifier> genshinIdentifiers = Arrays.asList(CapeUtil.getIdentifier("keqingcape.png"), CapeUtil.getIdentifier("kiaracape.png"), CapeUtil.getIdentifier("monacape.png"), CapeUtil.getIdentifier("hutaocape.png"), CapeUtil.getIdentifier("faruzancape.png"));
   private static CapeRegistry REGISTRY;

   public CapeRegistry() {
      REGISTRY = this;
   }

   public static CapeRegistry getRegistry() {
      return REGISTRY == null ? new CapeRegistry() : REGISTRY;
   }

   
   public List<CapeEntry> getEntries() {
      return this.entries;
   }

   
   public List<String> getGenshinCapes() {
      return this.genshinCapes;
   }

   
   public List<Identifier> getGenshinIdentifiers() {
      return this.genshinIdentifiers;
   }
}
