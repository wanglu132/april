package com.april.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.april.config.ReloadableObjectsPool;

public class ReflectUtil {

	public static List<String> getAllClassFilesInClassPath() {
		String path = Thread.currentThread().getContextClassLoader()
				.getResource(".").getPath();
		FileUtil fu = new FileUtil();
		return fu.getAbsoluteFilesInDirectory(path, ".class");
	}

	public static List<String> getAllClassNamesInClassPath() {
		List<String> cls = new ArrayList<String>();
		String rootClassPath = Thread.currentThread().getContextClassLoader()
				.getResource(".").getPath();
		FileUtil fu = new FileUtil();
		List<String> rfs = fu.getRelativeFilesInDirectory(rootClassPath,
				".class");
		for (String f : rfs) {
			cls.add(f.replace("\\", ".").replace(".class", ""));
		}
		return cls;
	}

	public static List<Class<?>> getAllClassesInClassPath() {
		List<Class<?>> cls = new ArrayList<Class<?>>();
		List<String> cfs = getAllClassNamesInClassPath();
		for (String f : cfs) {
			try {
				cls.add(Class.forName(f));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return cls;
	}

	public static String absoluteClassFilePathToClassName(
			String absoluteClassFilePath) {
		String rootClassPath = Thread.currentThread().getContextClassLoader()
				.getResource(".").getPath();
		rootClassPath = rootClassPath.replace("/", "\\").substring(1);
		return absoluteClassFilePath.replace(rootClassPath, "").replace("\\",
				".").replace(".class", "");
	}

	public static String classNameToAbsoluteClassFilePath(String className) {
		String classFile = className.replace(".", "/") + ".class";
		return Thread.currentThread().getContextClassLoader().getResource(
				classFile).getPath();
	}

	public static Class<?> getClass(String className) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public static Class<?> reloadClass(String className) {
		CustomClassLoador ccl = new CustomClassLoador();
		Class<?> clazz = ccl.load_class(className);
		return clazz;
	}

	private static HashSet<String> s_processed = new HashSet<String>();

	private static void describe(String lead, Field field) {

		// get base and generic types, check kind
		Class<?> btype = field.getType();
		Type gtype = field.getGenericType();
		if (gtype instanceof ParameterizedType) {

			// list basic parameterized type information
			ParameterizedType ptype = (ParameterizedType) gtype;
			System.out.println(lead + field.getName()
					+ " is of parameterized type");
			System.out.println(lead + ' ' + btype.getName());

			// print list of actual types for parameters
			System.out.print(lead + " using types (");
			Type[] actuals = ptype.getActualTypeArguments();
			for (int i = 0; i < actuals.length; i++) {
				if (i > 0) {
					System.out.print(" ");
				}
				Type actual = actuals[i];
				if (actual instanceof Class) {
					System.out.print(((Class) actual).getName());
				} else {
					System.out.print(actuals[i]);
				}
			}
			System.out.println(")");

			// analyze all parameter type classes
			for (int i = 0; i < actuals.length; i++) {
				Type actual = actuals[i];
				if (actual instanceof Class) {
					analyze(lead, (Class) actual);
				}
			}

		} else if (gtype instanceof GenericArrayType) {

			// list array type and use component type
			System.out.println(lead + field.getName() + " is array type "
					+ gtype);
			gtype = ((GenericArrayType) gtype).getGenericComponentType();

		} else {

			// just list basic information
			System.out.println(lead + field.getName() + " is of type "
					+ btype.getName());
		}

		// analyze the base type of this field
		analyze(lead, btype);
	}

	private static void analyze(String lead, Class<?> clas) {

		// substitute component type in case of an array
		if (clas.isArray()) {
			clas = clas.getComponentType();
		}

		// make sure class should be expanded
		String name = clas.getName();
		if (!clas.isPrimitive() && !clas.isInterface()
				&& !name.startsWith("java.lang.")
				&& !s_processed.contains(name)) {

			// print introduction for class
			s_processed.add(name);
			System.out.println(lead + "Class " + clas.getName() + " details:");

			// process each field of class
			String indent = lead + ' ';
			Field[] fields = clas.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if (!Modifier.isStatic(field.getModifiers())) {
					describe(indent, field);
				}
			}
		}
	}
	
	public static void main(String[] args) throws SecurityException, NoSuchFieldException {
		Field field = ReloadableObjectsPool.class.getDeclaredField("objectPools");
		
		describe("", field);
		
	}

}
