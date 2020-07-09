package rwbykit.validator;

import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import rwbykit.validator.metadata.BeanMetaData;
import rwbykit.validator.metadata.impl.BeanMetaDataImpl;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean metadata management class
 *
 * @author tangxb
 */
public class BeanMetaDataManager {

    private final static Map<Class<?>, BeanMetaData<?>> beanMetaDataCache = new ConcurrentHashMap<>();

    private final ConstraintHelper constraintHelper;

    public BeanMetaDataManager(ConstraintHelper constraintHelper) {
        this.constraintHelper = constraintHelper;
    }

    /**
     * Obtain the metadata object information of the current object, first query whether it exists in the cache object,
     * and parse it if it does not exist, and store it in the cache after the analysis is completed
     *
     * @param beanClass Bean object type
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> BeanMetaData<T> getBeanMetaData(Class<?> beanClass) {

        Objects.requireNonNull(beanClass, "Class type must not null!");

        BeanMetaData<T> beanMetaData = (BeanMetaData<T>) beanMetaDataCache.get( beanClass );

        if ( beanMetaData != null ) {
            return beanMetaData;
        }
        beanMetaData = (BeanMetaData<T>) createBeanMetaData(beanClass);
        BeanMetaData<T> previousBeanMetaData = (BeanMetaData<T>) beanMetaDataCache.putIfAbsent(beanClass, beanMetaData);
        if (previousBeanMetaData != null) {
            return previousBeanMetaData;
        }

        return beanMetaData;
    }
    
    protected <T> BeanMetaData<T> createBeanMetaData(Class<T> clazz) {
        return new BeanMetaDataImpl<>(clazz, constraintHelper);
    }

    public void clear() {
        synchronized (beanMetaDataCache) {
            beanMetaDataCache.clear();
        }
    }

}
