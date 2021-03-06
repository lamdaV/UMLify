package analyzer.relationParser;

import analyzer.utility.StyleMap;

/**
 * RelationInfo that interprets Has-A Relationships.
 */
public class RelationHasA implements IRelationInfo {
    public static final String REL_KEY = "has_a";

    private final boolean many;
    private final int count;

    /**
     * Constructs a RelationHasA object.
     *
     * @param count count value of the relation.
     */
    public RelationHasA(int count) {
        this.many = count <= 0;
        this.count = Math.abs(count);
    }

    /**
     * Returns true if this is a one-to-many relationship.
     *
     * @return true if it is a one-to-many relationship.
     */
    boolean isMany() {
        return this.many;
    }

    /**
     * Returns the exact cardinality of this relationship.
     *
     * @return Integer of the cardinality.
     */
    public int getCount() {
        return this.count;
    }

    @Override
    public String toString() {
        return isMany() ? String.format("has many %d..n", this.count) : "has " + getCount();
    }

    @Override
    public StyleMap getStyleMap() {
        StyleMap styleMap = new StyleMap();
        styleMap.addStyle("arrowhead", "vee");
        styleMap.addStyle("style", "");
        if (isMany()) {
            styleMap.addStyle("headlabel", String.format("%d..*", getCount()));
        } else {
            styleMap.addStyle("headlabel", String.format("%d", getCount()));
        }
        return styleMap;
    }

    @Override
    public String getRelKey() {
        return RelationHasA.REL_KEY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RelationHasA) {
            RelationHasA x = RelationHasA.class.cast(obj);
            return x.count == this.count && x.many == this.many;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return count * 31 + (this.many ? 1 : 15);
    }
}
