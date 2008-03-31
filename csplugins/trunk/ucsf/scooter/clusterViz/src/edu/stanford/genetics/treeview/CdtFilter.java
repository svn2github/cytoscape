/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: CdtFilter.java,v $
 * $Revision: 1.5 $
 * $Date: 2004/12/21 03:28:14 $
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

import java.io.*;

/**
 *  Class to filter through files for .cdt and .pcl files.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version    $Revision: 1.5 $ $Date: 2004/12/21 03:28:14 $
 */
public class CdtFilter extends javax.swing.filechooser.FileFilter implements FilenameFilter {
	/**
	 *  from the <code>FilenameFilter</code> interface.
	 *
	 * @param  dir   Directory to look in
	 * @param  file  File name
	 * @return       Returns true if file ends with .cdt or .pcl
	 */
	public boolean accept(File dir, String file) {
		dir = null;
		// don't use dir!!!
		if (file.toLowerCase().endsWith(".cdt")) {
			return true;
		}
		if (file.toLowerCase().endsWith(".pcl")) {
			return true;
		}
		return false;
	}


	/**
	 *  accepts or rejects files and directories
	 *
	 * @param  f  the file in question
	 * @return    returns true if it's a directory, or if it ends in .pcl or .cdt
	 */
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		return accept(f, f.getName());
	}

	/* inherit */
	public String getDescription() {
		return "CDT or PCL Files";
	}

}

