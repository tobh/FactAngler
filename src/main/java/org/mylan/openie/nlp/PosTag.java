package org.mylan.openie.nlp;

import java.util.List;

/**
 * Describe class PosTag here.
 *
 *
 * Created: Tue Dec  4 16:30:24 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class PosTag extends PosComponent {
    private final String token;

    public PosTag(final String pos, final String token) {
        super(pos);
        this.token = token;
    }

    public PosComponent getNext() {
        PosComponent next;
        if (!isRoot() &&
            getParent().getChilds().indexOf(this) + 1 < getParent().getChilds().size()) {
            next = getParent().getChilds().get(getParent().getChilds().indexOf(this) + 1);
        } else {
            next = getNextBranch();
        }
        return next;
    }

    public void add(PosComponent component) {
        StringBuilder sb = new StringBuilder();
        sb.append("Component.toString(): ");
        sb.append(component);
        sb.append("\n");
        sb.append("Component.getText(): ");
        sb.append(component.getText());
        sb.append("\n");
        sb.append("Component.getTextWithPos(): ");
        sb.append(component.getTextWithPos());
        throw new UnsupportedOperationException(sb.toString());
    }

    public List<PosComponent> getChilds() {
        throw new UnsupportedOperationException("PosTags have no childs");
    }

    public String getText() {
        return token;
    }

    public String getTextWithPos() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(toString());
        sb.append(": ");
        sb.append(getText());
        sb.append("]");
        return sb.toString();
    }

    public String toString() {
        return pos;
    }

    public boolean isEndNode() {
        return true;
    }
}
