package analyzer.utility;

import utility.ClassType;

import java.util.Collection;

/**
 * a filter for IClassModel
 *
 * @author zhang
 */
public abstract class IClassModelFilter implements IClassModel {
    private final IClassModel classModel;
    private final IClassModel underlyingModel;

    /**
     * Constructs a Class Model Filter
     *
     * @param classModel classModel decorated.
     */
    public IClassModelFilter(IClassModel classModel) {
        this.classModel = classModel;
        this.underlyingModel = classModel.getUnderlyingClassModel();
    }

    public String getName() {
        return classModel.getName();
    }

    public ClassType getType() {
        return classModel.getType();
    }

    public boolean isFinal() {
        return classModel.isFinal();
    }

    public boolean isStatic() {
        return classModel.isStatic();
    }

    public boolean isSynthetic() {
        return classModel.isSynthetic();
    }

    public IClassModel getSuperClass() {
        return classModel.getSuperClass();
    }

    public Collection<? extends IClassModel> getInterfaces() {
        return classModel.getInterfaces();
    }

    public Collection<? extends IFieldModel> getFields() {
        return classModel.getFields();
    }

    public Collection<? extends IMethodModel> getMethods() {
        return classModel.getMethods();
    }

    @Override
    public IClassModel getUnderlyingClassModel() {
        return underlyingModel;
    }

    @Override
    public String toString() {
        return classModel.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IClassModel) {
            IClassModel c = (IClassModel) obj;
            return getUnderlyingClassModel().equals(c.getUnderlyingClassModel());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getUnderlyingClassModel().hashCode();
    }
}
