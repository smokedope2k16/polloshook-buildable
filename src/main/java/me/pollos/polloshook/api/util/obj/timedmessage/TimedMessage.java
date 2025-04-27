package me.pollos.polloshook.api.util.obj.timedmessage;

public class TimedMessage {
    private final long delay;
    private final String message;

    private TimedMessage(long delay, String message) {
        this.delay = delay;
        this.message = message;
    }

    public static TimedMessage of(long delay, String message) {
        return new TimedMessage(delay, message);
    }

    public long getDelay() {
        return this.delay;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TimedMessage)) {
            return false;
        }
        TimedMessage other = (TimedMessage) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.delay != other.delay) {
            return false;
        }
        String thisMessage = this.message;
        String otherMessage = other.message;
        if (thisMessage == null) {
            if (otherMessage != null) {
                return false;
            }
        } else if (!thisMessage.equals(otherMessage)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(Object other) {
        return other instanceof TimedMessage;
    }

    public int hashCode() {
        int result = 1;
        long delay = this.delay;
        result = 59 * result + (int) (delay >>> 32 ^ delay);
        String message = this.message;
        result = 59 * result + (message == null ? 43 : message.hashCode());
        return result;
    }

    public String toString() {
        return "TimedMessage(delay=" + this.delay + 
               ", message=" + this.message + ")";
    }
}