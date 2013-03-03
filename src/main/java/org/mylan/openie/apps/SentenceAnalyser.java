package org.mylan.openie.apps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.mylan.openie.ml.Category;
import org.mylan.openie.nlp.BerkeleyParser;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.relation.extraction.ParsedPosRelationExtractor;
import org.mylan.openie.relation.extraction.RelationExtractor;
import org.mylan.openie.relation.filter.RelationConstraintType;
import org.mylan.openie.relation.filter.RelationFilter;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class SentenceAnalyser here.
 *
 *
 * Created: Wed Dec 12 17:12:18 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SentenceAnalyser {
    private final RelationFilter filter;
    private final RelationExtractor extractor;
    private final List<PosComponent> sentences;

    public SentenceAnalyser() {
        this(RelationFilter.create());
    }

    public SentenceAnalyser(final RelationFilter filter) {
        this.filter = filter;
        extractor = new ParsedPosRelationExtractor();
        sentences = new BerkeleyParser().parseFile();
    }

    public void printRelations() {
        printRelations(Category.values());
    }

    public void printRelations(final Category ... categories) {
        Scanner scanner = new Scanner(System.in);
        for (PosComponent sentence : sentences) {
            System.out.println(sentence.getText());
            System.out.println(sentence.getTextWithPos());
            printRelationsOfSentence(sentence, categories);

            while (!scanner.hasNextLine()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
            scanner.nextLine();

            System.out.println("-----------------------------------------------\n");
        }
    }

    private void printRelationsOfSentence(final PosComponent sentence, final Category ... categories) {
        for (Relation relation : extractor.getRelations(new Sentence(sentence.getEndNodes()))) {
            Category category = filter.getCategory(relation);
            if (Arrays.asList(categories).contains(category)) {
                category.print(relation);
                for (RelationConstraintType constraintType : RelationConstraintType.values()) {
                    System.out.print(constraintType + ": ");
                    constraintType.print(relation);
                    System.out.print(" | ");
                }
                System.out.println();
            }
        }
    }

    public List<Relation> getRelationsFromFile() {
        return getRelationsFromFile(Category.IS_RELATION);
    }

    public List<Relation> getRelationsFromFile(final Category ... categories) {
        List<Relation> relations = new ArrayList<Relation>();
        for (PosComponent sentence : sentences) {
            List<Relation> relationsAllCategories = extractor.getRelations(new Sentence(sentence.getEndNodes()));
            for (Relation relation : relationsAllCategories) {
                if (Arrays.asList(categories).contains(filter.getCategory(relation))) {
                    relations.add(relation);
                }
            }
        }
        return relations;
    }

    public static void main(String[] args) {
        SentenceAnalyser analyser = new SentenceAnalyser();
        if (args.length == 1) {
            if (args[0].equals("is_relation")) {
                analyser.printRelations(Category.IS_RELATION);
            } else {
                analyser.printRelations(Category.IS_NO_RELATION);
            }
        } else {
            analyser.printRelations();
        }
    }
}
