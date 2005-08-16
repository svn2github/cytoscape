package modulefinder;

import java.util.*;

import cytoscape.*;
import cytoscape.cytoscape.data.ExpressionData;

public class ModuleFinder implements Runnable {

	public ModuleFinderParams mfParams;
	public CyNetwork cyNetwork;
	public ExpressionData exprData;
	public HashMap classMi = null;  
	public HashSet pairMi = null;	
		
	public ModuleFinder(CyNetwork cyNetwork, ModuleFinderParams mfParams) {
		this.mfParams = mfParams;
		this.cyNetwork = cyNetwork;
	}
	
	public void run() {
		DataReader dr = new DataReader();
		
		try {
			exprData = new ExpressionData(mfParams.exprFileName);
		
			if (mfParams.classMiAttrName == null) {
				System.err.println("Load Class Mutual information as a Node Attribute!\n");
				throw new RuntimeException("Load Class Mutual Information as a Node Attribute!");
			} else {
				GraphObjAttributes nodeAttributes = cyNetwork.getNodeAttributes();
			
				for (Iterator nodeIt = cyNetwork.nodesIterator(); nodeIt.hasNext();) {
					classMi = (Node)nodeIt.next().
				}
			}
				
				//for test
				for (Iterator nodeIt = cyNetwork.nodesIterator();nodeIt.hasNext();) {
				      double [] tempArray = new double[attrNames.length];
				      Node current = (Node)nodeIt.next();
				      for(int j = 0;j<attrNames.length;j++){
					mRNAMeasurement tempmRNA = expressionData.getMeasurement(nodeAttributes.getCanonicalName(current),attrNames[j]);
					if(tempmRNA == null){
					  tempArray[j] = ZStatistics.oneMinusNormalCDFInverse(.5);
					}
			}
			
			if (mfParams.pairMiAttrName == null) {
				System.err.println("Load Pairsiwe Mutual Information as a Edge Attribute!\n");
				throw new RuntimeException("Load Pairwise Mutual Information as a Edge Attribute!");
			} else {
				
			}	
		} catch (Exception e) {
			System.err.println("[ModuleFinder]: error in data reading!" + e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
