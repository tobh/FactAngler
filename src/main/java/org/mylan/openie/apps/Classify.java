package org.mylan.openie.apps;

import java.util.List;

import org.mylan.openie.ml.Category;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.relation.extraction.RelationExtractor;
import org.mylan.openie.relation.extraction.TaggedPosRelationExtractor;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.utils.ConsoleInputListener;
import org.mylan.openie.utils.Worker;

/**
 * Describe class Classify here.
 *
 *
 * Created: Thu Jan  3 17:06:30 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Classify {
    public static void main(String[] args) {
        final FisherClassifier classifier = new FisherClassifier();
        classifier.restore();
        final RelationExtractor extractor = new TaggedPosRelationExtractor();

        ConsoleInputListener in = new ConsoleInputListener("Write sentence to analyse and classify");
        in.add(new Worker() {
                protected void execute(final String input) {
                    Sentence sentence = new Sentence(input);
                    System.out.println("Sentence: " + sentence.getComponents());
                    List<Relation> relations = extractor.getRelations(sentence);
                    System.out.println("Found " + relations.size() + " relations");

                    for (Relation relation : relations) {
                        Category classifiedCategory = classifier.classify(relation);
                        double probabilityIsRelation = classifier.getFisherProbability(relation, Category.IS_RELATION);
                        double probabilityNoRelation = classifier.getFisherProbability(relation, Category.IS_NO_RELATION);

                        classifiedCategory.print(relation);
                        System.out.println("Kategorie: " + classifiedCategory);
                        System.out.println("Probability IS_RELATION: " + probabilityIsRelation);
                        System.out.println("Probability IS_NO_RELATION: " + probabilityNoRelation);
                    }
                }
            }
            );
        in.start();
    }
}
