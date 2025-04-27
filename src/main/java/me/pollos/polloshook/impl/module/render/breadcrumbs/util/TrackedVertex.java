package me.pollos.polloshook.impl.module.render.breadcrumbs.util;

import java.util.ArrayList;

public record TrackedVertex(ArrayList<TraceVectors> vertices, boolean friend) {
   public TrackedVertex(ArrayList<TraceVectors> vertices, boolean friend) {
      this.vertices = vertices;
      this.friend = friend;
   }

   public ArrayList<TraceVectors> vertices() {
      return this.vertices;
   }

   public boolean friend() {
      return this.friend;
   }
}
