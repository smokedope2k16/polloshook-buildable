package me.pollos.polloshook.api.util.obj.rectangle;

public class Rectangle {
    private float x;
    private float y;
    private float width;
    private float height;

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle copy() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Rectangle setX(float x) {
        this.x = x;
        return this;
    }

    public Rectangle setY(float y) {
        this.y = y;
        return this;
    }

    public Rectangle setWidth(float width) {
        this.width = width;
        return this;
    }

    public Rectangle setHeight(float height) {
        this.height = height;
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Rectangle)) {
            return false;
        }
        Rectangle other = (Rectangle) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.x, other.x) != 0) {
            return false;
        }
        if (Float.compare(this.y, other.y) != 0) {
            return false;
        }
        if (Float.compare(this.width, other.width) != 0) {
            return false;
        }
        return Float.compare(this.height, other.height) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Rectangle;
    }

    public int hashCode() {
        int result = 1;
        result = 59 * result + Float.floatToIntBits(this.x);
        result = 59 * result + Float.floatToIntBits(this.y);
        result = 59 * result + Float.floatToIntBits(this.width);
        result = 59 * result + Float.floatToIntBits(this.height);
        return result;
    }

    public String toString() {
        return "Rectangle(x=" + this.x + 
               ", y=" + this.y + 
               ", width=" + this.width + 
               ", height=" + this.height + ")";
    }
}