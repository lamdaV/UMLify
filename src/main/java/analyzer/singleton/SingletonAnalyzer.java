package analyzer.singleton;

import analyzer.utility.*;
import config.IConfiguration;
import utility.MethodType;
import utility.Modifier;

import java.util.Collection;

/**
 * A Singleton Pattern Analyzer. It will add the sterotype Singleton to each detected class and shade its outline color (default: blue).
 */
public class SingletonAnalyzer implements IAnalyzer {
    @Override
    public void analyze(ISystemModel systemModel, IConfiguration iConfig) {
        SingletonConfiguration config = iConfig.createConfiguration(SingletonConfiguration.class);
        systemModel.getClasses().stream()
                .filter(this::checkSingleton)
                .forEach((clazz) -> {
                    systemModel.addClassModelStyle(clazz, "color", config.getColor());
                    systemModel.addClassModelStereotype(clazz, "Singleton");
                });
    }

    /**
     * We use the following rules to determine if a class is a singleton:
     * <p>
     * 1. It has one private constructor 2. It has one static field of itself 3.
     * There is a nonprivate getter for this singleton 4. Either the nonprivate
     * getter or static initializer
     *
     * @param clazz
     * @return
     */
    private boolean checkSingleton(IClassModel clazz) {
        // check all methods to make sure there is only private constructor
        Collection<? extends IMethodModel> methods = clazz.getMethods();
        IMethodModel privateConstructor = null;
        for (IMethodModel method : methods) {
            if (method.getMethodType() == MethodType.CONSTRUCTOR) {
                if (method.getModifier() == Modifier.PRIVATE && privateConstructor == null) {
                    privateConstructor = method;
                } else {
                    // non private constructor or multiple constructor
                    return false;
                }
            }
        }

        // look for the static instance of singleton
        Collection<? extends IFieldModel> fields = clazz.getFields();
        IFieldModel staticSingletonField = null;
        for (IFieldModel field : fields) {
            if (clazz.equals(field.getClassModel())) {
                if (field.isStatic() && field.getModifier() == Modifier.PRIVATE && staticSingletonField == null) {
                    staticSingletonField = field;
                } else {
                    // more than two staticSingletonField
                    return false;
                }
            }
        }
        // no static field
        if (staticSingletonField == null)
            return false;

        IMethodModel staticGetInstanceMethod = null;
        for (IMethodModel method : methods) {
            if (clazz.equals(method.getReturnType())) {
                if (method.getModifier() != Modifier.PRIVATE) {
                    if (method.isStatic() && staticGetInstanceMethod == null) {
                        staticGetInstanceMethod = method;
                    } else {
                        // more than two staticGetInstanceMethod
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
