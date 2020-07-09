package rwbykit.validator.metadata;

public interface BeanMetaData<T> {

    Class<T> getBeanClass();

    boolean hasConstraints();

    BeanDescriptor<T> getBeanDescriptor();


}
