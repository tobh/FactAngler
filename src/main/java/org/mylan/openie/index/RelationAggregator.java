package org.mylan.openie.index;

import org.mylan.openie.relation.RelationVisitor;
import org.mylan.openie.shared.SimpleRelation;

/**
 * Describe class RelationAggregator here.
 *
 *
 * Created: Tue Mar 25 13:55:25 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationAggregator extends RelationVisitor {
	private AggregationIndex index;

    public RelationAggregator(final AggregationIndex index) {
        super();
        this.index = index;
    }

    public void visitRelation(SimpleRelation relation) {
		index.add(relation);
	}
}
