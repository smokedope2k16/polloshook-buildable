package me.pollos.polloshook.api.util.binds.keyboard.impl;

public abstract class Keybind {
    private final int key;

    public abstract void onKeyPress();

    public static Keybind noKeyBind() {
        return new Keybind(-1) {
            public void onKeyPress() {
            }
        };
    }

    public Keybind(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Keybind)) {
            return false;
        } else {
            Keybind other = (Keybind)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                return this.getKey() == other.getKey();
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof Keybind;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getKey();
        return result;
    }

    public String toString() {
        return "Keybind(key=" + this.getKey() + ")";
    }
}