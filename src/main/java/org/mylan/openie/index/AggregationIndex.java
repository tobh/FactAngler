package org.mylan.openie.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.shared.SimpleRelation;
import org.mylan.openie.sql.RelationSql;
import org.mylan.openie.utils.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Describe class AggregationIndex here.
 *
 *
 * Created: Wed Mar 26 15:07:52 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class AggregationIndex {
    private static final Logger LOGGER = Logger.getLogger("AggregationIndex.class");

    private static final String RELATION_FIELD = "relation";
    private static final String FIRST_ARGUMENT_FIELD = "first_argument";
    private static final String RELATION_PATTERN_FIELD = "relation_pattern";
    private static final String LAST_ARGUMENT_FIELD = "last_argument";

    private final Indexer indexer;
    private final Searcher searcher;

    public AggregationIndex() {
    	Properties indexProperty = Property.create("index.properties");
        String aggregationIndexLocation = indexProperty.getProperty("aggregationIndex");
        indexer = new Indexer(aggregationIndexLocation);
        searcher = new Searcher(aggregationIndexLocation);
    }

    public void buildIndex(int limit) {
        RelationSql relationSql = new RelationSql();
        relationSql.accept(new RelationAggregator(this), limit);
    }

    public void add(final SimpleRelation relation) {
    	add(relation.toString(),
    		relation.getFirstArgument(),
    		relation.getRelationPattern(),
    		relation.getLastArgument());
    }

    public void add(final Relation relation) {
    	add(relation.toString(),
    		relation.getFirstArgument().toString(),
    		relation.getRelationPattern().toString(),
    		relation.getLastArgument().toString());
    }

    private void add(final String relation,
                     final String firstArgument,
                     final String relationPattern,
                     final String lastArgument) {
    	Document document = new Document();
    	document.add(new Field(RELATION_FIELD, relation, Field.Store.YES, Field.Index.UN_TOKENIZED));
    	document.add(new Field(FIRST_ARGUMENT_FIELD, firstArgument, Field.Store.YES, Field.Index.UN_TOKENIZED));
    	document.add(new Field(RELATION_PATTERN_FIELD, relationPattern, Field.Store.YES, Field.Index.UN_TOKENIZED));
    	document.add(new Field(LAST_ARGUMENT_FIELD, lastArgument, Field.Store.YES, Field.Index.UN_TOKENIZED));
    	indexer.addDocument(document);
    }

    public void aggregateIndexedRelations(int limit) {
        Map<String, TermHandler> handlers = new HashMap<String, TermHandler>();
        handlers.put(RELATION_FIELD, new RelationTextHandler(limit));
        handlers.put(FIRST_ARGUMENT_FIELD, new FirstArgumentHandler(limit));
        handlers.put(RELATION_PATTERN_FIELD, new RelationPatternHandler(limit));
        handlers.put(LAST_ARGUMENT_FIELD, new LastArgumentHandler(limit));

        IndexReader indexReader = searcher.getIndexReader();
        try {
            TermEnum terms = indexReader.terms();
            while (terms.next()) {
                handlers.get(terms.term().field()).add(terms.term().text(), terms.docFreq());
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.fatal("Problem reading index");
        }

        for (TermHandler handler : handlers.values()) {
            handler.flush();
        }
    }

    public int getCount(final String query) throws ParseException {
        Hits hits = searcher.search(query, "relation_text");
        return hits.length();
    }

    public List<Integer> getRelationIds(final String query) throws ParseException {
        Hits hits = searcher.search(query, "relation_text");
        List<Integer> relationIds = new ArrayList<Integer>(hits.length());
        for (int i = 0; i < hits.length(); ++i) {
            try {
                relationIds.add(Integer.parseInt(hits.doc(i).get("relation_id")));
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            }
        }
        return relationIds;
    }
}
