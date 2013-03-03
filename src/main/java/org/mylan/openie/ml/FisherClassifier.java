package org.mylan.openie.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mylan.openie.sql.FisherClassifierSql;
import org.mylan.openie.utils.IntValue;

/**
 * Describe class FisherClassifier here.
 *
 *
 * Created: Tue Jan  1 18:07:34 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class FisherClassifier {
    private Map<Feature, FeatureCount> featureCounts;
    private Map<Category, IntValue> categoryCounts;
    private double assumedProbability = 0.5;
    private double assumedProbabilityWeight = 1.0;
    private Map<Category, Probability> minimum;

    public FisherClassifier() {
        featureCounts = new HashMap<Feature, FeatureCount>();
        categoryCounts = new HashMap<Category, IntValue>();

        minimum = new HashMap<Category, Probability>();
        setMinimumProbability(Category.IS_RELATION, 0.8);
        setMinimumProbability(Category.IS_NO_RELATION, 0.2);
    }

    private double getCategoryProbability(final Feature feature, final Category category) {
        double featureGivenCategoryProbability = getFeatureProbability(feature, category);
        if (featureGivenCategoryProbability == 0.0) {
            return 0;
        }
        double sumFeatureGivenCategoryProbabilities = 0.0;
        for (Category cat : Category.values()) {
            sumFeatureGivenCategoryProbabilities += getFeatureProbability(feature, cat);
        }
        return featureGivenCategoryProbability / sumFeatureGivenCategoryProbabilities;
    }

    public void train(final Item item, final Category category) {
        Set<Feature> features = item.getFeatures();
        for (Feature feature : features) {
            incrementFeatureCountPerCategory(feature, category);
        }
        incrementCategoryCount(category);
    }

    public Category classify(final Item item) {
        double isRelationProbability = getFisherProbability(item, Category.IS_RELATION);
        double isNoRelationProbability = getFisherProbability(item, Category.IS_NO_RELATION);

        if (isRelationProbability > getMinimumProbability(Category.IS_RELATION) && isRelationProbability > isNoRelationProbability * 2) {
            return Category.IS_RELATION;
        }

        if (isNoRelationProbability > getMinimumProbability(Category.IS_NO_RELATION) && isNoRelationProbability > isRelationProbability * 2) {
            return Category.IS_NO_RELATION;
        }

        return Category.NONE;
    }

    // conditional probability: P(feature|category)
    protected double getFeatureProbability(final Feature feature, final Category category) {
        int categoryCount = getCategoryCount(category);
        if (categoryCount == 0) {
            return 0.0;
        }
        return (double) getFeatureCountPerCategory(feature, category) / categoryCount;
    }

    protected double getWeightedProbability(final Feature feature, final Category category) {
        double basicProbability = getCategoryProbability(feature, category);
        int featureCount = 0;
        for (Category cat : Category.values()) {
            featureCount += getFeatureCountPerCategory(feature, cat);
        }
        double weightedProbability = ((assumedProbabilityWeight * assumedProbability) + (featureCount * basicProbability)) / (assumedProbabilityWeight + featureCount);
        return weightedProbability;
    }

    public double getFisherProbability(final Item item, final Category category) {
        double probability = 1.0;
        Set<Feature> features = item.getFeatures();
        for (FeatureType type : FeatureType.values()) {
            double min = 1.0;
            double max = 0.0;
            for (Feature feature : features) {
                if (feature.getType().equals(type)) {
                    double featureProbability = getWeightedProbability(feature, category);
                    if (featureProbability > max) {
                        max = featureProbability;
                    }
                    if (featureProbability < min) {
                        min = featureProbability;
                    }
                }
            }
            // use min and max and for featureTypes with only one feature use it twice
            probability *= min * max;
        }

        double fisherScore = -2 * Math.log(probability);

        return getInverseChiSquare(fisherScore, (FeatureType.values().length * 2) * 2);
    }

    private double getInverseChiSquare(final double chi, final int freedomDegrees) {
        double m = chi / 2.0;
        double term = Math.exp(-m);
        double sum = term;
        for (int i = 1; i < (freedomDegrees / 2); ++i) {
            term *= m / i;
            sum += term;
        }
        return Math.min(sum, 1.0);
    }

    public double getMinimumProbability(final Category category) {
        Probability min = minimum.get(category);
        if (min == null) {
            return 0.0;
        }
        return min.getValue();
    }

    public void setMinimumProbability(final Category category, double minimumValue) {
        Probability min = minimum.get(category);
        if (min == null) {
            min = new Probability();
            minimum.put(category, min);
        }
        min.setValue(minimumValue);
    }

    protected int getFeatureCountPerCategory(final Feature feature, final Category category) {
        return getInitializedFeatureCount(feature).getCount(category);
    }

    protected void incrementFeatureCountPerCategory(final Feature feature, final Category category) {
        getInitializedFeatureCount(feature).increment(category);
    }

    protected int getCategoryCount(final Category category) {
        return getInitializedCategoryCount(category).getValue();
    }

    protected void incrementCategoryCount(final Category category) {
        getInitializedCategoryCount(category).increment();
    }

    protected int getTotalCount() {
        int count = 0;
        for (IntValue categoryCount : categoryCounts.values()) {
            count += categoryCount.getValue();
        }
        return count;
    }

    protected Set<Category> getCategories() {
        return categoryCounts.keySet();
    }

    private FeatureCount getInitializedFeatureCount(final Feature feature) {
        FeatureCount featureCount = featureCounts.get(feature);
        if (featureCount == null) {
            featureCount = new FeatureCount(feature);
            featureCounts.put(feature, featureCount);
        }
        return featureCount;
    }

    private IntValue getInitializedCategoryCount(final Category category) {
        IntValue count = categoryCounts.get(category);
        if (count == null) {
            count = new IntValue();
            categoryCounts.put(category, count);
        }
        return count;
    }

    public void save() {
        FisherClassifierSql sql = new FisherClassifierSql();
        List<FeatureCount> saveFeatureCount = new ArrayList<FeatureCount>();
        saveFeatureCount.addAll(featureCounts.values());
        sql.insertFeatureCount(saveFeatureCount);
        for (Category category : categoryCounts.keySet()) {
            sql.insertCategoryCount(category, categoryCounts.get(category).getValue());
        }
        sql.close();
    }

    public void restore() {
        FisherClassifierSql sql = new FisherClassifierSql();
        featureCounts = sql.getFeatureCount();
        categoryCounts = sql.getCategoryCount();
        sql.close();
    }
}
