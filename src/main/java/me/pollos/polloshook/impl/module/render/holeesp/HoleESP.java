package me.pollos.polloshook.impl.module.render.holeesp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.api.util.thread.ThreadUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.holeesp.mode.OutlineMode;
import net.minecraft.util.math.BlockPos;

public class HoleESP extends ToggleableModule {
   protected final NumberValue<Integer> range = (new NumberValue(8, 1, 30, new String[]{"Range", "distance", "r"})).withTag("range");
   protected final Value<Boolean> doubles = new Value(true, new String[]{"2x1Holes", "2x1", "dobules"});
   protected final Value<Boolean> protocolSafe;
   protected final Value<Boolean> terrain;
   protected final NumberValue<Float> height;
   protected final NumberValue<Float> twoByOneHeight;
   protected final Value<Boolean> voidHole;
   protected final NumberValue<Integer> voidRange;
   protected final Value<Boolean> legacyVoid;
   protected final NumberValue<Float> lineWidth;
   protected final EnumValue<OutlineMode> outlineMode;
   protected final NumberValue<Integer> outlineAlpha;
   protected final Value<Boolean> custom2x1Color;
   protected final ColorValue doublesColor;
   protected final NumberValue<Integer> boxAlpha;
   protected final NumberValue<Integer> updates;
   protected final ColorValue obbyColor;
   protected final ColorValue mixedColor;
   protected final ColorValue bedrockColor;
   protected final ColorValue terrainColor;
   protected final ColorValue voidColor;
   protected ExecutorService service;
   protected List<Hole> holes;
   protected List<BlockPos> voidHoles;
   protected StopWatch timer;

   public HoleESP() {
      super(new String[]{"HoleESP", "holeingtons"}, Category.RENDER);
      this.protocolSafe = (new Value(false, new String[]{"1.12.2Safe", "protocolsafe"})).setParent(this.doubles);
      this.terrain = new Value(true, new String[]{"Terrain", "terrainholes"});
      this.height = new NumberValue(1.0F, -1.0F, 1.0F, 0.1F, new String[]{"Height", "h"});
      this.twoByOneHeight = new NumberValue(0.0F, -1.0F, 1.0F, 0.1F, new String[]{"2x1Height", "2x1h"});
      this.voidHole = new Value(false, new String[]{"Void", "voidholes"});
      this.voidRange = (new NumberValue(12, 1, 30, new String[]{"VoidRange", "voiddistance", "voidr"})).withTag("range").setParent(this.voidHole);
      this.legacyVoid = (new Value(true, new String[]{"LegacyVoid", "protocolvoid"})).setParent(this.voidHole);
      this.lineWidth = new NumberValue(1.0F, 1.0F, 4.0F, 0.1F, new String[]{"LineWidth", "width"});
      this.outlineMode = new EnumValue(OutlineMode.NORMAL, new String[]{"Outline", "outlineomode"});
      this.outlineAlpha = (new NumberValue(125, 0, 255, new String[]{"OutlineAlpha", "outlinea"})).setParent(this.outlineMode, OutlineMode.OFF, true);
      this.custom2x1Color = (new Value(false, new String[]{"Custom2x1Color", "custom2x1colour"})).setParent(this.doubles);
      this.doublesColor = (new ColorValue(new Color(6684927), false, new String[]{"2x1Color", "2x1colour"})).setParent(this.custom2x1Color);
      this.boxAlpha = new NumberValue(30, 0, 255, new String[]{"Alpha", "fillalpha"});
      this.updates = (new NumberValue(0, 0, 500, new String[]{"UpdateDelay", "updatetime"})).withTag("ms");
      this.obbyColor = new ColorValue(new Color(956235776, true), false, new String[]{"ObsidianColor", "obsidiancolour"});
      this.mixedColor = new ColorValue(new Color(956235776, true), false, new String[]{"MixedColor", "mixedcolour"});
      this.bedrockColor = new ColorValue(new Color(939589376, true), false, new String[]{"BedrockColor", "bedrockcolour"});
      this.terrainColor = (new ColorValue(new Color(939589631, true), true, new String[]{"TerrainColor", "terraincolour"})).setParent(this.terrain);
      this.voidColor = (new ColorValue(new Color(948699391, true), false, new String[]{"VoidColor", "voidcolour"})).setParent(this.voidHole);
      this.holes = new ArrayList();
      this.voidHoles = new ArrayList();
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.range, this.doubles, this.protocolSafe, this.terrain, this.height, this.twoByOneHeight, this.voidHole, this.voidRange, this.legacyVoid, this.boxAlpha, this.lineWidth, this.outlineMode, this.outlineAlpha, this.updates, this.custom2x1Color, this.doublesColor, this.obbyColor, this.mixedColor, this.bedrockColor, this.terrainColor, this.voidColor});
      this.offerListeners(new Listener[]{new ListenerRender(this), new ListenerTick(this)});
      this.service = ThreadUtil.newDaemonScheduledExecutor("HoleESP");
   }

   public void onShutdown() {
      this.service.shutdown();
   }

   protected Color getBedrockColor() {
      return this.bedrockColor.getColor();
   }

   protected Color getObbyColor() {
      return this.obbyColor.getColor();
   }

   protected Color getTerrainColor() {
      return this.terrainColor.getColor();
   }

   protected Color getMixedColor() {
      return this.mixedColor.getColor();
   }
}
