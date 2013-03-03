package org.mylan.openie.nlp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.chunk.Chunk;
import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.sentences.SentenceChunker;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import java.util.Properties;
import org.mylan.openie.utils.Property;

/**
 * Describe class SentenceExtractor here.
 *
 *
 * Created: Fri Apr 18 17:45:19 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SentenceExtractor {
    private TokenizerFactory tokenizerFactory;
    private SentenceModel sentenceModel;
    private SentenceChunker sentenceChunker;
    private Set<String> additionalForbittenSentenceStart;

    public SentenceExtractor() {
    	additionalForbittenSentenceStart = new HashSet<String>();
    	additionalForbittenSentenceStart.add("*");
    	additionalForbittenSentenceStart.add("—");
    	additionalForbittenSentenceStart.add("-");

        Properties analyseProperty = Property.create("analyse.properties");
        String language = analyseProperty.getProperty("language");

        if (language.equals("german")) {
            sentenceModel = new GermanSentenceModel();
        } else {
            sentenceModel = new IndoEuropeanSentenceModel();
        }

        tokenizerFactory = new IndoEuropeanTokenizerFactory();
        sentenceChunker = new SentenceChunker(tokenizerFactory, sentenceModel);
    }

    public List<Sentence> extract(final String text) {
    	Set<Chunk> sentenceChunks = sentenceChunker.chunk(text.toCharArray(), 0, text.length()).chunkSet();
    	List<Sentence> sentences = new ArrayList<Sentence>(sentenceChunks.size());
        for (Chunk sentenceChunk : sentenceChunks) {
            int start = sentenceChunk.start();
            int end = sentenceChunk.end();
            while (end > start + 1 && additionalForbittenSentenceStart.contains(text.substring(start, start + 1))) {
                ++start;
            }

            String sentenceText = text.substring(start, end);
            if (!sentenceText.startsWith(",") && !sentenceText.contains("\n")) {
                sentences.add(new Sentence(sentenceText));
            }
        }
        return sentences;
    }
}
