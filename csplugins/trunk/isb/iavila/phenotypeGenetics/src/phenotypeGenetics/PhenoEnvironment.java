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
// PhenoEnvironment.java 
//-----------------------------------------------------------------------------------
package phenotypeGenetics;
//-----------------------------------------------------------------------------
import java.io.*;
import java.util.*;
//-----------------------------------------------------------------------------
/**
 * An enviroment consisting of multiple name-value pairs and the name of a phenotype
 */
public class PhenoEnvironment implements Serializable {

  HashMap environment;
  String phenoName; // Later: consider promoting to Vector or String array
  final static public String phenoNameString = "assay"; 

  //-----------------------------------------------------------------------------------
  /**
   * Construct new <code>PhenoEnvironment</code>    
   */
  public PhenoEnvironment() 
  {
    environment = new HashMap();
    // phenoName gets assigned to null
  }
  //------------------------------------------------------------------------------------ 
  /**
   * Construct new <code>PhenoEnvironment</code> from phenotype name and environment    
   */
  public PhenoEnvironment( String phenoName, HashMap environment ){

    this.phenoName = phenoName;
    this.environment = environment; 

  }
  //------------------------------------------------------------------------------------ 
  /**
   * Test for absence of all data
   */   
  public boolean isEmpty(){

    return ( environment.isEmpty() && phenoName==null ); 

  }
  //------------------------------------------------------------------------------------ 
  /**
   * Set the environment
   */
  public void setEnvironment( HashMap newEnv ){

    environment = newEnv ; 

  }
  //------------------------------------------------------------------------------------
  /**
   * Set the phenotype name 
   */
  public void setPhenoName( String newPhenoName  ){

    phenoName = newPhenoName ;

  }
  //------------------------------------------------------------------------------------
  /**
   * Get the environment
   */
  public HashMap getEnvironment ()
  {
    return environment;
  }
  //------------------------------------------------------------------------------------
  /**
   * Get the phenotype name 
   */
  public String getPhenoName ()
  {
    return phenoName;
  }
  //------------------------------------------------------------------------------------
  /**
   * Represent as a <code>HashMap</code>
   */
  public HashMap toHashMap ()
  {

    HashMap returnHash = new HashMap (); 
    returnHash.putAll( environment );
    returnHash.put( phenoNameString, phenoName );
    return returnHash;

  }
  //------------------------------------------------------------------------------------
  /**
   * Represent as a <code>String</code> 
   */
  public String toString ()
  {

    StringBuffer sb = new StringBuffer ();
    sb.append( phenoName  +", " );
    sb.append( environment.toString() );
    return sb.toString ();

  } // toString
  //------------------------------------------------------------------------------------


} // PhenoEnvironment
