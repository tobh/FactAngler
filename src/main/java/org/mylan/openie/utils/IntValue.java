package org.mylan.openie.utils;

/**
 * Describe class IntValue here.
 *
 *
 * Created: Thu Jan 10 17:44:38 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class IntValue {
    private int value = 0;

    public IntValue() {
        this(0);
    }

    public IntValue(final int value) {
        setValue(value);
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public void increment() {
        ++value;
    }

    public void increment(final int incrementValue) {
        value += incrementValue;
    }

    public String toString() {
        return "" + value;
    }

    public boolean equals(final Object o) {
        if (o instanceof IntValue) {
            IntValue ref = (IntValue) o;
            return getValue() == ref.getValue();
        }
        return super.equals(o);
    }

    public int hashCode() {
        return value;
    }
}
