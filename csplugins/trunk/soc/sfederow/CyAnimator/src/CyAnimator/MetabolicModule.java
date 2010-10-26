package CyAnimator;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import javax.swing.JFileChooser;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNode;
import cytoscape.visual.LineStyle;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.*;

public class MetabolicModule {
	private CyNetworkView networkView = null;
	private CyNetwork network = null;
	private FrameManager frameManager = null;
	
	private List<Node> nodeList = null;
	private List<Edge> edgeList = null;
	private List<NodeView> nodeViewList = null;
	private List<EdgeView> edgeViewList = null;
	private Map<String, Integer> concMap = null;
	private Map<String, Integer> fluxMap = null;
	
	
	public MetabolicModule(String filePath) throws IOException{
		System.out.println(filePath);
		String pathConc = filePath+"/concentrations.tab";
		String pathFlux = filePath+"/fluxes.tab";
		String pathS = filePath+"/S.tab";
		String pathRxns = filePath+"/rxns.tab";
		String pathMets = filePath+"/mets.tab";
		int rxncount = 50;
		int metcount = 32;
		int timepoints = 20;
		concMap = new HashMap<String,Integer>();
		fluxMap = new HashMap<String,Integer>();
		BufferedReader readConcbuffer = new BufferedReader(new FileReader(pathConc));
		String strRead;
		
		double concProfile[][] = new double[metcount][timepoints];
		double fluxProfile[][] = new double[rxncount][timepoints];
		
		
		//Read in the concentrations from a tab delimited file where each metabolite is a row
		//and each column is a timepoint in the simulation
		int i = 0;
		while ((strRead=readConcbuffer.readLine()) != null){

		
			
			String splitarray[] = strRead.split("\t");
			//System.out.println(splitarray[0]);
			
			double normfactor = 0;
			for(int k=0;k<timepoints;k++){
				if(Math.abs(Double.parseDouble(splitarray[k])) > normfactor){ 
					normfactor = Math.abs(Double.parseDouble(splitarray[k])); 
				}
			}
			
			for(int j=0;j<timepoints;j++){
				//System.out.println(splitarray[j]);
				concProfile[i][j] = Double.parseDouble(splitarray[j]);
			}
			i++;
			//String firstentry = splitarray[0];
			//String secondentry = splitarray[1];
			
		}
		
		BufferedReader readSbuffer = new BufferedReader(new FileReader(pathS));
		
		i = 0;
		while ((strRead=readSbuffer.readLine()) != null){
			String splitarray[] = strRead.split("\t");
			if(i==0){
				for(int j=0;j<splitarray.length;j++){
					//System.out.println(splitarray[j]+"\t"+j);
					fluxMap.put(splitarray[j],j);
				}
			}
			concMap.put(splitarray[0], i);
			i++;
		}
		
		BufferedReader readFluxbuffer = new BufferedReader(new FileReader(pathFlux));		
		i = 0;
		while ((strRead=readFluxbuffer.readLine()) != null){
			String splitarray[] = strRead.split("\t");
			
			double normfactor = 0;
			for(int k=0;k<timepoints;k++){
				if(Math.abs(Double.parseDouble(splitarray[k])) > normfactor){ 
					normfactor = Math.abs(Double.parseDouble(splitarray[k])); 
				}
			}
			for(int j=0;j<timepoints;j++){
				
				fluxProfile[i][j] = Double.parseDouble(splitarray[j])/normfactor;
			}
			i++;
		}

		
		
		//SmatrixToCyNetwork converter = new SmatrixToCyNetwork(pathS,pathRxns,pathMets);
		
		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();
		
		
		
		nodeList = network.nodesList();
		edgeList = network.edgesList();
		frameManager = new FrameManager();
		
		Pattern edgeSyntax = Pattern.compile("\\w+ \\((\\w+)\\) \\w+");
		Matcher tmp = null;
		/*
		for(int m=0;m<concProfile.length;m++){
			for(int n=0;n<concProfile[m].length;n++){
					System.out.println(concProfile[m][n]);
			}
		}
		*/
		/*
		for(int m=0;m<fluxProfile.length;m++){
			for(int n=0;n<fluxProfile[m].length;n++){
					System.out.println(fluxProfile[m][n]);
			}
		}
		*/
		/*
		for(Edge edge: edgeList){
			EdgeView edgeView = networkView.getEdgeView(edge);
			edgeView.setStrokeWidth(50);
			edgeView.setLineType(1);
			
			
		}
		*/
		
		
		
		networkView.updateView();
		Paint origColor = null;
		for(int k=1;k<timepoints;k++){
			
		
			for(Node node: nodeList){
				NodeView nodeView = networkView.getNodeView(node);
				if(k==1){ origColor = nodeView.getUnselectedPaint();}
				int col = fluxMap.get(node.getIdentifier());
				//System.out.println(node.getIdentifier()+"\t"+nodeView.getHeight()+"\t"+nodeView.getWidth());
				
				if(fluxProfile[col][k] < 0){ nodeView.setUnselectedPaint(Color.RED); }
				else{ nodeView.setUnselectedPaint(Color.GREEN);}//origColor); }
				
				//if(fluxProfile[col][k] < .05){
					//fluxProfile[col][k] = Math.abs(fluxProfile[col][k])*100;
				//}
				System.out.println(fluxProfile[col][k]);
				if(fluxProfile[col][k] == 0){ fluxProfile[col][k] = 1; }
				nodeView.setHeight(Math.abs(fluxProfile[col][k])*35);
				nodeView.setWidth(Math.abs(fluxProfile[col][k])*35);
				
				
			}
			
			for(Edge edge: edgeList){
				EdgeView edgeView = networkView.getEdgeView(edge);
				tmp = edgeSyntax.matcher(edge.getIdentifier());
				
				
				
				//edgeView.setUnselectedPaint(Color.GRAY);
				//System.out.println(edgeView.getStrokeWidth());
				//System.out.println(edgeView.getLineType());
				if(tmp.find()){
					
					int row = concMap.get(tmp.group(1));
					Stroke oldStroke = edgeView.getStroke();
					try{
						Stroke newStroke = LineStyle.extractLineStyle(oldStroke).getStroke(Float.parseFloat(concProfile[row][k]+"")*3);
					
						edgeView.setStroke(newStroke);
						edgeView.setStrokeWidth(Float.parseFloat(concProfile[row][k]+"")*5);
					}catch (Exception excp) {
	    				System.out.println("hey"+excp.getMessage()); 
	    			}
					//System.out.println(tmp.group(1)+"\t"+row+"\t"+k);
					//edgeView.setStrokeWidth(100*(float)concProfile[row][k]);
					//System.out.println(concProfile[row][k]);
				}
			}
			
			frameManager.addKeyFrame();
			
		}
			
		//networkView.updateView();
		ArrayList<CyFrame> adjFrames = new ArrayList<CyFrame>();
		ArrayList<CyFrame> frameList = frameManager.getKeyFrameList();
		for(CyFrame frame: frameList){
			frame.setInterCount(10);
			adjFrames.add(frame);
		}
		frameManager.setKeyFrameList(adjFrames);
		frameManager.play();
		
		
		
	}

	
	class SmatrixToCyNetwork{
		 
		 String[] rxns;
		 String[] mets;
		
		 
		 public SmatrixToCyNetwork(String pathS, String pathRxns,String pathMets) throws Exception{
			 BufferedReader readRxnbuffer = new BufferedReader(new FileReader(pathRxns));
			 String strRead;
			 	
			 while((strRead=readRxnbuffer.readLine()) != null){

				rxns = strRead.split("\t");
			 }
				
				
			 BufferedReader readMetbuffer = new BufferedReader(new FileReader(pathMets));
				
			 while ((strRead=readMetbuffer.readLine()) != null){

				mets = strRead.split("\t");
			}
			 
		 }
		
		
		
		CyNetwork network = Cytoscape.createNetwork("S_matrix");
		
	//rxns.length;i++){
			//CyNode node = Cytoscape.getCyNode(rxns[i], true);
		
			//network.addNode(node);
		//}
		/*
		System.out.println(network.getNodeCount());
		*/
		public CyNetwork returnS(){
			return null;
		}
		
	 }
	public FrameManager getFrameManager(){
		return frameManager;
	}
}



