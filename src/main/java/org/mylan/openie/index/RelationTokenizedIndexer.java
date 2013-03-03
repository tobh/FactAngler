package org.mylan.openie.index;

import org.mylan.openie.relation.RelationVisitor;
import org.mylan.openie.shared.SimpleRelation;

/**
 * Describe class RelationTokenizedIndexer here.
 *
 *
 * Created: Mon Mar 31 15:02:02 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationTokenizedIndexer extends RelationVisitor {
    private RelationIndex index;

    public RelationTokenizedIndexer(final RelationIndex index) {
        super();
        this.index = index;
    }

    public void visitRelation(SimpleRelation relation) {
        index.add(relation);
    }
}
