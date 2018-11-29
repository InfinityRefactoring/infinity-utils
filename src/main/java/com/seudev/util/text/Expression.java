package com.seudev.util.text;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;

public class Expression implements Comparable<Expression>, Serializable {

	private static final long serialVersionUID = -5849814835368769937L;

	private final int START;
	private final String EXPRESSION;
	private final String SUB_EXPRESSION;

	public Expression(int start, String expression, String subExpression) {
		START = start;
		EXPRESSION = requireNonNull(expression, "expression");
		SUB_EXPRESSION = requireNonNull(subExpression, "subExpression");
	}

	@Override
	public int compareTo(Expression expression) {
		return Integer.compare(START, expression.START);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Expression) {
			Expression e = (Expression) obj;
			return ((START == e.START) && EXPRESSION.equals(e.EXPRESSION));
		}
		return false;
	}

	public int getEnd() {
		return (START + EXPRESSION.length());
	}

	public String getExpression() {
		return EXPRESSION;
	}

	public int getStart() {
		return START;
	}

	public String getSubExpression() {
		return SUB_EXPRESSION;
	}

	@Override
	public int hashCode() {
		return Objects.hash(START, EXPRESSION);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("Expression [START=").append(START)
				.append(", EXPRESSION=").append(EXPRESSION)
				.append(", SUB_EXPRESSION=").append(SUB_EXPRESSION)
				.append("]").toString();
	}

}
