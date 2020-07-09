package rwbykit.validator.metadata.impl;

import org.hibernate.validator.internal.engine.DefaultPropertyNodeNameProvider;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.properties.DefaultGetterPropertySelectionStrategy;
import org.hibernate.validator.internal.properties.javabean.JavaBeanHelper;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.springframework.util.ObjectUtils;
import rwbykit.validator.metadata.BeanDescriptor;
import rwbykit.validator.metadata.PropertyDescriptor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BeanDescriptorImpl<T> implements BeanDescriptor<T> {

    private final Class<T> classType;
    private final Map<String, PropertyDescriptor> propertyDescriptors;
    private final ConstraintHelper constraintHelper;
    private final JavaBeanHelper javaBeanHelper;

    public BeanDescriptorImpl(Class<T> classType, ConstraintHelper constraintHelper, JavaBeanHelper javaBeanHelper) {
        this.classType = classType;
        this.constraintHelper = constraintHelper;
        this.javaBeanHelper = javaBeanHelper;
        this.propertyDescriptors = buildPropertyDescriptors(classType);
    }

    @Override
    public Set<PropertyDescriptor> getPropertyDescriptors() {
        return CollectionHelper.toImmutableSet(CollectionHelper.newHashSet(this.propertyDescriptors.values()));
    }

    @Override
    public boolean isBeanConstrained() {
        return this.propertyDescriptors.values().parallelStream().anyMatch(PropertyDescriptor::hasConstraints);
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String propertyName) {
        return propertyDescriptors.get(propertyName);
    }

    private Map<String, PropertyDescriptor> buildPropertyDescriptors(Class<?> classType) {
        Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>(8);
        Arrays.stream(classType.getDeclaredFields())
                .filter(this::hasAnnotation)
                .forEach(field -> propertyDescriptorMap.put(field.getName(), PropertyDescriptorImpl.of(ConstraintLocation.forField(javaBeanHelper.field(field)), constraintHelper)));
        return CollectionHelper.toImmutableMap(propertyDescriptorMap);
    }

    private boolean hasAnnotation(Field field) {
        return !ObjectUtils.isEmpty(field.getDeclaredAnnotations());
    }

	@Override
	public Class<T> classType() {
		return this.classType;
	}


}
