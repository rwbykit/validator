package rwbykit.validator;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import rwbykit.validator.groups.ValidationOrder;
import rwbykit.validator.groups.ValidationOrderGenerator;
import rwbykit.validator.metadata.BeanMetaData;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.groups.Default;
import javax.validation.metadata.BeanDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * 校验者
 * @author Cytus_
 */
public class Validator implements javax.validation.Validator {

    private final BeanMetaDataManager beanMetaDataManager;

    /**
     * The default group array used in case any of the validate methods is called without a group.
     */
    private static final Collection<Class<?>> DEFAULT_GROUPS = Collections.<Class<?>>singletonList( Default.class );

    /**
     * Used to resolve the group execution order for a validate call.
     */
    private final transient ValidationOrderGenerator validationOrderGenerator;

    private final ConstraintViolationContext constraintViolationContext;

    private final ConstraintValidatorManager constraintValidatorManager;

    Validator(BeanMetaDataManager beanMetaDataManager,
              ValidationOrderGenerator validationOrderGenerator,
              MessageInterpolator messageInterpolator,
              ConstraintValidatorManager constraintValidatorManager) {
        this.beanMetaDataManager = beanMetaDataManager;
        this.validationOrderGenerator = validationOrderGenerator;
        this.constraintViolationContext = new ConstraintViolationContext(messageInterpolator);
        this.constraintValidatorManager = constraintValidatorManager;
    }

    /**
     * 校验
     * @param value
     * @param groups
     * @param <T>
     * @return
     */
    @Override
    public final <T> Set<ConstraintViolation<T>> validate(T value, Class<?>... groups) {
        Objects.requireNonNull(value, "The value object must not null!");

        BeanMetaData<T> beanMetaData = beanMetaDataManager.getBeanMetaData(value.getClass());
        if (!beanMetaData.hasConstraints()) {
            return Collections.emptySet();
        }
        ValidationOrder validationOrder = determineGroupValidationOrder(groups);
        ValueContext<T> valueContext = ValueContext.of(beanMetaData, value);
        ValidatorContext<T> validatorContext = ValidatorContext.of(beanMetaData, validationOrder, this.constraintViolationContext, this.constraintValidatorManager);
        return validateInContext(validatorContext, valueContext);
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExecutableValidator forExecutables() {
        throw new UnsupportedOperationException();
    }

    /**
     * Context check
     * @param validatorContext
     * @param valueContext
     * @param <T>
     * @return
     */
    protected  <T> Set<ConstraintViolation<T>> validateInContext(ValidatorContext<T> validatorContext, ValueContext<T> valueContext) {

        if (valueContext.getCurrentBean() == null) {
            return Collections.emptySet();
        }

        validatorContext.validate(valueContext);

        return validatorContext.getConstraintViolations();

    }


    private ValidationOrder determineGroupValidationOrder(Class<?>... groups) {
        Collection<Class<?>> resultGroups;
        // if no groups is specified use the default
        if ( groups.length == 0 ) {
            resultGroups = DEFAULT_GROUPS;
        }
        else {
            resultGroups = Arrays.asList( groups );
        }
        return validationOrderGenerator.getValidationOrder( resultGroups );
    }

}
