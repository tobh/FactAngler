package org.mylan.openie.apps;

import org.mylan.openie.index.RelationIndex;

/**
 * Describe class BuildRelationIndex here.
 *
 *
 * Created: Thu Mar  6 23:09:35 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class BuildRelationIndex {
    public static void main(String[] args) {
    	RelationIndex index = new RelationIndex();
        int limit = 100000;
        index.buildIndex(limit);
    }
}
