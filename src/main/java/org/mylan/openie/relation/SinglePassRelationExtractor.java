package org.mylan.openie.relation;

import org.mylan.openie.corpus.TextCorpus;
import org.mylan.openie.index.BuildRelationIndexStrategy;
import org.mylan.openie.index.RelationIndex;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.nlp.SentenceRelationCandidateExtractionVisitor;
import org.mylan.openie.nlp.SentenceVisitor;
import org.mylan.openie.sql.EvaluateSentenceVisitor;
import org.mylan.openie.sql.SaveRelationsWithProbabilitiesStrategy;
import org.mylan.openie.nlp.SentenceRelationExtractionVisitor;

/**
 * Describe class SinglePassRelationExtractor here.
 *
 *
 * Created: Mon Mar 31 15:18:53 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SinglePassRelationExtractor {
    public void extract(final TextCorpus corpus) {
        RelationIndex relationIndex = new RelationIndex();
        FisherClassifier classifier = new FisherClassifier();
        classifier.restore();

        ProcessRelationStrategyComposite strategy = new ProcessRelationStrategyComposite();
        //strategy.add(new BuildRelationIndexStrategy(relationIndex, classifier));
        strategy.add(new SaveRelationsWithProbabilitiesStrategy(classifier));

        // extract everything, not only correct relations
        //SentenceVisitor visitor = new SentenceRelationCandidateExtractionVisitor(strategy);

        // extract only correct relations
        SentenceVisitor visitor = new SentenceRelationExtractionVisitor(strategy);

        // analyse sentences
        corpus.accept(visitor);
        // analyse sentences and evaluate relations
        //SentenceVisitor evaluationVisitor = new EvaluateSentenceVisitor(classifier);
        //corpus.accept(evaluationVisitor);
    }
}
