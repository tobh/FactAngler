package org.mylan.openie.converter;

import info.bliki.wiki.model.WikiModel;

/**
 * Describe class MediawikiToPlainTextConverter here.
 *
 *
 * Created: Thu Feb  7 19:07:28 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class MediawikiToPlainTextConverter {
    private static final WikiModel WIKI_MODEL = new WikiModel("", "");
    private static final HtmlToPlainTextConverter HTML_CONVERTER = new HtmlToPlainTextConverter();

    public String convert(final String mediawikiText) {
    	// remove wikitable before parsing (outOfMemory problems)
        return HTML_CONVERTER.convert(WIKI_MODEL.render(mediawikiText.replaceAll("(?s)\\{\\|.*?\\|\\}", "")));
    }
}
