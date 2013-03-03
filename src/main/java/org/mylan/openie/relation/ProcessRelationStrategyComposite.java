package org.mylan.openie.relation;

import java.util.LinkedList;
import java.util.List;

import org.mylan.openie.relation.instance.Relation;


/**
 * Describe class ProcessStrategyComposite here.
 *
 *
 * Created: Mon Mar 31 15:44:42 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ProcessRelationStrategyComposite extends ProcessRelationStrategy {
    private List<ProcessRelationStrategy> strategies;

    public ProcessRelationStrategyComposite() {
        strategies = new LinkedList<ProcessRelationStrategy>();
    }

    public void add(final ProcessRelationStrategy strategy) {
        strategies.add(strategy);
    }

    public void process(final List<Relation> relations) {
        for (ProcessRelationStrategy strategy : strategies) {
            strategy.process(relations);
        }
    }
}
