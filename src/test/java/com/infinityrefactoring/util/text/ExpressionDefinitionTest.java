package com.infinityrefactoring.util.text;

import static com.infinityrefactoring.util.text.ExpressionDefinitions.ofDollarCurlyBracket;
import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ExpressionDefinitionTest {

	@Test
	public void testFileInterpolation() throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(currentThread().getContextClassLoader().getResourceAsStream("message-template.html")))) {
			String template = in.lines()
					.collect(joining("\n"));

			Map<String, String> map = new HashMap<>();
			map.put("${header1}", "Header1");
			map.put("${link1}", "http://example.com");
			map.put("${header2}", "two");
			map.put("${((code1 * 10) / 2) + 5.2}", "3495084308");
			map.put("${item1 += 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.'}", "foo");
			map.put("${\"Aliquam tincidunt mauris eu risus.\" += 10}", "bar");
			map.put("${header3}", "three");
			map.put("${header-width}", "100");
			map.put("${header-width - 20}", "80");
			map.put("${'luctus turpis elit sit amet quam.' +=  \n	\"Vivamus pretium ornare est.\"}", "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.");
			String message = ofDollarCurlyBracket().interpolate(template, e -> map.get(e.getExpression()));

			try (BufferedReader in2 = new BufferedReader(new InputStreamReader(currentThread().getContextClassLoader().getResourceAsStream("message.html")))) {
				String expectedMessage = in2.lines()
						.collect(joining("\n"));

				assertEquals(expectedMessage, message);
			}
		}
	}

	@Test
	public void testIgnoreTextBetweenSingleQuotes() {
		String template = "Lorem ${ipsum} dolor sit amet, 'consectetur adipiscing' elit, \\\\sed do \\${ 'eiusmod' ${(tempor + 1 * 10) / 5 == x ? 'incididunt ut' : 'labore \\\\et dolore'} magna \\${aliqua\\}  \\\\\\\\.";
		SortedMap<Expression, SortedSet<Integer>> expressions = ofDollarCurlyBracket().findAll(template);

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
		String message = ofDollarCurlyBracket().interpolate(template, e -> "REPLACE" + i.getAndIncrement());

		assertEquals("Lorem REPLACE1 dolor sit amet, 'consectetur adipiscing' elit, \\sed do ${ 'eiusmod' REPLACE2 magna ${aliqua}  \\\\.", message);
	}

	@Test
	public void testInterpolation() {
		String template = "Hello ${firstName} ${lastName}!";
		String text = ofDollarCurlyBracket().interpolate(template, e -> e.getSubExpression().equals("firstName") ? "Thomás" : "Sousa Silva");
		assertEquals("Hello Thomás Sousa Silva!", text);
	}

}
