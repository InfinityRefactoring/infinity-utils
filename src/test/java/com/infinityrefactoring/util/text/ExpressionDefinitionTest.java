package com.infinityrefactoring.util.text;

import static com.infinityrefactoring.util.text.ExpressionDefinition.DOLLAR_CURLY_BRACKET;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ExpressionDefinitionTest {

	@Test
	public void testIgnoreTextBetweenSingleQuotes() throws Exception {
		String template = "Lorem ${ipsum} dolor sit amet, 'consectetur adipiscing' elit, \\\\sed do \\${ 'eiusmod' ${(tempor + 1 * 10) / 5 == x ? 'incididunt ut' : 'labore \\\\et dolore'} magna \\${aliqua\\}  \\\\\\\\.";
		SortedMap<Expression, SortedSet<Integer>> expressions = DOLLAR_CURLY_BRACKET.findAll(template);

		assertEquals(2, expressions.size());

		Iterator<Entry<Expression, SortedSet<Integer>>> iterator = expressions.entrySet().iterator();

		//${ipsum}
		assertTrue(iterator.hasNext());
		Entry<Expression, SortedSet<Integer>> entry1 = iterator.next();
		Expression expression1 = entry1.getKey();

		assertEquals(6, expression1.getStart());
		assertEquals("${ipsum}", expression1.getExpression());
		assertEquals("ipsum", expression1.getSubExpression());
		assertEquals(14, expression1.getEnd());
		assertTrue(entry1.getValue().isEmpty());

		//${(tempor + 1 * 10) / 5 == x ? 'incididunt ut' : 'labore \\\\et dolore'}
		assertTrue(iterator.hasNext());
		Entry<Expression, SortedSet<Integer>> entry2 = iterator.next();
		Expression expression2 = entry2.getKey();

		assertEquals(85, expression2.getStart());
		assertEquals("${(tempor + 1 * 10) / 5 == x ? 'incididunt ut' : 'labore \\\\et dolore'}", expression2.getExpression());
		assertEquals("(tempor + 1 * 10) / 5 == x ? 'incididunt ut' : 'labore \\\\et dolore'", expression2.getSubExpression());
		assertEquals(155, expression2.getEnd());
		assertFalse(entry2.getValue().isEmpty());
		Integer[] escapes = entry2.getValue().toArray(new Integer[6]);
		assertEquals(6, escapes.length);
		assertArrayEquals(new Integer[]{62, 71, 162, 171, 175, 177}, escapes);

		AtomicInteger i = new AtomicInteger(1);
		String message = DOLLAR_CURLY_BRACKET.interpolate(template, e -> "REPLACE" + i.getAndIncrement());

		assertEquals("Lorem REPLACE1 dolor sit amet, 'consectetur adipiscing' elit, \\sed do ${ 'eiusmod' REPLACE2 magna ${aliqua}  \\\\.", message);
	}

	@Test
	public void testInterpolation() throws Exception {
		String template = "Hello ${firstName} ${lastName}!";
		String text = DOLLAR_CURLY_BRACKET.interpolate(template, e -> e.getSubExpression().equals("firstName") ? "Thomás" : "Sousa Silva");
		assertEquals("Hello Thomás Sousa Silva!", text);
	}

}
