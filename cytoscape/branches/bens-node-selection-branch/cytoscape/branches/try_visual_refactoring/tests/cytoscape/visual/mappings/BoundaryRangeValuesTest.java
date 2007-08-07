
/*
  File: BoundaryRangeValuesTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
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

// BoundaryRangeValuesTest.java


//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.visual.mappings.BoundaryRangeValues;

//----------------------------------------------------------------------------
public class BoundaryRangeValuesTest extends TestCase {

//----------------------------------------------------------------------------
    public BoundaryRangeValuesTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testAll () throws Exception { 
	BoundaryRangeValues bv = new BoundaryRangeValues();
	assertTrue( bv.lesserValue == null );
	assertTrue( bv.equalValue == null );
	assertTrue( bv.greaterValue == null );

	Double d1 = new Double(1.0);
	Double d2 = new Double(2.0);
	Double d3 = new Double(3.0);
	BoundaryRangeValues bv2 = new BoundaryRangeValues(d1,d2,d3);
	assertTrue( bv2.lesserValue == d1 );
	assertTrue( bv2.equalValue == d2 );
	assertTrue( bv2.greaterValue == d3 );

    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (BoundaryRangeValuesTest.class));
    }
//----------------------------------------------------------------------------
}


