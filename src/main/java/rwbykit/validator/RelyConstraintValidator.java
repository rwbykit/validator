package rwbykit.validator;

import javax.validation.ConstraintValidatorContext;
import java.util.Map;

public interface RelyConstraintValidator {

    boolean isValid(Object value, Map<String, Object> relyValue, ConstraintValidatorContext context);

}
