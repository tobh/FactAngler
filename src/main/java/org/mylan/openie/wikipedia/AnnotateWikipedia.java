package org.mylan.openie.wikipedia;

import java.util.LinkedList;
import java.util.List;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.nlp.SentenceExtractor;

/**
 * Describe class AnnotateWikipedia here.
 *
 *
 * Created: Fri Nov  9 13:54:15 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class AnnotateWikipedia {
    private Wikipedia wikipedia;
    private SentenceExtractor extractor;

    public AnnotateWikipedia(final Wikipedia wikipedia) {
        this.wikipedia = wikipedia;
        extractor = new SentenceExtractor();
    }

    public int annotateSentencesFromArticles(int start,
                                             int limit) {
        List<WikipediaArticle> articles = wikipedia.getArticles(start, limit);
        List<WikipediaSentence> sentences = new LinkedList<WikipediaSentence>();
        for(WikipediaArticle article : articles) {
            appendSentences(sentences, article.getPageId(), article.getArticleText());
        }
        wikipedia.insertSentences(sentences);
        return sentences.size();
    }

    private void appendSentences(final List<WikipediaSentence> wikipediaSentences,
                                                     int pageId,
                                                     final String articleText) {
    	List<Sentence> sentences = extractor.extract(articleText);
    	for (Sentence sentence : sentences) {
    		wikipediaSentences.add(new WikipediaSentence(pageId, sentence));
    	}
    }
}
