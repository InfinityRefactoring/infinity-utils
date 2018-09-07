package com.infinityrefactoring.util.text;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableSortedMap;
import static java.util.Collections.unmodifiableSortedSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

public class ExpressionDefinition {

	public static final ExpressionDefinition DOLLAR_CURLY_BRACKET = new ExpressionDefinition("${", "}", '\\', true, true);
	public static final ExpressionDefinition CURLY_BRACKET = new ExpressionDefinition("{", "}", '\\', true, true);
	public static final ExpressionDefinition SQUARE_BRACKET = new ExpressionDefinition("[", "]", '\\', true, true);
	public static final ExpressionDefinition SINGLE_QUOTE_LITERAL = new ExpressionDefinition("'", "'", '\\', false, true);
	public static final ExpressionDefinition DOUBLE_QUOTE_LITERAL = new ExpressionDefinition("\"", "\"", '\\', true, false);
	private final Map<String, SortedMap<Expression, SortedSet<Integer>>> CACHE = new HashMap<>();

	private final String START;
	private final String END;
	private final char ESCAPE;
	private final boolean IGNORE_SINGLE_QUOTE_LITERAL;
	private final boolean IGNORE_DOUBLE_QUOTE_LITERAL;

	public ExpressionDefinition(String start, String end, char escape, boolean ignoreSingleQuoteLiteral, boolean ignoreDoubleQuoteLiteral) {
		if (start.isEmpty() || end.isEmpty()) {
			throw new ExpressionException("Syntax error: The open and close arguments must be not empty");
		}
		START = start;
		END = end;
		ESCAPE = escape;
		IGNORE_SINGLE_QUOTE_LITERAL = ignoreSingleQuoteLiteral;
		IGNORE_DOUBLE_QUOTE_LITERAL = ignoreDoubleQuoteLiteral;
	}

	public SortedMap<Expression, SortedSet<Integer>> findAll(String template) {
		return findAll(template, true);
	}

	public SortedMap<Expression, SortedSet<Integer>> findAll(String template, boolean cacheable) {
		return findAll(template, 0, cacheable);
	}

	public SortedMap<Expression, SortedSet<Integer>> findAll(String template, int offset, boolean cacheable) {
		if (cacheable && (offset == 0) && CACHE.containsKey(template)) {
			return CACHE.get(template);
		}
		SortedSet<Expression> expressions = new TreeSet<>();
		int currentOffset = offset, startExpressionIndex = offset;
		while (startExpressionIndex >= 0) {
			startExpressionIndex = indexOf(template, currentOffset);
			if (startExpressionIndex >= 0) {
				Expression expression = getExpression(template, startExpressionIndex);
				expressions.add(expression);
				currentOffset = expression.getEnd();
			}
		}
		SortedMap<Expression, SortedSet<Integer>> map = getRemovableEscapeIndexes(template, expressions, offset);
		if (cacheable && (offset == 0)) {
			CACHE.put(template, map);
		}
		return map;
	}

	public String getEnd() {
		return END;
	}

	public char getEscape() {
		return ESCAPE;
	}

	/**
	 * Returns the complete expression that starts with the given character in the given index and ends in the associated character.
	 *
	 * @param template the text that contains the expression
	 * @param startExpressionIndex the index that starts the expression
	 * @return the complete expression without the bounds parentheses
	 */
	public Expression getExpression(String template, int startExpressionIndex) {
		String subExpression = template.substring((startExpressionIndex + START.length()), getExpressionEndIndex(template, startExpressionIndex));
		return new Expression(this, startExpressionIndex, subExpression);
	}

	/**
	 * Returns the index of character that is closing the expression that starts in the given index.
	 *
	 * @param template the text that contains the expression
	 * @param startExpressionIndex the index that starts the expression
	 * @return the index
	 */
	public int getExpressionEndIndex(String template, int startExpressionIndex) {
		int openeds = 0;
		for (int i = (startExpressionIndex + START.length()); i < template.length(); i++) {
			char c = template.charAt(i);

			if (isEscapeChar(c)) {
				i++;
				continue;
			}
			if (ignoreSingleQuoteLiteral(c)) {
				i = SINGLE_QUOTE_LITERAL.getExpressionEndIndex(template, i);
				continue;
			}
			if (ignoreDoubleQuoteLiteral(c)) {
				i = DOUBLE_QUOTE_LITERAL.getExpressionEndIndex(template, i);
				continue;
			}
			if (template.startsWith(END, i)) {
				if (openeds == 0) {
					return i;
				}
				openeds--;
			} else if (template.startsWith(START, i)) {
				openeds++;
			}
		}
		throw new ExpressionException(format("Syntax error, insert \"%s\" to complete the expression: %s\nOr use the escape char %s before of the %s to escape this expression.",
				END,
				template.subSequence(startExpressionIndex, template.length()),
				ESCAPE, START));
	}

	public String getStart() {
		return START;
	}

	public int indexOf(String template, int offset) {
		for (int i = offset; i < template.length(); i++) {
			char c = template.charAt(i);

			if (isEscapeChar(c)) {
				i++;
				continue;
			}
			if (ignoreSingleQuoteLiteral(c)) {
				i = SINGLE_QUOTE_LITERAL.getExpressionEndIndex(template, i);
				continue;
			}
			if (ignoreDoubleQuoteLiteral(c)) {
				i = DOUBLE_QUOTE_LITERAL.getExpressionEndIndex(template, i);
				continue;
			}
			if (template.startsWith(START, i)) {
				return i;
			}
		}
		return -1;
	}

	public String interpolate(String template, boolean cacheable, Function<Expression, ?> formatter) {
		return interpolate(template, 0, cacheable, formatter);
	}

	public String interpolate(String template, Function<Expression, ?> formatter) {
		return interpolate(template, true, formatter);
	}

	public String interpolate(String template, int offset, boolean cacheable, Function<Expression, ?> formatter) {
		SortedMap<Expression, SortedSet<Integer>> expressions = findAll(template, offset, cacheable);
		if (expressions.isEmpty()) {
			return template;
		}
		StringBuilder builder = new StringBuilder(template);
		int offsetBuilder = 0;
		Set<Entry<Expression, SortedSet<Integer>>> entrySet = expressions.entrySet();
		for (Entry<Expression, SortedSet<Integer>> entry : entrySet) {
			Expression expression = entry.getKey();

			offsetBuilder = removePreviousEscapes(builder, offsetBuilder, entry.getValue(), expression);

			int startIndex = (offsetBuilder + expression.getStart());
			int expressionLength = expression.getExpression().length();
			Object value;
			try {
				value = formatter.apply(expression);
				if (value == null) {
					throw new ExpressionException("The expression value must be not null.");
				}
			} catch (RuntimeException ex) {
				throw new ExpressionException("The expression cannot be interpolate: " + expression.getExpression(), ex);
			}
			String formattedValue = value.toString();
			builder.replace(startIndex, (startIndex + expressionLength), formattedValue);
			offsetBuilder += (formattedValue.length() - expressionLength);

			offsetBuilder = removeNextEscapes(builder, offsetBuilder, entry.getValue(), expression);
		}
		return builder.toString();
	}

	public boolean isIgnoreDoubleQuoteLiteral() {
		return IGNORE_DOUBLE_QUOTE_LITERAL;
	}

	public boolean isIgnoreSingleQuoteLiteral() {
		return IGNORE_SINGLE_QUOTE_LITERAL;
	}

	public void removeCache(String template) {
		CACHE.remove(template);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("ExpressionDefinition [START=").append(START)
				.append(", END=").append(END)
				.append(", ESCAPE=").append(ESCAPE)
				.append(", IGNORE_SINGLE_QUOTE_LITERAL=").append(IGNORE_SINGLE_QUOTE_LITERAL)
				.append(", IGNORE_DOUBLE_QUOTE_LITERAL=").append(IGNORE_DOUBLE_QUOTE_LITERAL)
				.append("]").toString();
	}

	private void addEscapeIndexes(String template, int offset, int limit, SortedSet<Integer> removableEscapeIndexes) {
		for (int i = offset; i < limit; i++) {
			char c = template.charAt(i);

			if (ignoreSingleQuoteLiteral(c)) {
				i = SINGLE_QUOTE_LITERAL.getExpressionEndIndex(template, i);
				continue;
			}
			if (ignoreDoubleQuoteLiteral(c)) {
				i = DOUBLE_QUOTE_LITERAL.getExpressionEndIndex(template, i);
				continue;
			}

			if (isEscapeChar(c)) {
				removableEscapeIndexes.add(i);
				i++;
			}
		}
	}

	private SortedMap<Expression, SortedSet<Integer>> getRemovableEscapeIndexes(String template, SortedSet<Expression> expressions, int offset) {
		SortedMap<Expression, SortedSet<Integer>> map = new TreeMap<>();
		for (Expression expression : expressions) {
			SortedSet<Integer> removableEscapeIndexes = new TreeSet<>();

			addEscapeIndexes(template, offset, expression.getStart(), removableEscapeIndexes);

			if (expression == expressions.last()) {
				addEscapeIndexes(template, expression.getEnd(), template.length(), removableEscapeIndexes);
			}
			map.put(expression, unmodifiableSortedSet(removableEscapeIndexes));
			offset = expression.getEnd();
		}
		return unmodifiableSortedMap(map);
	}

	private boolean ignoreDoubleQuoteLiteral(char c) {
		return IGNORE_DOUBLE_QUOTE_LITERAL && (c == '"') && ((c != START.charAt(0)) || (START.length() > 1));
	}

	private boolean ignoreSingleQuoteLiteral(char c) {
		return IGNORE_SINGLE_QUOTE_LITERAL && (c == '\'') && ((c != START.charAt(0)) || (START.length() > 1));
	}

	private boolean isEscapeChar(char c) {
		return (c == ESCAPE);
	}

	private int removeNextEscapes(StringBuilder builder, int offsetBuilder, SortedSet<Integer> removableEscapeIndexes, Expression expression) {
		if ((!removableEscapeIndexes.isEmpty()) && (removableEscapeIndexes.last() > expression.getStart())) {
			removableEscapeIndexes = removableEscapeIndexes.tailSet(expression.getEnd());
			for (Integer index : removableEscapeIndexes) {
				builder.deleteCharAt(offsetBuilder + index);
				offsetBuilder--;
			}
		}
		return offsetBuilder;
	}

	private int removePreviousEscapes(StringBuilder builder, int offsetBuilder, SortedSet<Integer> removableEscapeIndexes, Expression expression) {
		if (!removableEscapeIndexes.isEmpty()) {
			if (removableEscapeIndexes.last() > expression.getStart()) {
				removableEscapeIndexes = removableEscapeIndexes.headSet(expression.getStart());
			}
			for (Integer index : removableEscapeIndexes) {
				builder.deleteCharAt(offsetBuilder + index);
				offsetBuilder--;
			}
		}
		return offsetBuilder;
	}

}
