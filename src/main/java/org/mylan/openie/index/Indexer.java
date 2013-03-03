package org.mylan.openie.index;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

/**
 * Describe class Indexer here.
 *
 *
 * Created: Sun Mar 16 13:59:01 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Indexer {
    private static final Logger LOGGER = Logger.getLogger("Indexer.class");

    private final String indexLocation;
    private IndexWriter indexWriter = null;
    private static final double MAX_BUFFER = 256.;

    public Indexer(final String indexLocation) {
        this.indexLocation = indexLocation;
    }

    public void addDocument(final Document document) {
        initIndexWriter();
        try {
            indexWriter.addDocument(document);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
            LOGGER.fatal("Could not add document to IndexWriter");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.fatal("Could not add document to IndexWriter");
        }
    }

    public void initIndexWriter() {
        if (indexWriter == null) {
            try {
                indexWriter = new IndexWriter(indexLocation,
                                              new StandardAnalyzer());
                indexWriter.setRAMBufferSizeMB(MAX_BUFFER);
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Could not open IndexWriter");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Could not open IndexWriter");
            }
        }
    }

    public void closeIndexWriter() {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (CorruptIndexException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem closing IndexWriter");
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.fatal("Problem closing IndexWriter");
            }
        }
    }

    public void flushIndexWriter() {
        if (indexWriter != null) {
            try {
                indexWriter.flush();
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
