package com.seudev.util.el;

public class ExpressionException extends RuntimeException {

	private static final long serialVersionUID = 8395331473822186358L;

	public ExpressionException(String message) {
		super(message);
	}

	public ExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressionException(Throwable cause) {
		super(cause);
	}

}
