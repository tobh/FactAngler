package org.mylan.openie.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.mylan.openie.ml.Category;
import org.mylan.openie.ml.FisherClassifier;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.shared.Result;
import org.mylan.openie.shared.SimpleRelation;
import org.mylan.openie.sql.RelationSql;
import org.mylan.openie.utils.Property;

/**
 * Describe class RelationIndex here.
 *
 *
 * Created: Sun Mar 16 15:09:15 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationIndex {
    private static final Logger LOGGER = Logger.getLogger("RelationIndex.class");

    private static final String SENTENCE_FIELD = "sentence";
    private static final String RELATION_FIELD = "relation";
    private static final String FIRST_ARGUMENT_FIELD = "first_argument";
    private static final String RELATION_PATTERN_FIELD = "relation_pattern";
    private static final String LAST_ARGUMENT_FIELD = "last_argument";
    private static final String UNTOKENIZED_RELATION_FIELD = "untokenized_relation";
    private static final String UNTOKENIZED_FIRST_ARGUMENT_FIELD = "untokenized_first_argument";
    private static final String UNTOKENIZED_RELATION_PATTERN_FIELD = "untokenized_relation_pattern";
    private static final String UNTOKENIZED_LAST_ARGUMENT_FIELD = "untokenized_last_argument";
    private static final String IS_RELATION_FIELD = "is_relation";
    private static final String NO_RELATION_FIELD = "no_relation";

    private final Indexer indexer;
    private final Searcher searcher;

    public RelationIndex() {
    	Properties indexProperty = Property.create("index.properties");
        String relationIndexLocation = indexProperty.getProperty("relationIndex");
        indexer = new Indexer(relationIndexLocation);
        searcher = new Searcher(relationIndexLocation);
    }

    public void buildIndex(int limit) {
        RelationSql relationSql = new RelationSql();
        relationSql.accept(new RelationTokenizedIndexer(this), limit);
        indexer.closeIndexWriter();
    }

    public void add(final Relation relation, final FisherClassifier classifier) {
    	add(relation.getSentence().toString(),
    		relation.toString(),
    		relation.getFirstArgument().toString(),
    		relation.getRelationPattern().toString(),
    		relation.getLastArgument().toString(),
    		classifier.getFisherProbability(relation, Category.IS_RELATION),
    		classifier.getFisherProbability(relation, Category.IS_NO_RELATION));
    }

    public void add(final SimpleRelation relation) {
    	add(relation.getSentence(),
    		relation.toString(),
    		relation.getFirstArgument(),
    		relation.getRelationPattern(),
    		relation.getLastArgument(),
    		relation.getIsRelationProbability(),
    		relation.getNoRelationProbability());
    }

    private void add(final String sentence,
                     final String relation,
                     final String first_argument,
                     final String relation_pattern,
                     final String last_argument,
                     double is_relation,
                     double no_relation) {
    	Document document = new Document();
        document.add(new Field(SENTENCE_FIELD, sentence, Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field(RELATION_FIELD, relation, Field.Store.NO, Field.Index.TOKENIZED));
        document.add(new Field(FIRST_ARGUMENT_FIELD, first_argument, Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field(RELATION_PATTERN_FIELD, relation_pattern, Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field(LAST_ARGUMENT_FIELD, last_argument, Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field(UNTOKENIZED_RELATION_FIELD, relation, Field.Store.NO, Field.Index.UN_TOKENIZED));
        document.add(new Field(UNTOKENIZED_FIRST_ARGUMENT_FIELD, first_argument, Field.Store.NO, Field.Index.UN_TOKENIZED));
        document.add(new Field(UNTOKENIZED_RELATION_PATTERN_FIELD, relation_pattern, Field.Store.NO, Field.Index.UN_TOKENIZED));
        document.add(new Field(UNTOKENIZED_LAST_ARGUMENT_FIELD, last_argument, Field.Store.NO, Field.Index.UN_TOKENIZED));
        document.add(new Field(IS_RELATION_FIELD, "" + is_relation, Field.Store.YES, Field.Index.NO));
        document.add(new Field(NO_RELATION_FIELD, "" + no_relation, Field.Store.YES, Field.Index.NO));
        document.setBoost((float)((is_relation - no_relation) * is_relation));
        indexer.addDocument(document);
    }

    public Result<SimpleRelation> find(final String query, int start, int limit) throws ParseException {
        List<SimpleRelation> results = new ArrayList<SimpleRelation>(limit);
        Hits hits = searcher.search(query, RELATION_FIELD);
        LOGGER.info("Searched for \" " + query + " \"");
        for (int i = start; (i < start + limit) && (i < hits.length()); ++i) {
            try {
                Document document = hits.doc(i);
                results.add(new SimpleRelation(document.get(FIRST_ARGUMENT_FIELD),
                                               document.get(RELATION_PATTERN_FIELD),
                                               document.get(LAST_ARGUMENT_FIELD),
                                               document.get(SENTENCE_FIELD),
                                               Double.parseDouble(document.get(IS_RELATION_FIELD)),
                                               Double.parseDouble(document.get(NO_RELATION_FIELD))));
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            }
        }
        return new Result<SimpleRelation>(hits.length(), results);
    }
}
