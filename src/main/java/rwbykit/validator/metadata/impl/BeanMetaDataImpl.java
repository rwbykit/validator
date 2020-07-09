package rwbykit.validator.metadata.impl;

import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import rwbykit.validator.metadata.BeanDescriptor;
import rwbykit.validator.metadata.BeanMetaData;

public class BeanMetaDataImpl<T> implements BeanMetaData<T> {

    private final BeanDescriptor<T> beanDescriptor;
    private final ConstraintHelper constraintHelper;

    public BeanMetaDataImpl(Class<T> classType, ConstraintHelper constraintHelper) {
        this.constraintHelper = constraintHelper;
        this.beanDescriptor = makeBeanDescriptor(classType);
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
        return new BeanDescriptorImpl<T>(classType, constraintHelper);
    }

}
