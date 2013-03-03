package org.mylan.openie.index;

import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class BuildRelationIndexStrategy here.
 *
 *
 * Created: Mon Mar 31 16:21:33 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class BuildRelationIndexStrategy extends BuildIndexStrategy {
    private RelationIndex index;
    private FisherClassifier classifier;

    public BuildRelationIndexStrategy(final RelationIndex index, final FisherClassifier classifier) {
        super();
        this.index = index;
        this.classifier = classifier;
    }

    public void addToIndex(final Relation relation) {
    	index.add(relation, classifier);
    }
}
