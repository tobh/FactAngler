package org.mylan.openie.index;

import java.util.Map;

/**
 * Describe class RelationPatternHandler here.
 *
 *
 * Created: Wed Mar 26 22:49:22 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationPatternHandler extends TermHandler {
    public RelationPatternHandler(int limit) {
        super(limit);
    }

    protected void save(final Map<String, Integer> counts) {
        relationSql.insertRelationTextCounts(counts);
    }
}
