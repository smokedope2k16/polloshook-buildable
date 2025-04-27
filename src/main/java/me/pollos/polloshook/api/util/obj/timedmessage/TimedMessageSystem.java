package me.pollos.polloshook.api.util.obj.timedmessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.obj.MessageSender;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.impl.events.update.UpdateEvent;

public class TimedMessageSystem extends SubscriberImpl implements Labeled {
    private final Module module;
    private final List<TimedMessage> timedMessages = new ArrayList<>();
    private NumberValue<Float> value;
    private final String label;

    private TimedMessageSystem(Module module, String label) {
        this.module = module;
        this.label = label;
    }

    public static TimedMessageSystem create(Module module, String label) {
        return new TimedMessageSystem(module, label);
    }

    public TimedMessageSystem subscribe() {
        PollosHook.getEventBus().subscribe(this);
        PollosHook.getEventBus().register(new Listener<UpdateEvent>(UpdateEvent.class) {
            public void call(UpdateEvent event) {
                if (!PlayerUtil.isNull() && 
                    (!(TimedMessageSystem.this.module instanceof ToggleableModule) || 
                     ((ToggleableModule)TimedMessageSystem.this.module).isEnabled())) {
                    
                    List<TimedMessage> toRemove = new ArrayList<>();
                    TimedMessageSystem.this.timedMessages.forEach(message -> {
                        if (System.currentTimeMillis() >= message.getDelay()) {
                            MessageSender sender = new MessageSender(message.getMessage());
                            sender.send();
                            toRemove.add(message);
                        }
                    });
                    TimedMessageSystem.this.timedMessages.removeAll(toRemove);
                }
            }
        });
        return this;
    }

    public void submit(String message) {
        long delayMillis = (long)(this.value.getValue() * 1000L);
        this.timedMessages.add(TimedMessage.of(System.currentTimeMillis() + delayMillis, message));
    }

    protected Module getModule() {
        return this.module;
    }

    protected List<TimedMessage> getTimedMessages() {
        return this.timedMessages;
    }

    protected NumberValue<Float> getValue() {
        return this.value;
    }

    public void setValue(NumberValue<Float> value) {
        this.value = value;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TimedMessageSystem)) {
            return false;
        }
        TimedMessageSystem other = (TimedMessageSystem) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        
        Module thisModule = this.module;
        Module otherModule = other.module;
        if (thisModule == null ? otherModule != null : !thisModule.equals(otherModule)) {
            return false;
        }
        
        List<TimedMessage> thisMessages = this.timedMessages;
        List<TimedMessage> otherMessages = other.timedMessages;
        if (thisMessages == null ? otherMessages != null : !thisMessages.equals(otherMessages)) {
            return false;
        }
        
        NumberValue<Float> thisValue = this.value;
        NumberValue<Float> otherValue = other.value;
        if (thisValue == null ? otherValue != null : !thisValue.equals(otherValue)) {
            return false;
        }
        
        String thisLabel = this.label;
        String otherLabel = other.label;
        return thisLabel == null ? otherLabel == null : thisLabel.equals(otherLabel);
    }

    protected boolean canEqual(Object other) {
        return other instanceof TimedMessageSystem;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 59 * result + (module == null ? 43 : module.hashCode());
        result = 59 * result + (timedMessages == null ? 43 : timedMessages.hashCode());
        result = 59 * result + (value == null ? 43 : value.hashCode());
        result = 59 * result + (label == null ? 43 : label.hashCode());
        return result;
    }

    public String toString() {
        return "TimedMessageSystem(module=" + module + 
               ", timedMessages=" + timedMessages + 
               ", value=" + value + 
               ", label=" + label + ")";
    }
}