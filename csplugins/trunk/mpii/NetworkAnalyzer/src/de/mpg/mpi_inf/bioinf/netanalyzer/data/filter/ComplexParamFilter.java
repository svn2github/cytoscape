package de.mpg.mpi_inf.bioinf.netanalyzer.data.filter;

import de.mpg.mpi_inf.bioinf.netanalyzer.data.ComplexParam;

/**
 * Interface implemented by all filters for complex parameters.
 * 
 * @author Yassen Assenov
 */
public interface ComplexParamFilter {

	/**
	 * Performs filtering on the given complex parameter.
	 * 
	 * @param aParam Complex parameter instance whose data is to be filtered.
	 * @return New instance of the same complex parameter type whose data is the result of applying
	 *         filtering criteria on <code>aParam</code>'s data.
	 * @throws UnsupportedOperationException If the complex parameter is not of the expected type.
	 */
	public ComplexParam filter(ComplexParam aParam);
}
