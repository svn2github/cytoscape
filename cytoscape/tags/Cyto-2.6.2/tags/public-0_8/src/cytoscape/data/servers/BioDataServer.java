// BioDataServer.java:  define the interface
//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.servers;
//-----------------------------------------------------------------------------------
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Hashtable;
import cytoscape.data.GoTerm;
//------------------------------------------------------------------------------
public interface BioDataServer {

  String getServerType () throws Exception;
    //---------------------------------------------------------------
    // load data into the server
    //---------------------------------------------------------------

  void addBindingPairs (Vector bindingPairs) throws Exception;
  void addGeneProducts (Vector geneProducts) throws Exception;
  void addGeneSynonyms (Vector geneSynonyms) throws Exception;
  void addGoTerms (Vector terms) throws Exception;
  void addBiologicalProcesses (Hashtable newHash) throws Exception;
  void addMolecularFunctions (Hashtable newHash) throws Exception;
  void addCellularComponents (Hashtable newHash) throws Exception;

  void clearGoPathTerminators () throws Exception;
  void addGoPathTerminators (Vector newTerminators) throws Exception;

  String describe () throws Exception;

    //---------------------------------------------------------------
    // get genes for testing, based upon current contents of the server
    //---------------------------------------------------------------

    String getGeneWithHighestBiologicalProcessCount (boolean multipleParentage)
           throws Exception;
    String getBioProcessTestGene (int numberOfBioProcessesSought, 
                                  boolean multipleParentage)
           throws Exception;

    //---------------------------------------------------------------
    // simple operations directly concerning genes
    //---------------------------------------------------------------

     String getCanonicalName (String geneName) throws Exception;
  String [] getSynonyms (String geneName) throws Exception;
  String [] getAllGenes () throws Exception;
     String getGeneInfo (String geneName) throws Exception;

     String getGoTermName (int id) throws Exception;

     int [] getBioProcessIDs (String geneName) throws Exception;
     String getBioProcessName (int id) throws Exception;

     int [] getMolecularFunctionIDs (String geneName) throws Exception;
     String getMolecularFunctionName (int id) throws Exception;

     int [] getCellularComponentIDs (String geneName) throws Exception;
     String getCellularComponentName (int id) throws Exception;
    //---------------------------------------------------------------
    // operations concerning (possibly complicated) process hierarchies
    // aka,  paths or ontologies
    //---------------------------------------------------------------
        // a Vector of Vectors of Integers, all the different, complete
        // paths from the specified GO bio process ID to the root
     Vector getAllGoHierarchyPaths (int goTermID) throws Exception;
     Vector getAllBioProcessPaths (int bioProcessID) throws Exception;
     Vector getAllMolecularFunctionPaths (int molecularFunctionID) 
            throws Exception;
     Vector getAllCellularComponentPaths (int molecularFunctionID) 
            throws Exception;

        // take one of the Vectors of Integers from above, and return
        // a truncated hierarchy (or a single category) according to
        // the current mapping
     String mapGoPathToSingleNode (Vector path) throws Exception;
     //String mapBioProcessHierarchyToSingleCategory (Vector hierarchy) 
     //   throws Exception;

  String [] eliminateDuplicatePaths (Vector allMappedPaths) 
            throws Exception;
  String [] eliminateDuplicatePaths (String [] allMappedPaths) 
            throws Exception;

//------------------------------------------------------------------------------
}  
