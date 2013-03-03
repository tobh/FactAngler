package org.mylan.openie.relation.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.relation.instance.Pattern;
import org.mylan.openie.relation.instance.Relation;
import org.mylan.openie.utils.Property;


/**
 * Describe interface RelationConstraintType here.
 *
 *
 * Created: Thu Dec 13 19:46:24 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */

public enum RelationConstraintType {
    TREE_DISTANCE_ARGUMENTS(new ArgumentsTreeDistanceConstraint()),
    POS_FIRST_ARGUMENT_CONNECTING_COMPONENT(new FirstArgumentConnectingComponentPosTypeConstraint()),
    POS_LAST_ARGUMENT_CONNECTING_COMPONENT(new LastArgumentConnectingComponentPosTypeConstraint()),
    POS_RELATION_PATTERN_CONNECTING_COMPONENT(new RelationPatternConnectingComponentPosTypeConstraint()),
    FORBITTEN_FIRST_POS_RELATION_PATTERN(new RelationPatternForbittenFirstPosTypeConstraint()),
    FORBITTEN_LAST_POS_RELATION_PATTERN(new RelationPatternForbittenLastPosTypeConstraint()),
    FORBITTEN_SINGLE_POS_ARGUMENTS(new ArgumentsForbittenSinglePosTypeConstraint()),
    FORBITTEN_POS_ARGUMENTS_CONNECTING_COMPONENTS(new ArgumentsConnectingComponentsForbittenPosTypeConstraint());

    private static Properties properties;
    private final RelationConstraint relationConstraint;

    private RelationConstraintType(final RelationConstraint relationConstraint) {
        this.relationConstraint = relationConstraint;
    }

    public List<Relation> apply(final List<Relation> relations) {
        return relationConstraint.apply(relations);
    }

    public void print(final Relation relation) {
        relationConstraint.print(relation);
    }

    public boolean hasPassedConstraint(final Relation relation) {
        return relationConstraint.hasPassedConstraint(relation);
    }

    private static String getProperty(final String propertyName) {
        if (properties == null) {
            properties = Property.create("analyse.properties");
        }
        String language = properties.getProperty("language");
        return properties.getProperty(language + propertyName);
    }

    private abstract static class RelationConstraint {
        public abstract List<Relation> apply(final List<Relation> relations);
        public abstract boolean hasPassedConstraint(final Relation relation);
        protected abstract String getFeatureString(final Relation relation);

        public void print(final Relation relation) {
            StringBuilder sb = new StringBuilder();
            if (hasPassedConstraint(relation)) {
                sb.append((char) 27);
                sb.append("[32m");
            } else {
                sb.append((char) 27);
                sb.append("[31m");
            }
            sb.append(getFeatureString(relation));
            sb.append((char) 27);
            sb.append("[0m");
            System.out.print(sb);
        }
    }

    private abstract static class PosTypeConstraint extends RelationConstraint {
        protected final Set<String> tags = new HashSet<String>();

        public PosTypeConstraint(final String ... posTags) {
            super();
            for (String posTag : posTags) {
                tags.add(posTag);
            }
        }

        protected abstract List<PosComponent> getComponents(final Relation relation);

        public boolean hasPassedConstraint(final Relation relation) {
            List<PosComponent> components = getComponents(relation);
            boolean tagFound = false;
            for (PosComponent component : components) {
                List<String> pos = getPos(component);
                for (String tag : tags) {
                    if (pos.contains(tag)) {
                        tagFound = true;
                    }
                }
            }
            if (tagFound) {
                return true;
            } else {
                return false;
            }
        }

        protected List<String> getPos(final PosComponent component) {
            List<String> pos = new LinkedList<String>();
            pos.add(component.toString());
            return pos;
        }

        public List<Relation> apply(final List<Relation> relations) {
            Iterator<Relation> iter = relations.iterator();
            while (iter.hasNext()) {
                if(!hasPassedConstraint(iter.next())) {
                    iter.remove();
                }
            }
            return relations;
        }

        protected String getFeatureString(final Relation relation) {
            StringBuilder sb = new StringBuilder();
            for (PosComponent component : getComponents(relation)) {
                sb.append(component.toString());
                sb.append(" ");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }
    }

    private static class RelationPatternConnectingComponentPosTypeConstraint extends PosTypeConstraint {
        public RelationPatternConnectingComponentPosTypeConstraint() {
            super(getProperty("RelationPatternConnectingComponentPos").split(" "));
        }

        protected List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> connectingComponent = new LinkedList<PosComponent>();
            PosComponent component = PosComponent.getConnectingComponent(relation.getRelationPattern().getComponents());
            if (component != null) {
                connectingComponent.add(component);
            }
            return connectingComponent;
        }
    }

    private abstract static class ArgumentConnectingComponentPosTypeConstraint extends PosTypeConstraint {
        public ArgumentConnectingComponentPosTypeConstraint() {
            super(getProperty("ArgumentConnectingComponentPos").split(" "));
        }

        protected List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> connectingComponent = new LinkedList<PosComponent>();
            PosComponent component = PosComponent.getConnectingComponent(getArgument(relation).getComponents());
            if (component != null) {
                connectingComponent.add(component);
            }
            return connectingComponent;
        }

        protected abstract Pattern getArgument(final Relation relation);
    }

    private static class FirstArgumentConnectingComponentPosTypeConstraint extends ArgumentConnectingComponentPosTypeConstraint {
        protected Pattern getArgument(final Relation relation) {
            return relation.getFirstArgument();
        }
    }

    private static class LastArgumentConnectingComponentPosTypeConstraint extends ArgumentConnectingComponentPosTypeConstraint {
        protected Pattern getArgument(final Relation relation) {
            return relation.getLastArgument();
        }
    }

    private abstract static class ForbittenPosTypeConstraint extends PosTypeConstraint {
        public ForbittenPosTypeConstraint(final String ... posTags) {
            super(posTags);
        }

        public boolean hasPassedConstraint(final Relation relation) {
            return !super.hasPassedConstraint(relation);
        }
    }

    private static class RelationPatternForbittenFirstPosTypeConstraint extends ForbittenPosTypeConstraint {
        public RelationPatternForbittenFirstPosTypeConstraint() {
            super(getProperty("RelationPatternForbittenFirstPos").split(" "));
        }

        public List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> components = new LinkedList<PosComponent>();
            if (!relation.getRelationPattern().getComponents().isEmpty()) {
                components.add(relation.getRelationPattern().getComponents().get(0));
            }
            return components;
        }
    }

    private static class ArgumentsForbittenSinglePosTypeConstraint extends ForbittenPosTypeConstraint {
        public ArgumentsForbittenSinglePosTypeConstraint() {
            super(getProperty("ArgumentsForbittenSinglePos").split(" "));
        }

        public List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> components = new LinkedList<PosComponent>();
            if (relation.getFirstArgument().getComponents().size() == 1) {
                components.add(relation.getFirstArgument().getComponents().get(0));
            }
            if (relation.getLastArgument().getComponents().size() == 1) {
                components.add(relation.getLastArgument().getComponents().get(0));
            }
            return components;
        }
    }

    private abstract static class ForbittenLastPosTypeConstraint extends ForbittenPosTypeConstraint {
        public ForbittenLastPosTypeConstraint(final String ... posTags) {
            super(posTags);
        }

        protected abstract List<Pattern> getPatterns(final Relation relation);

        protected List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> lastComponents = new LinkedList<PosComponent>();
            for (Pattern pattern : getPatterns(relation)) {
                if (!pattern.getComponents().isEmpty()) {
                    lastComponents.add(pattern.getComponents().get(pattern.getComponents().size() - 1));
                }
            }
            return lastComponents;
        }
    }

    private static class RelationPatternForbittenLastPosTypeConstraint extends ForbittenLastPosTypeConstraint {
        public RelationPatternForbittenLastPosTypeConstraint() {
            super(getProperty("RelationPatternForbittenLastPos").split(" "));
        }

        protected List<Pattern> getPatterns(final Relation relation) {
            List<Pattern> relationPattern = new LinkedList<Pattern>();
            relationPattern.add(relation.getRelationPattern());
            return relationPattern;
        }
    }

    private static class ArgumentsConnectingComponentsForbittenPosTypeConstraint extends ForbittenPosTypeConstraint {
        public ArgumentsConnectingComponentsForbittenPosTypeConstraint() {
            super(getProperty("ArgumentsConnectingComponentsForbittenPosType").split(" "));
        }

        protected List<PosComponent> getComponents(final Relation relation) {
            PosComponent firstArgument = relation.getFirstArgument().getComponents().get(relation.getFirstArgument().getComponents().size() - 1);
            PosComponent secondArgument = relation.getLastArgument().getComponents().get(0);
            return firstArgument.getConnectingComponentsWithoutCommonComponent(secondArgument);
        }
    }

    private abstract static class DistanceConstraint extends RelationConstraint {
        private final int maxDistance;

        public DistanceConstraint(final int maxDistance) {
            super();
            this.maxDistance = maxDistance;
        }

        protected abstract int getDistance(final Relation relation);

        public List<Relation> apply(final List<Relation> relations) {
            Iterator<Relation> iter = relations.iterator();
            while (iter.hasNext()) {
                if (!hasPassedConstraint(iter.next())) {
                    iter.remove();
                }
            }
            return relations;
        }

        public boolean hasPassedConstraint(final Relation relation) {
            return getDistance(relation) <= maxDistance;
        }

        protected String getFeatureString(final Relation relation) {
            StringBuilder sb = new StringBuilder();
            sb.append(getDistance(relation));
            sb.append(" (max = ");
            sb.append(maxDistance);
            sb.append(")");
            return sb.toString();
        }
    }

    private static class ArgumentsTreeDistanceConstraint extends DistanceConstraint {
        public ArgumentsTreeDistanceConstraint() {
            super(Integer.parseInt(getProperty("ArgumentsTreeDistance")));
        }

        protected int getDistance(final Relation relation) {
            PosComponent firstArgument = PosComponent.getConnectingComponent(relation.getFirstArgument().getComponents());
            PosComponent secondArgument = PosComponent.getConnectingComponent(relation.getLastArgument().getComponents());
            return firstArgument.getTreeDistance(secondArgument);
        }
    }
}
