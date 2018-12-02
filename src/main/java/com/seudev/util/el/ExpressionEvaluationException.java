package com.seudev.util.el;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public class ExpressionEvaluationException extends RuntimeException {

    private static final long serialVersionUID = -8147411208160304379L;
    
    /**
     * Constructs a new instance of ExpressionEvaluationException.
     */
    public ExpressionEvaluationException() {
        super();
    }

    /**
     * Constructs a new instance of ExpressionEvaluationException.
     *
     * @param message
     */
    public ExpressionEvaluationException(String message) {
        super(message);
    }

    /**
     * Constructs a new instance of ExpressionEvaluationException.
     *
     * @param message
     * @param cause
     */
    public ExpressionEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new instance of ExpressionEvaluationException.
     *
     * @param cause
     */
    public ExpressionEvaluationException(Throwable cause) {
        super(cause);
    }
    
}
