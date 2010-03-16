
/*
  File: ThesaurusTest.java 
  
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

// ThesaurusTest.java


//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.synonyms.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import cytoscape.data.synonyms.*;
import cytoscape.unitTests.AllTests;
//------------------------------------------------------------------------------
/**
 * test the Thesaurus class
 */
public class ThesaurusTest extends TestCase {


//------------------------------------------------------------------------------
public ThesaurusTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
/**
 * make sure that the ctor properly initializes all relevant data structures
 * as seen through the standard getter methods
 */
public void testCtor () throws Exception
{ 
  AllTests.standardOut("testCtor");
  String species = "fugu";
  Thesaurus thesaurus = new Thesaurus (species);
  assertTrue (thesaurus.nodeLabelCount () == 0);
  assertTrue (thesaurus.getSpecies().equals (species));

} // testCtor
//-------------------------------------------------------------------------
/**
 * add some simple canonical/common pairs; make sure we can get them back.
 */
public void testAddSimplePairs () throws Exception
{ 
  AllTests.standardOut ("testAddSimplePairs");

  String [] canonical = {"YCR097W", "YCR096C", "YMR056C","YBR085W"};
  String [] common    = {"MAT1A",   "MATA2",   "AAC1",   "AAC3"};

  assertTrue (canonical.length == common.length);
  String species = "Saccharomyces cerevisiae";
  Thesaurus thesaurus = new Thesaurus (species);

  assertTrue (thesaurus.getSpecies().equals(species));
  for (int i=0; i < canonical.length; i++)
     thesaurus.add (canonical [i], common [i]);

  assertTrue (thesaurus.nodeLabelCount () == 4);

  for (int i=0; i < canonical.length; i++) {
    assertTrue (thesaurus.getCommonName (canonical [i]).equals (common [i]));
    assertTrue (thesaurus.getNodeLabel (common [i]).equals (canonical [i]));
    String [] allCommonNames = thesaurus.getAllCommonNames (canonical [i]);
    assertTrue (allCommonNames.length == 1);
    assertTrue (allCommonNames [0].equals (common [i]));
    }

} // testAddSimplePairs
//-------------------------------------------------------------------------
/**
 * same as testAddSimplePairs (above), but now test that duplicate entries
 * cannot be added.
 */
public void testAddDuplicatePairs () throws Exception
{ 
  AllTests.standardOut ("testAddDuplicatePairs");

  String [] canonical = {"YCR097W", "YCR096C", "YMR056C","YBR085W"};
  String [] common    = {"MAT1A",   "MATA2",   "AAC1",   "AAC3"};

  assertTrue (canonical.length == common.length);
  String species = "Saccharomyces cerevisiae";
  Thesaurus thesaurus = new Thesaurus (species);

  for (int i=0; i < canonical.length; i++)
    thesaurus.add (canonical [i], common [i]);

  assertTrue (thesaurus.nodeLabelCount () == 4);

  try { // adding duplicates should throw an exception
    thesaurus.add (canonical [canonical.length-1], common [canonical.length-1]);
    assertTrue (true);  // should not be reached
    }
  catch (IllegalArgumentException e) {;}

  try { // adding duplicates should throw an exception
    thesaurus.add (canonical [canonical.length-1], "yojo");
    assertTrue (true);  // should not be reached
    }
  catch (IllegalArgumentException e) {;}

  try { // adding duplicates should throw an exception
    thesaurus.add ("yojoYama",  common [canonical.length-1]);
    assertTrue (true);  // should not be reached
    }
  catch (IllegalArgumentException e) {;}

} // testAddDuplicatePairs
//-------------------------------------------------------------------------
/**
 * same as testAddPairs (above), but now test that the entries
 * -can- be added again after they have been removed.  
 */
public void testAddRemoveAddAgain () throws Exception
{ 
  AllTests.standardOut ("testAddRemoveAddAgain");

  String [] canonical = {"YCR097W", "YCR096C", "YMR056C","YBR085W"};
  String [] common    = {"MAT1A",   "MATA2",   "AAC1",   "AAC3"};

  assertTrue (canonical.length == common.length);
  String species = "Saccharomyces cerevisiae";
  Thesaurus thesaurus = new Thesaurus (species);

  for (int i=0; i < canonical.length; i++)
    thesaurus.add (canonical [i], common [i]);

  assertTrue (thesaurus.nodeLabelCount () == 4);

  for (int i=0; i < canonical.length; i++)
    thesaurus.remove (canonical [i], common [i]);

  assertTrue (thesaurus.nodeLabelCount () == 0);

  for (int i=0; i < canonical.length; i++)
    thesaurus.add (canonical [i], common [i]);

  assertTrue (thesaurus.nodeLabelCount () == 4);

  for (int i=0; i < canonical.length; i++) {
    assertTrue (thesaurus.getCommonName (canonical [i]).equals (common [i]));
    assertTrue (thesaurus.getNodeLabel (common [i]).equals (canonical [i]));
    String [] allCommonNames = thesaurus.getAllCommonNames (canonical [i]);
    assertTrue (allCommonNames.length == 1);
    assertTrue (allCommonNames [0].equals (common [i]));
    }

} // testAddRemoveAddAgain
//-------------------------------------------------------------------------
/**
 * add some simple canonical/common pairs, then add a bunch of alternate
 * common names; make sure we can get them back.
 */
public void testAddAlternateCommonNames () throws Exception
{ 
  AllTests.standardOut ("testAddAlternateCommonNames");

  String []  canonical = {"YCR097W", "YCR096C", "YMR056C","YBR085W"};
  String []     common = {"MAT1A",   "MATA2",   "AAC1",   "AAC3"};

  String [][] alternates = {{"alt00",  "alt01",   "alt02"},
                            {"alt10",  "alt11",   "alt12"},
                            {"alt20",  "alt21",   "alt22"},
                            {"alt30",  "alt31",   "alt32"}};


  assertTrue (canonical.length == common.length);
  String species = "Saccharomyces cerevisiae";
  Thesaurus thesaurus = new Thesaurus (species);

  for (int i=0; i < canonical.length; i++)
     thesaurus.add (canonical [i], common [i]);

  assertTrue (thesaurus.nodeLabelCount () == 4);

  for (int i=0; i < canonical.length; i++) 
    for (int j=0; j < alternates [i].length; j++)
      thesaurus.addAlternateCommonName (canonical [i], alternates [i][j]);


    // now each canonicalName should have 4 common names:  1 preferred
    // and 3 alternate

  for (int i=0; i < canonical.length; i++) {
    String [] allCommonNames = thesaurus.getAllCommonNames (canonical [i]);
    assertTrue (allCommonNames.length == 4);
    assertTrue (allCommonNames [0].equals (common [i]));
    for (int j=0; j < alternates [i].length; j++) {
      String alternate = alternates [i][j];
      assertTrue (allCommonNames [j+1].equals (alternates [i][j]));
      } // for j
    } // for i

} // testAddAlternateCommonNames
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (ThesaurusTest.class));
}
//------------------------------------------------------------------------------
} // ThesaurusTest


