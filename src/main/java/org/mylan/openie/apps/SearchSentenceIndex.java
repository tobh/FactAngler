package org.mylan.openie.apps;

import org.apache.lucene.queryParser.ParseException;
import org.mylan.openie.index.SentenceIndex;
import org.mylan.openie.shared.Result;

/**
 * Describe class SearchSentenceIndex here.
 *
 *
 * Created: Sun Mar 16 22:37:49 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SearchSentenceIndex {
    public static void main(String[] args) throws ParseException {
        if (args.length != 1) {
            System.out.println("usage: SearchSentenceIndex \"search terms\"");
            System.exit(1);
        }

        SentenceIndex index = new SentenceIndex("sentence_index");
        Result<String> results = index.find(args[0], 0, 10);
        System.out.println(results.getResultCount() + " results for " + args[0]);
        for (String sentence : results.getResults()) {
            System.out.println(sentence);
        }
    }
}
