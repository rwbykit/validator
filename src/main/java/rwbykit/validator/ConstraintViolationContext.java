package rwbykit.validator;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import rwbykit.validator.message.MessageContext;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

public class ConstraintViolationContext {

    private final MessageInterpolator messageInterpolator;

    protected static final ThreadLocal<Locale> LOCALE = new InheritableThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return Locale.getDefault();
        }
    };

    public ConstraintViolationContext(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    public <A extends Annotation, T> ConstraintViolation<T> ofConstraintViolation(ConstraintDescriptor<A> constraintDescriptor,
                                                                                  ValueContext<T> valueContext,
                                                                                  String propertyPath) {
        String message = messageInterpolator.interpolate(constraintDescriptor.getMessageTemplate(),
                MessageContext.of(constraintDescriptor, valueContext.getPropertyValue(propertyPath)), LOCALE.get());
        return ConstraintViolationImpl.forBeanValidation(constraintDescriptor.getMessageTemplate(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                message,
                valueContext.getBeanType(),
                valueContext.getCurrentBean(),
                null,
                valueContext.getPropertyValue(propertyPath),
                PathImpl.createPathFromString(propertyPath),
                constraintDescriptor,
                null);
    }

    public final static void setLocale(Locale locale) {
        if (Objects.nonNull(locale)) {
            LOCALE.set(locale);
        }
    }


}
