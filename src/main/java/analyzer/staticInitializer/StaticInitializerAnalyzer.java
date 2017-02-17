package staticInitializer;

import analyzer.utility.IAnalyzer;
import analyzer.utility.IClassModel;
import analyzer.utility.IMethodModel;
import analyzer.utility.ISystemModel;
import config.IConfiguration;
import utility.MethodType;

public class StaticInitializerAnalyzer implements IAnalyzer{
	
	@Override
	public void analyze(ISystemModel systemModel, IConfiguration iConfig) {
		StaticInitializerConfiguration config = iConfig.createConfiguration(StaticInitializerConfiguration.class);

        systemModel.getClasses().stream()
                .filter(this::hasStaticInitializer)
                .forEach((clazz) -> {
                    systemModel.addClassModelStyle(clazz, "color", config.getFavorComColor());
                });
	}
	
	private boolean hasStaticInitializer(IClassModel clazz) {
		for(IMethodModel method :clazz.getMethods()){
        	if(method.getMethodType() == MethodType.STATIC_INITIALIZER){
        		return true;
        	}
        }
		return false;
    }

}
