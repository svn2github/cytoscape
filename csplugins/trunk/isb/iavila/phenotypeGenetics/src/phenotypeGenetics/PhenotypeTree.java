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
/**
 * Encodes known relations among phenotypes, such as temporal order or overlap. 
 * Currently, a set of (independent) <code>PhenotypeRanking</code>s.
 *
 * @see PhenotypeRanking
 * @author thorsson@systemsbiology.org
 */
package phenotypeGenetics;
import java.io.*;
import java.util.*;

public class PhenotypeTree {

  String name;
  String organism;
  Vector notes;
  Vector phenos;  
  //-----------------------------------------------------------------------------
  public PhenotypeTree (String name, String organism)
  {
    this.name = name;
    this.organism = organism;
    notes = new Vector ();
    phenos = new Vector ();
  } // ctor
  //-----------------------------------------------------------------------------
  public void setName (String newValue)
  {
    name = newValue;
  }
  //-----------------------------------------------------------------------------
  public String getName ()
  {
    return name;
  }
  //-----------------------------------------------------------------------------
  public void setOrganism (String newValue)
  {
    organism = newValue;
  }
  //-----------------------------------------------------------------------------
  public String getOrganism ()
  {
    return organism;
  }
  //-----------------------------------------------------------------------------
  public void addNote (String note)
  {
    notes.add (note);
  }
  //-----------------------------------------------------------------------------
  public void addPhenotype (PhenotypeRanking pheno)
  {
    phenos.add (pheno);
  }
  //-----------------------------------------------------------------------------
  public int numberOfNotes ()
  {
    return notes.size ();
  }
  //-----------------------------------------------------------------------------
  public String [] getNotes ()
  {
    return (String []) notes.toArray (new String [0]);
  
  }
  //-----------------------------------------------------------------------------
  public int numberOfPhenotypes()
  {
    return phenos.size ();
  }
  //-----------------------------------------------------------------------------
  public PhenotypeRanking [] getPhenotypes ()
  {
    return (PhenotypeRanking []) phenos.toArray (new PhenotypeRanking [0]);
  }
  //-----------------------------------------------------------------------------
  /**
   * <code>PhenotypeRanking</code>s involving a particular phenotype name 
   *
   * @see PhenotypeRanking
   *
   * @author thorsson@systemsbiology.org
   */
  public PhenotypeRanking getPhenotypeWithName ( String phenoName )
  {

    PhenotypeRanking [] allPhenos = 
      (PhenotypeRanking []) phenos.toArray (new PhenotypeRanking [0]); 
    PhenotypeRanking phenoReturn = new PhenotypeRanking();

    for (int i=0; i < phenos.size(); i++){
      PhenotypeRanking pr = allPhenos[i];
      String name = pr.getName();
      if ( name.equals(phenoName) ) phenoReturn = pr;
    }
    return phenoReturn;
  } // get PhenotypeWithName

  //-----------------------------------------------------------------------------
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ("name: " + name + "\n");
    sb.append ("organism: " + organism + "\n");

    String [] notesArray = getNotes ();
    for (int i=0; i < notesArray.length; i++)
      sb.append ("note " + i + ") " + notesArray [i] + "\n");

    PhenotypeRanking [] phenoArray = getPhenotypes ();
    for (int i=0; i < phenoArray.length; i++)
      sb.append ("pheno " + i + ") " + phenoArray [i] + "\n");

    return sb.toString ();
  } // toString


} // PhenotypeTree
