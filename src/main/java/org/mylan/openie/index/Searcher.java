package org.mylan.openie.index;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * Describe class Searcher here.
 *
 *
 * Created: Sun Mar 16 14:36:02 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Searcher {
    private static final Logger LOGGER = Logger.getLogger("Searcher.java");

    private final String indexLocation;
    private IndexSearcher indexSearcher = null;
    private Analyzer analyzer;

    public Searcher(final String indexLocation) {
        this.indexLocation = indexLocation;
        this.analyzer = new StandardAnalyzer();
    }

    public IndexReader getIndexReader() {
        initIndexSearcher();
        return indexSearcher.getIndexReader();
    }

    public Hits search(final String queryString, final String defaultQueryField) throws ParseException {
        initIndexSearcher();
        QueryParser queryParser = new QueryParser(defaultQueryField, analyzer);
        Query query = queryParser.parse(queryString);
        Hits hits = null;
        try {
            hits = indexSearcher.search(query);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
            LOGGER.fatal("Could not search index");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.fatal("Could not search index");
        }
        return hits;
    }

    public Hits search(final String queryString, final String defaultQueryField, final Sort sort) throws ParseException {
        initIndexSearcher();
        QueryParser queryParser = new QueryParser(defaultQueryField, analyzer);
        Query query = queryParser.parse(queryString);
        Hits hits = null;
        try {
            hits = indexSearcher.search(query, sort);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
            LOGGER.fatal("Could not search index");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.fatal("Could not search index");
        }
        return hits;
    }

    public void initIndexSearcher() {
        if (indexSearcher == null) {
            try {
                indexSearcher = new IndexSearcher(indexLocation);
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Could not open IndexSearcher");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Could not open IndexSearcher");
            }
        }
    }

    public void closeIndexSearcher() {
        if (indexSearcher != null) {
            try {
                indexSearcher.close();
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem closing IndexWriter");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem closing IndexWriter");
            }
        }
    }
}
