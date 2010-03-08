/*
  File: Instructions.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.data.eqn_attribs.interpreter;


public class Instructions {
	public static int FADD    = 1 << 4;  // add one float to another
	public static int FSUB    = 1 << 5;  // subtract one float from another
	public static int FMUL    = 1 << 6;  // multiply one float by another
	public static int FDIV    = 1 << 7;  // divide one float by another
	public static int FPOW    = 1 << 7;  // compute one float to the power of another
	public static int SCONCAT = 1 << 8;  // concatenate two strings
	public static int FFROMI  = 1 << 9;  // convert an integer to a float
	public static int BEQLF   = 1 << 10; // compare two floats for equality
	public static int BNEQLF  = 1 << 11; // compare two floats for inequality
	public static int BGTF    = 1 << 12; // determine if one float is greater than another
	public static int BLTF    = 1 << 13; // determine if one float is less than another
	public static int BGTEF   = 1 << 14; // determine if one float is greater than or equal to another
	public static int BLTEF   = 1 << 15; // determine if one float is less than or equal to another
	public static int BEQLS   = 1 << 16; // compare two strings for equality
	public static int BNEQLS  = 1 << 17; // compare two strings for inequality
	public static int BGTS    = 1 << 18; // determine if one string is greater than another
	public static int BLTS    = 1 << 19; // determine if one string is less than another
	public static int BGTES   = 1 << 20; // determine if one string is greater than or equal to another
	public static int BLTES   = 1 << 21; // determine if one string is less than or equal to another
	public static int CALL    = 1 << 22; // a function call
}
