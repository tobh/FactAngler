package org.mylan.openie.relation.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mylan.openie.ml.Category;
import org.mylan.openie.nlp.BerkeleyParser;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.relation.extraction.ParsedPosRelationExtractor;
import org.mylan.openie.relation.extraction.RelationExtractor;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class RelationEvaluator here.
 *
 *
 * Created: Sat May  3 12:35:36 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationEvaluator {
    public Map<Relation, Category> evaluate(final PosComponent sentence) {
        Sentence sentencePennTree = new Sentence(sentence.getEndNodes());

        RelationExtractor extractor = new ParsedPosRelationExtractor();

        // extract Relations from parsed sentence and evaluate them
        List<Relation> relations = extractor.getRelations(sentencePennTree);
        Map<Relation, Category> parsedRelationsWithCategory = new HashMap<Relation, Category>();
        RelationFilter filter = RelationFilter.create();
        for(Relation relation : relations) {
            parsedRelationsWithCategory.put(relation, filter.getCategory(relation));
        }

        // convert relations with parsed sentences to relations with tagged sentences
        Map<Relation, Category> taggedRelationsWithCategory = new HashMap<Relation, Category>();
        RelationConverter converter = new RelationConverter();
        for (Map.Entry<Relation, Category> entry : parsedRelationsWithCategory.entrySet()) {
            Relation convertedRelation = converter.convert(entry.getKey());
            if (convertedRelation != null) {
                taggedRelationsWithCategory.put(convertedRelation, entry.getValue());
            }
        }
        return taggedRelationsWithCategory;
    }

    public Map<Relation, Category> evaluate(final String sentence) {
        BerkeleyParser parser = new BerkeleyParser();
        String parsedSentence = parser.generateSyntaxTree(sentence);
        PosComponent sentenceTree = parser.parseSyntaxTree(parsedSentence);
        return evaluate(sentenceTree);
    }
}
