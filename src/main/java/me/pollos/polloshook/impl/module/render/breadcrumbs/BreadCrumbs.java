package me.pollos.polloshook.impl.module.render.breadcrumbs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;
import me.pollos.polloshook.impl.module.render.breadcrumbs.util.TimedVec3d;
import me.pollos.polloshook.impl.module.render.breadcrumbs.util.TrackedVertex;

public class BreadCrumbs extends ToggleableModule {
   protected final Value<Boolean> self = new Value(false, new String[]{"Self", "s"});
   protected final Value<Boolean> fade;
   protected final NumberValue<Float> alphaFactor;
   protected final Value<Boolean> pearls;
   protected final Value<Boolean> arrows;
   protected final Value<Boolean> bottles;
   protected final Value<Boolean> others;
   protected final NumberValue<Float> timeout;
   protected final NumberValue<Float> lineWidth;
   protected final ColorValue color;
   protected final ConcurrentHashMap<Integer, TrackedVertex> thrownEntities;
   protected final List<TimedVec3d> selfTracked;

   public BreadCrumbs() {
      super(new String[]{"BreadCrumbs", "BreadMan", "Breads", "trails", "pearltrajectory"}, Category.RENDER);
      this.fade = (new Value(false, new String[]{"Fade", "f", "fadeout"})).setParent(this.self);
      this.alphaFactor = (new NumberValue(1.5F, 1.0F, 5.0F, 0.1F, new String[]{"AlphaFactor", "alpha"})).setParent(this.fade);
      this.pearls = new Value(true, new String[]{"Pearls", "p", "pearlies"});
      this.arrows = new Value(false, new String[]{"Arrows", "keyCodec", "bullets"});
      this.bottles = new Value(false, new String[]{"Bottles", "beer", "exp", "xp"});
      this.others = new Value(false, new String[]{"Others", "oter"});
      this.timeout = (new NumberValue(2.5F, 0.1F, 5.0F, 0.1F, new String[]{"Timeout"})).withTag("second").setParent((Parent)this.getAnyEnabledParent());
      this.lineWidth = (new NumberValue(1.0F, 1.0F, 4.0F, 0.1F, new String[]{"LineWidth", "wirewidth"})).setParent((Parent)this.getAnyEnabledParent());
      this.color = (new ColorValue(new Color(-1), true, new String[]{"Color", "Color"})).setParent((Parent)this.getAnyEnabledParent());
      this.thrownEntities = new ConcurrentHashMap();
      this.selfTracked = new ArrayList();
      this.offerValues(new Value[]{this.self, this.fade, this.alphaFactor, this.pearls, this.arrows, this.bottles, this.others, this.timeout, this.lineWidth, this.color});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerRender(this)});
   }

   protected SupplierParent getAnyEnabledParent() {
      return new SupplierParent(() -> {
         return (Boolean)this.self.getValue() || (Boolean)this.pearls.getValue() || (Boolean)this.arrows.getValue() || (Boolean)this.bottles.getValue() || (Boolean)this.others.getValue();
      }, false);
   }

   @Override
   protected String getTag() {
      return this.thrownEntities.isEmpty() ? null : String.valueOf(this.thrownEntities.size());
   }


   protected void onToggle() {
      this.thrownEntities.clear();
      this.selfTracked.clear();
   }

   public void onWorldLoad() {
      this.thrownEntities.clear();
      this.selfTracked.clear();
   }
}
