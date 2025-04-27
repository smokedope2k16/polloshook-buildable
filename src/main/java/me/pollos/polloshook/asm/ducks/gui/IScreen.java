package me.pollos.polloshook.asm.ducks.gui;

import java.util.List;
import net.minecraft.client.gui.Drawable;

public interface IScreen {
   List<Drawable> drawables();

   int getXMouse();

   int getYMouse();
}
