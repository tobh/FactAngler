package org.mylan.openie.apps;

import java.util.List;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.nlp.ArgumentExtractor;
import org.mylan.openie.nlp.BerkeleyParser;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.relation.extraction.ParsedPosRelationExtractor;
import org.mylan.openie.relation.extraction.RelationExtractor;
import org.mylan.openie.relation.filter.RelationConstraintType;
import org.mylan.openie.relation.filter.RelationFilter;
import org.mylan.openie.relation.instance.Pattern;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.wikipedia.WikipediaSentence;
import org.mylan.openie.relation.extraction.TaggedPosRelationExtractor;
import org.mylan.openie.nlp.BrownPosTagger;

/**
 * Describe class SentenceConstraintTester here.
 *
 *
 * Created: Thu Jan 10 19:02:20 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SentenceConstraintTester {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: SentenceConstraintTester start limit");
            System.exit(1);
        }
        int start = Integer.parseInt(args[0]);
        int limit = Integer.parseInt(args[1]);

        Wikipedia wikipedia = new Wikipedia();
        List<WikipediaSentence> sentences = wikipedia.getSentences(start, limit);
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        RelationExtractor extractor = new ParsedPosRelationExtractor();
        //RelationExtractor relationExtractor = new TaggedPosRelationExtractor();
        RelationFilter filter = RelationFilter.create();

        BerkeleyParser parser = new BerkeleyParser();
        BrownPosTagger tagger = new BrownPosTagger();

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

            String[] tokens = tagger.tokenize(sentence.getSentence().toString());
            StringBuilder sb = new StringBuilder();
            for (String token : tokens) {
                sb.append(token);
                sb.append(" ");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }

            System.out.println("Sentence tokenized: " + sb.toString());

            String parsedSentence = parser.generateSyntaxTree(sb.toString());
            PosComponent sentenceTree = parser.parseSyntaxTree(parsedSentence);

            System.out.print("Sentence Tree EndNodes: ");
            for (PosComponent component : sentenceTree.getEndNodes()) {
                System.out.print(component.getText() + " [" + component + "] ");
            }
            System.out.println();

            List<Relation> relations = extractor.getRelations(new Sentence(sentenceTree.getEndNodes()));
            //List<Relation> relations = relationExtractor.getRelations(sentence.getSentence());

            for (Relation relation : relations) {
                System.out.println(relation.getFirstArgument().toString() + " <-> "
                                   + relation.getRelationPattern().toString() + " <-> "
                                   + relation.getLastArgument().toString());
                System.out.println("evaluated as " + filter.getCategory(relation).toString());

                for (RelationConstraintType constraintType : RelationConstraintType.values()) {
                    System.out.print(constraintType + ": ");
                    constraintType.print(relation);
                    System.out.println();
                    }
                System.out.println("--------------------------------");
            }
        }
    }
}
