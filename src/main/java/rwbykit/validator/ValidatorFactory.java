package rwbykit.validator;

import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManagerImpl;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.CachingResourceBundleLocator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import rwbykit.validator.groups.ValidationOrderGenerator;

import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.ValidatorContext;

/**
 * Constraint annotation and constraint validator registration class
 *
 * @author rwbykits
 */
public class ValidatorFactory implements javax.validation.ValidatorFactory {

    private final static ValidatorFactory factory = Factory.VALIDATOR_FACTORY;

    private final Validator validator;

    private final MessageInterpolator messageInterpolator;

    private final ConstraintValidatorManager constraintValidatorManager;

    private final ConstraintHelper constraintHelper;

    private final BeanMetaDataManager beanMetaDataManager;

    private final static class Factory {
        private final static ValidatorFactory VALIDATOR_FACTORY = new ValidatorFactory();
    }

    private ValidatorFactory() {
        constraintHelper = ConstraintHelper.forAllBuiltinConstraints();
        beanMetaDataManager = new BeanMetaDataManager(constraintHelper);
        messageInterpolator = new ResourceBundleMessageInterpolator(
                new CachingResourceBundleLocator(new PlatformResourceBundleLocator("org.hibernate.validator.ValidationMessages")));
        this.constraintValidatorManager = new ConstraintValidatorManagerImpl(new ConstraintValidatorFactoryImpl(), ConstraintValidatorInitializationContext.of());
        validator = new Validator(beanMetaDataManager, new ValidationOrderGenerator(), messageInterpolator, constraintValidatorManager);
    }

    public final static ValidatorFactory buildValidatorFactory() {
        return factory;
    }

    @Override
    public final Validator getValidator() {
        return this.validator;
    }

    @Override
    public ValidatorContext usingContext() {
        return null;
    }

    @Override
    public MessageInterpolator getMessageInterpolator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TraversableResolver getTraversableResolver() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.constraintValidatorManager.getDefaultConstraintValidatorFactory();
    }

    @Override
    public ParameterNameProvider getParameterNameProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClockProvider getClockProvider() {
        return DefaultClockProvider.INSTANCE;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        constraintValidatorManager.clear();
        constraintHelper.clear();;
        beanMetaDataManager.clear();
    }

}
