package org.mylan.openie.apps;

import org.mylan.openie.corpus.Wikipedia;
import org.mylan.openie.relation.SinglePassRelationExtractor;

/**
 * Describe class SinglePassExtraction here.
 *
 *
 * Created: Mon Mar 31 17:30:29 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SinglePassExtraction {
    public static void main(String[] args) {
    	SinglePassRelationExtractor extration = new SinglePassRelationExtractor();
        extration.extract(new Wikipedia());
    }
}
