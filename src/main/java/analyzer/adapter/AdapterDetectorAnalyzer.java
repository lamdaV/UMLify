package analyzer.adapter;

import analyzer.utility.IClassModel;
import analyzer.utility.IMethodModel;
import config.IConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An Adapter Pattern Analyzer. It will highlight in some color (defaulted to
 * red) all classes part of the Adapter pattern instance.
 * <p>
 * Created by fineral on 2/9/2017.
 */
public class AdapterDetectorAnalyzer extends AdapterDecoratorTemplate {

    @Override
    protected IAdapterDecoratorConfiguration setupConfig(IConfiguration config) {
        return config.createConfiguration(AdapterDetectorConfiguration.class);
    }

    @Override
    protected boolean detectPattern(IClassModel clazz, IClassModel composedClazz, IClassModel parent,
            Set<IMethodModel> overridingMethods) {
        if (clazz.equals(parent) || clazz.isSubClazzOf(composedClazz) || composedClazz.isSubClazzOf(parent))
            return false;
        return usedByAllAdaptedMethods(composedClazz, overridingMethods);
    }

    private boolean usedByAllAdaptedMethods(IClassModel type, Set<IMethodModel> adaptedMethods) {
        Set<IMethodModel> methodCalledOnTheField = new HashSet<>();
        for (IMethodModel method : adaptedMethods) {
            Collection<? extends IMethodModel> methodAccessed = method.getCalledMethods().stream()
                    .filter((m) -> m.getBelongTo().equals(type)).collect(Collectors.toList());
            methodCalledOnTheField.addAll(methodAccessed);
        }
        return methodCalledOnTheField.size() == adaptedMethods.size();
    }

}
