package org.mylan.openie.sql;

import java.util.List;

import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.relation.ProcessRelationStrategy;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class SaveRelationStrategy here.
 *
 *
 * Created: Mon Mar 31 16:51:45 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SaveRelationStrategy extends ProcessRelationStrategy {
    private final RelationSql relationSql;
    private final FisherClassifier classifier;

    public SaveRelationStrategy(final FisherClassifier classifier) {
        super();
        this.classifier = classifier;
        this.relationSql = new RelationSql();
    }

    public void process(final List<Relation> relations) {
        relationSql.insert(relations, classifier);
    }
}
