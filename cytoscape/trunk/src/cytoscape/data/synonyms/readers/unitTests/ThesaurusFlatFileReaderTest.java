// ThesaurusFlatFileReaderTest
//------------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data.synonyms.readers.unitTests;
//------------------------------------------------------------------------------
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.*;
import junit.framework.*;

import cytoscape.data.synonyms.*;
import cytoscape.data.synonyms.readers.ThesaurusFlatFileReader;
//------------------------------------------------------------------------------
/**
 * test the ThesaurusFlatFileReader class
 */
public class ThesaurusFlatFileReaderTest extends TestCase {


//------------------------------------------------------------------------------
public ThesaurusFlatFileReaderTest (String name) 
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
 *  read a small sample yeast thesaurus file
 */
public void testReadSmallYeastThesaurus () throws Exception
{ 
  System.out.println ("testReadSmallYeastThesaurus");

  String filename = "sampleData/yeastSmall.txt";
  ThesaurusFlatFileReader reader = new ThesaurusFlatFileReader (filename);
  Thesaurus thesaurus = reader.getThesaurus ();

  assertTrue (thesaurus.getSpecies().equals ("Saccharomyces cerevisiae"));
  assertTrue (thesaurus.canonicalNameCount () == 8);

    // the values of these next 3 arrays are extracted, by hand, from the flat file
  String [] canonical = {"YHR047C", "YBL074C", "YKL106W", "YLR027C", 
                         "YGL119W", "YBR236C", "YKL112W", "YMR072W"};
  String [] common    = {"AAP1'", "AAR2", "AAT1", "AAT2", 
                         "ABC1", "ABD1", "ABF1", "ABF2"};
  int [] alternateNameCount = {1, 0, 0, 1, 0, 0, 3, 0};

  for (int i=0; i < canonical.length; i++) {
    assertTrue (thesaurus.getCommonName (canonical [i]).equals (common [i]));
    assertTrue (thesaurus.getCanonicalName (common [i]).equals (canonical [i]));
    String [] alternateNames = thesaurus.getAlternateCommonNames (canonical [i]);
    if (i == 6) { // do a name-by-name comparison for this orf with 3 alternate names
      String orf = canonical [i];
      assertTrue (orf.equals ("YKL112W"));
      assertTrue (alternateNames[0].equals ("BAF1"));
      assertTrue (alternateNames[1].equals ("OBF1"));
      assertTrue (alternateNames[2].equals ("REB2"));
      } // i == 6
    for (int j=0; j < alternateNames.length; j++)
      System.out.println (canonical [i] + " -> " + alternateNames [j]);
    assertTrue (alternateNames.length == alternateNameCount [i]);
    }
  
} // testReadSmallYeastThesaurus
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  junit.textui.TestRunner.run (new TestSuite (ThesaurusFlatFileReaderTest.class));

} // main
//------------------------------------------------------------------------------
} // class ThesaurusFlatFileReaderTest
