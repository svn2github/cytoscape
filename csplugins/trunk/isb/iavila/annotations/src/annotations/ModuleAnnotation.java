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
 * A container of a module annotation. It contains an <code>OntologyTerm</code> and 
 * its <code>double</code> p-value.
 *
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org, iliana.avila@gmail.com>
 */
package annotations;

import cytoscape.data.annotation.OntologyTerm;

public class ModuleAnnotation {

  /**
   * The <code>OntologyTerm</code> for this <code>ModuleAnnotation</code>.
   */
  protected OntologyTerm ontologyTerm;

  /**
   * The p-value for this <code>ModuleAnnotation</code>.
   */
  protected double pValue;

  /**
   * Constructor.
   */
  public ModuleAnnotation (){}//ModuleAnnotation

  /**
   * Constructor.
   * 
   * @param ontology_term the <code>OntologyTerm</code> for this <code>ModuleAnnotation</code>
   * @param p_value the pValue for this <code>ModuleAnnotation</code>
   */
  public ModuleAnnotation (OntologyTerm ontology_term, double p_value){
    setOntologyTerm(ontology_term);
    setPValue(p_value);
  }//ModuleAnnotation

  /**
   * Sets the <code>OntologyTerm</code> for this <code>ModuleAnnotation</code>.
   */
  public void setOntologyTerm (OntologyTerm ontology_term){
    this.ontologyTerm = ontology_term;
  }//setOntologyTerm

  /**
   * Sets the p-value for this <code>ModuleAnnotation</code>.
   */
  public void setPValue (double p_value){
    this.pValue = p_value;
  }//setPValue

  /**
   * @return the <code>OntologyTerm</code> for this <code>ModuleAnnotation</code>. 
   */
  public OntologyTerm getOntologyTerm (){
    return this.ontologyTerm;
  }//getOntologyTerm

  /**
   * @return the p-value for this <code>ModuleAnnotation</code>.
   */
  public double getPValue (){
    return this.pValue;
  }//getPValue
  
}//ModuleAnnotation
