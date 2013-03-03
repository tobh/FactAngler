package org.mylan.openie.nlp;

import java.util.LinkedList;
import java.util.List;

/**
 * Describe class PosComposite here.
 *
 *
 * Created: Tue Dec  4 16:38:29 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class PosComposite extends PosComponent {
    private final List<PosComponent> childs = new LinkedList<PosComponent>();

    public PosComposite(final String pos) {
        super(pos);
    }

    public PosComponent getNext() {
        PosComponent next;
        if (!childs.isEmpty()) {
            next = childs.get(0);
        } else {
            next = getNextBranch();
        }
        return next;
    }

    public void add(final PosComponent component) {
        component.setParent(this);
        childs.add(component);
    }

    public List<PosComponent> getChilds() {
        return childs;
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        for(PosComponent child : childs) {
            sb.append(child.getText());
            sb.append(" ");
        }
        if (!childs.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getTextWithPos() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(toString());
        sb.append(": ");
        for(PosComponent child : childs) {
            sb.append(child.getTextWithPos());
            sb.append(" ");
        }
        if (!childs.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");

        return sb.toString();
    }

    public String toString() {
        return pos;
    }

    public void remove(final PosComponent component) {
	component.setParent(null);
        childs.remove(component);
    }
}
