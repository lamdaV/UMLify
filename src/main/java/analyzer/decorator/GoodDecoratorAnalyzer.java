package analyzer.decorator;

import analyzer.utility.IClassModel;
import analyzer.utility.IFieldModel;
import analyzer.utility.IMethodModel;
import config.IConfiguration;

import java.util.Set;

/**
 * A Good Decorator Pattern Analyzer. It will highlight in green all suspected
 * decorator classes in green (default).
 * <p>
 * Created by lamd on 2/2/2017.
 */
public class GoodDecoratorAnalyzer extends DecoratorTemplate {
    protected boolean isFieldCalled(IClassModel parent, IMethodModel method) {
        return method.getAccessedFields().stream().map(IFieldModel::getFieldType)
                .anyMatch((type) -> type.equals(parent));
    }

    @Override
    protected IAdapterDecoratorConfiguration setupConfig(IConfiguration config) {
        return config.createConfiguration(GoodDecoratorConfiguration.class);
    }

    @Override
    protected boolean detectPattern(IClassModel clazz, IClassModel composedClazz, IClassModel parent,
                                    Set<IMethodModel> overridingMethods) {
        return composedClazz.isSubClazzOf(parent);
    }

}
