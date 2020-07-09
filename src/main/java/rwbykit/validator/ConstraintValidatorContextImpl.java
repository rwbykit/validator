package rwbykit.validator;

import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.internal.metadata.core.ConstraintHelper;
import org.springframework.util.ObjectUtils;

import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;

public class ConstraintValidatorContextImpl implements ConstraintValidatorContext {

    private final ConstraintDescriptor constraintDescriptor;

    public ConstraintValidatorContextImpl(ConstraintDescriptor constraintDescriptor) {
        this.constraintDescriptor = constraintDescriptor;
    }

    public static ConstraintValidatorContext of(ConstraintDescriptor constraintDescriptor) {
        return new ConstraintValidatorContextImpl(constraintDescriptor);
    }

    @Override
    public void disableDefaultConstraintViolation() {
        throw new UnsupportedOperationException("Unsupported Operation!");
    }

    @Override
    public String getDefaultConstraintMessageTemplate() {
        return ObjectUtils.nullSafeToString(constraintDescriptor.getAttributes().get(ConstraintHelper.MESSAGE));
    }

    @Override
    public ClockProvider getClockProvider() {
        return DefaultClockProvider.INSTANCE;
    }


    @Override
    public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String messageTemplate) {
        throw new UnsupportedOperationException("Unsupported Operation!");
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException("Unsupported Operation!");
    }

}
