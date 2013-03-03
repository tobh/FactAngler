package org.mylan.openie.relation;

import java.util.LinkedList;
import java.util.List;

import org.mylan.openie.shared.SimpleRelation;

/**
 * Describe class RelationVisitorComposite here.
 *
 *
 * Created: Wed Mar 26 15:40:39 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationVisitorComposite extends RelationVisitor {
    private final List<RelationVisitor> visitors;

    public RelationVisitorComposite() {
        super();
        visitors = new LinkedList<RelationVisitor>();
    }

    public void visitRelation(final SimpleRelation relation) {
        for (RelationVisitor visitor : visitors) {
            visitor.visitRelation(relation);
        }
    }

    public void add(final RelationVisitor visitor) {
        visitors.add(visitor);
    }
}
