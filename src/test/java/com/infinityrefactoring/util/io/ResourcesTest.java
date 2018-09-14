package com.infinityrefactoring.util.io;

import static com.infinityrefactoring.util.io.Resources.getLocaleOfFilename;
import static com.infinityrefactoring.util.io.Resources.getLocalizedFilename;
import static com.infinityrefactoring.util.io.Resources.getLocalizedResourceNames;
import static com.infinityrefactoring.util.io.Resources.getLocalizedResources;
import static com.infinityrefactoring.util.io.Resources.getResources;
import static java.util.Collections.singleton;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.forLanguageTag;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public class ResourcesTest {

	@Test
	public void testGetLocaleOfFilename() {
		assertEquals(null, getLocaleOfFilename("messages.properties", null));
		assertEquals(null, getLocaleOfFilename("messages.properties", ".properties"));
		assertEquals(Locale.forLanguageTag("en"), getLocaleOfFilename("messages_en.properties", ".properties"));
		assertEquals(Locale.forLanguageTag("pt"), getLocaleOfFilename("messages_pt.properties", ".properties"));
		assertEquals(Locale.forLanguageTag("pt-BR"), getLocaleOfFilename("messages_pt_BR.properties", ".properties"));
	}

	@Test
	public void testGetLocalizedFilename() {
		assertEquals("messages", getLocalizedFilename("messages", null, null));
		assertEquals("messages.properties", getLocalizedFilename("messages", null, ".properties"));
		assertEquals("messages_en", getLocalizedFilename("messages", ENGLISH, null));
		assertEquals("messages_en.properties", getLocalizedFilename("messages", ENGLISH, ".properties"));
		assertEquals("messages_pt_BR", getLocalizedFilename("messages", forLanguageTag("pt-BR"), null));
		assertEquals("messages_pt_BR.properties", getLocalizedFilename("messages", forLanguageTag("pt-BR"), ".properties"));
	}

	@Test
	public void testGetLocalizedResourceNames() {
		Set<String> resources = new HashSet<>(Arrays.asList("bar", "foo"));
		assertArrayEquals(new String[]{"bar", "foo"}, getLocalizedResourceNames(resources, null, null).toArray(String[]::new));
		assertArrayEquals(new String[]{"bar.properties", "foo.properties"}, getLocalizedResourceNames(resources, null, ".properties").toArray(String[]::new));
		assertArrayEquals(new String[]{"bar_en.properties", "foo_en.properties"}, getLocalizedResourceNames(resources, ENGLISH, ".properties").toArray(String[]::new));
		assertArrayEquals(new String[]{"bar_pt_BR.properties", "foo_pt_BR.properties"}, getLocalizedResourceNames(resources, forLanguageTag("pt-BR"), ".properties").toArray(String[]::new));
	}

	@Test
	public void testGetLocalizedResources() {
		Set<String> resources = new HashSet<>(Arrays.asList("interpolation/message.html", "interpolation/message-template.html"));
		assertEquals(0, getLocalizedResources(resources, forLanguageTag("pt-BR"), null).toArray().length);

		assertTrue(getLocalizedResources(resources, null, null)
				.allMatch(url -> url.getPath().endsWith("interpolation/message.html")
						|| url.getPath().endsWith("interpolation/message-template.html")));

		assertEquals(0, getLocalizedResources(singleton("foo"), null, ".properties").toArray().length);

		assertTrue(getLocalizedResources(singleton("foo"), forLanguageTag("pt-BR"), ".properties").toArray(URL[]::new)[0].getPath().endsWith("foo_pt_BR.properties"));
	}

	@Test
	public void testGetResources() {
		Set<URL> set = getResources("interpolation");
		assertEquals(2, set.size());
		assertTrue(set.stream().anyMatch(url -> url.getPath().endsWith("message-template.html")));
		assertTrue(set.stream().anyMatch(url -> url.getPath().endsWith("message.html")));
	}

}
