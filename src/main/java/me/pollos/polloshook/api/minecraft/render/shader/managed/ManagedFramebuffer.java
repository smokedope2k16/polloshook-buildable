package me.pollos.polloshook.api.minecraft.render.shader.managed;

import net.minecraft.client.gl.Framebuffer;

public interface ManagedFramebuffer {
   Framebuffer getFramebuffer();

   void beginWrite(boolean var1);

   void draw();

   void draw(int var1, int var2, boolean var3);

   void clear();

   void clear(boolean var1);
}
