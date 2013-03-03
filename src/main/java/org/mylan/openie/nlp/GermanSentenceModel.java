package org.mylan.openie.nlp;

import com.aliasi.sentences.HeuristicSentenceModel;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import org.mylan.openie.utils.Property;
import java.util.Properties;

/**
 * Describe class GermanSentenceModel here.
 *
 *
 * Created: Tue May  6 16:54:39 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class GermanSentenceModel extends HeuristicSentenceModel {
    private static final Set<String> POSSIBLE_STOPS;
    private static final Set<String> IMPOSSIBLE_PENULTIMATES;
    private static final Set<String> IMPOSSIBLE_SENTENCE_STARTS;

    static {
        Properties analyseProperty = Property.create("analyse.properties");
        String possibleStops = analyseProperty.getProperty("germanPossibleStops");
        String impossiblePenultimates = analyseProperty.getProperty("germanImpossiblePenultimates");
        String impossibleSentenceStarts = analyseProperty.getProperty("germanImpossibleSentenceStarts");

        POSSIBLE_STOPS = new HashSet<String>();
        POSSIBLE_STOPS.addAll(Arrays.asList(possibleStops.split(" ")));
        IMPOSSIBLE_PENULTIMATES = new HashSet<String>();
        IMPOSSIBLE_PENULTIMATES.addAll(Arrays.asList(impossiblePenultimates.split(" ")));
        IMPOSSIBLE_SENTENCE_STARTS = new HashSet<String>();
        IMPOSSIBLE_SENTENCE_STARTS.addAll(POSSIBLE_STOPS);
        IMPOSSIBLE_SENTENCE_STARTS.addAll(Arrays.asList(impossibleSentenceStarts.split(" ")));
    }

    public GermanSentenceModel() {
        super(POSSIBLE_STOPS,
              IMPOSSIBLE_PENULTIMATES,
              IMPOSSIBLE_SENTENCE_STARTS,
              false,
              false);
    }
}
