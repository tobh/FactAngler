package org.mylan.openie.apps;

import java.util.List;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.utils.ConsoleInputListener;
import org.mylan.openie.utils.Worker;
import org.mylan.openie.wikipedia.WikipediaArticle;

/**
 * Describe class BrowseArticles here.
 *
 *
 * Created: Thu Nov 15 19:03:29 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class BrowseArticles {
    private final Wikipedia wikipedia;
    private List<WikipediaArticle> articles;
    private int articlesStartId;
    private int articlesCount;
    private int articleId;
    private int maxArticleId;
    private ConsoleInputListener console;

    public BrowseArticles(final ConsoleInputListener console) {
        wikipedia = new Wikipedia();
        articlesStartId = Integer.MAX_VALUE;
        articlesCount = 1000;
        articleId = 0;
        maxArticleId = wikipedia.getArticleCount();

        console.add(new Worker("n", "next") { protected void execute(final String input) {next();}},
                    new Worker("p", "previous") { protected void execute(final String input) {previous();}},
                    new Worker("f", "fast forward") { protected void execute(final String input) {fastForward();}},
                    new Worker("b", "fast back") { protected void execute(final String input) {fastBack();}},
                    new Worker("F", "Forward") { protected void execute(final String input) {bigForward();}},
                    new Worker("B", "Back") { protected void execute(final String input) {bigBack();}}
                    );
        this.console = console;
    }

    public void start() {
        console.start();
    }

    public void close() {
        wikipedia.close();
    }

    private void next() {
        ++articleId;
        printArticle();
    }

    private void previous() {
        --articleId;
        printArticle();
    }

    private void fastForward() {
        articleId += articlesCount;
        printArticle();
    }

    private void fastBack() {
        articleId -= articlesCount;
        printArticle();
    }

    private void bigForward() {
        articleId += 100 * articlesCount;
        printArticle();
    }

    private void bigBack() {
        articleId -= 100 * articlesCount;
        printArticle();
    }

    private void printArticle() {
        ensureArticleIsFetched();
        int id = articleId - articlesStartId;
        System.out.println("page_id: " + articles.get(id).getPageId());
        System.out.println(articles.get(id).getArticleText());
    }

    private void ensureArticleIsFetched() {
        if (articleId > maxArticleId) {
            articleId = maxArticleId;
            System.out.println("upper article bound reached!");
        }
        if (articleId < 1) {
            articleId = 1;
            System.out.println("lower article bound reached!");
        }

        if (articleId < articlesStartId || articleId > articlesStartId + articlesCount) {
            articlesStartId = articleId - (articlesCount / 2);
            if (articlesStartId < 0) {
                articlesStartId = 0;
            }
            articles = wikipedia.getArticles(articlesStartId, articlesCount);
        }
    }

    public static void main(String[] args) throws Exception {
        String commandos = "(n)ext, (p)revious, (f)ast forward, fast (b)ack, 100 * (F)orward, 100 * (B)ack";
        ConsoleInputListener in = new ConsoleInputListener(commandos);

        BrowseArticles browser = new BrowseArticles(in);
        browser.start();
        browser.close();
    }
}
