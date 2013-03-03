package org.mylan.openie.relation;

import org.mylan.openie.shared.SimpleRelation;

/**
 * Describe class RelationVisitor here.
 *
 *
 * Created: Tue Mar 25 13:45:23 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class RelationVisitor {
    public abstract void visitRelation(final SimpleRelation relation);
}
