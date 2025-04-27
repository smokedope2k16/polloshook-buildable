package me.pollos.polloshook.impl.module.misc.deathcoordslog;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.misc.deathcoordslog.util.DeathWaypoint;

public class DeathCoordsLog extends ToggleableModule {
   protected final Value<Boolean> waypoint = new Value(false, new String[]{"Waypoint", "point"});
   protected final NumberValue<Float> timeout;
   protected final List<DeathWaypoint> waypointList;

   public DeathCoordsLog() {
      super(new String[]{"DeathCoordsLog", "deathcoordslogger", "deathpos"}, Category.MISC);
      this.timeout = (new NumberValue(3.0F, 0.0F, 5.0F, 0.5F, new String[]{"Timeout", "time"})).setParent(this.waypoint).withTag("second");
      this.waypointList = new ArrayList();
      this.offerValues(new Value[]{this.waypoint, this.timeout});
      this.offerListeners(new Listener[]{new ListenerScreen(this), new ListenerDisconnect(this), new ListenerRender(this)});
   }
}
