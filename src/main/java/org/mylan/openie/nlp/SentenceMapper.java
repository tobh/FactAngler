package org.mylan.openie.nlp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Describe class SentenceMapper here.
 *
 *
 * Created: Thu Jan 24 14:22:49 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class SentenceMapper {
    public Map<PosComponent, List<PosComponent>> mapPosComponents(final List<PosComponent> sentenceTreePos, final List<PosComponent> sentenceSimplePos) {
        Map<PosComponent, List<PosComponent>> mapping = new HashMap<PosComponent, List<PosComponent>>();
        for (PosComponent component : sentenceTreePos) {
            mapping.put(component, new LinkedList<PosComponent>());
        }

        for (Position position = calculateCosts(sentenceTreePos, sentenceSimplePos); position != null; position = position.getPredecessor()) {
            List<PosComponent> mappedSimplePos = mapping.get(position.getOldComponent());
            mappedSimplePos.add(position.getMappedComponent());
        }

        return mapping;
    }

    private Position calculateCosts(final List<PosComponent> sentenceTreePos, final List<PosComponent> sentenceSimplePos) {
        int treeSize = sentenceTreePos.size();
        int simpleSize = sentenceSimplePos.size();

        int[][] costs = new int[treeSize + 1][simpleSize + 1];
        for (int i = 0; i < simpleSize + 1; ++i) {
            costs[0][i] = i;
        }
        for (int i = 0; i < treeSize + 1; ++i) {
            costs[i][0] = i;
        }

        Position[][] predecessors = new Position[treeSize + 1][simpleSize + 1];

        for (int i = 1; i < costs.length; ++i) {
            for (int j = 1; j < costs[i].length; ++j) {
                int insertion = costs[i - 1][j] + 1;
                int hit = costs[i - 1][j - 1];
                int substitution = hit + 2;
                int deletion = costs[i][j - 1] + 1;

                boolean isHit = sentenceTreePos.get(i - 1).getText().equals(sentenceSimplePos.get(j - 1).getText());

                if (isHit) {
                    costs[i][j] = hit;
                    predecessors[i][j] = new Position(predecessors[i - 1][j - 1], sentenceTreePos.get(i - 1), sentenceSimplePos.get(j - 1));
                } else if (insertion <= substitution && insertion <= deletion) {
                    costs[i][j] = insertion;
                    predecessors[i][j] = new Position(predecessors[i - 1][j], sentenceTreePos.get(i - 1), sentenceSimplePos.get(j - 1));
                } else if (deletion <= substitution) {
                    costs[i][j] = deletion;
                    predecessors[i][j] = new Position(predecessors[i][j - 1], sentenceTreePos.get(i - 1), sentenceSimplePos.get(j - 1));
                } else {
                    costs[i][j] = substitution;
                    predecessors[i][j] = new Position(predecessors[i - 1][j - 1], sentenceTreePos.get(i - 1), sentenceSimplePos.get(j - 1));
                }
            }
        }
        return predecessors[predecessors.length - 1][predecessors[0 ].length - 1];
    }

    private class Position {
        private Position predecessor;
        private PosComponent oldComponent;
        private PosComponent mappedComponent;

        public Position(final Position predecessor, final PosComponent oldComponent, final PosComponent mappedComponent) {
            this.predecessor = predecessor;
            this.oldComponent = oldComponent;
            this.mappedComponent = mappedComponent;
        }

        public Position getPredecessor() {
            return predecessor;
        }

        public PosComponent getOldComponent() {
            return oldComponent;
        }

        public PosComponent getMappedComponent() {
            return mappedComponent;
        }
    }
}
