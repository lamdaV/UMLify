package analyzer.decorator;

import analyzer.utility.IClassModel;
import analyzer.utility.ISystemModel;

/**
 * A Decorator Abstract class that contains basic utility methods used by both
 * Good and Bad Decorator Analyzers.
 * <p>
 * Created by lamd on 2/7/2017.
 */
public abstract class DecoratorTemplate extends AdapterDecoratorTemplate {
    @Override
    protected void updateRelatedClasses(ISystemModel systemModel, IClassModel decoratorClass, IClassModel composedClazz,
                                        IClassModel parent) {
        systemModel.getClasses().stream()
                .filter((classModel) -> decoratorClass.equals(classModel.getSuperClass()))
                .forEach((classModel) -> {
                    addCommonFillColor(systemModel, classModel);
                    systemModel.addClassModelSteretypes(classModel, this.config.getChildStereotype());
                });
    }
}
