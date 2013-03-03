package org.mylan.openie.apps;

import java.util.List;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.ml.Category;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.nlp.ArgumentExtractor;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.relation.extraction.RelationExtractor;
import org.mylan.openie.relation.extraction.TaggedPosRelationExtractor;
import org.mylan.openie.relation.instance.Pattern;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.wikipedia.WikipediaSentence;
import org.mylan.openie.relation.filter.RelationEvaluator;
import java.util.Map;


/**
 * Describe class ExtractNounPhrasesAndRelations here.
 *
 *
 * Created: Mon Mar 17 14:28:06 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ExtractNounPhrasesAndRelations {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ExtractNounPhrases start limit");
            System.exit(1);
        }
        int start = Integer.parseInt(args[0]);
        int limit = Integer.parseInt(args[1]);

        Wikipedia wikipedia = new Wikipedia();
        List<WikipediaSentence> sentences = wikipedia.getSentences(start, limit);
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        RelationExtractor relationExtractor = new TaggedPosRelationExtractor();
        FisherClassifier classifier = new FisherClassifier();
        classifier.restore();

        RelationEvaluator evaluator = new RelationEvaluator();

        for (WikipediaSentence sentence : sentences) {
            List<Pattern> arguments = argumentExtractor.getArguments(sentence.getSentence());
            System.out.println("Sentence: " + sentence.getSentence());
            System.out.print("with POS: ");
            for (PosComponent component : sentence.getSentence().getComponents()) {
                System.out.print(component.getText() + " [" + component + "] ");
            }
            System.out.println();
            System.out.println();
            System.out.print("Arguments: ");
            for (Pattern argument : arguments) {
                System.out.println(argument + " : " + argument.getComponents());
            }
            System.out.println();
            System.out.println("Relations:");

            Map<Relation, Category> evaluatedRelations = evaluator.evaluate(sentence.getSentence().toString());
            //  List<Relation> relations = relationExtractor.getRelations(sentence.getSentence());

            for (Map.Entry<Relation, Category> entry : evaluatedRelations.entrySet()) {
                Relation relation = entry.getKey();

                System.out.println(relation.getFirstArgument().toString() + " <-> " 
                                   + relation.getRelationPattern().toString() + " <-> "
                                   + relation.getLastArgument().toString());
                System.out.println("evaluated as " + entry.getValue().toString());
                System.out.println("classified as " + classifier.classify(relation).toString());
                System.out.println("IS_RELATION: " + classifier.getFisherProbability(relation, Category.IS_RELATION));
                System.out.println("NO_RELATION: " + classifier.getFisherProbability(relation, Category.IS_NO_RELATION));
                System.out.println("--------------------------------");
            }
        }
    }
}
