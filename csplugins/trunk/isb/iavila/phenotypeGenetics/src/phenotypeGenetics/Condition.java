/**  Copyright (c) 2005 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
// Condition.java: an experimental condition in an epistasis experiment
//------------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package phenotypeGenetics;
//------------------------------------------------------------------------------
import java.io.*;
import java.util.*;
//------------------------------------------------------------------------------
/**
 *  An experimental condition, typically an environmental or genetic manipulation
 *  whose consequences are a change in the organism's phenotype.
 */
public class Condition {
  // zero or more name/value pairs:
  HashMap map;
  String gene;

  // define the recognized categories:
  final static public int CATEGORY_UNASSIGNED = 0;
  final static public int GENETIC = 1;
  final static public int ENVIRONMENTAL = 2;
  final static public int CATEGORY_SENTINEL = 3;  // marks end-of-range
  int category;

  // define the recognized alleleForms
  final static public int ALLELEFORM_UNASSIGNED = 100;
  final static public int LF = 101;
  final static public int LF_PARTIAL = 102;
  final static public int GF = 103;
  final static public int GF_PARTIAL = 104;
  final static public int DN = 105;
  final static public int ALLELEFORM_SENTINEL = 106;  // marks end-of-range
  int alleleForm;
  String allele;


  //-----------------------------------------------------------------------------
  public Condition ()
  {
    map = new HashMap ();
    category = CATEGORY_UNASSIGNED;
    alleleForm = ALLELEFORM_UNASSIGNED;
    allele = null;

  } // ctor
  //-----------------------------------------------------------------------------

  public boolean isEmpty(){
 
    return ( category == CATEGORY_UNASSIGNED  
             && alleleForm==ALLELEFORM_UNASSIGNED 
             && map.size()==0 
             && allele == null );

  }   
  //-----------------------------------------------------------------------------

  public void setCategory (int newValue) throws IllegalArgumentException
  {
    if (newValue < CATEGORY_UNASSIGNED || newValue >= CATEGORY_SENTINEL)
      throw new IllegalArgumentException ("illegal category value: " + newValue);

    category = newValue;

  }
  //-----------------------------------------------------------------------------
  public void setAlleleForm (int newValue) throws IllegalArgumentException
  {
    if (newValue < ALLELEFORM_UNASSIGNED || newValue >= ALLELEFORM_SENTINEL)
      throw new IllegalArgumentException ("illegal alleleForm value: " + newValue);

    alleleForm = newValue;
  }
  //-----------------------------------------------------------------------------
  public void setAllele (String newValue) 
  {
    allele = newValue;
  }
  //-----------------------------------------------------------------------------
  public static String categoryToString (int value)
  {
    switch (value) {
    case CATEGORY_UNASSIGNED:
      return "UNASSIGNED";
    case GENETIC:
      return "GENETIC";
    case ENVIRONMENTAL:
      return "ENVIRONMENTAL";
    default:
      return "ILLEGAL VALUE";
    } // switch on value

  } // categoryToString
  //-----------------------------------------------------------------------------
  public static String alleleFormToString (int value)
  {
    switch (value) {
    case ALLELEFORM_UNASSIGNED:
      return "UNASSIGNED";
    case LF:
      return "lf";
    case LF_PARTIAL:
      return "lf(partial)";
    case GF:
      return "gf";
    case GF_PARTIAL:
      return "gf(partial)";
    case DN:
      return "dn";
    default:
      return "ILLEGAL VALUE";
    } // switch on value

  } // alleleFormToString
  //-----------------------------------------------------------------------------
  public void setAttribute (String name, String value)
  {
    map.put (name, value);
  }
  //-----------------------------------------------------------------------------
  public String getAttribute (String name)
  {
    return (String) map.get (name);
  }
  //-----------------------------------------------------------------------------
  public void setGene (String newValue)
  {
    gene = newValue;
  }
  //-----------------------------------------------------------------------------
  public String getGene ()
  {
    return gene;
  }
  //-----------------------------------------------------------------------------
  public int getCategory ()
  {
    return category;
  }
  //-----------------------------------------------------------------------------
  public int getAlleleForm ()
  {
    return alleleForm;
  }
  //-----------------------------------------------------------------------------
  public HashMap getMap ()
  {
    return map;
  }
  //-----------------------------------------------------------------------------
  public String getAllele ()
  {
    return allele;
  }
  //-----------------------------------------------------------------------------
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    if (gene != null)  sb.append ("gene: " + gene + "\n");
    sb.append ("category: " + categoryToString (category) + "\n");
    sb.append ("alleleForm: " + alleleFormToString (alleleForm) + "\n");
    sb.append ("allele: " + allele + "\n");

    String [] keys = (String []) map.keySet().toArray (new String [0]);
    for (int i=0; i < keys.length; i++) 
      sb.append (keys [i] + ": " + map.get (keys [i]) + "\n");

    return sb.toString ();

  } // toString
  //-----------------------------------------------------------------------------
} // Condition
