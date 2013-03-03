package org.mylan.openie.wikipedia;

/**
 * Describe class WikipediaArticle here.
 *
 *
 * Created: Mon Nov 12 16:41:39 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class WikipediaArticle {
    private final int pageId;
    private final String articleTitle;
    private final String articleText;

    public WikipediaArticle(int pageId,
                            final String articleTitle,
                            final String articleText) {
        this.pageId = pageId;
        this.articleTitle = articleTitle;
        this.articleText = articleText;
    }

    public int getPageId() {
        return pageId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleText() {
        return articleText;
    }
}
