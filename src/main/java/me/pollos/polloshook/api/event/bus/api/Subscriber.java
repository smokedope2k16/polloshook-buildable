package me.pollos.polloshook.api.event.bus.api;

import java.util.Collection;
import java.util.List;

import me.pollos.polloshook.api.event.bus.Listener;

public interface Subscriber {
   List<Listener> getListeners();
}
