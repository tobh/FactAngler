package org.mylan.openie.relation.filter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.mylan.openie.ml.Category;
import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class RelationFilter here.
 *
 *
 * Created: Thu Dec 13 19:16:38 2007
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public class RelationFilter {
    private EnumSet<RelationConstraintType> constraintTypes = EnumSet.noneOf(RelationConstraintType.class);

    public void addConstraint(final RelationConstraintType constraintType) {
        constraintTypes.add(constraintType);
    }

    public void removeConstraint(final RelationConstraintType constraintType) {
        constraintTypes.remove(constraintType);
    }

    public List<Relation> filter(final List<Relation> relationsToFilter) {
        return filter(relationsToFilter, Category.IS_RELATION);
    }

    public List<Relation> filter(final List<Relation> relationsToFilter, final Category ... categories) {
        List<Relation> relations = new ArrayList<Relation>();
        for (Relation relation : relationsToFilter) {
            for (Category category : categories) {
                if (getCategory(relation).equals(category)) {
                    relations.add(relation);
                }
            }
        }
        return relations;
    }

    public Category getCategory(final Relation relation) {
        for (RelationConstraintType constraint : RelationConstraintType.values()) {
            if (!constraint.hasPassedConstraint(relation)) {
                return Category.IS_NO_RELATION;
            }
        }
        return Category.IS_RELATION;
    }

    public static RelationFilter create() {
        RelationFilter filter = new RelationFilter();

        for (RelationConstraintType constraintType : RelationConstraintType.values()) {
            filter.addConstraint(constraintType);
        }

        return filter;
    }
}
