// MiscTest.java
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.util.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import cytoscape.util.Misc;
import java.awt.Color;
//------------------------------------------------------------------------------
public class MiscTest extends TestCase {


//------------------------------------------------------------------------------
public MiscTest (String name) 
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
public void testParseRGBTest () throws Exception
{ 
  System.out.println ("testRGBParseText");
  Color result = Misc.parseRGBText ("0,0,0");
  assertTrue (result.equals (Color.black));

  result = Misc.parseRGBText ("27,39,121");
  assertTrue (result.getRed () == 27);
  assertTrue (result.getGreen () == 39);
  assertTrue (result.getBlue () == 121);

  result = Misc.parseRGBText (" 27 , 39 , 121 ");
  assertTrue (result.getRed () == 27);
  assertTrue (result.getGreen () == 39);
  assertTrue (result.getBlue () == 121);

  result = Misc.parseRGBText ("255,255,255");
  assertTrue (result.equals (Color.white));

} // testParseRGBTest
//-------------------------------------------------------------------------
public void testParseList () throws Exception
{ 
  System.out.println ("testParseList");

  String delimiter = "::";
  String startToken = "(";
  String endToken = ")";
    // --------------------------------------------------------------------
    // 1) four tokens, separated by ::, surrounded with parens and whitespace
    // --------------------------------------------------------------------
  
  String a = "abcd";
  String b = "efgh";
  String c = "dog";
  String d = "cat";

  StringBuffer sb = new StringBuffer ();
  sb.append ("  ");
  sb.append (startToken);
  sb.append (a + delimiter);
  sb.append (b + delimiter);
  sb.append (c + delimiter);
  sb.append (d);
  sb.append (endToken);
  sb.append ("   ");

  String [] tokens = Misc.parseList (sb.toString (), startToken, endToken, delimiter);

  assertTrue (tokens.length == 4);
  assertTrue (tokens [0].equals (a));
  assertTrue (tokens [1].equals (b));
  assertTrue (tokens [2].equals (c));
  assertTrue (tokens [3].equals (d));

    // --------------------------------------------------------------------------
    // 2) add some embedded parens to the tokens.  they should survive the parsing
    // --------------------------------------------------------------------------
  a = "((abcd))";
  b = "ef()gh";
  c = "do))((g";
  d = "c*())*at";

  sb = new StringBuffer ();
  sb.append ("  ");
  sb.append (startToken);
  sb.append (a + delimiter);
  sb.append (b + delimiter);
  sb.append (c + delimiter);
  sb.append (d);
  sb.append (endToken);
  sb.append ("  ");

  tokens = Misc.parseList (sb.toString (), startToken, endToken, delimiter);
  assertTrue (tokens.length == 4);
  assertTrue (tokens [0].equals (a));
  assertTrue (tokens [1].equals (b));
  assertTrue (tokens [2].equals (c));
  assertTrue (tokens [3].equals (d));

    // --------------------------------------------------------------------------
    // 3) leave off the startToken.  
    // --------------------------------------------------------------------------
  a = "abcd))";
  b = "ef()gh";
  c = "do))((g";
  d = "c*())*at";

  sb = new StringBuffer ();
  sb.append (a + delimiter);
  sb.append (b + delimiter);
  sb.append (c + delimiter);
  sb.append (d);
  sb.append (endToken);
  sb.append ("   ");

  tokens = Misc.parseList (sb.toString (), startToken, endToken, delimiter);
  assertTrue (tokens.length == 1);

    // --------------------------------------------------------------------------
    // 4) leave off the endToken.  
    // --------------------------------------------------------------------------
  a = "abcd))";
  b = "ef()gh";
  c = "do))((g";
  d = "c*())*at";

  sb = new StringBuffer ();
  sb.append (startToken);
  sb.append (a + delimiter);
  sb.append (b + delimiter);
  sb.append (c + delimiter);
  sb.append (d);
  sb.append ("   ");

  tokens = Misc.parseList (sb.toString (), startToken, endToken, delimiter);
  assertTrue (tokens.length == 1);

    // --------------------------------------------------------------------------
    // 5) test some url's, which have embedded ":"
    // --------------------------------------------------------------------------
  a = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00600";
  b = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00562";
  c = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00500";
  d = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00860";
  String e = "http://www.genome.ad.jp/dbget-bin/get_pathway?org_name=hsa&mapno=00760";

  sb = new StringBuffer ();
  sb.append (startToken);
  sb.append (a + delimiter);
  sb.append (b + delimiter);
  sb.append (c + delimiter);
  sb.append (d + delimiter);
  sb.append (e);
  sb.append (endToken);

  tokens = Misc.parseList (sb.toString (), startToken, endToken, delimiter);
  assertTrue (tokens.length == 5);

  assertTrue (tokens [0].equals (a));
  assertTrue (tokens [1].equals (b));
  assertTrue (tokens [2].equals (c));
  assertTrue (tokens [3].equals (d));
  assertTrue (tokens [4].equals (e));

} // testParseList
//------------------------------------------------------------------------------
public void testGetPropertyValues () throws Exception
{ 
  System.out.println ("testGetPropertyValues");
  Properties props = new Properties ();
  props.put ("dog", "dozer");
  props.put ("cat", "(ernie::louie)");

  Vector dogs = Misc.getPropertyValues (props, "dog");
  assertTrue (dogs.size () == 1);

  Vector cats = Misc.getPropertyValues (props, "cat");
  assertTrue (cats.size () == 2);

  String [] catNames = (String []) cats.toArray (new String [0]);
  assertTrue (catNames.length == 2);
  assertTrue (catNames [0].equals ("ernie"));
  assertTrue (catNames [1].equals ("louie"));

} // testGetPropertyValues
//------------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (MiscTest.class));
}
//------------------------------------------------------------------------------
} // MiscTest
