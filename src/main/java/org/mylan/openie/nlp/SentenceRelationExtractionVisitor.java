package org.mylan.openie.nlp;

import java.util.List;

import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.relation.ProcessRelationStrategy;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class SentenceRelationExtractionVisitor here.
 *
 *
 * Created: Fri Mar 28 15:55:39 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SentenceRelationExtractionVisitor extends SentenceRelationCandidateExtractionVisitor {
    private final FisherClassifier classifier;

    public SentenceRelationExtractionVisitor(final ProcessRelationStrategy strategy) {
        super(strategy);
        classifier = new FisherClassifier();
        classifier.restore();
    }

    protected List<Relation> getRelations(final Sentence sentence) {
        return extractor.getRelations(sentence, classifier);
    }
}
