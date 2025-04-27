package me.pollos.polloshook.impl.module.movement.longjump.type;

import java.util.Iterator;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;

public class CowabungaLongJump extends LongJumpType {
   protected int airTicks = 0;
   protected int groundTicks = 0;
   protected double motionY = -13253.0D;

   public void move(MoveEvent event) {
      if (this.motionY == -13253.0D) {
         this.motionY = mc.player.getVelocity().y;
      }

      if (MovementUtil.anyMovementKeys()) {
         float direction = mc.player.getYaw() + (float)(mc.player.forwardSpeed < 0.0F ? 180 : 0) + (mc.player.sidewaysSpeed > 0.0F ? -90.0F * (mc.player.forwardSpeed < 0.0F ? -0.5F : (mc.player.forwardSpeed > 0.0F ? 0.5F : 1.0F)) : 0.0F) - (mc.player.sidewaysSpeed < 0.0F ? -90.0F * (mc.player.forwardSpeed < 0.0F ? -0.5F : (mc.player.forwardSpeed > 0.0F ? 0.5F : 1.0F)) : 0.0F);
         float x = MathHelper.cos((direction + 90.0F) * 3.1415927F / 180.0F);
         float z = MathHelper.sin((direction + 90.0F) * 3.1415927F / 180.0F);
         if (!mc.player.verticalCollision) {
            ++this.airTicks;
            if (mc.player.input.sneaking) {
               this.sendPos(2.14748365E9F);
            }

            this.groundTicks = 0;
            if (!mc.player.verticalCollision) {
               if (this.motionY == -0.07190068807140403D) {
                  this.motionY *= 0.3499999940395355D;
               }

               if (this.motionY == -0.10306193759436909D) {
                  this.motionY *= 0.550000011920929D;
               }

               if (this.motionY == -0.13395038817442878D) {
                  this.motionY *= 0.6700000166893005D;
               }

               if (this.motionY == -0.16635183030382D) {
                  this.motionY *= 0.6899999976158142D;
               }

               if (this.motionY == -0.19088711097794803D) {
                  this.motionY *= 0.7099999785423279D;
               }

               if (this.motionY == -0.21121925191528862D) {
                  this.motionY *= 0.20000000298023224D;
               }

               if (this.motionY == -0.11979897632390576D) {
                  this.motionY *= 0.9300000071525574D;
               }

               if (this.motionY == -0.18758479151225355D) {
                  this.motionY *= 0.7200000286102295D;
               }

               if (this.motionY == -0.21075983825251726D) {
                  this.motionY *= 0.7599999904632568D;
               }

               if (this.getDistance(mc.player) < 0.5D) {
                  if (this.motionY == -0.23537393014173347D) {
                     this.motionY *= 0.029999999329447746D;
                  }

                  if (this.motionY == -0.08531999505205401D) {
                     this.motionY *= -0.5D;
                  }

                  if (this.motionY == -0.03659320313669756D) {
                     this.motionY *= -0.10000000149011612D;
                  }

                  if (this.motionY == -0.07481386749524899D) {
                     this.motionY *= -0.07000000029802322D;
                  }

                  if (this.motionY == -0.0732677700939672D) {
                     this.motionY *= -0.05000000074505806D;
                  }

                  if (this.motionY == -0.07480988066790395D) {
                     this.motionY *= -0.03999999910593033D;
                  }

                  if (this.motionY == -0.0784000015258789D) {
                     this.motionY *= 0.10000000149011612D;
                  }

                  if (this.motionY == -0.08608320193943977D) {
                     this.motionY *= 0.10000000149011612D;
                  }

                  if (this.motionY == -0.08683615560584318D) {
                     this.motionY *= 0.05000000074505806D;
                  }

                  if (this.motionY == -0.08265497329678266D) {
                     this.motionY *= 0.05000000074505806D;
                  }

                  if (this.motionY == -0.08245009535659828D) {
                     this.motionY *= 0.05000000074505806D;
                  }

                  if (this.motionY == -0.08244005633718426D) {
                     this.motionY = -0.08243956442521608D;
                  }

                  if (this.motionY == -0.08243956442521608D) {
                     this.motionY = -0.08244005590677261D;
                  }

                  if (this.motionY > -0.1D && this.motionY < -0.08D && !mc.player.isOnGround() && mc.player.input.sneaking) {
                     this.motionY = -9.999999747378752E-5D;
                  }
               } else {
                  if (this.motionY < -0.2D && this.motionY > -0.24D) {
                     this.motionY *= 0.7D;
                  }

                  if (this.motionY < -0.25D && this.motionY > -0.32D) {
                     this.motionY *= 0.8D;
                  }

                  if (this.motionY < -0.35D && this.motionY > -0.8D) {
                     this.motionY *= 0.98D;
                  }

                  if (this.motionY < -0.8D && this.motionY > -1.6D) {
                     this.motionY *= 0.99D;
                  }
               }
            }

            Managers.getTimerManager().set(0.85F);
            if (mc.player.input.pressingForward) {
               try {
                  double[] yo = new double[]{0.420606D, 0.417924D, 0.415258D, 0.412609D, 0.409977D, 0.407361D, 0.404761D, 0.402178D, 0.399611D, 0.39706D, 0.394525D, 0.392D, 0.3894D, 0.38644D, 0.383655D, 0.381105D, 0.37867D, 0.37625D, 0.37384D, 0.37145D, 0.369D, 0.3666D, 0.3642D, 0.3618D, 0.35945D, 0.357D, 0.354D, 0.351D, 0.348D, 0.345D, 0.342D, 0.339D, 0.336D, 0.333D, 0.33D, 0.327D, 0.324D, 0.321D, 0.318D, 0.315D, 0.312D, 0.309D, 0.307D, 0.305D, 0.303D, 0.3D, 0.297D, 0.295D, 0.293D, 0.291D, 0.289D, 0.287D, 0.285D, 0.283D, 0.281D, 0.279D, 0.277D, 0.275D, 0.273D, 0.271D, 0.269D, 0.267D, 0.265D, 0.263D, 0.261D, 0.259D, 0.257D, 0.255D, 0.253D, 0.251D, 0.249D, 0.247D, 0.245D, 0.243D, 0.241D, 0.239D, 0.237D};
                  MovementUtil.setXZVelocity((double)x * yo[this.airTicks - 1] * 3.0D, (double)z * yo[this.airTicks - 1] * 3.0D, mc.player);
               } catch (Exception var9) {
               }
            } else {
               MovementUtil.setXZVelocity(0.0D, 0.0D, mc.player);
            }
         } else {
            Managers.getTimerManager().reset();
            this.airTicks = 0;
            ++this.groundTicks;
            double motionX = mc.player.getVelocity().x;
            double motionZ = mc.player.getVelocity().x;
            motionX /= 13.0D;
            motionZ /= 13.0D;
            MovementUtil.setXZVelocity(motionX, motionZ, mc.player);
            if (this.groundTicks == 1) {
               this.sendPos(0.0F);
               this.sendPos(0.0624F);
               this.sendPos(0.419F);
               this.sendPos(0.0624F);
               this.sendPos(0.419F);
            }

            if (this.groundTicks > 2) {
               this.groundTicks = 0;
               this.motionY = 0.42399999499320984D;
               mc.player.setVelocity((double)x * 0.3D, this.motionY, (double)z * 0.3D);
            }

            MovementUtil.setYVelocity(this.motionY, mc.player);
         }
      }
   }

   public void reset() {
      super.reset();
      this.groundTicks = 0;
      this.airTicks = 0;
   }

   protected double getDistance(PlayerEntity player) {
      Iterable<VoxelShape> boundingBoxes = mc.world.getCollisions(player, player.getBoundingBox().offset(0.0D, -69.0D, 0.0D));
      if (boundingBoxes.spliterator().getExactSizeIfKnown() > 0L) {
         return 0.0D;
      } else {
         double y = 0.0D;
         Iterator var5 = boundingBoxes.iterator();

         while(var5.hasNext()) {
            VoxelShape boundingBox = (VoxelShape)var5.next();
            if (boundingBox.getMax(Axis.Y) > y) {
               y = boundingBox.getMax(Axis.Y);
            }
         }

         return player.getY() - y;
      }
   }

   private void sendPos(float y) {
      if (!(y >= 2.14748365E9F) || !mc.isInSingleplayer()) {
         PacketUtil.move(mc.player.getX(), mc.player.getY() + (double)y, mc.player.getZ(), mc.player.isOnGround());
      }
   }
}
