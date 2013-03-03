package org.mylan.openie.client;

import org.mylan.openie.shared.SimpleRelation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.mylan.openie.shared.Result;

/**
 * Describe interface RelationSearchServiceAsync here.
 *
 *
 * Created: Tue Apr  8 17:51:40 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public interface RelationSearchServiceAsync {
    void search(final String searchTerm, AsyncCallback<Result<SimpleRelation>> callback);
}
