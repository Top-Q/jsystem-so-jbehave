package il.co.topq.auto.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jsystem.utils.StringUtils;

public class BeanUtils {
	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	public static boolean isInClasspath(String resourceName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL resource = classLoader.getResource(resourceName);
		return resource != null;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static List<Object> getAsInstances(List<Class<?>> classList) {
		List<Object> instances = new ArrayList<Object>();
		for (Class<?> clazz : classList) {
			try {
				Object instance = clazz.newInstance();
				instances.add(instance);
			} catch (Exception e) {
				System.out.println("Failed to create instance of type " + clazz.getName());
			}
		}
		return instances;
	}

	/**
	 * Finds all the classes in the specified packages
	 * 
	 * @return List of classes that holds the steps.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static List<Class<?>> findClassesInPackages(String[] packagesArray) {
		List<Class<?>> classList = new ArrayList<Class<?>>();
		for (String packageName : packagesArray) {
			if (!StringUtils.isEmpty(packageName)) {
				try {
					for (Class<?> clazz : BeanUtils.getClasses(packageName)) {
						classList.add(clazz);
					}
				} catch (Exception e) {
					System.out.println("Failed to create find classes in package " + packageName);
				}
			}
		}
		return classList;
	}

}
