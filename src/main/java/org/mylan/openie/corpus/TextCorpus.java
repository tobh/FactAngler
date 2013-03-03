package org.mylan.openie.corpus;

import org.mylan.openie.nlp.SentenceVisitor;


/**
 * Describe interface TextCorpus here.
 *
 *
 * Created: Fri Apr 18 19:44:49 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public interface TextCorpus {
    public void accept(final SentenceVisitor visitor);
}
