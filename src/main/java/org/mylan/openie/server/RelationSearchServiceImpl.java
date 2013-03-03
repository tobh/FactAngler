package org.mylan.openie.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.lucene.queryParser.ParseException;
import org.mylan.openie.client.RelationSearchService;
import org.mylan.openie.index.RelationIndex;
import org.mylan.openie.shared.SimpleRelation;
import org.mylan.openie.shared.Result;
import java.util.LinkedList;

/**
 * Describe class RelationSearchServiceImpl here.
 *
 *
 * Created: Tue Apr  8 18:03:53 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationSearchServiceImpl extends RemoteServiceServlet implements RelationSearchService {
    private static final long serialVersionUID = 1L;

    public Result<SimpleRelation> search(final String searchTerm) {
        RelationIndex index = new RelationIndex();
        Result<SimpleRelation> results = null;
        try {
            results = index.find(searchTerm, 0, 100);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (results == null) {
            results = new Result<SimpleRelation>(0, new LinkedList<SimpleRelation>());
        }
        return results;
    }
}
