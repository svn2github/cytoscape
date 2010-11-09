package org.cytoscape.filter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.cytoscape.work.Tunable;

/**
 * A base class for <code>CyFilter</code>s that declare their properties
 * through <code>Tunable</code>s.
 */
public abstract class TunableCyFilter implements CyFilter {
	private static final Collection<String> GETTER_NAME_PREFIXES = Collections.unmodifiableCollection(Arrays.asList(new String[] { "get", "is", "has" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private String name;

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String name, Class<T> type) {
		Class<?> currentClass = getClass();
		while (currentClass != null) {
			for (String prefix : GETTER_NAME_PREFIXES) {
				String suffix = name.substring(0, 1).toLowerCase() + name.substring(1);
				try {
					Method method = currentClass.getDeclaredMethod(String.format("%s%s", prefix, suffix)); //$NON-NLS-1$
					if (!method.isAnnotationPresent(Tunable.class)) {
						continue;
					}
					if (!type.isAssignableFrom(method.getReturnType())) {
						throw new ClassCastException();
					}
					return (T) method.invoke(this);
				} catch (SecurityException e) {
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (NoSuchMethodException e) {
				} catch (InvocationTargetException e) {
				}
			}
			
			try {
				Field field = currentClass.getDeclaredField(name);
				if (!field.isAnnotationPresent(Tunable.class)) {
					continue;
				}
				return (T) field.get(this);
			} catch (SecurityException e) {
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
			currentClass = currentClass.getSuperclass();
		}
		return null;
	}
	
	@Override
	public <T> void setProperty(String name, T value) {
		String suffix = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		String setterName = String.format("set%s", suffix); //$NON-NLS-1$
		Class<?> currentClass = getClass();
		
		while (currentClass != null) {
			try {
				for (String prefix : GETTER_NAME_PREFIXES) {
					String getterName = String.format("%s%s", prefix, suffix); //$NON-NLS-1$
					Method getter = currentClass.getDeclaredMethod(getterName);
					if (!getter.isAnnotationPresent(Tunable.class)) {
						continue;
					}
					Method setter = currentClass.getDeclaredMethod(setterName, value.getClass());
					setter.invoke(this, value);
					return;
				}
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			
			try {
				Field field = currentClass.getDeclaredField(name);
				if (!field.isAnnotationPresent(Tunable.class)) {
					continue;
				}
				field.set(this, value);
				return;
			} catch (SecurityException e) {
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
			currentClass = currentClass.getSuperclass();
		}
	};
	
	@Override
	public Map<String, Object> getAllProperties() {
		Map<String, Object> properties = new HashMap<String, Object>();
		Class<?> currentClass = getClass();
		
		while (currentClass != null) {
			for (Method method : currentClass.getDeclaredMethods()) {
				if (method.getParameterTypes().length > 0) {
					continue;
				}
				String name = extractPropertyName(method.getName());
				if (name == null) {
					continue;
				}
				try {
					if (!method.isAnnotationPresent(Tunable.class)) {
						continue;
					}
					Object value = method.invoke(this);
					properties.put(name, value);
				} catch (SecurityException e) {
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
			for (Field field : currentClass.getDeclaredFields()) {
				if (!field.isAnnotationPresent(Tunable.class)) {
					continue;
				}
				try {
					Object value = field.get(this);
					properties.put(field.getName(), value);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
			currentClass = currentClass.getSuperclass();
		}
		return properties;
	}

	private String extractPropertyName(String fullName) {
		for (String prefix : GETTER_NAME_PREFIXES) {
			if (!fullName.startsWith(prefix)) {
				return null;
			}
			
			String suffix = fullName.substring(prefix.length());
			char firstLetter = suffix.charAt(0);
			if (!Character.isUpperCase(firstLetter)) {
				return null;
			}
			
			return Character.toLowerCase(firstLetter) + suffix.substring(1);
		}
		return null;
	}
	
	@Override
	public JComponent getSettingsUI() {
		// TODO Should we reuse Tunable UI bits in work-swing-impl or create
		// a different UI widget library for filters?
		return null;
	}
}
