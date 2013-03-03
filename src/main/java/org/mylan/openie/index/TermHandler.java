package org.mylan.openie.index;

import java.util.HashMap;
import java.util.Map;

import org.mylan.openie.sql.RelationSql;

/**
 * Describe class TermHandler here.
 *
 *
 * Created: Wed Mar 26 22:21:10 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class TermHandler {
    private int limit;
    private Map<String, Integer> counts;
    protected RelationSql relationSql;

    public TermHandler(int limit) {
        this.limit = limit;
        counts = new HashMap<String, Integer>();
        relationSql = new RelationSql();
    }

    public void add(final String text, int count) {
        counts.put(text, count);
        if (counts.size() > limit) {
            flush();
        }
    }

    public void flush() {
        save(counts);
        counts = new HashMap<String, Integer>();
    }

    protected abstract void save(final Map<String, Integer> counts);
}
