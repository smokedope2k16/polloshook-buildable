package me.pollos.polloshook.api.util.font;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.awt.Font;
import java.io.Closeable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.mixins.util.IBufferBuilder;
import me.pollos.polloshook.impl.module.other.font.CustomFont;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements Closeable, Minecraftable {
   private static final Char2IntArrayMap colorCodes = new Char2IntArrayMap() {
      {
         this.put('0', 0);
         this.put('1', 170);
         this.put('2', 43520);
         this.put('3', 43690);
         this.put('4', 11141120);
         this.put('5', 11141290);
         this.put('6', 16755200);
         this.put('7', 11184810);
         this.put('8', 5592405);
         this.put('9', 5592575);
         this.put('A', 5635925);
         this.put('B', 5636095);
         this.put('C', 16733525);
         this.put('D', 16733695);
         this.put('E', 16777045);
         this.put('F', 16777215);
      }
   };
   private static final int BLOCK_SIZE = 256;
   private static final Object2ObjectArrayMap<Identifier, ObjectList<FontRenderer.DrawEntry>> GLYPH_PAGE_CACHE = new Object2ObjectArrayMap();
   private final float originalSize;
   private final ObjectList<GlyphMap> maps = new ObjectArrayList();
   private final Char2ObjectArrayMap<Glyph> allGlyphs = new Char2ObjectArrayMap();
   private int scaleMul = 0;
   private Font[] fonts;
   private int previousGameScale = -1;
   private static final char RND_START = 'a';
   private static final char RND_END = 'z';
   private static final Random RND = new Random();

   public FontRenderer(@NotNull Font[] fonts, float sizePx) {
      Preconditions.checkArgument(fonts.length > 0, "fonts.length == 0");
      this.originalSize = sizePx;
      this.init(fonts, sizePx);
   }

   private static int floorNearestMulN(int x) {
      return 256 * (int)Math.floor((double)x / 256.0D);
   }

   @NotNull
   public static String stripControlCodes(@NotNull String text) {
      char[] chars = text.toCharArray();
      StringBuilder f = new StringBuilder();

      for(int i = 0; i < chars.length; ++i) {
         char c = chars[i];
         if (c == 167) {
            ++i;
         } else {
            f.append(c);
         }
      }

      return f.toString();
   }

   private void sizeCheck() {
      int gs = getGuiScale();
      if (gs != this.previousGameScale) {
         this.close();
         this.init(this.fonts, this.originalSize);
      }

   }

   private void init(@NotNull Font[] fonts, float sizePx) {
      this.previousGameScale = getGuiScale();
      this.scaleMul = this.previousGameScale;
      this.fonts = new Font[fonts.length];

      for(int i = 0; i < fonts.length; ++i) {
         this.fonts[i] = fonts[i].deriveFont(sizePx * (float)this.scaleMul);
      }

   }

   @NotNull
   private GlyphMap generateMap(char from, char to) {
      GlyphMap gm = new GlyphMap(from, to, this.fonts, randomIdentifier());
      this.maps.add(gm);
      return gm;
   }

   private Glyph locateGlyph0(char glyph) {
      ObjectListIterator var2 = this.maps.iterator();

      GlyphMap map;
      do {
         if (!var2.hasNext()) {
            int base = floorNearestMulN(glyph);
            map = this.generateMap((char)base, (char)(base + 256));
            return map.getGlyph(glyph);
         }

         map = (GlyphMap)var2.next();
      } while(!map.contains(glyph));

      return map.getGlyph(glyph);
   }

   private Glyph locateGlyph1(char glyph) {
      return (Glyph)this.allGlyphs.computeIfAbsent(glyph, this::locateGlyph0);
   }

   public void drawString(@NotNull MatrixStack stack, @NotNull String s, float x, float y, float r, float g, float elementCodec, float keyCodec) {
      float shadowLength = ((CustomFont)Managers.getModuleManager().get(CustomFont.class)).getShadowLength();
      this.drawString(stack, s, x + shadowLength, y + shadowLength, r, g, elementCodec, keyCodec, true);
      this.drawString(stack, s, x, y, r, g, elementCodec, keyCodec, false);
   }

   public void drawString(@NotNull MatrixStack stack, @NotNull String s, float x, float y, float r, float g, float elementCodec, float keyCodec, boolean shadow) {
      this.sizeCheck();
      float brightnessMultiplier = shadow ? 0.25F : 1.0F;
      float red = r * brightnessMultiplier;
      float green = g * brightnessMultiplier;
      float blue = elementCodec * brightnessMultiplier;
      stack.push();
      stack.translate(x, y, 0.0F);
      stack.scale(1.0F / (float)this.scaleMul, 1.0F / (float)this.scaleMul, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      boolean smoothFont = (Boolean)((CustomFont)Managers.getModuleManager().get(CustomFont.class)).getSmoothFont().getValue();
      int gl = smoothFont ? 9728 : 9729;
      GL11.glTexParameteri(3553, 10241, gl);
      GL11.glTexParameteri(3553, 10240, gl);
      RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
      Matrix4f mat = stack.peek().getPositionMatrix();
      char[] chars = s.toCharArray();
      float xOffset = 0.0F;
      float yOffset = 0.0F;
      boolean inSel = false;
      int lineStart = 0;

      Identifier i1;
      for(int i = 0; i < chars.length; ++i) {
         char c = chars[i];
         if (inSel) {
            inSel = false;
            char c1 = Character.toUpperCase(c);
            if (colorCodes.containsKey(c1)) {
               int ii = colorCodes.get(c1);
               int[] col = RGBIntToRGB(ii);
               red = (float)col[0] / 255.0F;
               green = (float)col[1] / 255.0F;
               blue = (float)col[2] / 255.0F;
               red *= brightnessMultiplier;
               green *= brightnessMultiplier;
               blue *= brightnessMultiplier;
            } else if (c1 == 'R') {
               red = r * brightnessMultiplier;
               green = g * brightnessMultiplier;
               blue = elementCodec * brightnessMultiplier;
            }
         } else if (c == 167) {
            inSel = true;
         } else if (c == '\n') {
            yOffset += this.getStringHeight(s.substring(lineStart, i)) * (float)this.scaleMul;
            xOffset = 0.0F;
            lineStart = i + 1;
         } else {
            Glyph glyph = this.locateGlyph1(c);
            if (glyph != null) {
               if (glyph.value() != ' ') {
                  i1 = glyph.owner().bindToTexture;
                  FontRenderer.DrawEntry entry = new FontRenderer.DrawEntry(xOffset, yOffset, red, green, blue, glyph);
                  ((ObjectList)GLYPH_PAGE_CACHE.computeIfAbsent(i1, (k) -> {
                     return new ObjectArrayList();
                  })).add(entry);
               }

               xOffset += (float)glyph.width();
            }
         }
      }

      BufferBuilder bb = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      if (!((IBufferBuilder)bb).isBuilding()) {
         RenderSystem.disableBlend();
         stack.pop();
         GLYPH_PAGE_CACHE.clear();
      } else {
         int i = 0;
         ObjectIterator var45 = GLYPH_PAGE_CACHE.keySet().iterator();

         while(var45.hasNext()) {
            i1 = (Identifier)var45.next();
            RenderSystem.setShaderTexture(0, i1);
            List<FontRenderer.DrawEntry> objects = (List)GLYPH_PAGE_CACHE.get(i1);

            for(Iterator var27 = objects.iterator(); var27.hasNext(); ++i) {
               FontRenderer.DrawEntry object = (FontRenderer.DrawEntry)var27.next();
               float xo = object.atX;
               float yo = object.atY;
               float cr = object.r;
               float cg = object.g;
               float cb = object.elementCodec;
               Glyph glyph = object.toDraw;
               GlyphMap owner = glyph.owner();
               float w = (float)glyph.width();
               float h = (float)glyph.height();
               float u1 = (float)glyph.u() / (float)owner.width;
               float v1 = (float)glyph.v() / (float)owner.height;
               float u2 = (float)(glyph.u() + glyph.width()) / (float)owner.width;
               float v2 = (float)(glyph.v() + glyph.height()) / (float)owner.height;
               bb.vertex(mat, xo, yo + h, 0.0F).texture(u1, v2).color(cr, cg, cb, keyCodec);
               bb.vertex(mat, xo + w, yo + h, 0.0F).texture(u2, v2).color(cr, cg, cb, keyCodec);
               bb.vertex(mat, xo + w, yo, 0.0F).texture(u2, v1).color(cr, cg, cb, keyCodec);
               bb.vertex(mat, xo, yo, 0.0F).texture(u1, v1).color(cr, cg, cb, keyCodec);
            }
         }

         if (i > 0) {
            BufferRenderer.drawWithGlobalProgram(bb.end());
         }

         RenderSystem.disableBlend();
         stack.pop();
         GLYPH_PAGE_CACHE.clear();
      }
   }

   public float getStringWidth(String text) {
      char[] c = stripControlCodes(text).toCharArray();
      float currentLine = 0.0F;
      float maxPreviousLines = 0.0F;
      char[] var5 = c;
      int var6 = c.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         char c1 = var5[var7];
         if (c1 == '\n') {
            maxPreviousLines = Math.max(currentLine, maxPreviousLines);
            currentLine = 0.0F;
         } else {
            Glyph glyph = this.locateGlyph1(c1);
            float gWidth = glyph == null ? 1.0F : (float)glyph.width();
            currentLine += gWidth / (float)this.scaleMul;
         }
      }

      return Math.max(currentLine, maxPreviousLines);
   }

   public float getStringHeight(String text) {
      char[] c = stripControlCodes(text).toCharArray();
      if (c.length == 0) {
         c = new char[]{' '};
      }

      float currentLine = 0.0F;
      float previous = 0.0F;
      char[] var5 = c;
      int var6 = c.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         char c1 = var5[var7];
         if (c1 == '\n') {
            if (currentLine == 0.0F) {
               currentLine = (float)this.locateGlyph1(' ').height() / (float)this.scaleMul;
            }

            previous += currentLine;
            currentLine = 0.0F;
         } else {
            Glyph glyph = this.locateGlyph1(c1);
            if (glyph == null) {
               currentLine = Math.max(8.0F / (float)this.scaleMul, currentLine);
            } else {
               currentLine = Math.max((float)glyph.height() / (float)this.scaleMul, currentLine);
            }
         }
      }

      return currentLine + previous;
   }

   public void close() {
      ObjectListIterator var1 = this.maps.iterator();

      while(var1.hasNext()) {
         GlyphMap map = (GlyphMap)var1.next();
         map.destroy();
      }

      this.maps.clear();
      this.allGlyphs.clear();
   }

   public static int getGuiScale() {
      return (int)mc.getWindow().getScaleFactor();
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   @NotNull
   public static int[] RGBIntToRGB(int in) {
      int red = in >> 16 & 255;
      int green = in >> 8 & 255;
      int blue = in & 255;
      return new int[]{red, green, blue};
   }

   @Contract(
      value = "-> new",
      pure = true
   )
   @NotNull
   public static Identifier randomIdentifier() {
      return Identifier.of("polloshook", "temp/" + randomString(32));
   }

   private static String randomString(int length) {
      return (String)IntStream.range(0, length).mapToObj((operand) -> {
         return String.valueOf((char)RND.nextInt(97, 123)); 
      }).collect(Collectors.joining());
   }

   static record DrawEntry(float atX, float atY, float r, float g, float elementCodec, Glyph toDraw) {
      DrawEntry(float atX, float atY, float r, float g, float elementCodec, Glyph toDraw) {
         this.atX = atX;
         this.atY = atY;
         this.r = r;
         this.g = g;
         this.elementCodec = elementCodec;
         this.toDraw = toDraw;
      }

      public float atX() {
         return this.atX;
      }

      public float atY() {
         return this.atY;
      }

      public float r() {
         return this.r;
      }

      public float g() {
         return this.g;
      }

      public float elementCodec() {
         return this.elementCodec;
      }

      public Glyph toDraw() {
         return this.toDraw;
      }
   }
}