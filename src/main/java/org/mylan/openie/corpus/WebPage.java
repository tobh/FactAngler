package org.mylan.openie.corpus;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mylan.openie.converter.HtmlToPlainTextConverter;
import org.mylan.openie.nlp.Sentence;
import org.mylan.openie.nlp.SentenceExtractor;
import org.mylan.openie.nlp.SentenceVisitor;

/**
 * Describe class WebPage here.
 *
 *
 * Created: Fri May  2 23:03:34 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class WebPage implements TextCorpus {
    public static final Logger LOGGER = Logger.getLogger("WebPage.class");

    private List<URL> urls;

    public WebPage() {
        urls = new LinkedList<URL>();
    }

    public void add(final String ... addresses) throws MalformedURLException {
        List<URL> newUrls = new ArrayList<URL>(addresses.length);
        for (String address : addresses) {
            newUrls.add(new URL(address));
        }
        urls.addAll(newUrls);
    }

    public final void accept(final SentenceVisitor visitor) {
        SentenceExtractor extractor = new SentenceExtractor();
        for (URL url : urls) {
            String content = new HtmlToPlainTextConverter().convert(fetchContent(url));
            List<Sentence> sentences = extractor.extract(content);

            for (Sentence sentence : sentences) {
                visitor.visitSentence(sentence);
            }

            visitor.process();
        }
    }

    private String fetchContent(final URL url) {
        StringBuilder sb = new StringBuilder();
        try {
            URLConnection connection = url.openConnection();
            DataInputStream in = new DataInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while (reader.ready()) {
                sb.append(reader.readLine());
                sb.append("\n");
            }
        } catch (IOException e) {
            LOGGER.error("Couldn't fetch webpage " + url.toString());
        }
        return sb.toString();
    }
}
