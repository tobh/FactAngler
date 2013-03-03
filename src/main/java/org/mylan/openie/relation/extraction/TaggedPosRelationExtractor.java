package org.mylan.openie.relation.extraction;

import java.util.ArrayList;
import java.util.List;

import org.mylan.openie.nlp.ArgumentExtractor;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.relation.instance.Pattern;

/**
 * Describe class TaggedPosRelationExtractor here.
 *
 *
 * Created: Tue Feb  5 12:54:41 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class TaggedPosRelationExtractor extends RelationExtractor {
    private final ArgumentExtractor argumentExtractor;

    public TaggedPosRelationExtractor() {
        super();
        argumentExtractor = new ArgumentExtractor();
    }

    protected List<Pattern> getArguments(final Sentence sentence) {
        return argumentExtractor.getArguments(sentence);
    }

    protected Pattern getRelationPattern(final Sentence sentence, final Pattern firstArgument, final Pattern lastArgument) {
        int start = firstArgument.getComponentPositions().get(firstArgument.getComponentPositions().size() - 1) + 1;
        int end = lastArgument.getComponentPositions().get(0);

        int componentCount = end - start;
        componentCount = componentCount < 0 ? 0 : componentCount;
        List<PosComponent> components = new ArrayList<PosComponent>(componentCount);
        for (int i = start; i < end; ++i) {
            components.add(sentence.getComponents().get(i));
        }

        return new Pattern(sentence, components);
    }
}
