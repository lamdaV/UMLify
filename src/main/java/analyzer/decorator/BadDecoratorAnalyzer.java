package analyzer.decorator;

import analyzer.decorator.BadDecoratorConfiguration;
import analyzer.decorator.DecoratorTemplate;
import analyzer.decorator.IAdapterDecoratorConfiguration;
import analyzer.utility.IClassModel;
import analyzer.utility.IMethodModel;
import config.IConfiguration;
import utility.MethodType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Bad Decorator Analyzer. It will highlight classes that are likely
 * decorators that are incomplete in yellow (default).
 * <p>
 * Created by lamd on 2/2/2017.
 */
public class BadDecoratorAnalyzer extends DecoratorTemplate {
    @Override
    protected Set<IMethodModel> getMappedMethods(IClassModel child, IClassModel composedClass, IClassModel parent) {
        Collection<IMethodModel> overridedMethods = new ArrayList<>();
        for (IMethodModel m : parent.getMethods()) {
            if (m.getMethodType() == MethodType.METHOD || m.getMethodType() == MethodType.ABSTRACT)
                overridedMethods.add(m);
        }
        Set<IMethodModel> overridingMethods = new HashSet<>();
        for (IMethodModel m : child.getMethods()) {
            if (m.getMethodType() == MethodType.METHOD) {
                if (isDecoratedMethod(m, overridedMethods))
                    overridingMethods.add(m);
            }
        }

        if (overridingMethods.size() == overridedMethods.size()) {
            return null;
        }
        return overridingMethods;
    }

    @Override
    protected IAdapterDecoratorConfiguration setupConfig(IConfiguration config) {
        return config.createConfiguration(BadDecoratorConfiguration.class);
    }

    @Override
    protected boolean detectPattern(IClassModel clazz, IClassModel composedClass, IClassModel parent,
                                    Set<IMethodModel> overridingMethods) {
        return !clazz.equals(parent) && composedClass.isSubClazzOf(parent);
    }
}
