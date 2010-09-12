package cytoscape.csplugins.wordcloud.test;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.csplugins.wordcloud.CloudParameters;
import cytoscape.csplugins.wordcloud.CloudWordInfo;
import cytoscape.csplugins.wordcloud.SemanticSummaryClusterBuilder;
import cytoscape.csplugins.wordcloud.SemanticSummaryManager;
import cytoscape.csplugins.wordcloud.SemanticSummaryParameters;
import cytoscape.csplugins.wordcloud.SingleWordCluster;
import cytoscape.csplugins.wordcloud.WordClusters;
import cytoscape.csplugins.wordcloud.WordPair;

public class SemanticSummaryClusterBuilderUnitTest extends TestCase 
{

	//Variables
	String cloudName = "CloudName";
	SemanticSummaryParameters parentParams;
	CloudParameters cloudParams;
	
	@Before
	public void setUp() throws Exception 
	{
		//Create parent parameters
		parentParams = new SemanticSummaryParameters();
		
		//Create a set of CyNodes
		List<CyNode> allNodes = new ArrayList<CyNode>();
		CyNode node1 = Cytoscape.getCyNode("Regulation of apoptosis", true);
		CyNode node2 = Cytoscape.getCyNode("Positive regulation of apoptosis", true);
		CyNode node3 = Cytoscape.getCyNode("Positive regulation of programmed cell death", true);
		CyNode node4 = Cytoscape.getCyNode("Immune response", true);//only one count
		CyNode node5 = Cytoscape.getCyNode("Activation of immune response", true);
		CyNode node6 = Cytoscape.getCyNode("Activation of humoral immune response", true);
		
		//Add nodes to list
		allNodes.add(node1);
		allNodes.add(node2);
		allNodes.add(node3);
		allNodes.add(node4);
		allNodes.add(node5);
		allNodes.add(node6);
		
		//Create CloudParameters object
		cloudParams = new CloudParameters();
		cloudParams.setCloudName(cloudName);
		cloudParams.setNetworkParams(parentParams);
		
		//Set Lists in Params
		List<String> nodeNames = new ArrayList<String>();
		for(Iterator<CyNode> iter = allNodes.iterator(); iter.hasNext();)
			nodeNames.add(iter.next().toString());
		
		parentParams.setNetworkNodes(nodeNames);
		
		cloudParams.setSelectedNodes(nodeNames);
		cloudParams.setSelectedNumNodes(6);
		cloudParams.setNetworkNumNodes(6);
		
		//Register Cloud
		parentParams.addCloud(cloudName, cloudParams);
	}

	@Test
	public void testInitialize() 
	{
		SemanticSummaryClusterBuilder builder = new SemanticSummaryClusterBuilder();
		builder.initialize(cloudParams);
		
		//Test queue stuff
		ArrayList<WordPair> pair = builder.getQueue().getQueue();
		WordPair firstPair = pair.get(0);
		String word1 = firstPair.getFirstWord();
		String word2 = firstPair.getSecondWord();
		Double val = firstPair.getProbability();
		assertTrue(word1.equals("programmed"));
		assertTrue(word2.equals("cell"));
		assertTrue(val == 6.0);
		
		//Test Cluster stuff
		WordClusters clusters = builder.getClusters();
		ArrayList<SingleWordCluster> wordClusters = clusters.getClusters();
		int size = wordClusters.size();
		assertTrue(size == 10);
	}

	@Test
	public void testClusterData() 
	{
		SemanticSummaryClusterBuilder builder = new SemanticSummaryClusterBuilder();
		builder.initialize(cloudParams);
		builder.clusterData(3.0);
		
		//Test queue stuff
		ArrayList<WordPair> pair = builder.getQueue().getQueue();
		WordPair firstPair = pair.get(0);
		String word1 = firstPair.getFirstWord();
		String word2 = firstPair.getSecondWord();
		Double val = firstPair.getProbability();
		assertTrue(word1.equals("immune"));
		assertTrue(word2.equals("response"));
		assertTrue(val == 2.0);
		
		//Test Cluster stuff
		WordClusters clusters = builder.getClusters();
		ArrayList<SingleWordCluster> wordClusters = clusters.getClusters();
		int size = wordClusters.size();
		assertTrue(size == 7);
		
	}

	@Test
	public void testBuildCloudWords() 
	{
		SemanticSummaryClusterBuilder builder = new SemanticSummaryClusterBuilder();
		builder.initialize(cloudParams);
		builder.clusterData(3.0);
		builder.buildCloudWords();
		
		//Test build cloudWordInfo objects
		ArrayList<CloudWordInfo> info = builder.getCloudWords();
		
		CloudWordInfo first = info.get(0);
		int firstSize = first.getFontSize();
		String firstWord = first.getWord();
		int firstCluster = first.getCluster();
		Color firstColor = first.getTextColor();
		assertTrue(firstSize == SemanticSummaryManager.getInstance().getNullSemanticSummary().getMaxFont());
		assertTrue(firstWord.equals("regulation"));
		assertTrue(firstCluster == 0);
		assertTrue(firstColor.equals(Color.BLACK));
		
		int lastSize = info.size() - 1;
		CloudWordInfo last = info.get(lastSize);
		int lastFontSize = last.getFontSize();
		String lastWord = last.getWord();
		int lastCluster = last.getCluster();
		Color lastColor = last.getTextColor();
		assertTrue(lastFontSize == SemanticSummaryManager.getInstance().getNullSemanticSummary().getMinFont());
		assertTrue(lastWord.equals("death"));
		assertTrue(lastCluster == 6);
		assertTrue(lastColor.equals(Color.GRAY));
	}

}
