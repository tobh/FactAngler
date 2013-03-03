package org.mylan.openie.converter;

import au.id.jericho.lib.html.Source;

/**
 * Describe class HtmlToPlainTextConverter here.
 *
 *
 * Created: Fri Apr 18 17:24:49 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class HtmlToPlainTextConverter {
    public String convert(final String html) {
        Source htmlSource = new Source(html);
        String plainText = htmlSource.getRenderer().setMaxLineLength(Integer.MAX_VALUE).toString();

        // cleanup text
        // remove {{something}} and references (e.g. [3])
        plainText = plainText.replaceAll("(?m)<[^>]*?>", "");
        plainText = plainText.replaceAll("\\(?\\{\\{.*?\\}\\}\\)?", "");
        plainText = plainText.replaceAll("\\[.*?\\]", "");

        return plainText;
    }
}
