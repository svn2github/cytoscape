package cytoscape.data.readers;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import giny.view.*;
import giny.model.*;

public class GMLTree{
	GMLNode root;
	private static String NODE_OPEN = "[";
	private static String NODE_CLOSE = "]";
	public static int STRING = 0;
	public static int DOUBLE = 1;
	public static int INTEGER = 2;
	public GMLTree(){
		root = new GMLNode();
	}
	
	public GMLTree(GraphView myView){
		root = new GMLNode();
		root.addMapping("Creator",new GMLNode("\"Cytoscape\""));
		root.addMapping("Version",new GMLNode("1.0"));
		GMLNode graph = new GMLNode();
		root.addMapping("graph",graph);
		//for each node, add a mapping to the graph GMLNode
		Iterator viewIt = myView.getNodeViewsIterator();
		while(viewIt.hasNext()){
			NodeView currentView = (NodeView)viewIt.next();
			Node currentNode = currentView.getNode();
			GMLNode currentGML = new GMLNode();
			currentGML.addMapping("id",new GMLNode(""+(-currentNode.getRootGraphIndex())));
			currentGML.addMapping("label",new GMLNode("\""+currentView.getLabel()+"\""));
			GMLNode graphics = new GMLNode();
			graphics.addMapping("x",new GMLNode(""+currentView.getXPosition()));
			graphics.addMapping("y",new GMLNode(""+currentView.getYPosition()));
			currentGML.addMapping("graphics",graphics);
			graph.addMapping("node",currentGML);
		}
		viewIt = myView.getEdgeViewsIterator();
		while(viewIt.hasNext()){
			EdgeView currentView = (EdgeView)viewIt.next();
			Edge currentEdge = currentView.getEdge();
			GMLNode currentGML = new GMLNode();
			currentGML.addMapping("source",new GMLNode(""+(-currentEdge.getSource().getRootGraphIndex())));
			currentGML.addMapping("target",new GMLNode(""+(-currentEdge.getTarget().getRootGraphIndex())));
			GMLNode graphics = new GMLNode();
			graphics.addMapping("width",new GMLNode("1"));
			graphics.addMapping("type",new GMLNode("\"line\""));
			graphics.addMapping("fill",new GMLNode("\"#000000\""));
			currentGML.addMapping("graphics",graphics);
			graph.addMapping("edge",currentGML);
		}
	}
	public GMLTree(String filename){
		LinkedList tokenList = new LinkedList();
		TextFileReader reader = new TextFileReader(filename);
		reader.read();

		// handle the lines -> build GMLToken list
		StringTokenizer lines = new StringTokenizer(reader.getText(), "\n");

		while (lines.hasMoreTokens()) {
	    		StringTokenizer tokens = new StringTokenizer(lines.nextToken());
	    		while (tokens.hasMoreTokens()){
				tokenList.add(tokens.nextToken());
			}
    		}

		root = initializeTree(tokenList);
		
	}

	private static GMLNode initializeTree(List tokens){
		GMLNode result = new GMLNode();
		while(tokens.size()>0){
			String current = (String)tokens.remove(0);
			//expecting this be a key or a close node symbol
			if(current.equals(NODE_OPEN)){
				throw new RuntimeException("Error parsing GML file");
			}
			if(current.equals(NODE_CLOSE)){
				return result;	
			}
			//now find the thing we are trying to map
			String key = current;
			if(tokens.size() == 0){
				throw new RuntimeException("Error parsing GML file");
			}
			current = (String)tokens.remove(0);
			if(current.equals(NODE_OPEN)){
				result.addMapping(key,initializeTree(tokens));
			}
			else if(current.equals(NODE_CLOSE)){
				throw new RuntimeException("Error parsing GML file");
			}
			else{
				result.addMapping(key, new GMLNode(current));
			}
		}
		return result;
	}


	public String toString(){
		String result =  root.toString();
		return result.substring(3,result.length()-2)+"\n";
	}

	private Vector getVector(Vector keys,int type){
		Vector result = new Vector();
		GMLTree.getVector(root,keys,0,type,result);
		return result;
	}

	public Vector getVector(String keys,String delim,int type){
		Vector keyVector = new Vector();
		StringTokenizer tokenizer = new StringTokenizer(keys, delim);
		while(tokenizer.hasMoreTokens()){
			keyVector.add(tokenizer.nextToken());
		}
		return getVector(keyVector,type);
	
	}
	private static void getVector(GMLNode root, Vector keys, int index, int type, Vector result){
		Vector mapped = root.getMapping((String)keys.get(index));
		if(mapped != null){
			Iterator it = mapped.iterator();
			if(index >= keys.size()-1){
				while(it.hasNext()){
					GMLNode current = (GMLNode)it.next();
					if(current.terminal){
						if(type == STRING){
							result.add(current.stringValue());
						}
						else if(type == INTEGER){
							result.add(current.integerValue());
						}
						else if(type == DOUBLE){
							result.add(current.doubleValue());
						}
						else{
							throw new IllegalArgumentException("bad type");
						}
					}
				}
			}
			else{
				while(it.hasNext()){
					GMLNode current = (GMLNode)it.next();
					if(!current.terminal){
						GMLTree.getVector(current,keys,index+1,type,result);
					}
				}
			}
		}
	}
}
