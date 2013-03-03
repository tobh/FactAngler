package org.mylan.openie.ml;

/**
 * Describe class Feature here.
 *
 *
 * Created: Wed Jan  2 13:48:02 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Feature {
    private final String token;
    private final FeatureType type;

    public Feature(final String token, final FeatureType type) {
        this.token = token;
        this.type = type;
    }

    public Feature(final int numberToken, final FeatureType type) {
        this("" + numberToken, type);
    }

    public String getToken() {
        return token;
    }

    public FeatureType getType() {
        return type;
    }

    public boolean equals(final Object o) {
        if (o instanceof Feature) {
            Feature ref = (Feature) o;
            return getToken().equals(ref.getToken()) && getType().equals(ref.getType());
        }
        return super.equals(o);
    }

    public int hashCode() {
        return getToken().hashCode() + 31 * getType().hashCode();
    }
}
