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
 * Ranking of <code>Phenotype</code> string values with a given name 
 * from "lowest" to "highest" value
 *
 * @see Phenotype
 *
 * @author thorsson@systemsbiology.org
 */
package phenotypeGenetics;
import java.io.*;
import java.util.*;

// TODO:
// Flexible storage and add functions for phenovalues and valuerelations

public class PhenotypeRanking {

  String name;
  Vector notes;
  String []  phenovalues;
  String [][] valuerelations;  

  //-----------------------------------------------------------------------------
  public PhenotypeRanking ()
  {
    this (null);
  }
  //-----------------------------------------------------------------------------
  public PhenotypeRanking (String name)
  {
    this.name = name;
    notes = new Vector ();
    phenovalues = new String[0];
    valuerelations = new String[0][0];
  } // ctor
  //-----------------------------------------------------------------------------
  public boolean isEmpty ()
  {
    return( name == null && 
            notes.size() == 0 && 
            phenovalues.length == 0 && 
            valuerelations.length == 0);
  }
  //-----------------------------------------------------------------------------
  public void setName (String newName)
  {
    name = newName;
  }
  //-----------------------------------------------------------------------------
  public String getName ()
  {
    return name;
  }
  //-----------------------------------------------------------------------------
  public void addNote (String note)
  {
    notes.add (note);
  }
  //-----------------------------------------------------------------------------
  public String [] getNotes ()
  {
    return (String[]) notes.toArray (new String [0]);
  }
  //-----------------------------------------------------------------------------
  /**
   * Searches for the last occurence of the given argument, testing for equality
   * using the equals method. 
   * 
   * @param phenovalue The value of phenotype whose index is sought 
   *
   * @return the index of the last occurence of phenovalue; returns -1 if phenovalue 
   * is not found 
   */
  public int indexOf (String phenovalue){

    int returnVal=0;
    boolean found = false ; 
    if ( phenovalues.length != 0 ){
      for ( int i=0 ; i<phenovalues.length ; i++ ){
        if ( phenovalues[i].equals(phenovalue) ){
          returnVal = i ;
          found = true ; 
        }
      }
    }
    if ( found == false ) returnVal=-1 ; 
    return returnVal;
  }
  //-----------------------------------------------------------------------------
  /**
   * Determines the relation between two phenotype values 
   *
   * @param phenoValueA The value of the first phenotype
   * @param phenoValueB The value of the second phenotype
   *
   * @return the relation of the first phenotype value to the second; returns empty 
   *         string if either phenotype value is not found
   */
  public String relativePhenotypeValue(String phenotypeValueA, String phenotypeValueB ){ 
    String returnString = "";
    int indexA = indexOf( phenotypeValueA ); 
    int indexB = indexOf( phenotypeValueB ); 
    
    if ( indexA != -1 && indexB != -1 ){
      returnString = valuerelations[indexA][indexB]; 
    }
    return returnString; 
  }
  //-----------------------------------------------------------------------------
  /**
   * Sets the Phenotype values
   */
  public void setPhenotypeValues (String [] phenotypevalues)
  {
    phenovalues = phenotypevalues;
  }
  //-----------------------------------------------------------------------------
  public String [] getPhenotypeValues ()
  {
    return phenovalues;
  }
  //-----------------------------------------------------------------------------
  /**
   * Sets the relations between phenovalues
   */
  public void setValueRelations (String [][] valuerelationsIn)
  {
    valuerelations = valuerelationsIn;
  }
  //-----------------------------------------------------------------------------
  public String [][] getValueRelations ()
  {
    return valuerelations;
  }
  //-----------------------------------------------------------------------------
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();
    sb.append ("name: " + name + "\n");

    String [] notesArray = getNotes ();
    for (int i=0; i < notesArray.length; i++)
      sb.append ("note " + i + ") " + notesArray [i] + "\n");

    String [] phenovaluesArray = getPhenotypeValues ();
    sb.append("Phenotype values: {" );
    for (int i=0; i < phenovaluesArray.length-1; i++)
      sb.append (phenovaluesArray [i] + ",");
    sb.append (phenovaluesArray [phenovaluesArray.length-1] + " }\n");

    String [][] relationsMatrix = getValueRelations ();
    //Assumes square matrix, nv x nv
    int nv = relationsMatrix.length;
    sb.append ("Phenovalue relations: \n");
    for ( int i=0 ; i<nv ; i++ ){
      sb.append(" { ");
      for ( int j=0 ; j<nv-1 ; j++ )
        sb.append(relationsMatrix[i][j] + " , ");
      sb.append(relationsMatrix[i][nv-1] + " }\n" );
    }

    return sb.toString ();
  } // toString

} // PhenotypeRanking
