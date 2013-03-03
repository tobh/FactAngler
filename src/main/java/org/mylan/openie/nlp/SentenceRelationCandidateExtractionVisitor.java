package org.mylan.openie.nlp;

import java.util.LinkedList;
import java.util.List;

import org.mylan.openie.relation.ProcessRelationStrategy;
import org.mylan.openie.relation.extraction.RelationExtractor;
import org.mylan.openie.relation.extraction.TaggedPosRelationExtractor;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.nlp.Sentence;

/**
 * Describe class SentenceRelationCandidateExtractionVisitor here.
 *
 *
 * Created: Fri Mar 28 15:55:39 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SentenceRelationCandidateExtractionVisitor extends SentenceVisitor {
    protected final RelationExtractor extractor;

    private List<Relation> relations;
    private final ProcessRelationStrategy strategy;

    public SentenceRelationCandidateExtractionVisitor(final ProcessRelationStrategy strategy) {
        super();
        this.strategy = strategy;
        relations = new LinkedList<Relation>();
        extractor = new TaggedPosRelationExtractor();
    }

    public void visitSentence(final Sentence sentence) {
        List<Relation> extractedRelations = getRelations(sentence);
        relations.addAll(extractedRelations);
    }

    protected List<Relation> getRelations(final Sentence sentence) {
        return extractor.getRelations(sentence);
    }

    public void process() {
        strategy.process(relations);
        relations = new LinkedList<Relation>();
    }
}
