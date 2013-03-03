package org.mylan.openie.relation.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.nlp.Sentence;

/**
 * Describe class Pattern here.
 *
 *
 * Created: Tue Jan 15 03:04:12 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class Pattern {
    private final Sentence sentence;
    private final List<PosComponent> components;
    private List<Integer> positions;

    public Pattern(final Sentence sentence, final List<PosComponent> components) {
        this.sentence = sentence;
        this.components = components;
    }

    public List<PosComponent> getComponents() {
        return components;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public List<Integer> getComponentPositions() {
        if (positions == null) {
            Set<PosComponent> patternComponents = new HashSet<PosComponent>();
            patternComponents.addAll(getComponents());
            List<PosComponent> sentenceComponents = getSentence().getComponents();
            positions = new ArrayList<Integer>(components.size());
            for (int i = 0; i < sentenceComponents.size(); ++i) {
                if (patternComponents.contains(sentenceComponents.get(i))) {
                    positions.add(i);
                }
            }
        }
        return positions;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PosComponent component : getComponents()) {
            sb.append(component.getText());
            sb.append(" ");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof Pattern) {
            Pattern ref = (Pattern) o;
            if (getComponents().equals(ref.getComponents())) {
                return true;
            } else {
                return false;
            }
        }
        return super.equals(o);
    }

    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + sentence.hashCode();
        return hash;
    }
}
