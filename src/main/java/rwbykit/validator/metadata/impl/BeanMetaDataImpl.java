package rwbykit.validator.metadata.impl;

import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.properties.javabean.JavaBeanHelper;
import rwbykit.validator.metadata.BeanDescriptor;
import rwbykit.validator.metadata.BeanMetaData;

public class BeanMetaDataImpl<T> implements BeanMetaData<T> {

    private final BeanDescriptor<T> beanDescriptor;
    private final ConstraintHelper constraintHelper;
    private final JavaBeanHelper javaBeanHelper;

    private BeanMetaDataImpl(Class<T> classType, ConstraintHelper constraintHelper, JavaBeanHelper javaBeanHelper) {
        this.constraintHelper = constraintHelper;
        this.javaBeanHelper = javaBeanHelper;
        this.beanDescriptor = makeBeanDescriptor(classType);
    }

    public static <T> BeanMetaData<T> of(Class<T> classType, ConstraintHelper constraintHelper, JavaBeanHelper javaBeanHelper) {
        return new BeanMetaDataImpl<T>(classType, constraintHelper, javaBeanHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getBeanClass() {
        return (Class<T>) beanDescriptor.getClass();
    }

    @Override
    public boolean hasConstraints() {
        return beanDescriptor.isBeanConstrained();
    }

    @Override
    public BeanDescriptor<T> getBeanDescriptor() {
        return this.beanDescriptor;
    }

    protected BeanDescriptor<T> makeBeanDescriptor(Class<T> classType) {
        return new BeanDescriptorImpl<T>(classType, constraintHelper, javaBeanHelper);
    }

}
