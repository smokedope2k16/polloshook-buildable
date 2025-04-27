package me.pollos.polloshook.api.minecraft.render.shader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform1f;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform1i;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform2f;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform2i;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform3f;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform3i;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform4f;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.Uniform4i;
import me.pollos.polloshook.api.minecraft.render.shader.uniform.UniformMat4;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class ManagedUniform extends ManagedUniformBase implements Uniform1i, Uniform2i, Uniform3i, Uniform4i, Uniform1f, Uniform2f, Uniform3f, Uniform4f, UniformMat4 {
   private static final GlUniform[] NO_TARGETS = new GlUniform[0];
   private final int count;
   private GlUniform[] targets;
   private int i0;
   private int i1;
   private int i2;
   private int i3;
   private float f0;
   private float f1;
   private float f2;
   private float f3;
   private boolean firstUpload;

   public ManagedUniform(String name, int count) {
      super(name);
      this.targets = NO_TARGETS;
      this.firstUpload = true;
      this.count = count;
   }

   public boolean findUniformTargets(List<PostEffectPass> shaders) {
      List<GlUniform> list = new ArrayList();
      Iterator var3 = shaders.iterator();

      while(var3.hasNext()) {
         PostEffectPass shader = (PostEffectPass)var3.next();
         GlUniform uniform = shader.getProgram().getUniformByName(this.name);
         if (uniform != null) {
            if (uniform.getCount() != this.count) {
               int var10002 = this.count;
               throw new IllegalStateException("Mismatched number of values, expected " + var10002 + " but JSON definition declares " + uniform.getCount());
            }

            list.add(uniform);
         }
      }

      if (list.size() > 0) {
         this.targets = (GlUniform[])list.toArray(new GlUniform[0]);
         this.syncCurrentValues();
         return true;
      } else {
         this.targets = NO_TARGETS;
         return false;
      }
   }

   public boolean findUniformTarget(ShaderProgram shader) {
      GlUniform uniform = shader.getUniform(this.name);
      if (uniform != null) {
         this.targets = new GlUniform[]{uniform};
         this.syncCurrentValues();
         return true;
      } else {
         this.targets = NO_TARGETS;
         return false;
      }
   }

   private void syncCurrentValues() {
      if (!this.firstUpload) {
         GlUniform[] var1 = this.targets;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            GlUniform target = var1[var3];
            if (target.getIntData() != null) {
               target.setForDataType(this.i0, this.i1, this.i2, this.i3);
            } else {
               assert target.getFloatData() != null;

               target.setForDataType(this.f0, this.f1, this.f2, this.f3);
            }
         }
      }

   }

   public void set(int value) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.i0 != value)) {
         GlUniform[] var4 = targets;
         int var5 = targets.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            GlUniform target = var4[var6];
            target.set(value);
         }

         this.i0 = value;
         this.firstUpload = false;
      }

   }

   public void set(int value0, int value1) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.i0 != value0 || this.i1 != value1)) {
         GlUniform[] var5 = targets;
         int var6 = targets.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            GlUniform target = var5[var7];
            target.set(value0, value1);
         }

         this.i0 = value0;
         this.i1 = value1;
         this.firstUpload = false;
      }

   }

   public void set(int value0, int value1, int value2) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.i0 != value0 || this.i1 != value1 || this.i2 != value2)) {
         GlUniform[] var6 = targets;
         int var7 = targets.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            GlUniform target = var6[var8];
            target.set(value0, value1, value2);
         }

         this.i0 = value0;
         this.i1 = value1;
         this.i2 = value2;
         this.firstUpload = false;
      }

   }

   public void set(int value0, int value1, int value2, int value3) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.i0 != value0 || this.i1 != value1 || this.i2 != value2 || this.i3 != value3)) {
         GlUniform[] var7 = targets;
         int var8 = targets.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            GlUniform target = var7[var9];
            target.set(value0, value1, value2, value3);
         }

         this.i0 = value0;
         this.i1 = value1;
         this.i2 = value2;
         this.i3 = value3;
         this.firstUpload = false;
      }

   }

   public void set(float value) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.f0 != value)) {
         GlUniform[] var4 = targets;
         int var5 = targets.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            GlUniform target = var4[var6];
            target.set(value);
         }

         this.f0 = value;
         this.firstUpload = false;
      }

   }

   public void set(float value0, float value1) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.f0 != value0 || this.f1 != value1)) {
         GlUniform[] var5 = targets;
         int var6 = targets.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            GlUniform target = var5[var7];
            target.set(value0, value1);
         }

         this.f0 = value0;
         this.f1 = value1;
         this.firstUpload = false;
      }

   }

   public void set(Vector2f value) {
      this.set(value.x(), value.y());
   }

   public void set(float value0, float value1, float value2) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.f0 != value0 || this.f1 != value1 || this.f2 != value2)) {
         GlUniform[] var6 = targets;
         int var7 = targets.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            GlUniform target = var6[var8];
            target.set(value0, value1, value2);
         }

         this.f0 = value0;
         this.f1 = value1;
         this.f2 = value2;
         this.firstUpload = false;
      }

   }

   public void set(Vector3f value) {
      this.set(value.x(), value.y(), value.z());
   }

   public void set(float value0, float value1, float value2, float value3) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0 && (this.firstUpload || this.f0 != value0 || this.f1 != value1 || this.f2 != value2 || this.f3 != value3)) {
         GlUniform[] var7 = targets;
         int var8 = targets.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            GlUniform target = var7[var9];
            target.set(value0, value1, value2, value3);
         }

         this.f0 = value0;
         this.f1 = value1;
         this.f2 = value2;
         this.f3 = value3;
         this.firstUpload = false;
      }

   }

   public void set(Vector4f value) {
      this.set(value.x(), value.y(), value.z(), value.w());
   }

   public void set(Matrix4f value) {
      GlUniform[] targets = this.targets;
      int nbTargets = targets.length;
      if (nbTargets > 0) {
         GlUniform[] var4 = targets;
         int var5 = targets.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            GlUniform target = var4[var6];
            target.set(value);
         }
      }

   }

   public void setFromArray(float[] values) {
      if (this.count != values.length) {
         throw new IllegalArgumentException("Mismatched values size, expected " + this.count + " but got " + values.length);
      } else {
         GlUniform[] targets = this.targets;
         int nbTargets = targets.length;
         if (nbTargets > 0) {
            GlUniform[] var4 = targets;
            int var5 = targets.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               GlUniform target = var4[var6];
               target.set(values);
            }
         }

      }
   }
}
