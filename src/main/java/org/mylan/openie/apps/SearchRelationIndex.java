package org.mylan.openie.apps;

import org.apache.lucene.queryParser.ParseException;
import org.mylan.openie.index.RelationIndex;
import org.mylan.openie.shared.Result;
import org.mylan.openie.shared.SimpleRelation;

/**
 * Describe class SearchRelationIndex here.
 *
 *
 * Created: Thu Mar  6 23:57:49 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SearchRelationIndex {
    public static void main(String[] args) throws ParseException {
        int start = 0;
        int limit = 10;
        if (args.length == 3) {
            start = Integer.parseInt(args[1]);
            limit = Integer.parseInt(args[2]);
        } else if (args.length != 1) {
            System.out.println("usage: SearchRelationIndex \"search terms\" [start end]");
            System.exit(1);
        }

        RelationIndex index = new RelationIndex();
        Result<SimpleRelation> results = index.find(args[0], start, limit);
        System.out.println(results.getResultCount() + " results for " + args[0]);
        for (SimpleRelation relation : results.getResults()) {
        	System.out.println(relation);
        }
    }
}
