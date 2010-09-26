package de.mpg.mpi_inf.bioinf.netanalyzer.sconnect;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Utility class containing helping methods related to communications with the MPII server over HTTP.
 * 
 * @author Yassen Assenov
 */
public abstract class ServerUtils {

	/**
	 * Translates the given string to UTF-8 encoding.
	 * 
	 * @param aString String to be encoded.
	 * @return The string encoded in UTF-8.
	 * @throws UnsupportedEncodingException If the system does not support the UTF-8 encoding.
	 */
	public static String encode(String aString) throws UnsupportedEncodingException {
		return URLEncoder.encode(aString, "UTF-8");
	}
}
