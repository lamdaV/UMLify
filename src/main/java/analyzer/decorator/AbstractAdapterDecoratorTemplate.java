package analyzer.decorator;

import analyzer.utility.*;
import config.IConfiguration;

import java.util.*;

/**
 * Created by lamd on 2/2/2017.
 */
public abstract class AbstractAdapterDecoratorTemplate implements IAnalyzer {
//    private Collection<IClassModel> getPotentialParents(Collection<? extends IClassModel> classes, IClassModel clazz) {
//        Collection<IClassModel> potentialParents = new LinkedList<>();
//        Collection<IClassModel> candidates = new LinkedList<>();
//
//        // Put clazz's super class and interfaces into the potential parent's Collection.
//        potentialParents.add(clazz.getSuperClass());
//        clazz.getInterfaces().forEach(potentialParents::add);
//
//        // Go through the potentialParents and find its matching ClassModel in classes;
//        for (IClassModel classModel : potentialParents) {
//            for (IClassModel fullModels : classes) {
//                if (fullModels.equals(classModel)) {
//                    candidates.add(fullModels);
//                    break;
//                }
//            }
//        }
//
//        return candidates;
//    }

    /**
     * Evaluates a given parent class and the child and detect whether they meet the desired pattern.
     * <p>
     * For example: decorator detection may check if child has a field of the parent,
     * a constructor that takes the field as an argument, and if the child overrides each of the parent's
     * methods where the child method's body uses the field of the parent type.
     *
     * @param child  IClassModel of the dependent Relation.
     * @param parent IClassModel of the depended Relation.
     * @return true if the parent and child should be updated for this analyzer.
     */
    protected abstract boolean evaluateParent(IClassModel child, IClassModel parent);

    /**
     * Constructs a IClassModel for all matched parents.
     *
     * @param validatedParent IClassModel of a class that has been validated by evaluateParent.
     * @return IClassModel for the depended relation.
     */
    protected abstract IClassModel createParentClassModel(IClassModel validatedParent);

    protected abstract void styleParent(IClassModel parent, ISystemModel systemModel);

    /**
     * Returns a Collection of ClassModel that are parents of the given ClassModel that fulfills the evaluation criteria
     * defined by the subclass.
     * <p>
     * The Collection may be empty.
     *
     * @param classes
     * @param clazz   IClassModel to be evaluated.
     * @return Collection of IClassModel of ParentClassModel defined by the subclass.
     */
    private void evaluateClass(ISystemModel systemModel, Collection<? extends IClassModel> classes, IClassModel clazz) {
//        Collection<IClassModel> potentialParents = getPotentialParents(classes, clazz);
        classes.stream()
                .filter((parent) -> evaluateParent(clazz, parent))      // Subclasses define how to filter.
                .forEach((parent) -> styleParent(parent, systemModel));     // Create a ClassModel for each of the filtered parents.
//                .collect(Collectors.toList());                          // Collect the results into a Collection List.
    }

//    private Map<IClassModel, Collection<IClassModel>> createUpdateMap(ISystemModel systemModel, Collection<? extends IClassModel> classes) {
////        Map<IClassModel, Collection<IClassModel>> updateMap = new HashMap<>();
//        evaluateClass(systemModel, cla);
//        for (IClassModel clazz : classes) {
////            updateMap.put(clazz, evaluateClass(systemModel, classes, clazz));
//            evaluateClass(systemModel, classes, clazz);
//        }
//
//        return updateMap;
    }

    /**
     * Constructs a IClassModel for all matched child.
     *
     * @param child IClassModel of a child with matched parents defined by evaluateParent.
     * @return IClassModel for the dependent relation.
     */
    protected abstract IClassModel createChildClassModel(IClassModel child);

    /**
     * Hook method for subclasses to override. This is intended for subclasses to add final edits to the classModel Collection
     * for the given clazz.
     * <p>
     * For example: If a user wishes to also identify classes that extends some abstract decorator, then this method should be
     * overriden and return an updated list of the IClassModel.
     *
     * @param updatedClasses Collection of updatedClasses
     * @param updateMap      Map of all classes.
     * @param clazz          IClassModel to be updated.  @return Updated Collection of IClassModel.
     */
    protected Set<IClassModel> updateRelatedClasses(Set<IClassModel> updatedClasses, Map<IClassModel, Collection<IClassModel>> updateMap, IClassModel clazz) {
        return updatedClasses;
    }

    private Set<? extends IClassModel> updateClasses(Map<IClassModel, Collection<IClassModel>> updateMap, ISystemModel systemModel) {
        Set<IClassModel> updatedClasses = new HashSet<>();

        Collection<IClassModel> matchedClasses;
        for (IClassModel clazz : updateMap.keySet()) {
            matchedClasses = updateMap.get(clazz);

            if (!matchedClasses.isEmpty()) {
                for (IClassModel match : matchedClasses) {
                    updatedClasses.add(match);
                    systemModel.addClassModelStyle(match, );
                }
                updatedClasses.add(createChildClassModel(clazz));

                // Hook.
                updatedClasses = updateRelatedClasses(updatedClasses, updateMap, clazz);
            } else {
                // It is a normal class if nothing is matched.
                if (!updatedClasses.contains(clazz)) {
                    updatedClasses.add(createParentClassModel(clazz));
                }
            }
        }

        return updatedClasses;
    }

    /**
     * Create a Relation between the child and parent ClassModel.
     *
     * @param info Current IRelationInfo between child to parent.
     * @return Decorated Relation between child and parent.
     */
    protected abstract IRelationInfo createRelation(IRelationInfo info);

    /**
     * Hook method for subclasses to override. This is intended for subclasses to add final edits to the Relations Map for
     * the given clazz.
     * <p>
     * For example: If a user wishes to also modify the relations of classes that extends some detected abstract decorator,
     * then this method should be overriden and return an Relations Map.
     *
     * @param updatedRelations Map of UpdatedRelations.
     * @param clazz            IClassModel of relation to update Map with.
     * @return Updated Relations Map with changes of clazz.
     */
    protected Map<ClassPair, List<IRelationInfo>> updateRelatedRelations(Map<ClassPair, List<IRelationInfo>> updatedRelations, IClassModel clazz) {
        return updatedRelations;
    }

    private Map<ClassPair, List<IRelationInfo>> updateRelations(Map<IClassModel, Collection<IClassModel>> updateMap, Map<ClassPair, List<IRelationInfo>> relations) {
        // Create the copy of the relations Map.
        Map<ClassPair, List<IRelationInfo>> updatedRelations = new HashMap<>();
        updatedRelations.putAll(relations);

        Collection<IClassModel> matchedClasses;
        ClassPair pair;
        List<IRelationInfo> infos, newInfos;
        for (IClassModel clazz : updateMap.keySet()) {
            matchedClasses = updateMap.get(clazz);

            // If match is not empty, this loop will update the updatedRelations
            // corresponding clazz -> match ClassPair.
            if (!matchedClasses.isEmpty()) {
                for (IClassModel match : matchedClasses) {
                    pair = new ClassPair(clazz, match);

                    newInfos = new LinkedList<>();
                    infos = relations.get(pair);
                    for (IRelationInfo info : infos) {
                        newInfos.add(createRelation(info));
                    }

                    updatedRelations.put(pair, newInfos);
                }

                // Hook: Allow subclasses to modify relations related to clazz.
                updatedRelations = updateRelatedRelations(updatedRelations, clazz);
            }
        }

        return updatedRelations;
    }

    private void updateClasses(ISystemModel systemModel, Set<? extends IClassModel> classes) {
        classes.forEach((clazz) -> evaluateClass(systemModel, clazz));

    @Override
    public final void analyze(ISystemModel systemModel, IConfiguration config) {
        Set<? extends IClassModel> classes = systemModel.getClasses();

//        Map<IClassModel, Collection<IClassModel>> updateMap = createUpdateMap(classes);
        classes = updateClasses(classes);
        relations = updateRelations(updateMap, relations);
    }
}
