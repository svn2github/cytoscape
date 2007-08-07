// IPBioDataServertest.java:  a junit test, focusing on the

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//  retrieval of synonyms and canonicalName information for genes,
//  information which is periodically updated from ????
//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.servers.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.isb.data.*;

import cytoscape.data.servers.IPBioDataServer;
//------------------------------------------------------------------------------
public class IPBioDataServerTest extends TestCase {

  static String serverName;
  static IPBioDataServer server;
//------------------------------------------------------------------------------
public IPBioDataServerTest (String name) 
{
  super (name);

} // ctor
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
/*
 * YPD contains this mapping (among many others)
 *
 *   ACT1   YFL039C ACT1/END7/ABY1/(SLC1)/YFL039C
 *
 *  the canonical name is YJL005W; all others are synonyms.  test
 *  this mapping here.
 *
**/
public void testGetCanonicalName () throws Exception
{ 
  System.out.println ("testGetCanonicalName");
  String result = server.getCanonicalName ("ACT1");
  assertTrue (result.equals ("YFL039C"));
  assertTrue (server.getCanonicalName ("YFL039C").equals  ("YFL039C"));

} // testGetCanonicalName
//-------------------------------------------------------------------------
/*
 * YPD contains this mapping (among many others)
 *
 *   ACT1   YFL039C ACT1/END7/ABY1/(SLC1)/YFL039C
 *
 *  the canonical name is YJL005W; all others are synonyms.  test
 *  this mapping here.  the answer should be:
 *
 *          END7 ABY1 YFL039C
 *
**/
public void testGetSynonyms () throws Exception
{ 
  String geneName = "ACT1";
  System.out.println ("testGetSynonyms");
  String [] result = server.getSynonyms (geneName);

  assertTrue (result.length == 3);  // both (SLC1) and ACT1 are ignored

} // testGetSynonyms
//-------------------------------------------------------------------------
public void testGetTestGenes () throws Exception
{ 
  System.out.println ("testGetTestCaseGenes");
  String geneA;  // for genes with single parentage;
  String geneB;  // for genes with multiple parentage;
  int bioProcessCountA, bioProcessCountB;
  boolean multipleParentage = false;

    //-------------------------------------------------------------------
    // first:  look for the gene w/highest path count, single parentage
    //-------------------------------------------------------------------
  geneA = server.getGeneWithHighestBiologicalProcessCount (multipleParentage);
  bioProcessCountA = server.getBioProcessIDs (geneA).length;
  int [] bioProcessIDs = server.getBioProcessIDs (geneA);
  //System.out.println ("test gene, max processes, single: " + geneA + 
  //                    ", pid count: " + bioProcessCountA);
  int pathCountGeneA = 0;
  for (int i=0; i < bioProcessIDs.length; i++) {
    //System.out.print (geneA + " (" + i + "," + bioProcessIDs [i]);
    Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [i]);
    pathCountGeneA += allPaths.size ();
    //System.out.println (allPaths);
    }

  //System.out.println ("\n         total GeneA paths: " + pathCountGeneA);

    //-------------------------------------------------------------------
    // first:  look for the gene w/highest path count, multiple parentage
    //-------------------------------------------------------------------
  multipleParentage = true;
  geneB = server.getGeneWithHighestBiologicalProcessCount (multipleParentage);
  bioProcessCountB = server.getBioProcessIDs (geneB).length;
  bioProcessIDs = server.getBioProcessIDs (geneB);
  //System.out.println ("test gene, max processes, multiple: " + geneB + 
  //                    ", pid count: " + bioProcessCountB);
  int pathCountGeneB = 0;
  for (int i=0; i < bioProcessIDs.length; i++) {
    //System.out.print (geneB + " (" + i + "," + bioProcessIDs [i]);
    Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [i]);
    pathCountGeneB += allPaths.size ();
    //System.out.println (allPaths);
    }
  //System.out.println ("\n         total GeneB paths: " + pathCountGeneB);

  assertTrue (!geneA.equalsIgnoreCase (geneB));
  assertTrue (bioProcessCountA <= bioProcessCountB);
  // assertTrue (pathCountGeneA <= pathCountGeneB);


  for (int i=1; i < 5; i++) {
    geneA = server.getBioProcessTestGene (i, false);
    bioProcessCountA = server.getBioProcessIDs (geneA).length;
    assertTrue (bioProcessCountA == i);
    geneB = server.getBioProcessTestGene (i, true);
    bioProcessCountB = server.getBioProcessIDs (geneB).length;
    assertTrue (bioProcessCountB == i);
    assertTrue (!geneA.equalsIgnoreCase (geneB));
    } // for i

  //System.out.println ("\n         total GeneB paths: " + pathCountGeneB);

} // testGetTestCaseGenes
//-------------------------------------------------------------------------
/*
 * request a gene with only 1 GO process ontology ID, and in which that
 * ID has only a single pathway, leaf-to-node, that is:  just a single
 * hierarchy of biological processes
 * thus, we expect to see
 *   - one PID
 *   - one bio process hierarchy
**/
public void testGetBioProcessHierarchy_1_pid_singleHierarchy () throws Exception
{ 
  System.out.println ("testGetBioProcessHierarchy_1_pid_singleHierarchy");

  String gene = server.getBioProcessTestGene (1, false);
  int [] bioProcessIDs = server.getBioProcessIDs (gene);
  int bioProcessIDCount = bioProcessIDs.length;
  assertTrue (bioProcessIDCount == 1);
  Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [0]);
  // System.out.println (gene + ": " + bioProcessIDs [0] + ": " + allPaths);
  assertTrue (allPaths.size () == 1);

} // testGetBioProcessHierarchy_1_pid_singleHierarchy
//-------------------------------------------------------------------------
/*
 * request a gene with only 1 GO process ontology ID, but in which 
 * that GO term has two parents.  thus, there should be
 *   - one PID
 *   - two bio process hierarchies
**/
public void testGetBioProcessHierarchy_1_pid_multipleHierarchy () throws Exception
{ 
  System.out.println ("testGetBioProcessHierarchy_1_pid_multipleHierarchy");

  String gene = server.getBioProcessTestGene (1, true);
  int [] bioProcessIDs = server.getBioProcessIDs (gene);
  int bioProcessIDCount = bioProcessIDs.length;
  assertTrue (bioProcessIDCount == 1);

  Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [0]);
  //System.out.println (gene + ": " + bioProcessIDs [0] + ": " + allPaths);
  assertTrue (allPaths.size () == 2);

} // testGetBioProcessHierarchy_1_pid_multipleHierarchy
//-------------------------------------------------------------------------
/*
 * request a gene with exactly 2 GO process ontology ID's, with
 * single hierarchies for each ID.  thus we expect to see
 *   - two PID's
 *   - two bio process hierarchies
 *
**/
public void testGetBioProcessHierarchy_2_pid_singleHierarchy () throws Exception
{ 
  System.out.println ("testGetBioProcessHierarchy_2_pid_singleHierarchy");

  String gene = server.getBioProcessTestGene (2, false);
  int [] bioProcessIDs = server.getBioProcessIDs (gene);
  int bioProcessIDCount = bioProcessIDs.length;
  assertTrue (bioProcessIDCount == 2);

  for (int i=0; i < bioProcessIDCount; i++) {
    Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [i]);
    //System.out.println (gene + ": " + bioProcessIDs [i] + ": " + allPaths);
    assertTrue (allPaths.size () >= 1);
    }

} // testGetBioProcessHierarchy_2_pid_singleHierarchy
//-------------------------------------------------------------------------
/*
 * request a gene with exactly 2 GO process ontology ID's, with
 * a double  hierarchy for one or both ID.  thus we expect to see
 *   - two PID's
 *   - three or more bio process hierarchies
 *
**/
public void testGetBioProcessHierarchy_2_pid_multipleHierarchy () throws Exception
{ 
  System.out.println ("testGetBioProcessHierarchy_2_pid_multipleHierarchy");

  String gene = server.getBioProcessTestGene (2, true);
  int [] bioProcessIDs = server.getBioProcessIDs (gene);
  int bioProcessIDCount = bioProcessIDs.length;
  assertTrue (bioProcessIDCount == 2);

  int totalPaths = 0;
  for (int i=0; i < bioProcessIDCount; i++) {
    Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [i]);
    // System.out.println (gene + ": " + bioProcessIDs [i] + ": " + allPaths);
    totalPaths += allPaths.size ();
    }

  assertTrue (totalPaths >= 3);

} // testGetBioProcessHierarchy_2_pid_multipleHierarchy
//-------------------------------------------------------------------------
/*
 * request a gene with exactly 3 GO process ontology ID's, with
 * a single hierarchy for each.  thus we expect to see
 *   - 3 PID's
 *   - 3 process hierarchies
 *
**/
public void testGetBioProcessHierarchy_3_pid_singleHierarchy () throws Exception
{ 
  System.out.println ("testGetBioProcessHierarchy_3_pid_singleHierarchy");

  String gene = server.getBioProcessTestGene (3, false);
  int [] bioProcessIDs = server.getBioProcessIDs (gene);
  int bioProcessIDCount = bioProcessIDs.length;
  assertTrue (bioProcessIDCount == 3);

  for (int i=0; i < bioProcessIDCount; i++) {
    Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [i]);
    //System.out.println (gene + ": " + bioProcessIDs [i] + ": " + allPaths);
    assertTrue (allPaths.size () == 1);
    }


} // testGetBioProcessHierarchy_3_pid_singleHierarchy
//-------------------------------------------------------------------------
/*
 * request a gene with exactly 3 GO process ontology ID's, with
 * a double  hierarchy for one or more of the ID's.  thus we expect to see
 *   - 3 PID's
 *   - four or more bio process hierarchies
 *
**/
public void testGetBioProcessHierarchy_3_pid_multipleHierarchy () throws Exception
{ 
  System.out.println ("testGetBioProcessHierarchy_3_pid_multipleHierarchy");

  String gene = server.getBioProcessTestGene (3, true);

  int [] bioProcessIDs = server.getBioProcessIDs (gene);
  int bioProcessIDCount = bioProcessIDs.length;
  assertTrue (bioProcessIDCount == 3);

  int totalPaths = 0;
  for (int i=0; i < bioProcessIDCount; i++) {
    Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [i]);
    //System.out.println (gene + ": " + bioProcessIDs [i] + ": " + allPaths);
    totalPaths += allPaths.size ();
    }

  assertTrue (totalPaths >= 4);

} // testGetBioProcessHierarchy_2_pid_multipleHierarchy
//-------------------------------------------------------------------------
/*
 * check a few well-known GO terms
 *
**/
public void testGetBioProcessName () throws Exception
{ 
  System.out.println ("testGetBioProcessName");
  assertTrue (server.getBioProcessName (3673).equalsIgnoreCase ("Gene_Ontology"));
  assertTrue (server.getBioProcessName (8150).equalsIgnoreCase ("biological_process"));
  assertTrue (server.getBioProcessName (8152).equalsIgnoreCase ("metabolism"));

} // testGetBioProcessName
//-------------------------------------------------------------------------
/*
 * check a few well-known GO terms
 *
**/
public void testGetGOName () throws Exception
{ 
  System.out.println ("testGetBioProcessName");
  assertTrue (server.getGoTermName (3673).equalsIgnoreCase ("Gene_Ontology"));
  assertTrue (server.getGoTermName (8150).equalsIgnoreCase ("biological_process"));
  assertTrue (server.getGoTermName (8152).equalsIgnoreCase ("metabolism"));

} // testGetGOTermName
//-------------------------------------------------------------------------
/*
 * are bioProcess hierarchies appropriately collapsed to the simple
 * scheme used by Trey and Vesteinn in the Spring 2001 Science paper?
 *
 * this scheme is used:

 *   map some hand-crafted hierarchies, and check the results against
 *   answers we can figure out in advance
 *
 * note:  'CGM' is shorthand for 'cell growth and/or maintenance',
 *       a pivotal term in the current (august 2001) mapping scheme
 *
**/
public void testBioProcessHierarchyMappingExplicitly () throws Exception
{ 
  System.out.println ("testBioProcessHierarchyMappingExplicitly");

    // can we pick off the immediate subcategory of metabolism below CGM?
    // -----------------------------------------------------------------

  Vector hierarchy = new Vector ();
  hierarchy.addElement (new Integer (6511));  // ubiquitin-dependent protein degradation
  hierarchy.addElement (new Integer (6508));  // proteolysis and peptidolysis
  hierarchy.addElement (new Integer (6411));  // protein metabolism and modification
  hierarchy.addElement (new Integer (8152));  // metabolism
  hierarchy.addElement (new Integer (8151));  // cell growth and/or maintenance
  hierarchy.addElement (new Integer (8150));  // biological_process
  hierarchy.addElement (new Integer (3673));  // Gene_Ontology

  String result = server.mapGoPathToSingleNode (hierarchy);
  assertTrue (result.equals ("protein metabolism and modification"));


    // can we pick off the immediate subcategory below CGM, with metabolism absent?
    // ----------------------------------------------------------------------------

  hierarchy = new Vector ();
  hierarchy.addElement (new Integer (7120));  // axial budding
  hierarchy.addElement (new Integer (7114));  // budding
  hierarchy.addElement (new Integer (8151));  // cell growth and/or maintenance
  hierarchy.addElement (new Integer (8150));  // biological_process
  hierarchy.addElement (new Integer (3673));  // Gene_Ontology

  result = server.mapGoPathToSingleNode (hierarchy);
  assertTrue (result.equals ("budding"));
  
    // can we pick off the immediate subcategory below biological_proces, 
    // with CGM absent?
    // ----------------------------------------------------------------------------

  hierarchy = new Vector ();
  hierarchy.addElement (new Integer (6970));  // osmotic response
  hierarchy.addElement (new Integer (9628));  // response to abiotic stimulus
  hierarchy.addElement (new Integer (9605));  // response to external stimulus
  hierarchy.addElement (new Integer (7154));  // cell communication
  hierarchy.addElement (new Integer (8150));  // biological_process
  hierarchy.addElement (new Integer (3673));  // Gene_Ontology

  result = server.mapGoPathToSingleNode (hierarchy);
  assertTrue (result.equals ("cell communication"));

} // testBioProcessHierarchyMappingExplicitly
//-----------------------------------------------------------------------------  
/*
 * are bioProcess hierarchies appropriately collapsed to the simple
 * scheme used by Trey and Vesteinn in the Spring 2001 Science paper?
 *
 * this scheme is used:
 *    generate a number of gene names automatically, with different
 *    numbers of paths & parents, and check the results against
 *    the allowed, collapsed bio process terms
 *
**/
public void testBioProcessHierarchyMappingRandomly () throws Exception
{ 
  System.out.println ("testBioProcessHierarchyMappingRandomly");
  int maxGenes = 10;

  Vector paths = new Vector ();
  for (int i=1; i <= maxGenes; i++) {
    boolean multipleLineage = true;
    String gene = server.getBioProcessTestGene (i, multipleLineage);
    int [] bioProcessIDs = server.getBioProcessIDs (gene);
    int bioProcessIDCount = bioProcessIDs.length;
    assertTrue (bioProcessIDCount == i);
    for (int p=0; p < bioProcessIDCount; p++) {
      Vector allPaths = server.getAllBioProcessPaths (bioProcessIDs [p]);
      for (int v=0; v < allPaths.size (); v++) {
         Vector path = (Vector) allPaths.elementAt (v);
         String mappedCategory = server.mapGoPathToSingleNode (path);
         } // for v
      } // for p
    } // for i

} // testBioProcessHierarchyMappingRandomly
//-------------------------------------------------------------------------
/*
 * when bioProcessHierarchies are collapsed, previously unique
 * sibling hierarchies -- alternative process assignments made
 * in the lab, for the same gene -- will often become duplicates:
 * the differences are in the finer categorization, which our
 * current (august 2001)  mapping ignores
 * 
 * for example, YJL087C has 2 GO process ID's, each of which has 2
 * distinct paths from leaf to root
 *   id 1:  6388   tRNA splicing
 *   id 2:  6989   unfolded protein response, ligation of mRNA encoding 
 *                 UFP-specific transcription factor by RNA ligase
 *   the two paths associated with 6388 both collapse to 
 *      nucleobase, nucleoside, nucleotide and nucleic acid metabolism
 *      nucleobase, nucleoside, nucleotide and nucleic acid metabolism
 *   and the two paths assocated with 6989 map respectively to
 *      stress response  
 *      cell communication
 * 
 *  since the biological process go id's for a gene are subject
 *  to change, the most reliable (though highly contrived) test of duplicate 
 *  elimination is to create two go identical paths, and make sure
 *  that, after mapping & elimination, we have just one go term
 *
**/
public void testBioProcessHierarchyMappingDuplicateElimination () throws Exception
{ 
  System.out.println ("testBioProcessHierarchyMappingDuplicateElimination");
  String node0 = "cell organization and biogenesis";
  String node1 = "protein targeting";

  Vector allMappedPaths = new Vector ();
  allMappedPaths.addElement (node0);
  allMappedPaths.addElement (node1);
  allMappedPaths.addElement (node1);
  allMappedPaths.addElement (node1);
  allMappedPaths.addElement (node1);
  allMappedPaths.addElement (node1);
  allMappedPaths.addElement (node0);
  allMappedPaths.addElement (node0);


  String [] uniqueMappedPaths = server.eliminateDuplicatePaths (allMappedPaths);
  assertTrue (uniqueMappedPaths.length == 2);
  boolean foundNode0 = false;
  boolean foundNode1 = false;
  for (int i=0; i < uniqueMappedPaths.length; i++) {
    String path = uniqueMappedPaths [i];
    if (path.equals (node0)) foundNode0 = true;
    if (path.equals (node1)) foundNode1 = true;
    } // for i

  assertTrue (foundNode0 && foundNode1);

} // testBioProcessHierarchyMappingDuplicateElimination
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  if (args.length != 1) {
    System.out.println ("usage:  IPBioDataServerText <directory w/GO data>");
    System.exit (1);
    }

  server = new IPBioDataServer (args [0]);

  junit.textui.TestRunner.run (new TestSuite (IPBioDataServerTest.class));

}
//------------------------------------------------------------------------------
} // IPBioDataServerTest


