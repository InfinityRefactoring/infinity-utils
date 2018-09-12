package com.infinityrefactoring.util.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Set;

import org.junit.Test;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public class ResourcesTest {

	@Test
	public void testGetResources() throws Exception {
		Set<URL> set = Resources.getResources("interpolation");
		assertEquals(2, set.size());
		assertTrue(set.stream().anyMatch(url -> url.getPath().endsWith("message-template.html")));
		assertTrue(set.stream().anyMatch(url -> url.getPath().endsWith("message.html")));
	}

}
