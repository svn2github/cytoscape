/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: Util.java,v $
 * $Revision: 1.5 $
 * $Date: 2006/09/27 21:12:22 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/* I have decided to stuff random helper routines in here */

public class Util {
    // this class is mostly for static methods
	public static String URLtoFilePath(String fileURL) {
		String dir = null;
		try {
			dir = URLDecoder.decode(fileURL, "UTF-8");
			dir = dir.replace('/', File.separatorChar);
		} catch (UnsupportedEncodingException e) {
			// this should really never be called.
			e.printStackTrace();
		}
		return dir;
	}
}
