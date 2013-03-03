package org.mylan.openie.relation.extraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mylan.openie.nlp.ArgumentExtractor;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.nlp.SentenceMapper;
import org.mylan.openie.relation.instance.Pattern;

/**
 * Describe class RelationExtractor here.
 *
 *
 * Created: Thu Jan 10 23:00:48 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ParsedPosRelationExtractor extends RelationExtractor {
    private final SentenceMapper mapper;
    private final ArgumentExtractor extractor;

    public ParsedPosRelationExtractor() {
        super();
        mapper = new SentenceMapper();
        extractor = new ArgumentExtractor();
    }

    protected List<Pattern> getArguments(final Sentence sentence) {
        Sentence sentenceBrownPos = new Sentence(sentence.toString());
        Map<PosComponent, List<PosComponent>> mapping = mapper.mapPosComponents(sentenceBrownPos.getComponents(), sentence.getComponents());
        List<Pattern> argumentsBrownPos = extractor.getArguments(sentenceBrownPos);
        List<Pattern> argumentsPennTree = new ArrayList<Pattern>(argumentsBrownPos.size());
        for (Pattern pattern : argumentsBrownPos) {
            List<PosComponent> components = pattern.getComponents();
            List<PosComponent> pennTreePosPatternComponents = new LinkedList<PosComponent>();
            for (PosComponent component : components) {
                pennTreePosPatternComponents.addAll(mapping.get(component));
            }
            argumentsPennTree.add(new Pattern(sentence, pennTreePosPatternComponents));
        }
        return argumentsPennTree;
    }

    protected Pattern getRelationPattern(final Sentence sentence, final Pattern firstArgument, final Pattern lastArgument) {
        List<PosComponent> sentenceComponents = sentence.getComponents();
        int start = sentenceComponents.indexOf(firstArgument.getComponents().get(firstArgument.getComponents().size() - 1)) + 1;
        int end = sentenceComponents.indexOf(lastArgument.getComponents().get(0));
        List<PosComponent> components = new LinkedList<PosComponent>();
        for (int i = start; i < end; ++i) {
            if (sentenceComponents.get(i).isEndNode()) {
                components.add(sentenceComponents.get(i));
            }
        }
        return new Pattern(sentence, components);
    }
}
