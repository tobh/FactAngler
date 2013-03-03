package org.mylan.openie.nlp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Describe class PosComponent here.
 *
 *
 * Created: Fri Nov 16 13:02:01 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public abstract class PosComponent implements Iterable<PosComponent> {
    protected final String pos;
    protected PosComponent parent = null;

    public PosComponent(final String pos) {
        this.pos = pos;
    }

    public abstract void add(PosComponent component);
    public abstract PosComponent getNext();
    public abstract List<PosComponent> getChilds();
    public abstract String getText();
    public abstract String getTextWithPos();

    protected void setParent(PosComponent parent) {
        this.parent = parent;
    }

    public PosComponent getParent() {
        return parent;
    }

    public PosComponent getRoot() {
        if (isRoot()) {
            return this;
        } else {
            return getParent().getRoot();
        }
    }

    protected boolean isRoot() {
        if (this.getParent() == null) {
            return true;
        }
        return false;
    }

    public boolean isEndNode() {
        return false;
    }

    public PosComponent getNextBranch() {
        PosComponent next = this;

        while (!next.isRoot() &&
               next.getParent().getChilds().indexOf(next) == next.getParent().getChilds().size() - 1) {
            next = next.getParent();
        }
        if (!next.isRoot()) {
            next = next.parent.getChilds().get(next.parent.getChilds().indexOf(next) + 1);
        } else {
            next = null;
        }

        return next;
    }

    public boolean hasChild(final PosComponent possibleChild) {
        for(PosComponent child : this) {
            if (possibleChild == child) {
                return true;
            }
        }
        return false;
    }

    public int getDistance(final PosComponent component) throws IllegalArgumentException {
        PosComponent root = this.getRoot();
        PosComponent first = null;
        PosComponent second = null;

        for (PosComponent child : root) {
            if (child == this) {
                first = this;
                second = component;
                break;
            } else if (child == component) {
                first = component;
                second = this;
                break;
            }
        }

        if (first.hasChild(second)) {
            throw new IllegalArgumentException("Distance between parent and child is not defined");
        }

        first = first.getNextBranch();
        int distance = 1;
        while (first != second) {
            ++distance;
            first = first.getNext();
        }
        return distance;
    }

    public int getTreeDistance(final PosComponent component) {
        List<PosComponent> thisToRoot = getComponentsToRoot(this);
        List<PosComponent> componentToRoot = getComponentsToRoot(component);
        PosComponent commonComponent = getConnectingComponent(component);
        int distance = (thisToRoot.indexOf(commonComponent) + 1) + (componentToRoot.indexOf(commonComponent) + 1);
        return distance;
    }

    public List<PosComponent> getConnectingComponentsWithoutCommonComponent(final PosComponent component) {
        List<PosComponent> componentToRoot = getComponentsToRoot(component);

        PosComponent commonComponent = getConnectingComponent(component);
        List<PosComponent> connectingComponents = new ArrayList<PosComponent>();
        PosComponent currentComponent = this;
        while (currentComponent != commonComponent) {
            currentComponent = currentComponent.getParent();
            connectingComponents.add(currentComponent);
        }

        connectingComponents.remove(commonComponent);

        for (int i = componentToRoot.indexOf(commonComponent) - 1; i > 0; --i) {
            connectingComponents.add(componentToRoot.get(i));
        }
        return connectingComponents;
    }

    public static PosComponent getConnectingComponent(final List<PosComponent> components) {
        PosComponent connectingComponent = null;

        if (!components.isEmpty()) {
            connectingComponent = components.get(0);
        }
        for (PosComponent component : components) {
            connectingComponent = connectingComponent.getConnectingComponent(component);
        }
        return connectingComponent;
    }

    public PosComponent getConnectingComponent(final PosComponent component) {
        List<PosComponent> thisToRoot = getComponentsToRoot(this);
        List<PosComponent> componentToRoot = getComponentsToRoot(component);
        PosComponent connectingComponent = null;
        for (PosComponent current : thisToRoot) {
            if (componentToRoot.contains(current)) {
                connectingComponent = current;
                break;
            }
        }
        return connectingComponent;
    }

    private List<PosComponent> getComponentsToRoot(final PosComponent component) {
        List<PosComponent> componentToRoot = new ArrayList<PosComponent>();
        PosComponent currentComponent = component;
        componentToRoot.add(currentComponent);
        while (!currentComponent.isRoot()) {
            currentComponent = currentComponent.getParent();
            componentToRoot.add(currentComponent);
        }
        return componentToRoot;
    }

    public List<PosComponent> getEndNodes() {
        List<PosComponent> endNodes = new ArrayList<PosComponent>();
        for (PosComponent component : this) {
            if (component.isEndNode()) {
                endNodes.add(component);
            }
        }
        return endNodes;
    }

    public Iterator<PosComponent> iterator() {
        return new PosIterator();
    }

    private class PosIterator implements Iterator<PosComponent> {
        private PosComponent next;
        private PosComponent stop;

        public PosIterator() {
            next = PosComponent.this;
            stop = next.getNextBranch();
        }

        public boolean hasNext() {
            if (next != stop) {
                return true;
            }
            return false;
        }

        public PosComponent next() {
            PosComponent current = next;
            next = next.getNext();
            return current;
        }

        public void remove() {
	    throw new UnsupportedOperationException();
        }
    }
}
