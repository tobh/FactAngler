package org.mylan.openie.sql;

import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.nlp.SentenceVisitor;
import org.mylan.openie.relation.filter.RelationEvaluator;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.ml.Category;
import java.util.Map;
import java.util.HashMap;
import org.mylan.openie.ml.FisherClassifier;

/**
 * Describe class EvaluateSentenceVisitor here.
 *
 *
 * Created: Sat May  3 13:17:42 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class EvaluateSentenceVisitor extends SentenceVisitor {
    private FisherClassifier classifier;
    private RelationEvaluator evaluator;
    private RelationSql sql;
    private Map<Relation, Category> evaluatedRelations;

    public EvaluateSentenceVisitor(final FisherClassifier classifier) {
        super();
        this.classifier = classifier;
        evaluator = new RelationEvaluator();
        sql = new RelationSql();
        evaluatedRelations = new HashMap<Relation, Category>();
    }

    public void visitSentence(final Sentence sentence) {
        Map<Relation, Category> x = evaluator.evaluate(sentence.toString());
        evaluatedRelations.putAll(x);
    }

    public void process() {
        sql.insertEvaluated(evaluatedRelations, classifier);
        evaluatedRelations = new HashMap<Relation, Category>();
    }
}
