package org.mylan.openie.apps;

import java.util.Map;

import org.mylan.openie.ml.Category;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.sql.RelationSql;

/**
 * Describe class Train here.
 *
 *
 * Created: Mon Jan 28 23:07:34 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Train {
    public static void main(String[] args) {
        RelationSql sql = new RelationSql();
        int relationCount = sql.getTrainingRelationsCount();
        FisherClassifier classifier = new FisherClassifier();
        int limit = 10000;
        for (int i = 0; i < relationCount; i += limit) {
            Map<Relation, Category> relationsWithCategory = sql.getTrainingRelations(i, limit);
            for (Map.Entry<Relation, Category> entry : relationsWithCategory.entrySet()) {
                classifier.train(entry.getKey(), entry.getValue());
            }
        }
        classifier.save();
    }
}
