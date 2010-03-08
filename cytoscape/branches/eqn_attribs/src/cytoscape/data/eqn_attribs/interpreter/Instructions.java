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
	public static final int FADD    = 1 << 0;  // add one float to another
	public static final int FSUB    = 1 << 1;  // subtract one float from another
	public static final int FMUL    = 1 << 2;  // multiply one float by another
	public static final int FDIV    = 1 << 3;  // divide one float by another
	public static final int FPOW    = 1 << 4;  // compute one float to the power of another
	public static final int SCONCAT = 1 << 5;  // concatenate two strings
	public static final int SCONV   = 1 << 6;  // convert anything to a string
	public static final int BEQLF   = 1 << 7;  // compare two floats for equality
	public static final int BNEQLF  = 1 << 8;  // compare two floats for inequality
	public static final int BGTF    = 1 << 9;  // determine if one float is greater than another
	public static final int BLTF    = 1 << 10; // determine if one float is less than another
	public static final int BGTEF   = 1 << 11; // determine if one float is greater than or equal to another
	public static final int BLTEF   = 1 << 12; // determine if one float is less than or equal to another
	public static final int BEQLS   = 1 << 13; // compare two strings for equality
	public static final int BNEQLS  = 1 << 14; // compare two strings for inequality
	public static final int BGTS    = 1 << 15; // determine if one string is greater than another
	public static final int BLTS    = 1 << 16; // determine if one string is less than another
	public static final int BGTES   = 1 << 17; // determine if one string is greater than or equal to another
	public static final int BLTES   = 1 << 18; // determine if one string is less than or equal to another
	public static final int BEQLB   = 1 << 19; // compare two booleans for equality
	public static final int BNEQLB  = 1 << 20; // compare two booleans for inequality
	public static final int CALL    = 1 << 21; // a function call
	public static final int FUMINUS = 1 << 22; // unary minus
	public static final int FUPLUS  = 1 << 23; // unary plus
}
