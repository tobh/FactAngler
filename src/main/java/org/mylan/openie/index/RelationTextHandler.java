package org.mylan.openie.index;

import java.util.Map;

/**
 * Describe class RelationTextHandler here.
 *
 *
 * Created: Wed Mar 26 22:41:37 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationTextHandler extends TermHandler {
    public RelationTextHandler(int limit) {
        super(limit);
    }

    protected void save(final Map<String, Integer> counts) {
        relationSql.insertRelationTextCounts(counts);
    }
}
