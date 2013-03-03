package org.mylan.openie.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mylan.openie.ml.Category;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.nlp.BrownPosTagger;
import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.PosTag;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.relation.RelationVisitor;
import org.mylan.openie.relation.instance.Pattern;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.shared.SimpleRelation;
import org.mylan.openie.utils.Property;

/**
 * Describe class RelationSql here.
 *
 *
 * Created: Wed Jan 16 21:00:16 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationSql {
    private static final Logger LOGGER = Logger.getLogger(RelationSql.class);
    private static final BrownPosTagger TAGGER = new BrownPosTagger();

    private Mysql sql;

    private PreparedStatement getTrainingRelationStatement;
    private PreparedStatement getTrainingRelationCountStatement;
    private PreparedStatement getRelationStatement;
    private PreparedStatement getRelationCountStatement;
    private PreparedStatement classifyAndInsertRelationStatement;
    private PreparedStatement evaluateAndInsertRelationStatement;
    private PreparedStatement insertRelationStatement;
    private PreparedStatement insertTrainingRelationStatement;
    private PreparedStatement insertRelationTextCountStatement;
    private PreparedStatement insertFirstArgumentCountStatement;
    private PreparedStatement insertLastArgumentCountStatement;
    private PreparedStatement insertRelationPatternCountStatement;

    public RelationSql() {
        initSql();
        initPreparedStatements();
    }

    private void initSql() {
        sql = new Mysql("Relation");
        sql.openConnection();
    }

    private void initPreparedStatements() {
        Properties properties = Property.create("sql.properties");
        String relationTable = properties.getProperty("relationTable");
        String evaluationTable = relationTable + "_evaluated";
        String trainingRelationTable = relationTable + "_train";
        String positivRelationTable = relationTable + "_positiv";

        getTrainingRelationStatement = sql.createPreparedStatement("SELECT * FROM " + trainingRelationTable + " WHERE relation_id BETWEEN ? AND ?");
        getTrainingRelationCountStatement = sql.createPreparedStatement("SELECT count(*) FROM " + trainingRelationTable);
        getRelationStatement = sql.createPreparedStatement("SELECT sentence, first_argument, relation_pattern, second_argument, is_relation, no_relation FROM " + relationTable + " WHERE relation_id BETWEEN ? AND ?");
        getRelationCountStatement = sql.createPreparedStatement("SELECT count(*) FROM " + relationTable);
        classifyAndInsertRelationStatement = sql.createPreparedStatement("INSERT INTO " + relationTable + " (relation_id, first_argument, relation_pattern, second_argument, category, is_relation, no_relation, sentence) VALUES (NULL,?,?,?,?,?,?,?)");
        evaluateAndInsertRelationStatement = sql.createPreparedStatement("INSERT INTO " + evaluationTable + " (relation_id, first_argument, relation_pattern, second_argument, category, evaluated_category, is_relation, no_relation, sentence) VALUES (NULL,?,?,?,?,?,?,?,?)");
        insertRelationStatement = sql.createPreparedStatement("INSERT INTO " + positivRelationTable + " (relation_id, first_argument, relation_pattern, second_argument, sentence) VALUES (NULL,?,?,?,?)");
        insertTrainingRelationStatement = sql.createPreparedStatement("INSERT INTO " + trainingRelationTable + " (relation_id, sentence_text, sentence_pos, first_argument_text, relation_pattern_text, second_argument_text, first_argument_positions, relation_pattern_positions, second_argument_positions, category) VALUES (NULL,?,?,?,?,?,?,?,?,?)");
        insertRelationTextCountStatement = sql.createPreparedStatement("INSERT INTO relation_count (id, text, amount) VALUES (NULL,?,?)");
        insertFirstArgumentCountStatement = sql.createPreparedStatement("INSERT INTO first_argument_count (id, text, amount) VALUES (NULL,?,?)");
        insertLastArgumentCountStatement = sql.createPreparedStatement("INSERT INTO last_argument_count (id, text, amount) VALUES (NULL,?,?)");
        insertRelationPatternCountStatement = sql.createPreparedStatement("INSERT INTO relation_pattern_count (id, text, amount) VALUES (NULL,?,?)");
    }

    public void close() {
        sql.closeConnection(getTrainingRelationStatement,
                            getTrainingRelationCountStatement,
                            getRelationStatement,
                            getRelationCountStatement,
                            classifyAndInsertRelationStatement,
                            evaluateAndInsertRelationStatement,
                            insertRelationStatement,
                            insertTrainingRelationStatement,
                            insertRelationTextCountStatement,
                            insertFirstArgumentCountStatement,
                            insertLastArgumentCountStatement,
                            insertRelationPatternCountStatement);
    }

    public Map<Relation, Category> getTrainingRelations(final int startRelationId, final int limit) {
        Map<Relation, Category> relations = new HashMap<Relation, Category>();
        try {
            getTrainingRelationStatement.setInt(1, startRelationId);
            getTrainingRelationStatement.setInt(2, startRelationId + limit - 1);
            ResultSet resultSet = getTrainingRelationStatement.executeQuery();
            while (resultSet.next()) {
                List<PosComponent> sentenceComponents = buildPosComponents(TAGGER.tokenize(resultSet.getString("sentence_text")),
                                                                           resultSet.getString("sentence_pos").split(" "));

                Sentence sentence = new Sentence(sentenceComponents);
                Pattern firstArgument = new Pattern(sentence, getComponentsFromString(sentence, resultSet.getString("first_argument_positions")));
                Pattern relationPattern = new Pattern(sentence, getComponentsFromString(sentence, resultSet.getString("relation_pattern_positions")));
                Pattern lastArgument = new Pattern(sentence, getComponentsFromString(sentence, resultSet.getString("second_argument_positions")));
		relations.put(new Relation(sentence,
					   firstArgument,
                                           relationPattern,
                                           lastArgument),
                              Category.valueOf(resultSet.getString("category")));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return relations;
    }

    public int getTrainingRelationsCount() {
        int count = -1;
        try {
            ResultSet resultSet = getTrainingRelationCountStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void accept(final RelationVisitor visitor, int limit) {
        int relationCount = getRelationsCount();
        for (int i = 0; i < relationCount; i += limit) {
            List<SimpleRelation> relations = getRelations(i, limit);
            for (SimpleRelation relation : relations) {
                visitor.visitRelation(relation);
            }
            LOGGER.info((i + limit) + " / " + relationCount + " relations visited");
        }
    }

    public List<SimpleRelation> getRelations(final int startRelationId, final int limit) {
        List<SimpleRelation> relations = new ArrayList<SimpleRelation>(limit);
        try {
            getRelationStatement.setInt(1, startRelationId);
            getRelationStatement.setInt(2, startRelationId + limit - 1);
            ResultSet resultSet = getRelationStatement.executeQuery();
            while (resultSet.next()) {
                relations.add(new SimpleRelation(resultSet.getString("first_argument"),
                                                 resultSet.getString("relation_pattern"),
                                                 resultSet.getString("second_argument"),
                                                 resultSet.getString("sentence"),
                                                 resultSet.getInt("is_relation"),
                                                 resultSet.getInt("no_relation")));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return relations;
    }

    public int getRelationsCount() {
        int count = -1;
        try {
            ResultSet resultSet = getRelationCountStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void insert(final List<Relation> relations, final FisherClassifier classifier) {
        try {
            sql.setAutoCommit(false);
            for (Relation relation : relations) {
            	classifyAndInsertRelationStatement.setString(1, relation.getFirstArgument().toString());
            	classifyAndInsertRelationStatement.setString(2, relation.getRelationPattern().toString());
            	classifyAndInsertRelationStatement.setString(3, relation.getLastArgument().toString());
            	classifyAndInsertRelationStatement.setString(4, classifier.classify(relation).toString());
            	classifyAndInsertRelationStatement.setDouble(5, classifier.getFisherProbability(relation, Category.IS_RELATION));
            	classifyAndInsertRelationStatement.setDouble(6, classifier.getFisherProbability(relation, Category.IS_NO_RELATION));
            	classifyAndInsertRelationStatement.setString(7, relation.getSentence().toString());
            	classifyAndInsertRelationStatement.addBatch();
            }
            classifyAndInsertRelationStatement.executeBatch();
            sql.commit();
        } catch (SQLException e) {
            LOGGER.error("Exception storing Relation: " + e);
            try {
                sql.rollback();
            } catch (SQLException rollBack) {
                LOGGER.error("Problem during rollback(): " + rollBack);
            }
        } finally {
            sql.setAutoCommit(true);
        }
    }

    public void insertEvaluated(final Map<Relation, Category> evaluatedRelations, final FisherClassifier classifier) {
        try {
            sql.setAutoCommit(false);
            for (Map.Entry<Relation, Category> entry : evaluatedRelations.entrySet()) {
            	evaluateAndInsertRelationStatement.setString(1, entry.getKey().getFirstArgument().toString());
            	evaluateAndInsertRelationStatement.setString(2, entry.getKey().getRelationPattern().toString());
            	evaluateAndInsertRelationStatement.setString(3, entry.getKey().getLastArgument().toString());
            	evaluateAndInsertRelationStatement.setString(4, classifier.classify(entry.getKey()).toString());
            	evaluateAndInsertRelationStatement.setString(5, entry.getValue().toString());
            	evaluateAndInsertRelationStatement.setDouble(6, classifier.getFisherProbability(entry.getKey(), Category.IS_RELATION));
            	evaluateAndInsertRelationStatement.setDouble(7, classifier.getFisherProbability(entry.getKey(), Category.IS_NO_RELATION));
            	evaluateAndInsertRelationStatement.setString(8, entry.getKey().getSentence().toString());
            	evaluateAndInsertRelationStatement.addBatch();
            }
            evaluateAndInsertRelationStatement.executeBatch();
            sql.commit();
        } catch (SQLException e) {
            LOGGER.error("Exception storing Relation: " + e);
            try {
                sql.rollback();
            } catch (SQLException rollBack) {
                LOGGER.error("Problem during rollback(): " + rollBack);
            }
        } finally {
            sql.setAutoCommit(true);
        }
    }

    public void insertTrainingRelations(final Map<Relation, Category> relationsWithCategory) {
        try {
            sql.setAutoCommit(false);
            for (Map.Entry<Relation, Category> entry : relationsWithCategory.entrySet()) {
                insertTrainingRelationStatement.setString(1, entry.getKey().getSentence().toString());
                insertTrainingRelationStatement.setString(2, entry.getKey().getSentence().getPosString());
                insertTrainingRelationStatement.setString(3, entry.getKey().getFirstArgument().toString());
                insertTrainingRelationStatement.setString(4, entry.getKey().getRelationPattern().toString());
                insertTrainingRelationStatement.setString(5, entry.getKey().getLastArgument().toString());
                insertTrainingRelationStatement.setString(6, createSimplePositionString(entry.getKey().getFirstArgument().getComponentPositions()));
                insertTrainingRelationStatement.setString(7, createSimplePositionString(entry.getKey().getRelationPattern().getComponentPositions()));
                insertTrainingRelationStatement.setString(8, createSimplePositionString(entry.getKey().getLastArgument().getComponentPositions()));
                insertTrainingRelationStatement.setString(9, entry.getValue().toString());
                insertTrainingRelationStatement.addBatch();
            }
            insertTrainingRelationStatement.executeBatch();
            sql.commit();
        } catch (SQLException e) {
            LOGGER.error("Exception storing Relation: " + e);
            try {
                sql.rollback();
            } catch (SQLException rollBack) {
                LOGGER.error("Problem during rollback(): " + rollBack);
            }
        } finally {
            sql.setAutoCommit(true);
        }
    }

    public void insertRelationTextCounts(final Map<String, Integer> counts) {
        insertCounts(insertRelationTextCountStatement, counts);
    }

    public void insertFirstArgumentCounts(final Map<String, Integer> counts) {
        insertCounts(insertFirstArgumentCountStatement, counts);
    }

    public void insertLastArgumentCounts(final Map<String, Integer> counts) {
        insertCounts(insertLastArgumentCountStatement, counts);
    }

    public void insertRelationPatternCounts(final Map<String, Integer> counts) {
        insertCounts(insertRelationPatternCountStatement, counts);
    }

    private void insertCounts(final PreparedStatement statement, final Map<String, Integer> counts) {
        try {
            sql.setAutoCommit(false);
            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                statement.setString(1, entry.getKey());
                statement.setInt(2, entry.getValue());
                statement.addBatch();
            }
            statement.executeBatch();
            sql.commit();
        } catch (SQLException e) {
            LOGGER.error("Exception storing counts: " + e);
            try {
                sql.rollback();
            } catch (SQLException rollBack) {
                LOGGER.error("Problem during rollback(): " + rollBack);
            }
        }
        finally {
            sql.setAutoCommit(true);
        }
    }

    private String createSimplePositionString(final List<Integer> positions) {
        StringBuilder sb = new StringBuilder();
        for (Integer position : positions) {
            sb.append(position);
            sb.append(" ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private List<PosComponent> buildPosComponents(String[] text, String[] pos) {
        List<PosComponent> components = new ArrayList<PosComponent>();
        for (int i = 0; i < text.length; ++i) {
            components.add(new PosTag(pos[i], text[i]));
        }
        return components;
    }

    private List<PosComponent> getComponentsFromString(final Sentence sentence, final String positionString) {
        String[] positions = positionString.split(" ");
        List<PosComponent> sentenceComponents = sentence.getComponents();
        List<PosComponent> components = new ArrayList<PosComponent>(positions.length);
        for (String position : positions) {
	    if (!position.equals("")) {
		components.add(sentenceComponents.get(Integer.parseInt(position)));
	    }
        }
        return components;
    }
}
