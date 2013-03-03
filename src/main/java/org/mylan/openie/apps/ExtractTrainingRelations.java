package org.mylan.openie.apps;

import java.util.Map;

import org.mylan.openie.ml.Category;
import org.mylan.openie.nlp.BerkeleyParser;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.relation.filter.RelationEvaluator;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.sql.RelationSql;

/**
 * Describe class ExtractTrainingRelations here.
 *
 *
 * Created: Wed Jan 16 21:51:16 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class ExtractTrainingRelations {
    public static void main(String[] args) {
        RelationSql relationSql = new RelationSql();
        RelationEvaluator evaluator = new RelationEvaluator();
        for (PosComponent sentence : new BerkeleyParser().parseFile()) {
            Map<Relation, Category> evaluatedRelations = evaluator.evaluate(sentence);
            relationSql.insertTrainingRelations(evaluatedRelations);
        }
        relationSql.close();
    }
}
