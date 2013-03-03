package org.mylan.openie.ml;

import org.mylan.openie.relation.instance.Relation;

/**
 * Describe class Category here.
 *
 *
 * Created: Wed Jan  2 18:48:59 2008
 *
 * @author <a href="mailto:tobias.hauth@gmail.com">Tobias Hauth</a>
 * @version 1.0
 */
public enum Category {
    IS_RELATION("32"), IS_NO_RELATION("31"), NONE("33");

    private String color;

    Category(final String color) {
        this.color = color;
    }

    public void print(final Relation relation) {
        StringBuilder sb = new StringBuilder();
        sb.append((char) 27);
        sb.append("[");
        sb.append(color);
        sb.append("m");
        sb.append(relation);
        sb.append((char) 27);
        sb.append("[0m");

        System.out.println(sb.toString());
    }
}
