/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: DummyHeaderInfo.java,v $
 * $Revision: 1.9 $
 * $Date: 2005/11/25 07:24:08 $
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

import java.util.Observer;


public class DummyHeaderInfo implements HeaderInfo {
  String [] header1 = new String [] {"Bob1", "Alice1"};
  String [] header2 = new String [] {"Bob2", "Alice2"};
  String [] header3 = new String [] {"Bob3", "Alice3"};
  public String[] getHeader(int i) {
	if (i == 1) {
	  return header1;
	}
	if (i == 2) {
	  return header2;
	}
	  return header3;
  }

	/**
	 *  Gets the header info for gene/array i, col name
	 *
	 * @param  i  index of the header to get
	 * @return    The array of header values
	 */
	public String getHeader(int i, String name) {
	  return (getHeader(i))[getIndex(name)];
	}


	String [] names = new String [] {"Bob", "Alice"};
	/**
	 *  Gets the names of the headers
	 *
	 * @return    The list of names
	 */
	public String[] getNames() {
	  return names;
	}
	public int getNumNames() {
		return names.length;
	}
	public int getNumHeaders() {
	  return 3;
	}

	public int getIndex(String name) {
	  if (name.equals("Bob")) {
		return 0;
	  }
	  return 1;
	}

	public int getHeaderIndex(String id) {
		for (int i = 0; i < getNumHeaders(); i++) {
			if ((getHeader(i)[0]).equals(id)) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * noop, since this object is static.
	 */
	public void addObserver(Observer o) {}		
	public void deleteObserver(Observer o) {}
	public boolean addName(String name, int location) {return false;}
	public boolean setHeader(int i, String name, String value) {return false;}
	public boolean getModified() {return false;}
	public void setModified(boolean mod) {}

	public String getHeader(int rowIndex, int columnIndex) {
		  return (getHeader(rowIndex))[columnIndex];
	}

	  
}


