package org.cytoscape.data.webservice.cytscanner;

import giny.model.Edge;
import giny.model.Node;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.customgraphic.CustomGraphicsPool;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.URLImageCustomGraphics;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import static org.cytoscape.data.webservice.cytscanner.TwitterTerm.*; 

public class CyTScanner {
	
	private final String id;
	
	//Cleint Factory
	private final TwitterFactory factory;
	private final Twitter t;
	
	private CyNetwork root;
	
	public CyTScanner(String id, String pw) throws IllegalStateException, TwitterException {
		factory = new TwitterFactory();
		t = factory.getInstance(id, pw);
		
		t.getId();
		
		this.id = id;
		root = Cytoscape.createNetwork("Twitter Session (Login as " + id + "): " + new Date(System.currentTimeMillis()).toLocaleString());
	}
	
	public void start() throws TwitterException, IOException {
		final Set<Node> nodes = new HashSet<Node>();
		final Set<Edge> edges = new HashSet<Edge>();
		
		
		final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
		final VisualStyle vs = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle("Minimal");
		NodeAppearanceCalculator nac = vs.getNodeAppearanceCalculator();
		
		DiscreteMapping nodeGraphics = new DiscreteMapping(Cytoscape.getVisualMappingManager().getCustomGraphicsPool().getNullGraphics(),
				"ID", ObjectMapping.NODE_MAPPING);

//		edgeLineStyle.putMapValue("physical", LineStyle.SOLID);
//		edgeLineStyle.putMapValue("genetic", LineStyle.LONG_DASH);
		
		Node me = Cytoscape.getCyNode(id, true);
		
		nodes.add(me);
		CustomGraphicsPool pool = Cytoscape.getVisualMappingManager().getCustomGraphicsPool();
		
		final ResponseList<Status> tl = t.getHomeTimeline();
		for(Status st: tl) {
			final String twStr = st.getText();
			
			Node user = Cytoscape.getCyNode(st.getUser().getScreenName(), true);
			Node tweet = Cytoscape.getCyNode(Long.toString(st.getId()), true);
			nodes.add(user);
			nodes.add(tweet);
			CyCustomGraphics<?> cg = new URLImageCustomGraphics(st.getUser().getProfileImageURL().toString());
			if (cg.getImage().getHeight(null)> 48 || cg.getImage().getWidth(null) > 48)
				cg.resizeImage(48, 48);
				
			pool.addGraphics(user.getIdentifier(), cg);
			nodeGraphics.putMapValue(user.getIdentifier(), cg);
			nodeAttr.setAttribute(tweet.getIdentifier(), TWEET.name(), st.getText());
			nodeAttr.setAttribute(user.getIdentifier(), "Source", st.getSource());
			nodeAttr.setAttribute(tweet.getIdentifier(), "TYPE", "TWEET");
			nodeAttr.setAttribute(user.getIdentifier(), "TYPE", "USER");
			nodeAttr.setAttribute(tweet.getIdentifier(), "USER ID", user.getIdentifier());
			
			if(twStr.length()>40)
				nodeAttr.setAttribute(tweet.getIdentifier(), "LABEL", twStr.substring(0, 40) + "...");
			else
				nodeAttr.setAttribute(tweet.getIdentifier(), "LABEL", twStr);
			nodeAttr.setAttribute(user.getIdentifier(), "LABEL", st.getUser().getName());
			
			Edge e1 = Cytoscape.getCyEdge(me, user, "interaction", st.getSource(), true);
			Edge e2 = Cytoscape.getCyEdge(user, tweet, "interaction", "tweet", true);
			edges.add(e1);
			edges.add(e2);
		}
		
		final CyNetwork timeLineGraph = Cytoscape.createNetwork(nodes, edges, "Your timeline at " + new Date(System.currentTimeMillis()).toString(), root);
		final CyNode nestedNode = Cytoscape.getCyNode(timeLineGraph.getTitle(), true);
		nestedNode.setNestedNetwork(timeLineGraph);
		root.addNode(nestedNode);
		
		
		
		final Calculator nodeGraphicsCalc = new BasicCalculator(vs.getName() + "-"
				+ "NodeCustomGraphicsMapping", nodeGraphics, VisualPropertyType.NODE_CUSTOM_GRAPHICS);
		nac.setCalculator(nodeGraphicsCalc);
		
		
		Cytoscape.getVisualMappingManager().setVisualStyle(vs);
		System.out.println("Target VS = " + vs.getName());
		CyNetworkView view = Cytoscape.getNetworkView(timeLineGraph.getIdentifier());
		view.setVisualStyle(vs.getName());
		view.redrawGraph(false, true);
		
		
	}
	
	private void modVS(VisualStyle vs) {
		
	}

}
