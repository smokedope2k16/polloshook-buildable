package me.pollos.polloshook.api.event.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.pollos.polloshook.api.event.bus.api.Subscriber;

public class SubscriberImpl implements Subscriber {
   protected final List<Listener<?>> listeners = new ArrayList();

   public List getListeners() {
      return this.listeners;
   }
}
