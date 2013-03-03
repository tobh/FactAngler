package org.mylan.openie.nlp;

import org.mylan.openie.nlp.Sentence;

/**
 * Describe class SentenceVisitor here.
 *
 *
 * Created: Fri Mar 28 15:47:27 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class SentenceVisitor {
    public abstract void visitSentence(final Sentence sentence);
    public void process() {};
}
