package rwbykit.validator.metadata;

import rwbykit.validator.annotation.Regulated;

import javax.validation.metadata.ConstraintDescriptor;
import java.lang.reflect.Type;
import java.util.Set;

public interface PropertyDescriptor {

    String getPropertyName();

    Set<ConstraintDescriptor<?>> getConstraints();

    Set<ConstraintDescriptor<Regulated>> getRegulatedConstraints();

    Type getType();

    boolean hasConstraints();

}
