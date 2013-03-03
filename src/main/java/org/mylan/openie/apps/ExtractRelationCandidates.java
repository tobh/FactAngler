package org.mylan.openie.apps;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.index.BuildRelationCandidateIndexStrategy;
import org.mylan.openie.index.RelationCandidateIndex;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.nlp.SentenceRelationCandidateExtractionVisitor;
import org.mylan.openie.nlp.SentenceVisitor;
import org.mylan.openie.relation.ProcessRelationStrategyComposite;
import org.mylan.openie.sql.SaveRelationsWithProbabilitiesStrategy;

/**
 * Describe class ExtractRelationCandidates here.
 *
 *
 * Created: Tue Apr  1 00:00:59 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ExtractRelationCandidates {
    public static void main(String[] args) {
        FisherClassifier classifier = new FisherClassifier();
        classifier.restore();
        RelationCandidateIndex index = new RelationCandidateIndex(classifier);

        ProcessRelationStrategyComposite strategy = new ProcessRelationStrategyComposite();
        strategy.add(new BuildRelationCandidateIndexStrategy(index));
        strategy.add(new SaveRelationsWithProbabilitiesStrategy(classifier));
        SentenceVisitor visitor = new SentenceRelationCandidateExtractionVisitor(strategy);
        Wikipedia wikipedia = new Wikipedia();
        wikipedia.accept(visitor);
    }
}
