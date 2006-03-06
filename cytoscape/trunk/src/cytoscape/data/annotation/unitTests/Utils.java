
/*
  File: Utils.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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

// Utils


//---------------------------------------------------------------------------------------------------
// $Revision$  
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.data.annotation.unitTests;
//---------------------------------------------------------------------------------------------------
import cytoscape.data.annotation.*;
//---------------------------------------------------------------------------------------------------
class Utils {


//---------------------------------------------------------------------------------------------------
/**
 *  creates a 12 term ontology, for use with the Annotation and Ontology tests.
 *
 *  for background;  annotations -- which are basically, a list of name/number pairs,
 *  -require- an ontology.  and an antology  is a controlled vocabulary of terms,
 *  usually in a hierarchy, that allows us to map the annotation's number to a 
 *  node in the hierarchy and (implicitly) to the  entire branch of the hierarchy of 
 *  which that node is the leaf.
 *
 *  this ontology looks like this:
 *
 *                          [90001]
 *   [80001]   [80005]  [80007]   [80004] [80010]
 *    [40]      [251]  [500,530]   [520]   [522]
 *                       [666]
 *
 */
static Ontology createMinimalKeggMetabolicPathwayOntology ()
{
  Ontology ontology = new Ontology ("KEGG", "Metabolic Pathways");

  String [] names = {"Metabolism",
                     "Carbohydrate Metabolism",
                     "Amino Acid Metabolism",
                     "Glutamate metabolism",
                     "Metabolism of Complex Carbohydrates",
                     "Aminosugars metabolism",
                     "Nucleotide Metabolism",
                     "Nucleotide sugars metabolism",
                     "Biosynthesis of Secondary Metabolites",
                     "Erythromycin biosynthesis",
                     "Pentose and glucuronate interconversions",
                     "Starch and sucrose metabolism",
                     "Two Parents",
                    };

  int [] ids = {90001, 80001, 80005, 80007, 80004, 80010, 40, 251, 500, 520, 522, 530, 666};
  int [][] parents = {{},
                      {90001},
                      {90001},
                      {90001},
                      {90001},
                      {90001},
                      {80001},
                      {80005},
                      {80007},
                      {80004},
                      {80010},
                      {80007},
                      {500, 530}};

  OntologyTerm [] terms = new OntologyTerm [names.length];
  for (int i=0; i < names.length; i++) {
    terms [i] = new OntologyTerm (names [i], ids [i]);
    for (int p=0; p < parents [i].length; p++)
      terms [i].addParent (parents [i][p]);
    ontology.add (terms [i]);
    } // for i
    
  
  /************************
  carbohydrateMetabolism.addParent (metabolism.getId ());
  aminoAcidMetabolism.addParent (metabolism.getId ());
  glutamateMetabolism.addParent (aminoAcidMetabolism.getId ());
  metabolismOfComplexCarbohydrates.addParent (metabolism.getId ());
  aminosugarsMetabolism.addParent (metabolismOfComplexCarbohydrates.getId ());
  nucleotideMetabolism.addParent (metabolism.getId ());
  nucleotideSugarsMetabolism.addParent (nucleotideMetabolism.getId ());
  biosynthesisOfSecondaryMetabolites.addParent (metabolism.getId ());
  erythromycinBiosynthesis.addParent (biosynthesisOfSecondaryMetabolites.getId ());
  pentoseAndGlucuronateInterconversions.addParent (carbohydrateMetabolism.getId ());
  starchAndSucroseMetabolism.addParent (metabolismOfComplexCarbohydrates.getId ());
  twoParents.addParent (530);
  twoParents.addParent (500);

  ontology.add (metabolism);
  ontology.add (carbohydrateMetabolism);
  ontology.add (aminoAcidMetabolism);
  ontology.add (glutamateMetabolism);
  ontology.add (metabolismOfComplexCarbohydrates);
  ontology.add (aminosugarsMetabolism);
  ontology.add (nucleotideMetabolism);
  ontology.add (nucleotideSugarsMetabolism);
  ontology.add (biosynthesisOfSecondaryMetabolites);
  ontology.add (erythromycinBiosynthesis);
  ontology.add (pentoseAndGlucuronateInterconversions);
  ontology.add (starchAndSucroseMetabolism);
  ontology.add (twoParents);
  *****************/

  return ontology;

} // createMinimalKeggMetabolicPathwayOntology
//------------------------------------------------------------------------------
} // class Utils


