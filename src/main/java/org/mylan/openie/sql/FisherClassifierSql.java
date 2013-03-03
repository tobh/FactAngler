package org.mylan.openie.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mylan.openie.ml.Category;
import org.mylan.openie.ml.Feature;
import org.mylan.openie.ml.FeatureCount;
import org.mylan.openie.ml.FeatureType;
import org.mylan.openie.utils.IntValue;
import org.mylan.openie.utils.Property;

/**
 * Describe class FisherClassifierSql here.
 *
 *
 * Created: Thu Jan 24 18:31:00 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class FisherClassifierSql {
    private static final Logger LOGGER = Logger.getLogger(FisherClassifierSql.class);

    private Mysql sql;

    private PreparedStatement getFeatureCountStatement;
    private PreparedStatement getCategoryCountStatement;
    private PreparedStatement insertFeatureCountStatement;
    private PreparedStatement insertCategoryCountStatement;

    public FisherClassifierSql() {
        initSql();
        initPreparedStatements();
    }

    private void initSql() {
        sql = new Mysql("Classifier");
        sql.openConnection();
    }

    private void initPreparedStatements() {
        Properties properties = Property.create("sql.properties");

        String classifierFeatureTable = properties.getProperty("classifierFeatureTable");
        String classifierCategoryTable = properties.getProperty("classifierCategoryTable");

        getFeatureCountStatement = sql.createPreparedStatement("SELECT * FROM " + classifierFeatureTable);
        getCategoryCountStatement = sql.createPreparedStatement("SELECT * FROM " + classifierCategoryTable);
        insertFeatureCountStatement = sql.createPreparedStatement("INSERT INTO " + classifierFeatureTable + " (feature_token, feature_type, category, count) VALUES (?,?,?,?)");
        insertCategoryCountStatement = sql.createPreparedStatement("INSERT INTO " + classifierCategoryTable + " (category, count) VALUES (?,?)");
    }

    public void close() {
        sql.closeConnection(getFeatureCountStatement,
                            getCategoryCountStatement,
                            insertFeatureCountStatement,
                            insertCategoryCountStatement);
    }

    public Map<Feature, FeatureCount> getFeatureCount() {
        Map<Feature, FeatureCount> featureCounts = new HashMap<Feature, FeatureCount>();
        try {
            ResultSet resultSet = getFeatureCountStatement.executeQuery();
            while (resultSet.next()) {
                Feature feature = new Feature(resultSet.getString("feature_token"),
                                              FeatureType.valueOf(resultSet.getString("feature_type")));
                FeatureCount featureCount = featureCounts.get(feature);
                if (featureCount == null) {
                    featureCount = new FeatureCount(feature);
                    featureCounts.put(feature, featureCount);
                }
                featureCount.setCount(Category.valueOf(resultSet.getString("category")), resultSet.getInt("count"));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return featureCounts;
    }

    public Map<Category, IntValue> getCategoryCount() {
        Map<Category, IntValue> categoryCounts = new HashMap<Category, IntValue>();
        try {
            ResultSet resultSet = getCategoryCountStatement.executeQuery();
            while (resultSet.next()) {
                categoryCounts.put(Category.valueOf(resultSet.getString("category")), new IntValue(resultSet.getInt("count")));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryCounts;
    }

    public void insertFeatureCount(final List<FeatureCount> featureCounts) {
        try {
            sql.setAutoCommit(false);
            for (FeatureCount featureCount : featureCounts) {
                for (Category category : Category.values()) {
                    insertFeatureCountStatement.setString(1, featureCount.getFeature().getToken());
                    insertFeatureCountStatement.setString(2, featureCount.getFeature().getType().toString());
                    insertFeatureCountStatement.setString(3, category.toString());
                    insertFeatureCountStatement.setLong(4, featureCount.getCount(category));
                    insertFeatureCountStatement.addBatch();
                }
            }
            insertFeatureCountStatement.executeBatch();
            sql.commit();
        } catch (SQLException e) {
            LOGGER.debug("Exception storing FeatureCount: " + e);
            try {
                sql.rollback();
            } catch (SQLException rollBack) {
                LOGGER.debug("Problem during rollback(): " + rollBack);
            }
        } finally {
            sql.setAutoCommit(true);
        }
    }

    public void insertCategoryCount(final Category category, final int count) {
        try {
            insertCategoryCountStatement.setString(1, category.toString());
            insertCategoryCountStatement.setLong(2, count);
            insertCategoryCountStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.debug("Exception storing categoryCount: " + e);
        }
    }
}
