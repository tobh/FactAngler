package org.mylan.openie.relation.filter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.nlp.SentenceMapper;
import org.mylan.openie.relation.instance.Pattern;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class RelationConverter here.
 *
 *
 * Created: Fri Jan 18 00:01:14 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationConverter {
    private final SentenceMapper mapper;

    public RelationConverter() {
        mapper = new SentenceMapper();
    }

    public Relation convert(final Relation relation) {
        List<PosComponent> sentenceTreePos = relation.getSentence().getComponents();
        Sentence sentence = new Sentence(relation.getSentence().toString());

        Map<PosComponent, List<PosComponent>> mapping = mapper.mapPosComponents(sentenceTreePos, sentence.getComponents());
        Pattern firstArgument = buildPattern(sentence, relation.getFirstArgument().getComponents(), mapping);
        Pattern relationPattern = buildPattern(sentence, relation.getRelationPattern().getComponents(), mapping);
        Pattern lastArgument = buildPattern(sentence, relation.getLastArgument().getComponents(), mapping);
        return new Relation(sentence, firstArgument, relationPattern, lastArgument);
    }

    private Pattern buildPattern(final Sentence sentence,
                                 final List<PosComponent> oldPatternComponents,
                                 final Map<PosComponent, List<PosComponent>> mapping) {
        List<PosComponent> newComponents = new LinkedList<PosComponent>();
        for (PosComponent component : oldPatternComponents) {
            newComponents.addAll(mapping.get(component));
        }
        return new Pattern(sentence, newComponents);
    }
}
