package org.mylan.openie.relation.instance;

import java.util.HashSet;
import java.util.Set;

import org.mylan.openie.ml.Feature;
import org.mylan.openie.ml.FeatureType;
import org.mylan.openie.ml.Item;
import org.mylan.openie.nlp.Sentence;

/**
 * Describe class Relation here.
 *
 *
 * Created: Wed Dec 12 21:16:31 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Relation implements Item {
    private final Sentence sentence;
    private final Pattern firstArgument;
    private final Pattern lastArgument;
    private final Pattern relationPattern;
    private Set<Feature> features;

    public Relation(final Sentence sentence, final Pattern firstArgument, final Pattern relationPattern, final Pattern lastArgument) {
        this.sentence = sentence;
        this.firstArgument = firstArgument;
        this.lastArgument = lastArgument;
        this.relationPattern = relationPattern;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public Pattern getFirstArgument() {
        return firstArgument;
    }

    public Pattern getLastArgument() {
        return lastArgument;
    }

    public Pattern getRelationPattern() {
        return relationPattern;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstArgument);
        sb.append(" ");
        sb.append(relationPattern);
        sb.append(" ");
        sb.append(lastArgument);
        return sb.toString();
    }

    public Set<Feature> getFeatures() {
        if (features == null) {
            features = new HashSet<Feature>();
            for (FeatureType featureType : FeatureType.values()) {
                featureType.getFeatures(features, this);
            }
        }
        return features;
    }
}
