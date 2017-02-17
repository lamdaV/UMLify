package analyzer.staticInitializer;

import analyzer.utility.IAnalyzer;
import analyzer.utility.IClassModel;
import analyzer.utility.ISystemModel;
import config.IConfiguration;
import utility.MethodType;

public class StaticInitializerAnalyzer implements IAnalyzer {
    @Override
    public void analyze(ISystemModel systemModel, IConfiguration iConfig) {
        StaticInitializerConfiguration config = iConfig.createConfiguration(StaticInitializerConfiguration.class);

        systemModel.getClasses().stream()
                .filter(this::hasStaticInitializer)
                .forEach((clazz) -> systemModel.addClassModelStyle(clazz, "color", config.getColor()));
    }

    private boolean hasStaticInitializer(IClassModel clazz) {
        return clazz.getMethods().stream()
                .anyMatch((method) -> method.getMethodType() == MethodType.STATIC_INITIALIZER);
    }
}
