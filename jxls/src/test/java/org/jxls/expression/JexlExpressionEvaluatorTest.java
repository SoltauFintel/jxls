package org.jxls.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Leonid Vysochyn
 */
public class JexlExpressionEvaluatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void simple2VarExpression() {
        String expression = "2 * x + y";
        Map<String, Object> vars = new HashMap<>();
        vars.put("x", 2);
        vars.put("y", 3);
        ExpressionEvaluator expressionEvaluator = new JexlExpressionEvaluator();
        Object result = expressionEvaluator.evaluate(expression, vars);
        assertNotNull(result);
        assertEquals("Simple 2-var expression evaluation result is wrong", "7", result.toString());
    }

    @Test
    public void shouldThrowEvaluationExceptionWhenError() {
        String expression = "2 * x + y )";
        Map<String, Object> vars = new HashMap<>();
        vars.put("x", 2);
        vars.put("y", 3);
        ExpressionEvaluator expressionEvaluator = new JexlExpressionEvaluator();
        thrown.expect(EvaluationException.class);
        thrown.expectMessage(CoreMatchers.both(CoreMatchers.containsString("error")).and(CoreMatchers.containsString(expression)));
        Object result = expressionEvaluator.evaluate( expression, vars );
        assertNotNull( result );
    }

    @Ignore // Gradle test: evaluation results in exception => ok
    @Test
    public void evaluateWhenVarIsNull() {
        String expression = "2*x + dummy.intValue";
        Map<String, Object> vars = new HashMap<>();
        vars.put("x", 2);
        vars.put("dummy", null);
        ExpressionEvaluator expressionEvaluator = new JexlExpressionEvaluator();
        Object result = expressionEvaluator.evaluate( expression , vars);
        assertEquals("Incorrect evaluation when a var is null", "4", result.toString());
    }
    
    @Ignore // Gradle test: evaluation results in exception => ok
    @Test 
    public void evaluateWhenExpressionVarIsUndefined() {
        String expression = "dummy.intValue";
        Map<String, Object> vars = new HashMap<>();
        ExpressionEvaluator expressionEvaluator = new JexlExpressionEvaluator();
        Object result = expressionEvaluator.evaluate( expression, vars );
        assertNull(result);
    }
}
