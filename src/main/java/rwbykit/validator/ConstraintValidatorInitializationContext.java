package rwbykit.validator;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;
import rwbykit.validator.script.SpelScriptEvaluator;

import javax.validation.ClockProvider;
import java.time.Duration;

public class ConstraintValidatorInitializationContext implements HibernateConstraintValidatorInitializationContext {

    private final static HibernateConstraintValidatorInitializationContext INSTANCE = new ConstraintValidatorInitializationContext();

    public static HibernateConstraintValidatorInitializationContext of() {
        return ConstraintValidatorInitializationContext.INSTANCE;
    }

    @Override
    public ScriptEvaluator getScriptEvaluatorForLanguage(String languageName) {
        return SpelScriptEvaluator.INSTANCE;
    }

    @Override
    public ClockProvider getClockProvider() {
        return DefaultClockProvider.INSTANCE;
    }

    @Override
    public Duration getTemporalValidationTolerance() {
        return null;
    }
}
