package me.pollos.polloshook.api.util.math;

public class StopWatch implements Passable<StopWatch> {
    private volatile long time;

    public boolean passed(double ms) {
        return (double)(System.currentTimeMillis() - this.time) >= ms;
    }

    public boolean passed(long ms) {
        return this.passed((double)ms);
    }

    public StopWatch reset() {
        this.time = System.currentTimeMillis();
        return this;
    }

    public boolean sleep(double time) {
        if ((double)this.getTime() >= time) {
            this.reset();
            return true;
        } else {
            return false;
        }
    }

    public boolean sleep(long delay) {
        return this.sleep((double)delay);
    }

    public long getTime() {
        return System.currentTimeMillis() - this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}