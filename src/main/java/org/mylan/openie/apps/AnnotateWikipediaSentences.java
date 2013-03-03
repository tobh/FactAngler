package org.mylan.openie.apps;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.wikipedia.AnnotateWikipedia;

/**
 * Describe class AnnotateWikipediaSentences here.
 *
 *
 * Created: Fri Feb  8 16:51:48 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class AnnotateWikipediaSentences {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: AnnotateWikipediaSentences start limit");
            System.exit(1);
        }

        Wikipedia wikipedia = new Wikipedia();
        AnnotateWikipedia annotateWikipedia = new AnnotateWikipedia(wikipedia);
        int count = wikipedia.getArticleCount();
        int start = Integer.parseInt(args[0]);
        int limit = Integer.parseInt(args[1]);
        int sentenceCount = annotateWikipedia.annotateSentencesFromArticles(start, limit);
        System.out.println("#sentences = " + sentenceCount + " | article = " + (start + limit) + " / " + count);
        wikipedia.close();
    }
}
