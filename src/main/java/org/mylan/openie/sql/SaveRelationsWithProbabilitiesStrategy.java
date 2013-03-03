package org.mylan.openie.sql;

import java.util.List;

import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.relation.ProcessRelationStrategy;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class SaveRelationsWithProbabilitiesStrategy here.
 *
 *
 * Created: Mon Mar 31 16:56:18 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SaveRelationsWithProbabilitiesStrategy extends ProcessRelationStrategy {
    private final FisherClassifier classifier;
    private final RelationSql relationSql;

    public SaveRelationsWithProbabilitiesStrategy(final FisherClassifier classifier) {
        this.classifier = classifier;
        relationSql = new RelationSql();
    }

    public void process(final List<Relation> relations) {
        relationSql.insert(relations, classifier);
    }
}
