package org.mylan.openie.index;

import java.util.Map;

/**
 * Describe class FirstArgumentHandler here.
 *
 *
 * Created: Wed Mar 26 22:46:21 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class FirstArgumentHandler extends TermHandler {
    public FirstArgumentHandler(int limit) {
        super(limit);
    }

    protected void save(final Map<String, Integer> counts) {
        relationSql.insertFirstArgumentCounts(counts);
    }
}
