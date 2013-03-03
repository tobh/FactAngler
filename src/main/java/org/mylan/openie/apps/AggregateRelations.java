package org.mylan.openie.apps;

import org.mylan.openie.index.AggregationIndex;

/**
 * Describe class AggregateRelations here.
 *
 *
 * Created: Tue Mar 25 18:11:35 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class AggregateRelations {
    public static void main(String[] args) {
        AggregationIndex index = new AggregationIndex();
        index.aggregateIndexedRelations(1000000);
    }
}
