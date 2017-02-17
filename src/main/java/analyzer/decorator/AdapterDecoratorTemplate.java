package analyzer.decorator;

import analyzer.relationParser.RelationExtendsClass;
import analyzer.relationParser.RelationHasA;
import analyzer.utility.*;
import config.IConfiguration;
import utility.MethodType;

import java.util.*;

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
        for (IClassModel clazz : systemModel.getClasses()) {
            Collection<IClassModel> potentialParents = getPotentialParents(clazz, systemModel);
            Collection<IClassModel> potentialFields = getPotentialComposition(clazz, systemModel);
            evaluateClass(systemModel, clazz, potentialParents, potentialFields);
        }
    }

    /**
     * Returns a Collection of IClassModel that are potentially a composed
     * class.
     *
     * @param clazz       IClassModel of class for which to get potential composed
     *                    class.
     * @param systemModel ISystemModel that holds all classes and style information.
     * @return Collection of IClassModel that are potentially a composed class.
     */
    protected Collection<IClassModel> getPotentialComposition(IClassModel clazz, ISystemModel systemModel) {
        Set<? extends IClassModel> classes = systemModel.getClasses();
        Collection<IClassModel> potentialComposed = new LinkedList<>();
        for (IFieldModel f : clazz.getFields()) {
            IClassModel composeClazz = f.getClassModel();
            if (composeClazz != null && classes.contains(composeClazz)) {
                if (appearInConstructor(clazz, composeClazz))
                    potentialComposed.add(composeClazz);
            }
        }
        return potentialComposed;

    }

    /**
     * Returns a Collection of IClassModel of potential parents.
     *
     * @param child       IClassModel child in the child-parent super class relation.
     * @param systemModel ISystemModel storing the style class information.
     * @return Collection of potential parents.
     */
    protected Collection<IClassModel> getPotentialParents(IClassModel child, ISystemModel systemModel) {
        Set<? extends IClassModel> classes = systemModel.getClasses();

        Collection<IClassModel> potentialParents = new LinkedList<>();
        addToSet(child, potentialParents, classes);
        return potentialParents;
    }

    private void evaluateClass(ISystemModel systemModel, IClassModel clazz, Collection<IClassModel> potentialParents,
                               Collection<IClassModel> potentialFields) {
        for (IClassModel parent : potentialParents) {
            for (IClassModel compClazz : potentialFields) {
                Set<IMethodModel> overridingMethods = getMappedMethods(clazz, compClazz, parent);
                if (overridingMethods != null && detectPattern(clazz, compClazz, parent, overridingMethods)) {
                    styleParent(systemModel, parent);
                    styleChild(systemModel, clazz);
                    styleComposedClass(systemModel, compClazz);
                    styleChildParentRelationship(systemModel, clazz, parent);
                    styleComposedClassRelationship(systemModel, clazz, compClazz);
                    updateRelatedClasses(systemModel, clazz, compClazz, parent);
                }
            }
        }
    }

    /**
     * Returns a Set of IMethodModel that child class has in common with the
     * parent class.
     *
     * @param child         IClassModel child in the Child-Parent in a super class
     *                      relation.
     * @param composedClass IFieldModel's underlying class of the child.
     * @param parent        IClassModel parent in the Child-Parent in a super class
     *                      relation.
     * @return Set of IMethodModel that child class has in common with the
     * parent class.
     */
    protected Set<IMethodModel> getMappedMethods(IClassModel child, IClassModel composedClass, IClassModel parent) {
        Collection<IMethodModel> overridedMethods = new ArrayList<>();
        for (IMethodModel m : parent.getMethods()) {
            if (m.getMethodType() == MethodType.METHOD || m.getMethodType() == MethodType.ABSTRACT)
                overridedMethods.add(m);
        }
        Set<IMethodModel> overridingMethods = new HashSet<>();
        for (IMethodModel m : child.getMethods()) {
            if (m.getMethodType() == MethodType.METHOD) {
                if (isDecoratedMethod(m, overridedMethods) && isFieldCalled(composedClass, m))
                    overridingMethods.add(m);
            }
        }

        if (overridingMethods.size() != overridedMethods.size()) {
            return null;
        }
        return overridingMethods;
    }

    private boolean isFieldCalled(IClassModel composedClazz, IMethodModel method) {
        for (IFieldModel f : method.getAccessedFields()) {
            if (composedClazz.equals(f.getClassModel())) {
                return true;
            }
        }
        return false;
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

    private boolean appearInConstructor(IClassModel clazz, IClassModel composeClazz) {
        for (IMethodModel method : clazz.getMethods()) {
            if (method.getMethodType() == MethodType.CONSTRUCTOR) {
                for (ITypeModel args : method.getArguments()) {
                    if (composeClazz.equals(args.getClassModel())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addToSet(IClassModel clazz, Collection<IClassModel> set, Set<? extends IClassModel> classes) {
        if (clazz != null && classes.contains(clazz)) {
            set.add(clazz);
            addToSet(clazz.getSuperClass(), set, classes);
            clazz.getInterfaces().forEach((inter) -> addToSet(inter, set, classes));
        }
    }

    /**
     * Returns a setup IAdapterDecoratorConfiguration.
     *
     * @param config IConfiguration passed into analyze method.
     * @return IAdapterDecoratorConfiguration.
     */
    protected abstract IAdapterDecoratorConfiguration setupConfig(IConfiguration config);

    /**
     * Returns if the given method is a decorated method of some parent class.
     *
     * @param method        IMethodModel of child class.
     * @param parentMethods Collection of IMethodModels of parent methods.
     * @return true if the given method decorates a parentMethods.
     */
    protected boolean isDecoratedMethod(IMethodModel method, Collection<? extends IMethodModel> parentMethods) {
        return parentMethods.stream().anyMatch(method::hasSameSignature);
    }

    /**
     * Style the composed class
     *
     * @param systemModel   ISystemModel holding the style information.
     * @param composedClazz IClassModel of the composedClass.
     */
    protected void styleComposedClass(ISystemModel systemModel, IClassModel composedClazz) {
        addCommonFillColor(systemModel, composedClazz);
        systemModel.addClassModelStereotype(composedClazz, this.config.getComposedStereotype());
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
        systemModel.addClassModelStereotype(parent, this.config.getParentStereotype());
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
        systemModel.addClassModelStereotype(child, this.config.getChildStereotype());
    }

    /**
     * Style the composed class's Relationship.
     *
     * @param systemModel   ISystemModel holding the style information.
     * @param child         IClassModel the composedClass is composed within.
     * @param composedClazz IClassModel of the composedClass.
     */
    protected void styleComposedClassRelationship(ISystemModel systemModel, IClassModel child,
                                                  IClassModel composedClazz) {
        systemModel.addStyleToRelation(child, composedClazz, RelationHasA.REL_KEY, "xlabel",
                this.config.getChildComposedRelationshipLabel());
    }

    /**
     * Define how to style the Child-to-Parent Relationship.
     *
     * @param systemModel ISystemModel that stores the styling information per class.
     * @param clazz       IClassModel of a child of a Superclass relation.
     * @param parent      IClassModel of a parent of a Superclass relation.
     */
    protected void styleChildParentRelationship(ISystemModel systemModel, IClassModel clazz, IClassModel parent) {
        systemModel.addStyleToRelation(clazz, parent, RelationExtendsClass.REL_KEY, "xlabel",
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
}
