package org.mylan.openie.apps;

import org.mylan.openie.index.AggregationIndex;

/**
 * Describe class BuildAggregationIndex here.
 *
 *
 * Created: Wed Mar 26 23:04:16 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class BuildAggregationIndex {
    public static void main(String[] args) {
        AggregationIndex index = new AggregationIndex();
        index.buildIndex(100000);
    }
}
