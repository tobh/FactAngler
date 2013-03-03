package org.mylan.openie.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.shared.Result;
import org.mylan.openie.wikipedia.WikipediaSentence;

/**
 * Describe class SentenceIndex here.
 *
 *
 * Created: Sun Mar 16 16:33:22 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SentenceIndex {
    private static final Logger LOGGER = Logger.getLogger("SentenceIndex.class");

    private final Indexer indexer;
    private final Searcher searcher;

    public SentenceIndex(final String indexLocation) {
        indexer = new Indexer(indexLocation);
        searcher = new Searcher(indexLocation);
    }

    public void buildIndex(int sqlRelationLimit) {
        Wikipedia wikipedia = new Wikipedia();
        int sentenceCount = wikipedia.getSentenceCount();
        for (int i = 0; i < sentenceCount; i += sqlRelationLimit) {
            List<WikipediaSentence> sentences = wikipedia.getSentencesWithoutPos(i, sqlRelationLimit);
            for (WikipediaSentence sentence : sentences) {
                Document document = createDocument(sentence);
                indexer.addDocument(document);
            }
        }
        indexer.closeIndexWriter();
    }

    public Document createDocument(final WikipediaSentence sentence) {
        Document document = new Document();
        document.add(new Field("sentence", sentence.getSentence().toString(), Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field("sentence_id", "" + sentence.getSentenceId(), Field.Store.YES, Field.Index.NO));
        document.add(new Field("page_id", "" + sentence.getPageId(), Field.Store.YES, Field.Index.NO));
        return document;
    }

    public Result<String> find(final String query, int start, int limit) throws ParseException {
        List<String> results = new ArrayList<String>(limit);
        Hits hits = searcher.search(query, "sentence");
        for (int i = start; (i < start + limit) && (i < hits.length()); ++i) {
            try {
                Document document = hits.doc(i);
                StringBuilder sb = new StringBuilder();
                sb.append("page_id: ");
                sb.append(document.get("page_id"));
                sb.append(" sentence_id: ");
                sb.append(document.get("sentence_id"));
                sb.append(" sentence: ");
                sb.append(document.get("sentence"));
                results.add(sb.toString());
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            }
        }
        return new Result<String>(hits.length(), results);
    }
}
