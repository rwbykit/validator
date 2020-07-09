package rwbykit.validator.util;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Expression execute or parse utility class
 * @author tangxb
 */
public class Expressions {

    private final static ExpressionParser parser = new SpelExpressionParser();

    /**
     * Caching expression object
     */
    private static Map<String, Expression> expressions = new ConcurrentHashMap<String, Expression>();

    /**
     *
     * @param expression
     * @return
     */
    private static Expression expression(final String expression) {
        return expressions.computeIfAbsent(expression, (exp) -> parser.parseExpression(expression));
    }

    /**
     * Conditional calculation using SPEL expression mechanism provided by Spring
     * @param object
     * @param expression
     * @return
     */
    public static boolean conditionalCalculation(Object object, String expression) {
        return expression(expression).getValue(new StandardEvaluationContext(object), Boolean.class);
    }

}
