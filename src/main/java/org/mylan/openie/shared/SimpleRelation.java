package org.mylan.openie.shared;

import java.io.Serializable;

/**
 * Describe class SimpleRelation here.
 *
 *
 * Created: Thu Mar  6 22:18:06 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SimpleRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String firstArgument;
    private String relationPattern;
    private String lastArgument;
    private String sentence;
    private double isRelationProbability;
    private double noRelationProbability;

    @SuppressWarnings("unused")
    private SimpleRelation() {}

    public SimpleRelation(final String firstArgument, final String relationPattern, final String lastArgument, final String sentence, double isRelationProbability, double noRelationProbability) {
    	this.firstArgument = firstArgument;
        this.relationPattern = relationPattern;
        this.lastArgument = lastArgument;
        this.sentence = sentence;
        this.isRelationProbability = isRelationProbability;
        this.noRelationProbability = noRelationProbability;
    }

    public String getFirstArgument() {
        return firstArgument;
    }

    public String getRelationPattern() {
        return relationPattern;
    }

    public String getLastArgument() {
        return lastArgument;
    }

    public String getSentence() {
        return sentence;
    }

    public double getIsRelationProbability() {
    	return isRelationProbability;
    }

    public double getNoRelationProbability() {
    	return noRelationProbability;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstArgument);
        sb.append(" ");
        sb.append(relationPattern);
        sb.append(" ");
        sb.append(lastArgument);
        return sb.toString();
    }
}
