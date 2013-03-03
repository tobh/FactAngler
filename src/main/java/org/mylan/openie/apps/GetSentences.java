package org.mylan.openie.apps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.nlp.BrownPosTagger;
import org.mylan.openie.wikipedia.WikipediaSentence;

/**
 * usage : GetSentence count
 * Count is circa the amount of sentences that get printed to stdout.
 * This class is used to extract training set to use it with the berkeley
 * parser. Because of this, the extracted sentences get also tokenized. The
 * sentences are randomly extracted from a wikipedia database table. To get a
 * few connected sentences for each randomly chosen sentence the following 10
 * sentences are also extracted.
 *
 * Requirements: sql.properties
 *               annotate.properties
 *               HMM file for BrownPosTagger
 *
 * Created: Thu Dec  6 13:24:43 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class GetSentences {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("usage: GetSentences count");
            System.exit(1);
        }

	Wikipedia wikipedia = new Wikipedia();
        int sentenceCount = wikipedia.getSentenceCount();
        Random random = new Random();
        Set<Integer> sentenceIds = new HashSet<Integer>();

        int count = Integer.parseInt(args[0]);
        while (sentenceIds.size() <= count) {
            int sentenceId = random.nextInt(sentenceCount) + 1;
            for (int i = 0; i < 10; ++i) {
                if (sentenceId + i <= sentenceCount) {
                    sentenceIds.add(sentenceId + i);
                }
            }
        }

        List<Integer> sortedSentenceIds =new ArrayList<Integer>(sentenceIds);
        Collections.sort(sortedSentenceIds);

        BrownPosTagger tagger = new BrownPosTagger();
        for (Integer id : sortedSentenceIds) {
            List<WikipediaSentence> sentences = wikipedia.getSentences(id - 1, 1);
            String sentence = sentences.get(0).toString();
            if (!sentence.contains("\n")) {
                String[] tokens = tagger.tokenize(sentence);
                StringBuilder sb = new StringBuilder();
                for (String token : tokens) {
                    sb.append(token);
                    sb.append(" ");
                }
                if (tokens.length > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                System.out.println(sb.toString());
            }
        }
        wikipedia.close();
    }
}

