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
 * Populate a PhenotypeTree object from several pre-specified examples. 
 * PhenotypeTreeSelector will be eventually be superceded by a file 
 * reader e.g. PhenotypeTreeXmlReader.
 *
 * @see PhenotypeTree
 *
 * @version  %I%, %G% 
 * @author thorsson@systemsbiology.org
 */
package phenotypeGenetics;
import java.io.*; 
import org.jdom.*; 
import org.jdom.input.*; 
import org.jdom.output.*; 
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

public class PhenotypeTreeSelector { 
    
  Project project; 
  PhenotypeTree phenotypeTree;

  // The expected Project Names and Organisms
  String[][] permittedNameOrgPairs = 
  { {"Apoptosis","C. elegans"}, {"Sex determination","C. elegans"}, 
		{"RAS signaling", "C. elegans"}, {"Adhesion/filamentation","S. cerevisiae"} };

  //-------------------------------------------------------------------------
  /**
   * @param exampleIdentifier the tag for the example in pathway.organism convention .
   * 
   * @exception IllegalArgumentException If exampleIdentifier is not among one of: 
   * Ras_signaling.cElegans, sex_fate.cElegans, apoptosis.cElegans, 
   * adhesion-invasion.sCerevisiae
   */
  public PhenotypeTreeSelector (Project project) throws IllegalArgumentException
  {
    this.project = project;  
    String projectName = project.getName(); 
    String projectOrganism = project.getOrganism();
    
    boolean foundFlag = false ; 
    for (int i = 0; i < permittedNameOrgPairs.length;i++){
      if ( (permittedNameOrgPairs[i][0]).equals(projectName) && 
           permittedNameOrgPairs[i][1].equals(projectOrganism) ) foundFlag = true;
    }// for i
    if ( foundFlag == false ) 
	    throw new IllegalArgumentException( "Project name " + projectName + 
                                          " and organism name " + projectOrganism + 
                                          " not found." ); 
    
  }
  //-------------------------------------------------------------------------
  public void read () 
  {
    String projectName = project.getName(); 
    String projectOrganism = project.getOrganism();    
    
    //Find project code index - constructor will have verified that string will be found 
    int projectCode=0; 
    for (int i = 0;i < permittedNameOrgPairs.length;i++){
	    if ( (permittedNameOrgPairs[i][0]).equals(projectName) && 
           (permittedNameOrgPairs[i][1]).equals(projectOrganism) ) projectCode = i;
    }
    
    // Codes
    // case 0: apoptosis.cElegans
    // case 1: sex_fate.cElegans
    // case 2: Ras_signaling.cElegans
    // case 3: adhesion-invasion.sCerevisiae 

    // phenotypes need some kind of a lookup table of relations, opposite, partial for starters
    // try to list order from low to high
    // Value matrix: element (i,j) corresponds to relation val_i RELATION val_j
		
    PhenotypeTree returnTree = new PhenotypeTree(projectName, projectOrganism);
    PhenotypeRanking pRank = new PhenotypeRanking(); 
	
    switch (projectCode) {

    case 0:   // case 0: apoptosis.cElegans
	    String [] cellFateVals = { "alive", "dead" } ; 
	    String[][] cellFateRelations = {
        { "EQUAL" , "LESS" },
        { "GREATER", "EQUAL" }
	    }; 
	    String [] cellEngulfmentVals = {"not engulfed", "engulfed"}; 
	    String[][] cellEngulfmentRelations = {
        { "EQUAL" , "LESS" },
        { "GREATER", "EQUAL" }
	    }; 
	    String [] dnaConditionVals = {"intact", "degraded"}; 
	    String[][] dnaConditionRelations = {
        { "EQUAL" , "LESS" },
        { "GREATER", "EQUAL" }
	    }; 

	    String phenoName = "cell_aliveness"; 
	    pRank = new PhenotypeRanking(phenoName);
	    pRank.setPhenotypeValues (cellFateVals);
	    pRank.setValueRelations(cellFateRelations);
	    returnTree.addPhenotype(pRank);

	    phenoName = "cell_engulfment";
	    pRank = new PhenotypeRanking(phenoName);
	    pRank.setPhenotypeValues (cellEngulfmentVals);
	    pRank.setValueRelations(cellEngulfmentRelations);
	    returnTree.addPhenotype(pRank);

	    phenoName = "DNA_condition";
	    pRank = new PhenotypeRanking(phenoName);
	    pRank.setPhenotypeValues (dnaConditionVals);
	    pRank.setValueRelations(dnaConditionRelations);
	    returnTree.addPhenotype(pRank);
	    break;

    case 1:   // case 1: sex_fate.cElegans
	    String [] xxFateVals ={"male", "hermaphrodite"}; 
	    String [][] xxFateRelations = {
        { "EQUAL", "OPPOSITE" },
        { "OPPOSITE", "EQUAL"}
	    };
	    String [] xoFateVals ={"hermaphrodite", "male"}; 
	    String [][] xoFateRelations = {
        { "EQUAL", "OPPOSITE" },
        { "OPPOSITE", "EQUAL"}
	    };

	    phenoName = "XX";
	    pRank = new PhenotypeRanking("XX"); 
	    pRank.setPhenotypeValues (xxFateVals);
	    pRank.setValueRelations(xxFateRelations);
	    returnTree.addPhenotype(pRank);
	    pRank = new PhenotypeRanking("XO"); // We don't have XO in the .xml yet 
	    pRank.setPhenotypeValues (xoFateVals);
	    pRank.setValueRelations(xoFateRelations);
	    returnTree.addPhenotype(pRank);
	    break;
      
    case 2:   // case 2: Ras_signaling.cElegans
	    String [] vulvaVals = {"vulvaless", "weak vulvaless", "normal", "multivulva" };
	    String [][] vulvaRelations = {
        { "EQUAL", "LESS", "LESS", "LESS" },
        { "GREATER", "EQUAL", "LESS", "LESS" },
        { "GREATER", "GREATER", "EQUAL", "LESS"},
        { "GREATER", "GREATER", "GREATER", "EQUAL"}
	    };
	    phenoName = "vulva";
	    pRank = new PhenotypeRanking(phenoName);
	    pRank.setPhenotypeValues (vulvaVals);
	    pRank.setValueRelations( vulvaRelations );
	    returnTree.addPhenotype(pRank);
	    break;

    case 3:   // case 3: adhesion-invasion.sCerevisiae
	    String [] pseudohyphalVals = {"yeast form", "pseudohyphae", "hyperactive pseudohyphae" };
	    String [][] pseudohyphalRelations = {
        { "EQUAL", "LESS", "LESS" },
        { "GREATER", "EQUAL", "LESS" },
        { "GREATER", "GREATER", "EQUAL" }
	    };
	    String [] agarInvasionVals = 
        {"non-invasive", "hypo-invasive", "invasive", "hyper-invasive"};
	    String [][] agarInvasionRelations = {
        { "EQUAL", "LESS", "LESS", "LESS" },
        { "GREATER", "EQUAL", "LESS", "LESS" },
        { "GREATER", "GREATER", "EQUAL", "LESS"},
        { "GREATER", "GREATER", "GREATER", "EQUAL"}
	    };
	    String [] agarAdhesionVals = 
        {"non-adhesive", "hypo-adhesive", "adhesive", "hyper-adhesive"};
	    
      String [][] agarAdhesionRelations = {
        { "EQUAL", "LESS", "LESS", "LESS" },
        { "GREATER", "EQUAL", "LESS", "LESS" },
        { "GREATER", "GREATER", "EQUAL", "LESS"},
        { "GREATER", "GREATER", "GREATER", "EQUAL"}
	    };
	    
	    pRank = new PhenotypeRanking("cell morphology"); 
	    pRank.setPhenotypeValues (pseudohyphalVals);
	    pRank.setValueRelations( pseudohyphalRelations );
	    returnTree.addPhenotype(pRank);

	    phenoName = "agar invasion"; 
	    pRank = new PhenotypeRanking(phenoName);
	    pRank.setPhenotypeValues (agarInvasionVals);
	    pRank.setValueRelations( agarInvasionRelations );
	    returnTree.addPhenotype(pRank);

	    phenoName = "agar adhesion"; 
	    pRank = new PhenotypeRanking(phenoName);
	    pRank.setPhenotypeValues (agarAdhesionVals);
	    pRank.setValueRelations( agarAdhesionRelations );
	    returnTree.addPhenotype(pRank);
	    
	    break;
	    
    }
	
    phenotypeTree = returnTree; 
	
  } // read
    //-------------------------------------------------------------------------
  public PhenotypeTree getPhenotypeTree ()
  {
    return phenotypeTree ;
  }
  //-------------------------------------------------------------------------
} // class PhenotypeTreeSelector

