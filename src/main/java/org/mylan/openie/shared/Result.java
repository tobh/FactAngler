package org.mylan.openie.shared;

import java.util.List;
import java.io.Serializable;

/**
 * Describe class Result here.
 *
 *
 * Created: Sun Mar 16 15:18:07 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Result<ResultType> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int resultCount;
    private List<ResultType> results;

    @SuppressWarnings("unused")
    private Result() {
    }

    public Result(int resultCount, final List<ResultType> results) {
        this.resultCount = resultCount;
        this.results = results;
    }

    public int getResultCount() {
        return resultCount;
    }

    public List<ResultType> getResults() {
        return results;
    }
}
