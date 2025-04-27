package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.misc.nameprotect.NameProtect;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin({TextVisitFactory.class})
public abstract class MixinTextVisitFactory implements Minecraftable {
   private static boolean visitFormatted(String text, int startIndex, Style startingStyle, Style resetStyle, CharacterVisitor visitor) {
      if (Managers.getModuleManager().get(NameProtect.class) != null) {
         text = NameProtect.nameProtect(text);
      }

      int i = text.length();
      Style style = startingStyle;

      for(int j = startIndex; j < i; ++j) {
         char c = text.charAt(j);
         char d;
         if (c == 167) {
            if (j + 1 >= i) {
               break;
            }

            d = text.charAt(j + 1);
            Formatting formatting = Formatting.byCode(d);
            if (formatting != null) {
               style = formatting == Formatting.RESET ? resetStyle : style.withExclusiveFormatting(formatting);
            }

            ++j;
         } else if (Character.isHighSurrogate(c)) {
            if (j + 1 >= i) {
               if (!visitor.accept(j, style, 65533)) {
                  return false;
               }
               break;
            }

            d = text.charAt(j + 1);
            if (Character.isLowSurrogate(d)) {
               if (!visitor.accept(j, style, Character.toCodePoint(c, d))) {
                  return false;
               }

               ++j;
            } else if (!visitor.accept(j, style, 65533)) {
               return false;
            }
         } else if (!visitRegularCharacter(style, visitor, j, c)) {
            return false;
         }
      }

      return true;
   }

   @Unique
   private static boolean visitRegularCharacter(Style style, CharacterVisitor visitor, int index, char c) {
      return Character.isSurrogate(c) ? visitor.accept(index, style, 65533) : visitor.accept(index, style, c);
   }
}
