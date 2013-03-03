package org.mylan.openie.index;

import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class BuildRelationAggregationIndexStrategy here.
 *
 *
 * Created: Mon Mar 31 16:27:42 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class BuildRelationAggregationIndexStrategy extends BuildIndexStrategy {
    private AggregationIndex index;

    public BuildRelationAggregationIndexStrategy(final AggregationIndex index) {
        super();
        this.index = index;
    }

    protected void addToIndex(final Relation relation) {
        index.add(relation);
    }
}
