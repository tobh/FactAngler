package org.mylan.openie.wikipedia;

import org.mylan.openie.nlp.Sentence;

/**
 * Describe class WikipediaSentence here.
 *
 *
 * Created: Thu Nov 22 16:24:23 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class WikipediaSentence {
    private final int sentenceId;
    private final int pageId;
    private final Sentence sentence;

    public WikipediaSentence(int pageId,
                             final Sentence sentence) {
        this(0, pageId, sentence);
    }

    public WikipediaSentence(int sentenceId,
                             int pageId,
                             final Sentence sentence) {
        this.pageId = pageId;
        this.sentenceId = sentenceId;
        this.sentence = sentence;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public int getPageId() {
        return pageId;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public String toString() {
        return sentence.toString();
    }
}
