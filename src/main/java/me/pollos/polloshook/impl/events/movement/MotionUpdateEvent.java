package me.pollos.polloshook.impl.events.movement;


import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.events.StageEvent;

public class MotionUpdateEvent extends StageEvent {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;
   private boolean onGround;
   protected boolean modified;
   private final double initialX;
   private final double initialY;
   private final double initialZ;
   private final float initialYaw;
   private final float initialPitch;
   private final boolean initialOnGround;

   public MotionUpdateEvent(Stage stage, MotionUpdateEvent event) {
      this(stage, event.x, event.y, event.z, event.yaw, event.pitch, event.onGround);
   }

   public MotionUpdateEvent(Stage stage, double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround) {
      super(stage);
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = rotationYaw;
      this.pitch = rotationPitch;
      this.onGround = onGround;
      this.initialX = x;
      this.initialY = y;
      this.initialZ = z;
      this.initialYaw = rotationYaw;
      this.initialPitch = rotationPitch;
      this.initialOnGround = onGround;
   }

   public void setX(double x) {
      this.modified = true;
      this.x = x;
   }

   public void setY(double y) {
      this.modified = true;
      this.y = y;
   }

   public void setZ(double z) {
      this.modified = true;
      this.z = z;
   }

   public void setYaw(float rotationYaw) {
      this.modified = true;
      this.yaw = rotationYaw;
   }

   public void setPitch(float rotationPitch) {
      this.modified = true;
      this.pitch = rotationPitch;
   }

   public void setOnGround(boolean onGround) {
      this.modified = true;
      this.onGround = onGround;
   }

   
   public double getX() {
      return this.x;
   }

   
   public double getY() {
      return this.y;
   }

   
   public double getZ() {
      return this.z;
   }

   
   public float getYaw() {
      return this.yaw;
   }

   
   public float getPitch() {
      return this.pitch;
   }

   
   public boolean isOnGround() {
      return this.onGround;
   }

   
   public boolean isModified() {
      return this.modified;
   }

   
   public double getInitialX() {
      return this.initialX;
   }

   
   public double getInitialY() {
      return this.initialY;
   }

   
   public double getInitialZ() {
      return this.initialZ;
   }

   
   public float getInitialYaw() {
      return this.initialYaw;
   }

   
   public float getInitialPitch() {
      return this.initialPitch;
   }

   
   public boolean isInitialOnGround() {
      return this.initialOnGround;
   }
}
