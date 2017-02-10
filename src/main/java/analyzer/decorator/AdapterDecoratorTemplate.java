package analyzer.decorator;

import analyzer.relationParser.RelationHasA;
import analyzer.utility.*;
import config.IConfiguration;
import utility.MethodType;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An Abstract Template for Adapter and Decorator pattern detection.
 * <p>
 * Created by lamd on 2/2/2017.
 */
public abstract class AdapterDecoratorTemplate implements IAnalyzer {
    protected IAdapterDecoratorConfiguration config;

    @Override
    public final void analyze(ISystemModel systemModel, IConfiguration config) {
        this.config = setupConfig(config);
        systemModel.getClasses().forEach((clazz) -> {
            Collection<IClassModel> potentialParents = getPotentialParents(clazz, systemModel);
            Collection<IClassModel> potentialFields = getPotentialComposition(clazz, systemModel);
            potentialParents.stream().forEach((parent) -> {
                potentialFields.stream().forEach((compClazz) -> {
                    Set<IMethodModel> overridingMethods = methodsMapped(clazz, compClazz, parent);
                    if (overridingMethods != null && detectPattern(clazz, compClazz, parent, overridingMethods)) {
                        styleParent(systemModel, parent);
                        styleChild(systemModel, clazz);
                        styleComposedClass(systemModel, compClazz);
                        styleChildParentRelationship(systemModel, clazz, parent);
                        styleComposedClassRelationship(systemModel, clazz, compClazz);
                        updateRelatedClasses(systemModel, clazz, compClazz, parent);
                    }
                });
            });
        });
    }

    protected Set<IMethodModel> methodsMapped(IClassModel child, IClassModel composedClazz, IClassModel parent) {
        Collection<? extends IMethodModel> overridedMethods = parent.getMethods().stream()
                .filter((method) -> method.getMethodType() == MethodType.METHOD).collect(Collectors.toList());

        Set<IMethodModel> overridingMethods = new HashSet<>();
        child.getMethods().stream()
                .filter((method) -> method.getMethodType() == MethodType.METHOD
                        && isDecoratedMethod(method, overridedMethods) && isFieldCalled(composedClazz, method))
                .forEach(overridingMethods::add);

        if (overridingMethods.size() != overridedMethods.size()) {
            return null;
        }
        return overridingMethods;
    }

    private boolean isFieldCalled(IClassModel composedClazz, IMethodModel method) {
        return method.getAccessedFields().stream().map(IFieldModel::getFieldType)
                .anyMatch((type) -> type.equals(composedClazz));
    }

    protected void styleComposedClassRelationship(ISystemModel systemModel, IClassModel clazz, IClassModel composedClazz) {
        // Hook.
    }

    protected void styleComposedClass(ISystemModel systemModel, IClassModel classModel) {
        // Hook.
    }

    protected Collection<IClassModel> getPotentialComposition(IClassModel clazz, ISystemModel systemModel) {
        Set<? extends IClassModel> classes = systemModel.getClasses();

//        Collection<IClassModel> potentialComposed = new LinkedList<>();
//        clazz.getFields().forEach((f) -> {
//            IClassModel composeClazz = f.getClassModel();
//            if (composeClazz != null && classes.contains(composeClazz)) {
//                if (clazz.getMethods().stream()
//                        .filter((method) -> method.getMethodType() == MethodType.CONSTRUCTOR)
//                        .flatMap((method) -> method.getArguments().stream()).anyMatch(composeClazz::equals)) {
//                    potentialComposed.add(composeClazz);
//                }
//            }
//        });
//
//        return potentialComposed;
        return clazz.getFields().stream()
                .map(IFieldModel::getClassModel)
                .filter((composedClass) -> composedClass != null && classes.contains(composedClass))
                .filter((composedClass) -> clazz.getMethods().stream()
                        .filter((method) -> method.getMethodType() == MethodType.CONSTRUCTOR)
                        .flatMap((method) -> method.getArguments().stream()).anyMatch(composedClass::equals))
                .collect(Collectors.toSet());
    }

    protected Collection<IClassModel> getPotentialParents(IClassModel child, ISystemModel systemModel) {
        Set<? extends IClassModel> classes = systemModel.getClasses();

        Collection<IClassModel> potentialParents = new LinkedList<>();
        addToSet(classes, potentialParents, child.getSuperClass());
        child.getInterfaces().forEach((inter) -> addToSet(classes, potentialParents, inter));

        return potentialParents;
    }

    private void addToSet(Set<? extends IClassModel> classes, Collection<IClassModel> potentialParents,
                          IClassModel clazz) {
        if (classes.contains(clazz)) {
            potentialParents.add(clazz);
        }
    }


    protected abstract IAdapterDecoratorConfiguration setupConfig(IConfiguration config);

    /**
     * Returns if the given method is a decorated method of some parent class.
     *
     * @param method        IMethodModel of child class.
     * @param parentMethods Collection of IMethodModels of parent methods.
     * @return true if the given method decorates a parentMethods.
     */
    protected boolean isDecoratedMethod(IMethodModel method, Collection<? extends IMethodModel> parentMethods) {
        return parentMethods.stream()
                .anyMatch(method::hasSameSignature);
    }

    /**
     * A utility method to apply a common fill color style to a given
     * classModel.
     *
     * @param systemModel ISystemModel maintaining class styles.
     * @param classModel  IClassModel classModel to by styled.
     */
    protected void addCommonFillColor(ISystemModel systemModel, IClassModel classModel) {
        systemModel.addClassModelStyle(classModel, "style", "filled");
        systemModel.addClassModelStyle(classModel, "fillcolor", this.config.getFillColor());
    }

    /**
     * Define how to style the parent of the Child-to-Parent Relationship.
     *
     * @param systemModel ISystemModel that stores the styling information per class.
     * @param parent      IClassModel of the parent class in the Child-to-Parent
     *                    Relationship.
     */
    protected void styleParent(ISystemModel systemModel, IClassModel parent) {
        addCommonFillColor(systemModel, parent);
        systemModel.addClassModelSteretypes(parent, this.config.getParentStereotype());
    }

    /**
     * Define how to style the child of the Child-to-Parent Relationship.
     *
     * @param systemModel ISystemModel that stores the styling information per class.
     * @param child       IClassModel of the child class in the Child-to-Parent
     *                    Relationship.
     */
    protected void styleChild(ISystemModel systemModel, IClassModel child) {
        addCommonFillColor(systemModel, child);
        systemModel.addClassModelSteretypes(child, this.config.getChildStereotype());
    }

    /**
     * Define how to style the Child-to-Parent Relationship.
     *
     * @param systemModel ISystemModel that stores the styling information per class.
     * @param child       IClassModel of a child of a Superclass relation.
     * @param parent      IClassModel of a parent of a Superclass relation.
     */
    protected void styleChildParentRelationship(ISystemModel systemModel, IClassModel child, IClassModel parent) {
        systemModel.addStyleToRelation(child, parent, RelationHasA.REL_KEY, "xlabel",
                this.config.getChildParentRelationshipLabel());
    }

    /**
     * Updates Classes related the the class clazz if necessary. This is a hook
     * method.
     *
     * @param systemModel   ISystemModel that stores the styling information per class.
     * @param clazz         IClassModel of the child class with possible subclasses to be
     *                      updated.
     * @param parent        the parent class in this pattern
     * @param composedClazz the class composed in clazz
     */
    protected void updateRelatedClasses(ISystemModel systemModel, IClassModel clazz, IClassModel composedClazz,
                                        IClassModel parent) {
        // Hook
    }

    /**
     * Evaluates a given parent class and the child class and determine whether
     * they meet the desired pattern criteria.
     * <p>
     * For example: decorator detection may check if child has a field of the
     * parent, a constructor that takes the field as an argument, and if the
     * child overrides each of the parent's methods where the child method's
     * body uses the field of the parent type.
     *
     * @param clazz             IClassModel of the dependent Relation.
     * @param composedClazz     the field for this pattern
     * @param parent            IClassModel of the depended Relation.
     * @param overridingMethods the methods of parent that gets overrided in clazz. Those
     *                          methods belong to clazz.
     * @return true if the parent and child should be updated for this analyzer.
     */
    protected abstract boolean detectPattern(IClassModel clazz, IClassModel composedClazz, IClassModel parent,
                                             Set<IMethodModel> overridingMethods);
}
