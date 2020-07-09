package rwbykit.validator.message;

import javax.validation.MessageInterpolator;
import javax.validation.metadata.ConstraintDescriptor;

public class MessageContext implements MessageInterpolator.Context {

    private final ConstraintDescriptor<?> constraintDescriptor;
    private final Object validatedValue;

    private MessageContext(ConstraintDescriptor<?> constraintDescriptor, Object validatedValue) {
        this.constraintDescriptor = constraintDescriptor;
        this.validatedValue = validatedValue;
    }

    public static MessageContext of(ConstraintDescriptor<?> constraintDescriptor, Object validatedValue) {
        return new MessageContext(constraintDescriptor, validatedValue);
    }

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return this.constraintDescriptor;
    }

    @Override
    public Object getValidatedValue() {
        return validatedValue;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException("Unsupported operation!");
    }
}
