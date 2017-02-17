package analyzer.utility;

import java.util.Map;
import java.util.Set;

/**
 * An Interface for System Models.
 * <p>
 * Created by lamd on 12/9/2016.
 */
public interface ISystemModel {
    /**
     * Returns an Iterable of Class Models contained within the System Model.
     *
     * @return Iterable of Class Models.
     */
    Set<? extends IClassModel> getClasses();

    /**
     * Set the class set of systemModel
     *
     * @param classSet
     */
    void setClasses(Set<IClassModel> classSet);

    /**
     * Add node style to certain class,
     *
     * @param clazz the class to add style for
     * @param key   the key of Graphviz style
     * @param value the value of Graphviz style
     */
    void addClassModelStyle(IClassModel clazz, String key, String value);

    /**
     * Add a stereotypes to class model If stereotype is an empty string, it
     * would ignore and discard it
     *
     * @param clazz      to add stereotype to
     * @param stereotype the stereotype String
     */
    void addClassModelStereotype(IClassModel clazz, String stereotype);

    /**
     * Add style to a relation
     *
     * @param from   the edge from here
     * @param to     the edge end here
     * @param relKey an key that identifies the type of relation. It should be an
     *               unique string
     * @param key    the key of Graphviz style
     * @param value  the value of Graphviz style
     */
    void addStyleToRelation(IClassModel from, IClassModel to, String relKey, String key, String value);

    /**
     * Add a certain relation with specific styleMap
     *
     * @param from     the edge from here
     * @param to       the edge end here
     * @param relKey   an key that identifies the type of relation. It should be an
     *                 unique string
     * @param styleMap the styleMap with graphviz styles
     */
    void addRelation(IClassModel from, IClassModel to, String relKey, StyleMap styleMap);

    /**
     * @return the relation maps within this SytemModel
     */
    Map<ClassPair, Map<String, StyleMap>> getRelations();

    /**
     * @param clazz
     * @return the node style of a specific class
     */
    String getNodeStyle(IClassModel clazz);

    /**
     * @param c
     * @return the stereotypes list of a specific class
     */
    Set<String> getStereoTypes(IClassModel c);
}
