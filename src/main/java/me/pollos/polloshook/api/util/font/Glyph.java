package me.pollos.polloshook.api.util.font;

record Glyph(int u, int v, int width, int height, char value, GlyphMap owner) {
   Glyph(int u, int v, int width, int height, char value, GlyphMap owner) {
      this.u = u;
      this.v = v;
      this.width = width;
      this.height = height;
      this.value = value;
      this.owner = owner;
   }

   public int u() {
      return this.u;
   }

   public int v() {
      return this.v;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public char value() {
      return this.value;
   }

   public GlyphMap owner() {
      return this.owner;
   }
}
