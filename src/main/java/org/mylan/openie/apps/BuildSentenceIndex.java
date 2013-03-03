package org.mylan.openie.apps;

import org.mylan.openie.index.SentenceIndex;


/**
 * Describe class BuildSentenceIndex here.
 *
 *
 * Created: Sun Mar 16 16:55:14 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class BuildSentenceIndex {
    public static void main(String[] args) {
        SentenceIndex index = new SentenceIndex("sentence_index");
        index.buildIndex(100000);
    }
}
