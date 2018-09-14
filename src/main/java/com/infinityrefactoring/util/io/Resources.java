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
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * @author Thomás Sousa Silva (ThomasSousa96)
 */
public class Resources {

	public static Locale getLocaleOfFilename(String filename, String suffix) {
		if ((suffix != null) && (!suffix.isEmpty()) && filename.endsWith(suffix)) {
			filename = filename.substring(0, filename.length() - suffix.length());
		}
		int indexOfHyphen = filename.indexOf('_');
		if ((indexOfHyphen < 0) || ((filename.length() - indexOfHyphen) < 2)) {
			return null;
		}
		String languageTag = filename.substring(indexOfHyphen + 1).replace('_', '-');
		return Locale.forLanguageTag(languageTag);
	}

	/**
	 * Returns the properties file name.
	 * <h1>Examples:</h1>
	 * <ul>
	 * <li>getPropertiesFileName("messages", null, ".properties") returns "messages.properties"</li>
	 * <li>getPropertiesFileName("messages", Locale.forLanguageTag("pt-BR"), ".properties") returns "messages_pt_BR.properties"</li>
	 * </ul>
	 *
	 * @param baseName the properties file name
	 * @param locale the locale
	 * @return If the locale is null,then returns baseName.properties else returns baseName_LanguageTag.properties
	 */
	public static String getLocalizedFilename(String baseName, Locale locale, String suffix) {
		if (baseName == null) {
			throw new IllegalArgumentException("The baseName cannot be null.");
		} else if (locale != null) {
			baseName += ('_' + locale.toLanguageTag().replace('-', '_'));
		}
		return ((suffix == null) ? baseName : (baseName + suffix));
	}

	public static Stream<String> getLocalizedResourceNames(Set<String> resources, Locale locale, String suffix) {
		return resources.stream()
				.map(baseName -> getLocalizedFilename(baseName, locale, suffix));
	}

	public static Stream<URL> getLocalizedResources(Set<String> resources, Locale locale, String suffix) {
		return getLocalizedResources(resources, locale, suffix, Resources.class.getClassLoader());
	}

	public static Stream<URL> getLocalizedResources(Set<String> resources, Locale locale, String suffix, ClassLoader classLoader) {
		return getLocalizedResourceNames(resources, locale, suffix)
				.flatMap(name -> Resources.getResources(classLoader, name).stream());
	}

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
