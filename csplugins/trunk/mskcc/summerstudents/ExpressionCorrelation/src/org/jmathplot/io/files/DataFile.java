package org.jmathplot.io.files;

import java.io.*;

/**


 * <p>Copyright : BSD License</p>

 * @author Yann RICHET
 * @version 1.0
 */

public abstract class DataFile {

	protected File file;

	public DataFile(File f) {
		file  = f;
	}
}
