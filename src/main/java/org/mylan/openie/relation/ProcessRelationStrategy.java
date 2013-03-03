package org.mylan.openie.relation;

import java.util.List;

import org.mylan.openie.relation.instance.Relation;


/**
 * Describe class ProcessStrategy here.
 *
 *
 * Created: Mon Mar 31 15:40:24 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class ProcessRelationStrategy {
    public abstract void process(final List<Relation> relations);
}
