package me.pollos.polloshook.api.value.value;

import java.util.Arrays;
import java.util.function.Supplier;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.value.event.ValueEvent;
import me.pollos.polloshook.api.value.observer.Observable;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.interfaced.IValue;
import me.pollos.polloshook.api.value.value.parents.BooleanParent;
import me.pollos.polloshook.api.value.value.parents.EnumParent;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;

public class Value<T> extends Observable<ValueEvent<T>> implements Labeled, IValue {
    protected String[] aliases;
    protected T value;
    protected T defaultValue;
    protected Parent parent;

    public Value(T value, String... aliases) {
        this.aliases = aliases;
        this.value = value;
        this.defaultValue = value;
    }

    public Parent getParent() {
        return this.parent == null ? 
            new SupplierParent(() -> true, false) : 
            this.parent;
    }

    public void resetToDefaultValue() {
        this.setValue(this.defaultValue);
    }

    public void setValue(Enum<?> enum1) {
        setValue(enum1);
     }

    public void setValue(T value) {
        ValueEvent<T> event = this.onChange(new ValueEvent(this, value));
        if (!event.isCanceled()) {
            this.value = event.getValue();
        }
    }

    public Value<T> setParent(Parent parent) {
        this.parent = parent;
        return this;
    }

    public Value<T> setParent(Supplier<Boolean> parent) {
        this.setParent(parent, false);
        return this;
    }

    public Value<T> setParent(Supplier<Boolean> parent, boolean opposite) {
        this.parent = new SupplierParent(parent, opposite);
        return this;
    }

    public Value<T> setParent(EnumValue<?> parent, Enum<?> target) {
        this.setParent(parent, target, false);
        return this;
    }

    public Value<T> setParent(EnumValue<?> parent, Enum<?> target, boolean opposite) {
        this.parent = new EnumParent(parent, target, opposite);
        return this;
    }

    public Value<T> setParent(Value<Boolean> parent) {
        this.setParent(parent, false);
        return this;
    }

    public Value<T> setParent(Value<Boolean> parent, boolean opposite) {
        this.parent = new BooleanParent(parent, opposite);
        return this;
    }

    public String getLabel() {
        return this.aliases.length > 0 ? this.aliases[0] : null;
    }

    public String returnValue(String[] args) {
        return "Invalid Arguments";
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public T getValue() {
        return this.value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Value)) {
            return false;
        }
        Value<?> other = (Value<?>) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Arrays.deepEquals(this.aliases, other.aliases)) {
            return false;
        }
        if (this.value == null ? other.value != null : !this.value.equals(other.value)) {
            return false;
        }
        if (this.defaultValue == null ? other.defaultValue != null : !this.defaultValue.equals(other.defaultValue)) {
            return false;
        }
        Parent thisParent = this.getParent();
        Parent otherParent = other.getParent();
        return thisParent == null ? otherParent == null : thisParent.equals(otherParent);
    }

    protected boolean canEqual(Object other) {
        return other instanceof Value;
    }

    public int hashCode() {
        int result = 1;
        result = 59 * result + Arrays.deepHashCode(this.aliases);
        result = 59 * result + (this.value == null ? 43 : this.value.hashCode());
        result = 59 * result + (this.defaultValue == null ? 43 : this.defaultValue.hashCode());
        Parent parent = this.getParent();
        result = 59 * result + (parent == null ? 43 : parent.hashCode());
        return result;
    }

    private void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }
}