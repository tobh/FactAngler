package org.mylan.openie.index;

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
public class BuildRelationCandidateIndexStrategy extends BuildIndexStrategy {
    private final RelationCandidateIndex index;

    public BuildRelationCandidateIndexStrategy(final RelationCandidateIndex index) {
        super();
        this.index = index;
    }

    public void addToIndex(final Relation relation) {
    	index.add(relation);
    }
}
