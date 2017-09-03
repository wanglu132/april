package com.april.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 扫描classpath，搜索满足条件的类。
 */
public class ClassFinder<T> {

	private final Pattern classNamePattern;
	private final Class<T> superClass;
	private final List<Class<? extends T>> found;

	public ClassFinder(String classNameRegex, Class<T> superClass) {
		this.classNamePattern = Pattern.compile(classNameRegex);
		this.superClass = superClass;
		this.found = new LinkedList<>();
	}

	public List<Class<? extends T>> findClass() {
//		String pathSeparator = System.getProperty("path.separator");
//		String classpath = System.getProperty("java.class.path");
//
//		try {
//			for (String path : classpath.split(pathSeparator)) {
//				scan(path);
//			}
//		} catch (Exception e) {
//			throw new ClassFinderException("err msg", e);
//		}
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            // can be null in IE (see 6204697)
            loader = ClassLoader.getSystemClassLoader();
        }
		try {
			String path = loader.getResource("/").getPath().replaceAll("^/([A-Z]):/", "$1:/");
			scan(Paths.get(path));
		} catch (Exception e) {
			throw new ClassFinderException(e);
		}

		return found;
	}
	// 扫描jar或文件夹
//	private void scan(String path) throws Exception {
//		if (path.endsWith(".jar")) {
			// jar
//			URI uri = new URI("jar", Paths.get(path).toUri().toString(), null);
//
//			Map<String, String> attributes = new HashMap<>();
//			attributes.put("create", "true");
//
//			try (FileSystem zipFs = FileSystems.newFileSystem(uri, attributes)) {
//				scan(zipFs.getPath("/"));
//			}
//		} else {
//			// 文件夹
//			scan(Paths.get(path));
//		}
//	}

	// 遍历path
	private void scan(final Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				String fileName = path.relativize(file).toString();
				if (fileName.endsWith(".class")) {
					String className = fileNameToClassName(fileName);
					validateClass(className);
				}

				return FileVisitResult.CONTINUE;
			}

		});
	}

	private String fileNameToClassName(String fileName) {
		return fileName.replaceAll("\\.class$", "").replace('/', '.')
				.replace('\\', '.');
	}

	private void validateClass(String className) {
		if (classNamePattern.matcher(className).matches()) {
			try {
				Class<?> cls = Class.forName(className);
				if (superClass.isAssignableFrom(cls) && !superClass.equals(cls) && !cls.isInterface()) {
					found.add((Class<? extends T>) cls);
				}
			} catch (ClassNotFoundException e) {
				throw new ClassFinderException("Cannot load class: " + className, e);
			}
		}
	}

}

class ClassFinderException extends RuntimeException {

	private static final long serialVersionUID = -7584581456862339542L;

	public ClassFinderException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClassFinderException(String message) {
		super(message);
	}

	public ClassFinderException(Throwable cause) {
		super(cause);
	}

}