package analyzer.relationParser;

import analyzer.utility.StyleMap;

/**
 * RelationInfo that interprets Extends relations.
 */
public class RelationExtendsClass implements IRelationInfo {
    @Override
    public String toString() {
        return "Extends";
    }

    @Override
    public StyleMap getStyleMap() {
        StyleMap styleMap = new StyleMap();
        styleMap.addStyle("arrowtail", "onormal");
        styleMap.addStyle("style", "");
        return styleMap;
    }

    @Override
    public String getRelKey() {
        return "extends";
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == RelationExtendsClass.class;
    }

    @Override
    public int hashCode() {
        return 52;
    }
}
