package org.mylan.openie.index;

import java.util.List;

import org.mylan.openie.relation.ProcessRelationStrategy;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class BuildIndexStrategy here.
 *
 *
 * Created: Mon Mar 31 15:48:37 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class BuildIndexStrategy extends ProcessRelationStrategy {
    public void process(final List<Relation> relations) {
        for (Relation relation : relations) {
            addToIndex(relation);
        }
    }

    protected abstract void addToIndex(final Relation relation);
}
