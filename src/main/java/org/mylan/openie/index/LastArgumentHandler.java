package org.mylan.openie.index;

import java.util.Map;

/**
 * Describe class LastArgumentHandler here.
 *
 *
 * Created: Wed Mar 26 22:47:43 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class LastArgumentHandler extends TermHandler {
    public LastArgumentHandler(int limit) {
        super(limit);
    }

    protected void save(final Map<String, Integer> counts) {
        relationSql.insertLastArgumentCounts(counts);
    }
}
