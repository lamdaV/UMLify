package adapter;

import analyzer.utility.ClassPair;
import analyzer.utility.IClassModel;
import analyzer.utility.ISystemModel;
import analyzer.utility.StyleMap;

import java.util.*;

/**
 * The system model tracks all necessary information it needs to draw a UML. It
 * contains a classSet with all the information needed, and style information
 * regarding each class and edges
 * <p>
 * In the high-level overview, there should be only one SystemModel for one UML.
 * Each analyzer can register style information at SystemModel, or change the
 * style set.
 *
 * @author zhang
 */
public class SystemModel implements ISystemModel {
    private Set<IClassModel> classSet;

    private Map<IClassModel, StyleMap> nodeStyle;
    private Map<IClassModel, Set<String>> stereotypes;
    private Map<ClassPair, Map<String, StyleMap>> relations;

    /**
     * Create an SystemModel based on a class set
     *
     * @param classSet
     */
    public SystemModel(Set<IClassModel> classSet) {
        this.classSet = classSet;
        this.nodeStyle = new HashMap<>();
        this.relations = new HashMap<>();
        this.stereotypes = new HashMap<>();
    }

    public Set<IClassModel> getClasses() {
        return this.classSet;
    }

    @Override
    public void setClasses(Set<IClassModel> classSet) {
        this.classSet = classSet;
    }

    public Map<ClassPair, Map<String, StyleMap>> getRelations() {
        return this.relations;
    }

    @Override
    public Set<String> getStereoTypes(IClassModel clazz) {
        return this.stereotypes.getOrDefault(clazz, Collections.emptySet());
    }

    @Override
    public void addClassModelStyle(IClassModel clazz, String key, String value) {
        if (clazz == null) {
            throw new RuntimeException("ClassModel cannot be null");
        }
        if (!classSet.contains(clazz)) {
            throw new RuntimeException("ClassModel (" + clazz + ") cannot be null");
        }
        if (!this.nodeStyle.containsKey(clazz)) {
            this.nodeStyle.put(clazz, new StyleMap());
        }
        this.nodeStyle.get(clazz).addStyle(key, value);
    }

    @Override
    public String getNodeStyle(IClassModel clazz) {
        if (this.nodeStyle.containsKey(clazz)) {
            return this.nodeStyle.get(clazz).getStyleString();
        }
        return "";
    }

    @Override
    public void addClassModelStereotype(IClassModel clazz, String stereotype) {
        if (clazz == null) {
            throw new RuntimeException("ClassModel cannot be null");
        }
        if (!this.stereotypes.containsKey(clazz)) {
            this.stereotypes.put(clazz, new HashSet<>());
        }
        if (stereotype.length() > 1)
            this.stereotypes.get(clazz).add(stereotype);
    }

    @Override
    public void addStyleToRelation(IClassModel from, IClassModel to, String relKey, String key, String value) {
        getRelationStyleMap(from, to, relKey).addStyle(key, value);
    }

    @Override
    public void addRelation(IClassModel from, IClassModel to, String relKey, StyleMap styleMap) {
        getRelationStyleMap(from, to, relKey).addStyleMap(styleMap);
    }

    private StyleMap getRelationStyleMap(IClassModel from, IClassModel to, String relKey) {
        if (from == null) {
            throw new RuntimeException("from ClassModel cannot be null");
        }
        if (to == null) {
            throw new RuntimeException("to ClassModel cannot be null");
        }
        if (!classSet.contains(from)) {
            throw new RuntimeException("from ClassModel (" + from + ") must be in classSet");
        }
        if (!classSet.contains(to)) {
            throw new RuntimeException("to ClassModel (" + to + ") must be in classSet");
        }
        ClassPair pair = new ClassPair(from, to);
        if (!this.relations.containsKey(pair)) {
            this.relations.put(pair, new HashMap<>());
        }
        Map<String, StyleMap> map = this.relations.get(pair);
        if (!map.containsKey(relKey)) {
            map.put(relKey, new StyleMap());
        }
        return map.get(relKey);
    }

}
