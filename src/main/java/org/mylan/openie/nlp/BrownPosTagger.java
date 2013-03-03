package org.mylan.openie.nlp;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.Streams;
import org.apache.log4j.Logger;
import org.mylan.openie.utils.Property;

/**
 * Describe class BrownPosTagger here.
 *
 *
 * Created: Thu Jan 17 17:17:35 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class BrownPosTagger {
    private static TokenizerFactory TOKENIZER_FACTORY = new IndoEuropeanTokenizerFactory();
    private static final Logger LOGGER = Logger.getLogger(BrownPosTagger.class);
    private static HmmDecoder decoder;

    public BrownPosTagger() {
        Properties analyseProperty = Property.create("analyse.properties");
        String language = analyseProperty.getProperty("language");
        String hmmFile = "";
        if (language.equals("german")) {
            hmmFile = analyseProperty.getProperty(language + "HmmModel");
        } else {
            hmmFile = analyseProperty.getProperty(language + "HmmModel");
        }

        ObjectInputStream objectIn = null;
        HiddenMarkovModel hmm = null;
        try {
            FileInputStream fileIn = new FileInputStream(hmmFile);
            objectIn = new ObjectInputStream(fileIn);
            hmm = (HiddenMarkovModel) objectIn.readObject();
        } catch (Exception e) {
            e.getMessage();
            LOGGER.fatal("Couldn't initialize HMM");
        } finally {
            Streams.closeInputStream(objectIn);
        }

        decoder = new HmmDecoder(hmm);
    }

    public List<PosComponent> annotate(final String sentence) {
        String[] tokens = tokenize(sentence);
        String[] tags = decoder.firstBest(tokens);

        List<PosComponent> sentenceWithTags = new ArrayList<PosComponent>(tokens.length);
        for(int i = 0; i < tokens.length; ++i) {
            sentenceWithTags.add(new PosTag(tags[i], tokens[i]));
        }
        return sentenceWithTags;
    }

    public String[] tokenize(final String text) {
        char[] charSentence = text.toCharArray();
        Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(charSentence, 0, charSentence.length);
        String[] tokens = tokenizer.tokenize();
        LinkedList<String> tokenList = new LinkedList<String>();
        for (int i = 0; i < tokens.length; ++i) {
            String token = tokens[i];
            if (i < tokens.length - 2 &&
                tokens[i + 1].equals("'") &&
                tokens[i + 2].equals("s")) {
                token += "'s";
                i += 2;
            }
            tokenList.add(token);
        }
        return tokenList.toArray(new String[0]);
    }
}
