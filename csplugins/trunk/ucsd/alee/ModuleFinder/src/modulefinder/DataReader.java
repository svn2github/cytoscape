package modulefinder;

import java.io.*;
import java.util.*;

public class DataReader {

	BufferedReader br;
	
	String [] interactorList = null;
	int [][] interaction = null;
	
	public void readHprdNetwork() throws Exception {
		String interactorFile = "PROSTATE_BROAD_MIT/cytoscape/all_entrez_vertex.txt";
		String interactionFile = "PROSTATE_BROAD_MIT/cytoscape/interaction_entrez.sif"; 
			
		br = new BufferedReader(new FileReader(Const.EXPR_FILE_DIR + interactorFile)); 
		
		String line = null; int i=0;
		
		while ((line=br.readLine())!=null) {
			interactorList[i] = line;
			i++;
		}
		
		br = new BufferedReader(new FileReader(Const.EXPR_FILE_DIR + interactionFile)); 
		line = br.readLine();
		
		String[] temp = null;
		while ((line=br.readLine())!=null) {
			temp = line.split(" ");
			//interaction[i][] = new int[2];
			interaction[i][0] = findIndex(interactorList, temp[0]);
			interaction[i][1] = findIndex(interactorList, temp[2]);
			
		}
	}
	
	public int findIndex(String[] list, String st) {
		for (int i=0; i<list.length; i++) {
			if (list[i].equals(st)) { return i; } 
		}
		return 0;
	}

/*	public ExpressionData readExpr(String fileName) throws Exception {
		br = new BufferedReader(new FileReader(fileName));
    
		String line = null;
		line = br.readLine();
		StringTokenizer st;
		
		for (int i=0; i<numGenes; i++) {
			st = new StringTokenizer(line);
			
			for (int j=0; j<numSamples; j++) {
				//mtxExpr[i][j] = Float.valueOf(st.nextToken()).floatValue();
				mtxExpr[i][j] = Float.parseFloat(st.nextToken());
			}
		}	
	}*/
	
	public HashMap readClassMi(String fileName) throws Exception {
		HashMap hm = new HashMap();
		
		br= new BufferedReader(new FileReader(fileName));
		
		return hm;
		
	}
	
	public HashSet readPairMi(String fileName) throws Exception {
		HashSet hm = new HashSet();
		
		
		return hm;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
