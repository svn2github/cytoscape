/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: Debug.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/18 06:50:18 $
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
package edu.stanford.genetics.treeview.core;

import edu.stanford.genetics.treeview.RCSVersion;

public class Debug {
	/*
	 * (0 < level < 5)
	 * 0 - Debug off
	 * 1 - Programmer Statements
	 * 2 -
	 * 3 -
	 * 4 -
	 * 5 - Everything
	 */
	public static int level = 1;
	static RCSVersion version = 
		new RCSVersion("$Id: Debug.java,v 1.5 2004/12/21 03:28:14 " + 
					   "alokito Exp $, debugging " + (level == 0 ? "off" : "level " + level));

	public static void print(Object caller, String message, Object argument) {
		if (level > 0) {
			String c = (caller == null) ? "" : caller.toString();
			String a = (argument == null) ? "" : argument.toString();
			
			if(c.length() > 100)
				c = c.substring(0, 99);
			if(a.length() > 100)
				a = a.substring(0, 99);
			System.out.println(c + " : " + message + " : " + a);
		}
	}
	public static void print(Object caller, String message) {
		if (level > 0) {
			print(caller, message, (Object) "No Argument");	
		}
	}	
	public static void print(String message, Object argument) {
		if (level > 0) {
			print((Object) "No Caller", message, argument);
		}
	}
	public static void print(String message) {
		if (level > 0) {
			print("No Caller", message, "No Arguments");
		}
	}
	public static void print(Exception e) {
		if (level == 5)
			e.printStackTrace();
	}
}

