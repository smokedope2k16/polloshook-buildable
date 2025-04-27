package me.pollos.polloshook.impl.events.update;

import me.pollos.polloshook.api.event.events.Event;

public class TickEvent extends Event {
   public static final class PostWorldTick extends TickEvent {
   }

   public static final class Post extends TickEvent {
   }
}
