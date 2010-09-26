package de.mpg.mpi_inf.bioinf.netanalyzer.data.settings;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Comparator of methods based on the name of their return types.
 * 
 * @author Yassen Assenov
 */
public class ReturnTypeComparator implements Comparator<Method> {

	/**
	 * Initializes a new instance of <code>ReturnTypeComparator</code>.
	 */
	public ReturnTypeComparator() {
		// No specific initialization is required.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Method o1, Method o2) {
		final String r1 = o1.getReturnType().getSimpleName().toLowerCase();
		final String r2 = o2.getReturnType().getSimpleName().toLowerCase();
		return r1.compareTo(r2);
	}
}
