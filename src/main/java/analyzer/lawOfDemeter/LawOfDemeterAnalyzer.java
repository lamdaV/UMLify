package lawOfDemeter;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import analyzer.relationParser.RelationDependsOn;
import analyzer.utility.IAnalyzer;
import analyzer.utility.IClassModel;
import analyzer.utility.IFieldModel;
import analyzer.utility.IInstructionModel;
import analyzer.utility.IMethodModel;
import analyzer.utility.ISystemModel;
import analyzer.utility.ITypeModel;
import config.IConfiguration;
import model.InstructionField;
import model.InstructionMethod;
import utility.MethodType;

public class LawOfDemeterAnalyzer implements IAnalyzer {
    private LawOfDemeterConfig config;

    @Override
    public void analyze(ISystemModel systemModel, IConfiguration iConfig) {
        config = iConfig.createConfiguration(LawOfDemeterConfig.class);
        for (IClassModel clazz : systemModel.getClasses()) {
            Set<IClassModel> classFrieds = getFriendList(clazz);
            for (IMethodModel method : clazz.getMethods()) {
                Set<IClassModel> friends = getFriendList(method);
                friends.addAll(classFrieds);
                checkForViolation(method, classFrieds, systemModel);
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
        if (clazz.getSuperClass() != null)
            friends.add(clazz.getSuperClass());
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
            if (belongTo != null && !isFriend(belongTo, friends))
                styleViolationTo(method.getBelongTo(), belongTo, systemModel);
        }
    }

    private IClassModel getCalledOn(IInstructionModel called) {
        ITypeModel value;
        try {
            if (called instanceof InstructionMethod) {
                Field field = InstructionMethod.class.getDeclaredField("calledOn");
                field.setAccessible(true);
                value = (ITypeModel) field.get(called);
            } else if (called instanceof InstructionField) {
                Field field = InstructionField.class.getDeclaredField("calledOn");
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
        for (IClassModel friend : friends) {
            if (clazz.isSubClazzOf(friend))
                return true;
        }
        return false;
    }

    private void styleViolationTo(IClassModel thisClass, IClassModel dependClass, ISystemModel systemModel) {
        Set<? extends IClassModel> classes = systemModel.getClasses();
        systemModel.addClassModelStyle(thisClass, "style", "filled");
        systemModel.addClassModelStyle(thisClass, "color", config.getFillColor());
        if (classes.contains(dependClass))
            systemModel.addStyleToRelation(thisClass, dependClass, RelationDependsOn.REL_KEY, "xlabel",
                    config.getDependsLabel());
    }

}
