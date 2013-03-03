package org.mylan.openie.ml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mylan.openie.nlp.PosComponent;
import org.mylan.openie.relation.instance.Relation;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Arrays;
import org.mylan.openie.utils.Property;


/**
 * Describe class FeatureType here.
 *
 *
 * Created: Wed Jan  2 13:36:49 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */

public enum FeatureType {
    POS_FIRST_ARGUMENT(new FirstArgumentPosFeatureBuilder()),
    POS_LAST_ARGUMENT(new LastArgumentPosFeatureBuilder()),
        //POS_AFTER_LAST_ARGUMENT(new AfterLastArgumentPosFeatureBuilder()),
    POS_RELATION_PATTERN(new RelationPatternPosFeatureBuilder()),
    POS_CONNECTION_FIRST_ARGUMENT(new FirstArgumentConnectionPosFeatureBuilder()),
    POS_CONNECTION_LAST_ARGUMENT(new LastArgumentConnectionPosFeatureBuilder()),
    VERB_COUNT_RELATION_PATTERN(new VerbCountFeatureBuilder()),
    NOUN_COUNT_FIRST_ARGUMENT(new FirstArgumentNounCountFeatureBuilder()),
    NOUN_COUNT_LAST_ARGUMENT(new LastArgumentNounCountFeatureBuilder()),
    DISTANCE_ARGUMENTS(new DistanceBetweenArgumentsFeatureBuilder());

    private final FeatureBuilder featureBuilder;

    FeatureType(final FeatureBuilder featureBuilder) {
        this.featureBuilder = featureBuilder;
    }

    public void getFeatures(final Set<Feature> features, final Relation relation) {
        featureBuilder.getFeatures(features, relation);
    }

    private static abstract class FeatureBuilder {
        public abstract void getFeatures(final Set<Feature> features, final Relation relation);
    }

    private static class DistanceBetweenArgumentsFeatureBuilder extends FeatureBuilder {
        public void getFeatures(final Set<Feature> features, final Relation relation) {
            int endPositionFirstArgument = relation.getFirstArgument().getComponentPositions().get(relation.getFirstArgument().getComponentPositions().size() - 1);
            int startPositionLastArgument = relation.getLastArgument().getComponentPositions().get(0);
            int distance = startPositionLastArgument - endPositionFirstArgument;
            if (distance < 0) {
                distance = 0;
            }
            features.add(new Feature(distance, DISTANCE_ARGUMENTS));
        }
    }

    private static abstract class PosFeatureBuilder extends FeatureBuilder {
        protected abstract List<PosComponent> getComponents(final Relation relation);
        protected abstract FeatureType getType();

        public void getFeatures(final Set<Feature> features, final Relation relation) {
            List<PosComponent> components = getComponents(relation);
            int componentCount = components.size();
            if (componentCount < 3) {
                StringBuilder sb = new StringBuilder();
                for (PosComponent component : components) {
                    sb.append(component.toString());
                    sb.append(" ");
                }
                features.add(new Feature(sb.toString(), getType()));
            } else {
                for (int i = 2; i < components.size(); ++i) {
                    features.add(new Feature(components.get(i - 2).toString() + " " +
                                             components.get(i - 1).toString() + " " +
                                             components.get(i).toString(),
                                             getType()));
                }
            }
        }
    }

    private static class FirstArgumentPosFeatureBuilder extends PosFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            return relation.getFirstArgument().getComponents();
        }

        protected FeatureType getType() {
            return POS_FIRST_ARGUMENT;
        }
    }

    private static class LastArgumentPosFeatureBuilder extends PosFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            return relation.getLastArgument().getComponents();
        }

        protected FeatureType getType() {
            return POS_LAST_ARGUMENT;
        }
    }

    /*    private static class AfterLastArgumentPosFeatureBuilder extends PosFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> sentence = relation.getSentence().getComponents();
            List<PosComponent> lastArgument = relation.getLastArgument().getComponents();

            List<PosComponent> components = new ArrayList<PosComponent>(3);
            if (!lastArgument.isEmpty()) {
                PosComponent lastComponent = lastArgument.get(lastArgument.size() - 1);
                int lastComponentPosition = sentence.indexOf(lastComponent);
                for (int i = lastComponentPosition + 1; i < sentence.size() && i < lastComponentPosition + 4; ++i) {
                    components.add(sentence.get(i));
                }
            }

            return components;
        }

        protected FeatureType getType() {
            return POS_AFTER_LAST_ARGUMENT;
        }
        } */

    private static class RelationPatternPosFeatureBuilder extends PosFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            return relation.getRelationPattern().getComponents();
        }

        protected FeatureType getType() {
            return POS_RELATION_PATTERN;
        }
    }

    private static abstract class ConnectionPosFeatureBuilder extends PosFeatureBuilder {
        protected void addLastComponent(final List<PosComponent> relationComponents, List<PosComponent> connectingComponents) {
            int size = relationComponents.size();
            if (size > 0) {
                connectingComponents.add(relationComponents.get(size - 1));
            }
        }

        protected void addFirstComponent(final List<PosComponent> relationComponents, List<PosComponent> connectingComponents) {
            int size = relationComponents.size();
            if (size > 0) {
                connectingComponents.add(relationComponents.get(0));
            }
        }
    }

    private static class FirstArgumentConnectionPosFeatureBuilder extends ConnectionPosFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> connectingComponents = new ArrayList<PosComponent>(2);
            addLastComponent(relation.getFirstArgument().getComponents(), connectingComponents);
            addFirstComponent(relation.getRelationPattern().getComponents(), connectingComponents);
            return connectingComponents;
        }

        protected FeatureType getType() {
            return POS_CONNECTION_FIRST_ARGUMENT;
        }
    }

    private static class LastArgumentConnectionPosFeatureBuilder extends ConnectionPosFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            List<PosComponent> connectingComponents = new ArrayList<PosComponent>(2);
            addLastComponent(relation.getRelationPattern().getComponents(), connectingComponents);
            addFirstComponent(relation.getLastArgument().getComponents(), connectingComponents);
            return connectingComponents;
        }

        protected FeatureType getType() {
            return POS_CONNECTION_LAST_ARGUMENT;
        }
    }

    private static abstract class PosCountFeatureBuilder extends FeatureBuilder {
        protected abstract List<PosComponent> getComponents(final Relation relation);
        protected abstract Set<String> getPosSet();
        protected abstract FeatureType getType();

        public void getFeatures(final Set<Feature> features, final Relation relation) {
            List<PosComponent> components = getComponents(relation);
            Set<String> posStrings = getPosSet();
            int count = 0;
            for (PosComponent component : components) {
                if (posStrings.contains(component.toString())) {
                    ++count;
                }
            }
            features.add(new Feature("" + count, getType()));
        }
    }

    private static class VerbCountFeatureBuilder extends PosCountFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            return relation.getRelationPattern().getComponents();
        }

        protected Set<String> getPosSet() {
            Properties analyseProperties = Property.create("analyse.properties");
            String language = analyseProperties.getProperty("language");
            Set<String> verbs = new HashSet<String>();
            verbs.addAll(Arrays.asList(analyseProperties.getProperty(language + "VerbTags").split(" ")));
            return verbs;
        }

        protected FeatureType getType() {
            return VERB_COUNT_RELATION_PATTERN;
        }
    }

    private abstract static class NounCountFeatureBuilder extends PosCountFeatureBuilder {
        protected Set<String> getPosSet() {
            Properties analyseProperties = Property.create("analyse.properties");
            String language = analyseProperties.getProperty("language");
            Set<String> nouns = new HashSet<String>();
            nouns.addAll(Arrays.asList(analyseProperties.getProperty(language + "NounTags").split(" ")));
            return nouns;
        }
    }

    private static class FirstArgumentNounCountFeatureBuilder extends NounCountFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            return relation.getFirstArgument().getComponents();
        }

        protected FeatureType getType() {
            return NOUN_COUNT_FIRST_ARGUMENT;
        }
    }

    private static class LastArgumentNounCountFeatureBuilder extends NounCountFeatureBuilder {
        protected List<PosComponent> getComponents(final Relation relation) {
            return relation.getLastArgument().getComponents();
        }

        protected FeatureType getType() {
            return NOUN_COUNT_LAST_ARGUMENT;
        }
    }
}
