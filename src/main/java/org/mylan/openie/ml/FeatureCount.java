package org.mylan.openie.ml;

import java.util.HashMap;
import java.util.Map;

import org.mylan.openie.utils.IntValue;

/**
 * Describe class FeatureCount here.
 *
 *
 * Created: Wed Jan 23 22:12:11 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class FeatureCount {
    private final Feature feature;
    private final Map<Category, IntValue> countPerCategory;

    public FeatureCount(final Feature feature) {
        this.feature = feature;
        countPerCategory = new HashMap<Category, IntValue>();
    }

    public Feature getFeature() {
        return feature;
    }

    public int getCount(final Category category) {
        return getCategoryCount(category).getValue();
    }

    public void setCount(final Category category, final int count) {
        getCategoryCount(category).setValue(count);
    }

    public void increment(final Category category) {
        getCategoryCount(category).increment();
    }

    private IntValue getCategoryCount(final Category category) {
        IntValue count = countPerCategory.get(category);
        if (count == null) {
            count = new IntValue();
            countPerCategory.put(category, count);
        }
        return count;
    }

    public boolean equals(final Object o) {
        if (o instanceof FeatureCount) {
            FeatureCount ref = (FeatureCount) o;
            return getFeature().equals(ref.getFeature());
        }
        return super.equals(o);
    }

    public int hashCode() {
        return getFeature().hashCode();
    }
}
