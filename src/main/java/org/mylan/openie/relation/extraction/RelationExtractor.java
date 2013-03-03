package org.mylan.openie.relation.extraction;

import java.util.LinkedList;
import java.util.List;

import org.mylan.openie.ml.Category;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.ml.Item;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.relation.instance.Pattern;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class RelationExtractor here.
 *
 *
 * Created: Thu Jan 10 23:00:48 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class RelationExtractor {
    public final List<Relation> getRelations(final Sentence sentence) {
	return getRelations(sentence,
			    new FisherClassifier() {
				@SuppressWarnings("unused")
                                @Override
				public Category classify(final Item item) {
				    return Category.IS_RELATION;
				}
			    });
    }

    public final List<Relation> getRelations(final Sentence sentence, final FisherClassifier classifier) {
        List<Pattern> arguments = getArguments(sentence);
        List<Relation> relations = new LinkedList<Relation>();

        if (arguments.isEmpty() || arguments.size() > 50) {
            return relations;
        }

        for (int first = 0; first < arguments.size() - 1; ++first) {
            for (int last = first + 1; last < arguments.size(); ++last) {
                Pattern firstArgument = arguments.get(first);
                Pattern lastArgument = arguments.get(last);
                Pattern relationPattern = getRelationPattern(sentence, firstArgument, lastArgument);
		Relation relation = new Relation(sentence, firstArgument, relationPattern, lastArgument);
		if (classifier.classify(relation).equals(Category.IS_RELATION)) {
		    relations.add(relation);
		}
            }
        }
        return relations;
    }

    protected abstract List<Pattern> getArguments(final Sentence sentence);
    protected abstract Pattern getRelationPattern(final Sentence sentence, final Pattern firstArgument, final Pattern lastArgument);
}
