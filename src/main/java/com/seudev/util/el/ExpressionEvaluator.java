package com.seudev.util.el;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.el.ELProcessor;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ExpressionEvaluator {
    
    @Inject
    private ELProcessor elProcessor;
    
    public ExpressionEvaluator(ELProcessor elProcessor) {
        this.elProcessor = elProcessor;
    }

    public <R> R evalAs(String expression, Class<R> expectedResultClass) {
        Object result = elProcessor.eval(expression);
        if (expectedResultClass.isInstance(result)) {
            return expectedResultClass.cast(result);
        }

        throw new ExpressionEvaluationException(format("Is expected an instance of %s as the evaluation result of the \"%s\" expression.", expectedResultClass.getName(), expression));
    }
    
    public BigDecimal evalAsBigDecimal(String expression) {
        return evalAs(expression, BigDecimal.class);
    }
    
    public BigInteger evalAsBigInteger(String expression) {
        return evalAs(expression, BigInteger.class);
    }
    
    public boolean evalAsBoolean(String expression) {
        return evalAs(expression, Boolean.class);
    }
    
    public Character evalAsCharacter(String expression) {
        return evalAs(expression, Character.class);
    }
    
    public Double evalAsDouble(String expression) {
        return evalAs(expression, Double.class);
    }

    public Float evalAsFloat(String expression) {
        return evalAs(expression, Float.class);
    }
    
    public Integer evalAsInteger(String expression) {
        return evalAs(expression, Integer.class);
    }
    
    public Long evalAsLong(String expression) {
        return evalAs(expression, Long.class);
    }
    
    public Short evalAsShort(String expression) {
        return evalAs(expression, Short.class);
    }
    
    public String evalAsString(String expression) {
        return evalAs(expression, String.class);
    }

}
