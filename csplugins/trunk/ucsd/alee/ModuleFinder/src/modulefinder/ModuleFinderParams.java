/**
 * 
 */
package modulefinder;

/**
 * @author alee
 *
 */
public class ModuleFinderParams {

	/**
	 * @param args
	 */
	
	int numOfModules = 10;
	int candidatePathLength = 2;
	int maxThreads = 1;
	
	String pairMiAttrName = "miGeneToClassMethod1";
	String classMiAttrName = "pairMi";
	String exprFileName = "~alee/research/classification_network/data/expression/PROSTATE_BROAD_MIT/Prostate.data";
	String pairMiFileName = "~alee/research/classification_network/data/expression/PROSTATE_BROAD_MIT/Prostate.miGenePairs";
	String classMiFileName = "~alee/research/classification_network/data/expression/PROSTATE_BROAD_MIT/Prostate.miGeneToClassMethod1";
	
	public ModuleFinderParams ()
    {

    }
    
    public ModuleFinderParams(int numOfModules, int candidatePathLength, int maxThreads) {
    	this.numOfModules = numOfModules;
    	this.candidatePathLength = candidatePathLength;
    	this.maxThreads = maxThreads;
    }	
    
}
