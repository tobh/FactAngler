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
public class RelationCandidateIndex {
    private static final Logger LOGGER = Logger.getLogger("RelationCandidateIndex.class");

    private final Indexer indexer;
    private final Searcher searcher;
    private final FisherClassifier classifier;

    public RelationCandidateIndex(final FisherClassifier classifier) {
        this.classifier = classifier;
        Properties indexProperty = Property.create("index.properties");
        String relationCandidateIndexLocation = indexProperty.getProperty("relationCandidateIndex");
        indexer = new Indexer(relationCandidateIndexLocation);
        searcher = new Searcher(relationCandidateIndexLocation);
    }

    public void add(final Relation relation) {
    	Document document = new Document();
        document.add(new Field("sentence", "" + relation.getSentence().toString(), Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field("relation", relation.toString(), Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field("first_argument", relation.getFirstArgument().toString(), Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field("last_argument", relation.getLastArgument().toString(), Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field("relation_pattern", relation.getRelationPattern().toString(), Field.Store.YES, Field.Index.TOKENIZED));
        document.add(new Field("category", classifier.classify(relation).toString(), Field.Store.YES, Field.Index.NO));
        document.add(new Field("is_relation", "" + classifier.getFisherProbability(relation, Category.IS_RELATION), Field.Store.YES, Field.Index.NO));
        document.add(new Field("no_relation", "" + classifier.getFisherProbability(relation, Category.IS_NO_RELATION), Field.Store.YES, Field.Index.NO));
        indexer.addDocument(document);
    }

    public List<String> find(final String query, int start, int limit) throws ParseException {
        List<String> results = new ArrayList<String>(limit);
        Hits hits = searcher.search(query, "relation");
        for (int i = start; (i < start + limit) && (i < hits.length()); ++i) {
            try {
                Document document = hits.doc(i);
                StringBuilder sb = new StringBuilder();
                sb.append(document.get("category"));
                sb.append(" ( + ");
                sb.append(document.get("is_relation"));
                sb.append(" | - ");
                sb.append(document.get("no_relation"));
                sb.append(" ): ");
                sb.append(document.get("first_argument"));
                sb.append(" <--> ");
                sb.append(document.get("relation_pattern"));
                sb.append(" <--> ");
                sb.append(document.get("last_argument"));
                results.add(sb.toString());
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem getting document from index");
            }
        }
        return results;
    }
}
