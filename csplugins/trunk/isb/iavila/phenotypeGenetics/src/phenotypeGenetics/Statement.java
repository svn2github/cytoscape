/**  Copyright (c) 2003 Institute for Systems Biology
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
 * A container of a Biological Statement.
 * It contains a <code>String</code> node name, a <code>string</code> interaction
 * type, an <code>String[]</code> of nearest neighbors, an <code>OntologyTerm</code>
 * of the annotation, and the <code>double</code> p-value.
 *
 * @author Greg Carter
 */
package phenotypeGenetics;

import java.util.*;
import cytoscape.data.annotation.OntologyTerm;

public class Statement{

  /**
   * The <code>String</code> common name of the node.
   */
  protected String commonName;

  /**
   * The <code>String</code> canonical name of the node.
   */
  protected String canonicalName;

  /**
   * The <code>String</code> of the over-represented interaction class.
   */
  protected String interactionType;

  /**
   * The <code>OntologyTerm</code> of the over-represented annotation.
   */
  protected OntologyTerm ontologyTerm;

  /**
   * The <code>String[]</code> of first neighbors.
   */
  protected String[] neighbors;

  /**
   * The <code>String</code> of the alleleForm (lf, gf...).
   */
  protected String alleleForm;

  /**
   * The -log(p) from <code>StatementCalculator</code>.
   */
  protected double negLogP;

  /**
   * Constructor.
   * 
   * @param common_name
   * @param interaction_type
   * @param neighbors 
   * @param ontology
   * @param negLogP 
   */
  public Statement (String common_name, String allele, String[] neighbors, 
                    String interaction_type, OntologyTerm ontology, double negLogP){
    setCommonName(common_name);
    setAlleleForm(allele);
    setNeighbors(neighbors);
    setInteractionType(interaction_type);
    setOntologyTerm(ontology);
    setNegLogP(negLogP);
  }//Statement

  /**
   * Sets the <code>String</code> name of Node
   */
  public void setCommonName (String name){
    this.commonName = name;
  }//setCommonName

  /**
   * Sets the <code>String</code> name of Node
   */
  public void setCanonicalName (String name){
    this.canonicalName = name;
  }//setCanonicalName

  /**
   * Sets the <code>String</code> alleleForm of Node
   */
  public void setAlleleForm (String form){
    this.alleleForm = form;
  }//setAlleleForm

  /**
   * Returns the <code>String</code> alleleForm of Node
   */
  public String getAlleleForm (){
    return this.alleleForm;
  }//getAlleleForm

  /**
   * Sets the <code>String</code> of the interaction type
   */
  public void setInteractionType (String type){
    this.interactionType = type;
  }//setInteractionType

  /**
   * Sets the <code>String[]</code> of nearest neighbors
   */
  public void setNeighbors (String[] neighbors){
    this.neighbors = neighbors;
  }//setNeighbors

  /**
   * Sets the <code>OntologyTerm</code> of the annotation
   */
  public void setOntologyTerm (OntologyTerm oTerm){
    this.ontologyTerm = oTerm;
  }//setOntologyTerm

  /**
   * Sets the -log(p) for this <code>NNOverlap</code>.
   */
  public void setNegLogP (double p_value){
    this.negLogP = p_value;
  }//setNegLogP

  /**
   * Gets the <code>String</code> common name of the Node
   */
  public String getCommonName (){
    return this.commonName;
  }//getCommonName

  /**
   * Gets the <code>String</code> canonical name of the Node
   */
  public String getCanonicalName (){
    return this.canonicalName;
  }//getCanonicalName

  /**
   * Gets the <code>String</code> interaction type
   */
  public String getInteractionType (){
    return this.interactionType;
  }//getInteractionType

  /**
   * Gets the <code>OntologyTerm</code> 
   */
  public OntologyTerm getOntologyTerm (){
    return this.ontologyTerm;
  }//getOntologyTerm

  /**
   * Gets the <code>String[]</code> of nearest neighbors
   */
  public String[] getNeighbors (){
    return this.neighbors;
  }//getNeighbors

  /**
   * Gets the <code>int</code> number of nearest neighbors
   */
  public int getSize (){
    return this.neighbors.length;
  }//getSize

  /**
   * @return the p-value for this <code>Statement</code>.
   */
  public double getNegLogP (){
    return this.negLogP;
  }//getNegLogP

  /**
   * @return a <code>String</code> of the verb associated with the interaction
   */
  public String getVerb (){

    String theVerb = "is " + this.interactionType + " to";

    return theVerb;
  }//getVerb

  /**
   * @return a <code>String</code> of the verb associated with the interaction
   */
  public String getAlleleAction (){

    String theAction = "Not changing(?!)";

    if(this.alleleForm.compareTo("lf") == 0){
      theAction = "A loss-of-function mutation of";
    }else if(this.alleleForm.compareTo("gf") == 0){
      theAction = "A gain-of-function mutation of";
    }else if(this.alleleForm.compareTo("dn") == 0){
      theAction = "A dominant-negative mutation of"; 
    }else if(this.alleleForm.compareTo("lf(partial)") == 0){
      theAction = "A partial loss-of-function of";
    }else if(this.alleleForm.compareTo("gf(partial)") == 0){
      theAction = "A partial gain-of-function of";
    }
    
    return theAction;
  }//getAlleleAction
  
}//Statment
