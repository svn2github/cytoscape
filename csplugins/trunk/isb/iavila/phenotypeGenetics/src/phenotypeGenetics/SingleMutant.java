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
 * A representation of a single mutant containing information about gene name, 
 * allele, and allele form.
 *
 * @author thorsson
 */
package phenotypeGenetics;

import java.io.*;
import java.util.*;

public class SingleMutant implements Serializable {
  
  String name; // canonical name
  String commonName; // common name
  String allele; // allele
  int alleleForm; // allele form
  PhenoEnvironment pe; // environment VT:Consider whether to keep this
  
  /**  
   * Constructor
   */ 
  public SingleMutant () {
    alleleForm = Condition.ALLELEFORM_UNASSIGNED;
    pe = new PhenoEnvironment(); 
  }

  /**
   * Test for absence of all data
   */
  public boolean isEmpty () {
    return ( name==null && commonName==null && allele==null &&
             alleleForm==Condition.ALLELEFORM_UNASSIGNED &&
             pe.isEmpty() );
  }

  /**
   * Returns a short representation of the Single Mutant
   */
  public String toShortString () {
    return(new String(commonName+"("+Condition.alleleFormToString(alleleForm)+")"));
  } 

  /**
   * Returns the canonical name of the Mutant
   */
  public String getName () {
    return(this.name);
  }

  /**
   * Sets the canonical name of the mutant
   */
  public void setName (String n) {
    this.name = n;
  }

  /**
   * Returns the common Name of the mutant
   */
  public String getCommonName () {
    return(this.commonName);
  }

  /**
   * Sets the common name of the mutant
   */
  public void setCommonName (String n) {
    this.commonName = n;
  }

  /**
   * Gets the allele of the mutant
   */
  public String getAllele () {
    return(this.allele);
  }

  /**
   * Sets the allele of the mutant
   */
  public void setAllele (String a) {
    this.allele = a;
  }

  /**
   * Gets the allele form of the mutant
   */
  public int getAlleleForm () {
    return(this.alleleForm);
  }

  /**
   * Sets the allele form of the mutant
   */
  public void setAlleleForm (int f) {
    this.alleleForm = f;
  }

  /**
   * Gets the phenoEnvironment of the mutant
   */
  public PhenoEnvironment getPhenoEnvironment () {
    return(this.pe);
  }

  /**
   * Sets the phenoEnvironment of the mutant
   */
  public void setPhenoEnvironment (PhenoEnvironment pe) {
    this.pe = pe;
  }

}
