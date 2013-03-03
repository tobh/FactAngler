package org.mylan.openie.corpus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.mylan.openie.converter.MediawikiToPlainTextConverter;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.nlp.SentenceVisitor;
import org.mylan.openie.sql.Mysql;
import org.mylan.openie.utils.Property;
import org.mylan.openie.wikipedia.WikipediaArticle;
import org.mylan.openie.wikipedia.WikipediaSentence;

public class Wikipedia implements TextCorpus {
    private static final Logger LOGGER = Logger.getLogger(Wikipedia.class);

    private PreparedStatement getArticleTitleStatement;
    private PreparedStatement getArticleStatement;
    private PreparedStatement getArticleIdStatement;
    private PreparedStatement getArticleListStatement;
    private PreparedStatement getArticleCountStatement;
    private PreparedStatement getSentenceStatement;
    private PreparedStatement getSentenceCountStatement;
    private PreparedStatement insertSentenceStatement;
    private PreparedStatement lockSentenceStatement;
    private PreparedStatement unlockStatement;
    private PreparedStatement disableKeysSentenceStatement;
    private PreparedStatement enableKeysSentenceStatement;

    private Mysql sql;
    private MediawikiToPlainTextConverter converter;

    public Wikipedia() {
        converter = new MediawikiToPlainTextConverter();
        initSql();
    }

    public void initSql() {
        sql = new Mysql("Wikipedia");
        sql.openConnection();
        initPreparedStatements();
    }

    private void initPreparedStatements() {
        Properties sqlProperty = Property.create("sql.properties");
        String sentenceTable = sqlProperty.getProperty("wikipediaSentenceTable");

        getArticleTitleStatement = sql.createPreparedStatement("SELECT page_title FROM page WHERE page_id = ?");
        getArticleStatement = sql.createPreparedStatement("SELECT text.old_text FROM page, text WHERE page.page_title = ? AND page.page_latest = text.old_id");
        getArticleIdStatement = sql.createPreparedStatement("SELECT page.page_id FROM page WHERE page.page_title = ?");
        getArticleListStatement = sql.createPreparedStatement("SELECT page.page_id, page.page_title, text.old_text FROM page, text WHERE page.page_latest = text.old_id AND page.page_id BETWEEN ? AND ?");
        getArticleCountStatement = sql.createPreparedStatement("SELECT count(*) FROM page");
        getSentenceStatement = sql.createPreparedStatement("SELECT * FROM " + sentenceTable + " WHERE sentence_id BETWEEN ? AND ?");
        getSentenceCountStatement = sql.createPreparedStatement("SELECT count(*) FROM " + sentenceTable);
        insertSentenceStatement = sql.createPreparedStatement("INSERT INTO " + sentenceTable + " (sentence_id, page_id, text, pos) VALUES (NULL,?,?,?)");
        lockSentenceStatement = sql.createPreparedStatement("LOCK TABLES " + sentenceTable + " WRITE, page READ, text READ");
        unlockStatement = sql.createPreparedStatement("UNLOCK TABLES");
        disableKeysSentenceStatement = sql.createPreparedStatement("ALTER TABLE " + sentenceTable + " DISABLE KEYS");
        enableKeysSentenceStatement = sql.createPreparedStatement("ALTER TABLE " + sentenceTable + " ENABLE KEYS");
    }

    public void close() {
        sql.closeConnection(getArticleTitleStatement,
                            getArticleStatement,
                            getArticleIdStatement,
                            getArticleListStatement,
                            getArticleCountStatement,
                            getSentenceStatement,
                            getSentenceCountStatement,
                            insertSentenceStatement,
                            lockSentenceStatement,
                            unlockStatement,
                            disableKeysSentenceStatement,
                            enableKeysSentenceStatement);
    }

    public List<WikipediaArticle> getArticles(final int startPageId,
                                              final int limit) {
        List<WikipediaArticle> wikipediaArticles = new ArrayList<WikipediaArticle>(limit);
	try {
            getArticleListStatement.setInt(1, startPageId);
            getArticleListStatement.setInt(2, startPageId + limit - 1);
            ResultSet resultSet = getArticleListStatement.executeQuery();

            while (resultSet.next()) {
                wikipediaArticles.add(new WikipediaArticle(resultSet.getInt("page.page_id"),
                                                           resultSet.getString("page.page_title"),
                                                           converter.convert(new String(resultSet.getBytes("text.old_text")))));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wikipediaArticles;
    }

    public String getArticleTitle(int articleId) {
        String title = "";
        try {
            getArticleTitleStatement.setInt(1, articleId);
            ResultSet resultSet = getArticleTitleStatement.executeQuery();

            if (resultSet.next()) {
                title = new String(resultSet.getBytes("page_title"));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title;
    }

    public String getArticle(final String article) {
        String result = "";
	try {
	    getArticleStatement.setString(1, article);
	    ResultSet resultSet = getArticleStatement.executeQuery();
	    if (resultSet.next()) {
		result = new String(resultSet.getBytes("text.old_text"));
	    }
	    resultSet.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return result;
    }

    public String getArticleText(final String article) {
        return converter.convert(getArticle(article));
    }

    public int getArticleCount() {
        int count = -1;
        try {
            ResultSet resultSet = getArticleCountStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void accept(final SentenceVisitor visitor) {
    	int limit = 1000;
        int sentenceCount = getSentenceCount();
        for (int i = 0; i < sentenceCount; i += limit) {
            List<WikipediaSentence> sentences = getSentences(i, limit);
            for (WikipediaSentence sentence : sentences) {
                visitor.visitSentence(sentence.getSentence());
            }
            visitor.process();
        }
    }

    public List<WikipediaSentence> getSentences(int startSentenceId, int limit) {
        List<WikipediaSentence> sentences = new ArrayList<WikipediaSentence>(limit);
        try {
	    getSentenceStatement.setInt(1, startSentenceId);
	    getSentenceStatement.setInt(2, startSentenceId + limit - 1);
	    ResultSet resultSet = getSentenceStatement.executeQuery();
	    while (resultSet.next()) {
                sentences.add(new WikipediaSentence(resultSet.getInt("sentence_id"),
                                                    resultSet.getInt("page_id"),
                                                    new Sentence(new String(resultSet.getBytes("text")), new String(resultSet.getBytes("pos")))));
	    }
	    resultSet.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
        return sentences;
    }

       public List<WikipediaSentence> getSentencesWithoutPos(int startSentenceId, int limit) {
        List<WikipediaSentence> sentences = new ArrayList<WikipediaSentence>(limit);
        try {
	    getSentenceStatement.setInt(1, startSentenceId);
	    getSentenceStatement.setInt(2, startSentenceId + limit - 1);
	    ResultSet resultSet = getSentenceStatement.executeQuery();
	    while (resultSet.next()) {
                sentences.add(new WikipediaSentence(resultSet.getInt("sentence_id"),
                                                    resultSet.getInt("page_id"),
                                                    new Sentence(new String(resultSet.getBytes("text")))));
	    }
	    resultSet.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
        return sentences;
    }

    public int getSentenceCount() {
        int count = -1;
        try {
            ResultSet resultSet = getSentenceCountStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getArticleId(final String article) {
        int articleId = -1;
        try {
            getArticleIdStatement.setString(1, article);
            ResultSet resultSet = getArticleIdStatement.executeQuery();
            if (resultSet.next()) {
                articleId = resultSet.getInt("page.page_id");
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articleId;
    }

    public void insertSentences(final List<WikipediaSentence> sentences) {
        try {
            sql.setAutoCommit(false);
            for (WikipediaSentence sentence : sentences) {
                insertSentenceStatement.setLong(1, sentence.getPageId());
                insertSentenceStatement.setString(2, sentence.getSentence().toString());
                insertSentenceStatement.setString(3, sentence.getSentence().getPosString());
                insertSentenceStatement.addBatch();
            }
            insertSentenceStatement.executeBatch();
            sql.commit();
        } catch (SQLException e) {
            LOGGER.debug("Exception storing sentence: " + e);
            try {
                sql.rollback();
            } catch (SQLException rollBack) {
                LOGGER.debug("Problem during rollback(): " + rollBack);
            }
        } finally {
            sql.setAutoCommit(true);
        }
    }
}
