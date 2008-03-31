/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: RCSVersion.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/12/21 03:28:13 $
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


import java.util.*;

public class RCSVersion {
	static Vector versions;
	static boolean added;
	String version;

	public RCSVersion(String version) {
		this.version = version;
		if(versions == null)
			versions = new Vector();
		versions.addElement(version);
	}

	public static Vector allVersions() {
		if(!added) {
			added = true;
			new RCSVersion("$Id: RCSVersion.java,v 1.4 2004/12/21 03:28:13 alokito Exp $");
		}
		return versions;
	}

	public String getVersion() {
		return version;
	}

	public static String getAllVersions() {
		StringBuffer b = new StringBuffer();
		for(int i = 0; i < versions.size(); i++)
			b.append(((RCSVersion)versions.elementAt(i)).version + "\n");
		return b.toString();
	}
}
