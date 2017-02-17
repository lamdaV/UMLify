package analyzer.lawOfDemeter;

import analyzer.relationParser.RelationDependsOn;
import analyzer.utility.*;
import config.IConfiguration;
import model.InstructionField;
import model.InstructionMethod;
import utility.MethodType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class LawOfDemeterAnalyzer implements IAnalyzer {
    private LawOfDemeterConfiguration config;

    @Override
    public void analyze(ISystemModel systemModel, IConfiguration iConfig) {
        config = iConfig.createConfiguration(LawOfDemeterConfiguration.class);
        for (IClassModel clazz : systemModel.getClasses()) {
            Set<IClassModel> classFriends = getFriendList(clazz);
            for (IMethodModel method : clazz.getMethods()) {
                Set<IClassModel> friends = getFriendList(method);
                friends.addAll(classFriends);
                checkForViolation(method, classFriends, systemModel);
            }
        }
    }

    private Set<IClassModel> getFriendList(IClassModel clazz) {
        Set<IClassModel> friends = new HashSet<>();
        friends.add(clazz);
        for (IFieldModel fieldModel : clazz.getFields()) {
            ITypeModel typeModel = fieldModel.getFieldType();
            friends.addAll(typeModel.getDependentClass());
        }
        if (clazz.getSuperClass() != null) {
            friends.add(clazz.getSuperClass());
        }
        for (IClassModel intf : clazz.getInterfaces()) {
            friends.add(intf);
        }
        return friends;
    }

    private Set<IClassModel> getFriendList(IMethodModel method) {
        Set<IClassModel> friends = new HashSet<>();
        for (ITypeModel arg : method.getArguments()) {
            friends.addAll(arg.getDependentClass());
        }
        for (IMethodModel called : method.getCalledMethods()) {
            if (called.getMethodType() == MethodType.CONSTRUCTOR || called.getMethodType() == MethodType.STATIC) {
                friends.add(called.getBelongTo());
            }
        }
        return friends;
    }

    private void checkForViolation(IMethodModel method, Set<IClassModel> friends, ISystemModel systemModel) {
        for (IInstructionModel called : method.getInstructions()) {
            IClassModel belongTo = getCalledOn(called);
            if (belongTo != null && !isFriend(belongTo, friends)) {
                styleViolationTo(method.getBelongTo(), belongTo, systemModel);
            }
        }
    }

    private IClassModel getCalledOn(IInstructionModel called) {
        ITypeModel value;
        Field field;
        try {
            if (called instanceof InstructionMethod) {
                field = InstructionMethod.class.getDeclaredField("calledOn");
                field.setAccessible(true);
                value = (ITypeModel) field.get(called);
            } else if (called instanceof InstructionField) {
                field = InstructionField.class.getDeclaredField("calledOn");
                field.setAccessible(true);
                value = (ITypeModel) field.get(called);
            } else {
                throw new RuntimeException();
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
        return value.getClassModel();
    }

    private boolean isFriend(IClassModel clazz, Set<IClassModel> friends) {
        return friends.stream()
                .anyMatch(clazz::isSubClazzOf);
    }

    private void styleViolationTo(IClassModel thisClass, IClassModel dependClass, ISystemModel systemModel) {
        Set<? extends IClassModel> classes = systemModel.getClasses();
        systemModel.addClassModelStyle(thisClass, "style", "filled");
        systemModel.addClassModelStyle(thisClass, "color", config.getFillColor());
        if (classes.contains(dependClass)) {
            systemModel.addStyleToRelation(thisClass, dependClass, RelationDependsOn.REL_KEY, "xlabel",
                    config.getDependsLabel());
        }
    }
}
