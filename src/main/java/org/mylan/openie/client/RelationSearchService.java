package org.mylan.openie.client;

import com.google.gwt.user.client.rpc.RemoteService;
import org.mylan.openie.shared.SimpleRelation;
import org.mylan.openie.shared.Result;

/**
 * Describe interface RelationSearchService here.
 *
 *
 * Created: Tue Apr  8 17:49:59 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public interface RelationSearchService extends RemoteService {
    Result<SimpleRelation> search(final String searchTerm);
}
