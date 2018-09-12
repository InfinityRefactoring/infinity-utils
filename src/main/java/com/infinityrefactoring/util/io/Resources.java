package com.infinityrefactoring.util.io;

import static java.util.Collections.emptySet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public class Resources {

	public static URL getResource(String name) {
		return Resources.class.getClassLoader().getResource(name);
	}

	public static InputStream getResourceAsStream(String name) {
		return Resources.class.getClassLoader().getResourceAsStream(name);
	}

	public static Set<URL> getResources(ClassLoader classLoader, String name) {
		try {
			Enumeration<URL> resources = classLoader.getResources(name);
			if (!resources.hasMoreElements()) {
				return emptySet();
			}
			Set<URL> set = new HashSet<>();
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				if (url.getProtocol().equals("jar")) {
					String path = url.getPath();
					int indexOfExclamation = path.indexOf('!');
					String jarPath = path.substring("file:".length(), indexOfExclamation);
					JarFile jarFile = new JarFile(jarPath);
					Enumeration<JarEntry> entries = jarFile.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String entryName = entry.getName();
						if ((!entry.isDirectory()) && entryName.startsWith(entryName)) {
							set.add(new URL("jar", "", String.format("file:%s!/%s", jarPath, entryName)));
						}
					}
				} else {
					Files.walk(Paths.get(url.toURI()))
							.map(Path::toFile)
							.filter(f -> !f.isDirectory())
							.forEach(f -> {
								try {
									set.add(f.toURI().toURL());
								} catch (MalformedURLException ex) {
									throw new RuntimeException(ex);
								}
							});
				}
			}
			return set;
		} catch (IOException | URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Set<URL> getResources(String name) {
		return getResources(Resources.class.getClassLoader(), name);
	}

}
