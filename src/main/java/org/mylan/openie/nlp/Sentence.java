package org.mylan.openie.nlp;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe class Sentence here.
 *
 *
 * Created: Wed Feb 27 19:28:50 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Sentence {
    private static final BrownPosTagger TAGGER = new BrownPosTagger();

    private final String sentence;
    private List<PosComponent> components;

    public Sentence(final String sentence) {
        this.sentence = sentence;
    }

    public Sentence(final String sentence, final String posTags) {
        this(sentence);
        String[] tags = posTags.split(" ");
        String[] tokens = TAGGER.tokenize(sentence);
        components = new ArrayList<PosComponent>(tags.length);
        for (int i = 0; i < tags.length; ++i) {
            components.add(new PosTag(tags[i], tokens[i]));
        }
    }

    public Sentence(final List<PosComponent> components) {
        StringBuilder sb = new StringBuilder();
        for (PosComponent component : components) {
            sb.append(component.getText());
            sb.append(" ");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        this.sentence = sb.toString();
        this.components = components;
    }

    public List<PosComponent> getComponents() {
        if (components == null) {
            components = TAGGER.annotate(sentence);
        }
        return components;
    }

    public String toString() {
        return sentence;
    }

    public String getPosString() {
	List<PosComponent> components = getComponents();
	StringBuilder sb = new StringBuilder();
	for (PosComponent component : components) {
	    sb.append(component);
	    sb.append(" ");
	}

	if (sb.length() > 0) {
	    sb.deleteCharAt(sb.length() - 1);
	}
	return sb.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof Sentence) {
            Sentence ref = (Sentence) o;
            return ref.toString().equals(this.toString());
        }
        return super.equals(o);
    }

    public int hashCode() {
        return toString().hashCode();
    }
}
