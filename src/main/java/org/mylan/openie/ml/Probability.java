package org.mylan.openie.ml;

/**
 * Describe class Probability here.
 *
 *
 * Created: Thu Jan  3 16:28:01 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Probability {
    private double value;

    public Probability() {
        this(0.0);
    }

    public Probability(final double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    public String toString() {
        return "" + value;
    }

    public boolean equals(final Object o) {
        if (o instanceof Feature) {
            Probability ref = (Probability) o;
            return getValue() == ref.getValue();
        }
        return super.equals(o);
    }

    public int hashCode() {
        return new Double(getValue()).hashCode();
    }
}
