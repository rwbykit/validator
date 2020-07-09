package rwbykit.validator.script;

import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;
import rwbykit.validator.util.Expressions;

import java.util.Map;

public class SpelScriptEvaluator implements ScriptEvaluator {

    public final static ScriptEvaluator INSTANCE = new SpelScriptEvaluator();

    @Override
    public Object evaluate(String script, Map<String, Object> bindings) throws ScriptEvaluationException {
        return Expressions.conditionalCalculation(bindings, script);
    }
}
