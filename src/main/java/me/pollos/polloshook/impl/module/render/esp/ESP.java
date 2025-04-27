package me.pollos.polloshook.impl.module.render.esp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.esp.mode.OutlineMode;
import me.pollos.polloshook.impl.module.render.esp.util.ChorusPos;
import net.minecraft.util.math.Vec3d;

public class ESP extends ToggleableModule {
   protected final EnumValue<OutlineMode> mode;
   protected final Value<Boolean> items;
   protected final Value<Boolean> xpBottles;
   protected final Value<Boolean> enderPearls;
   protected final Value<Boolean> players;
   protected final Value<Boolean> self;
   protected final NumberValue<Float> lineWidth;
   protected final NumberValue<Integer> outlineAlpha;
   protected final NumberValue<Integer> fillAlpha;
   protected final ColorValue color;
   protected final Value<Boolean> itemNametags;
   protected final Value<Boolean> stack;
   protected final Value<Boolean> merge;
   protected final Value<Boolean> chorus;
   protected final NumberValue<Float> fadeTime;
   protected final Value<Boolean> pearlNametags;
   protected final ColorValue nametagsColor;
   protected final List<ChorusPos> chorusFruits;
   protected final ConcurrentHashMap<UUID, Vec3d> ignoredPlayers;
   protected Vec3d[] ignoreVec;

   public ESP() {
      super(new String[]{"ESP", "newesp", "cleanesp", "playeresp"}, Category.RENDER);
      this.mode = new EnumValue(OutlineMode.OFF, new String[]{"Mode", "m", "type"});
      this.items = (new Value(true, new String[]{"Items", "item"})).setParent(this.mode, OutlineMode.OFF, true);
      this.xpBottles = (new Value(true, new String[]{"XPBottles", "xp", "exp"})).setParent(this.mode, OutlineMode.OFF, true);
      this.enderPearls = (new Value(true, new String[]{"EnderPearls", "pearls", "epearls"})).setParent(this.mode, OutlineMode.OFF, true);
      this.players = (new Value(true, new String[]{"Players", "player"})).setParent(this.mode, OutlineMode.OFF, true);
      this.self = (new Value(false, new String[]{"Self", "me", "you"})).setParent(this.players);
      this.lineWidth = (new NumberValue(1.0F, 1.0F, 4.0F, 0.1F, new String[]{"LineWidth", "wirewidth"})).setParent(this.mode, OutlineMode.OFF, true);
      this.outlineAlpha = (new NumberValue(255, 0, 255, new String[]{"LineAlpha", "alphaline"})).setParent(this.mode, OutlineMode.FILL, true);
      this.fillAlpha = (new NumberValue(45, 0, 255, new String[]{"FillAlpha", "alphafill"})).setParent(this.mode, OutlineMode.OUTLINE, true);
      this.color = new ColorValue(new Color(-1, false), true, new String[]{"Color", "c"});
      this.itemNametags = new Value(false, new String[]{"ItemNametags", "nametags"});
      this.stack = (new Value(false, new String[]{"Stack", "s"})).setParent(this.itemNametags);
      this.merge = (new Value(true, new String[]{"Merge", "merg"})).setParent(this.itemNametags);
      this.chorus = new Value(true, new String[]{"Chorus", "chorusfruit"});
      this.fadeTime = (new NumberValue(2.5F, 1.0F, 5.0F, 0.1F, new String[]{"FadeTime", "fadedelay"})).withTag("second").setParent(this.chorus);
      this.pearlNametags = new Value(false, new String[]{"PearlNametags", "pn", "pearlnametag"});
      this.nametagsColor = (new ColorValue(new Color(-1, false), true, new String[]{"NametagColor", "nametagc"})).setParent(() -> {
         return (Boolean)this.itemNametags.getValue() || (Boolean)this.chorus.getValue() || (Boolean)this.pearlNametags.getValue();
      });
      this.chorusFruits = new ArrayList();
      this.ignoredPlayers = new ConcurrentHashMap();
      this.ignoreVec = null;
      this.offerValues(new Value[]{this.mode, this.items, this.xpBottles, this.enderPearls, this.players, this.self, this.lineWidth, this.outlineAlpha, this.fillAlpha, this.color, this.itemNametags, this.stack, this.merge, this.chorus, this.fadeTime, this.pearlNametags, this.nametagsColor});
      this.offerListeners(new Listener[]{new ListenerRender(this), new ListenerSound(this), new ListenerInterp(this), new ListenerPosLook(this)});
   }

   protected void onEnable() {
      this.chorusFruits.clear();
      this.ignoredPlayers.clear();
      this.ignoreVec = null;
   }
}
