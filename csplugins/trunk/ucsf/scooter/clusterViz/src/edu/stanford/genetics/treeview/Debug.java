/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: Debug.java,v $
 * $Revision: 1.6 $
 * $Date: 2006/08/18 06:50:17 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved.
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

import edu.stanford.genetics.treeview.RCSVersion;


/** I, Alok, didn't write this. */
public class Debug {
	public static final boolean on = true;
	static RCSVersion version = new RCSVersion("$Id: Debug.java,v 1.6 2006/08/18 06:50:17 rqluk Exp $, debugging" + (on ? "on" : "off"));

	public static void print(Object caller, String message, Object argument) {
		if(on) {
			String c = (caller == null) ? "" : caller.toString();
			String a = (argument == null) ? "" : argument.toString();

			if(c.length() > 79)
				c = c.substring(0, 79);
			if(a.length() > 77)
				a = a.substring(0, 77);
			System.out.println(c + ":" + message + "(" + a + ")");
		}
	}

	public static void print(Object caller, String message) {
		if(on)
			print(caller, message, null);
	}

	public static void print(String message) {
		if(on)
			print(null, message, null);
	}

}

