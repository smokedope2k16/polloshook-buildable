package me.pollos.polloshook.api.util.font;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

class GlyphMap implements Minecraftable {
   private static final int PADDING = 5;
   final char fromIncl;
   final char toExcl;
   final Font[] font;
   final Identifier bindToTexture;
   private final Char2ObjectArrayMap<Glyph> glyphs = new Char2ObjectArrayMap();
   int width;
   int height;
   boolean generated = false;

   public GlyphMap(char from, char to, Font[] fonts, Identifier identifier) {
      this.fromIncl = from;
      this.toExcl = to;
      this.font = fonts;
      this.bindToTexture = identifier;
   }

   public Glyph getGlyph(char c) {
      if (!this.generated) {
         this.generate();
      }

      return (Glyph)this.glyphs.get(c);
   }

   public void destroy() {
      MinecraftClient.getInstance().getTextureManager().destroyTexture(this.bindToTexture);
      this.glyphs.clear();
      this.width = -1;
      this.height = -1;
      this.generated = false;
   }

   public boolean contains(char c) {
      return c >= this.fromIncl && c < this.toExcl;
   }

   private Font getFontForGlyph(char c) {
      Font[] var2 = this.font;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Font font1 = var2[var4];
         if (font1.canDisplay(c)) {
            return font1;
         }
      }

      return this.font[0];
   }

   public void generate() {
      if (!this.generated) {
         int range = this.toExcl - this.fromIncl - 1;
         int charsVert = (int)(Math.ceil(Math.sqrt((double)range)) * 1.5D);
         this.glyphs.clear();
         int generatedChars = 0;
         int charNX = 0;
         int maxX = 0;
         int maxY = 0;
         int currentX = 0;
         int currentY = 0;
         int currentRowMaxY = 0;
         List<Glyph> glyphs1 = new ArrayList();
         AffineTransform af = new AffineTransform();

         for(FontRenderContext frc = new FontRenderContext(af, true, true); generatedChars <= range; ++charNX) {
            char currentChar = (char)(this.fromIncl + generatedChars);
            Font font = this.getFontForGlyph(currentChar);
            Rectangle2D stringBounds = font.getStringBounds(String.valueOf(currentChar), frc);
            int width = (int)Math.ceil(stringBounds.getWidth());
            int height = (int)Math.ceil(stringBounds.getHeight());
            ++generatedChars;
            maxX = Math.max(maxX, currentX + width);
            maxY = Math.max(maxY, currentY + height);
            if (charNX >= charsVert) {
               currentX = 0;
               currentY += currentRowMaxY + 5;
               charNX = 0;
               currentRowMaxY = 0;
            }

            currentRowMaxY = Math.max(currentRowMaxY, height);
            glyphs1.add(new Glyph(currentX, currentY, width, height, currentChar, this));
            currentX += width + 5;
         }

         BufferedImage bi = new BufferedImage(Math.max(maxX + 5, 1), Math.max(maxY + 5, 1), 2);
         this.width = bi.getWidth();
         this.height = bi.getHeight();
         Graphics2D g2d = bi.createGraphics();
         g2d.setColor(new Color(255, 255, 255, 0));
         g2d.fillRect(0, 0, this.width, this.height);
         g2d.setColor(Color.WHITE);
         g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
         g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         Iterator var20 = glyphs1.iterator();

         while(var20.hasNext()) {
            Glyph glyph = (Glyph)var20.next();
            g2d.setFont(this.getFontForGlyph(glyph.value()));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            g2d.drawString(String.valueOf(glyph.value()), glyph.u(), glyph.v() + fontMetrics.getAscent());
            this.glyphs.put(glyph.value(), glyph);
         }

         registerBufferedImageTexture(this.bindToTexture, bi);
         this.generated = true;
      }
   }

   public static void registerBufferedImageTexture(Identifier i, BufferedImage bi) {
      try {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         ImageIO.write(bi, "png", out);
         byte[] bytes = out.toByteArray();
         ByteBuffer data = BufferUtils.createByteBuffer(bytes.length).put(bytes);
         data.flip();
         NativeImageBackedTexture tex = new NativeImageBackedTexture(NativeImage.read(data));
         mc.execute(() -> {
            mc.getTextureManager().registerTexture(i, tex);
         });
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }
}
