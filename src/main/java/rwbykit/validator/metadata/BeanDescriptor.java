package rwbykit.validator.metadata;

import java.util.Set;

public interface BeanDescriptor<T> {

    Set<PropertyDescriptor> getPropertyDescriptors();

    boolean isBeanConstrained();

    PropertyDescriptor getPropertyDescriptor(String propertyName);
    
    Class<T> classType();

}
