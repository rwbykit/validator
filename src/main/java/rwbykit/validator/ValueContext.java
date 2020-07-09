package rwbykit.validator;

import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;
import rwbykit.validator.metadata.BeanDescriptor;
import rwbykit.validator.metadata.BeanMetaData;
import rwbykit.validator.metadata.PropertyDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Value Context object
 *
 * @author Cytus_
 */
public class ValueContext<T> {

    private final static Logger logger = LoggerFactory.getLogger(ValueContext.class);

    private final T currentBean;

    private final BeanDescriptor<T> beanDescriptor;

    private final Map<String, ConstraintLocation> locationMap;

    private final Map<String, Object> valueMap;


    private ValueContext(BeanMetaData<T> beanMetaData, T value) {
        this.currentBean = value;
        this.beanDescriptor = beanMetaData.getBeanDescriptor();
        this.locationMap = null;//CollectionHelper.toImmutableMap(makeValidationLocationMap());
        this.valueMap = BeanMap.create(value);
    }

    public static <T> ValueContext<T> of(BeanMetaData<T> beanMetaData, T value) {
        return new ValueContext<T>(beanMetaData, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V getPropertyValue(String name) {
        /*ConstraintLocation location = this.locationMap.get(name);
        if (Objects.nonNull(location)) {
            return (V) location.getValue(this.currentBean);
        }
        return null;*/
        return (V) this.valueMap.get(name);
    }

    public T getCurrentBean() {
        return this.currentBean;
    }

    public Class<T> getBeanType() {
        return this.beanDescriptor.classType();
    }

    /*private Map<String, ConstraintLocation> makeValidationLocationMap() {
        Set<PropertyDescriptor> propertyDescriptors = this.beanDescriptor.getPropertyDescriptors();
        Map<String, ConstraintLocation> locationMap = new HashMap<>(propertyDescriptors.size());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            ConstraintLocation location = createConstraintLocation(propertyDescriptor.getPropertyName(), this.beanDescriptor.classType());
            if (Objects.nonNull(location)) {
                locationMap.put(propertyDescriptor.getPropertyName(), location);
            }
        }
        return locationMap;
    }*/

    /*private ConstraintLocation createConstraintLocation(String propertyName, Class<?> classType) {
        try {
            return ConstraintLocation.
        } catch (NoSuchFieldException e) {
            logger.error("Class type[{}] not found field[{}]", classType.getName(), propertyName);
        }
        return null;
    }*/

}
