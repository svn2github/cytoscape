package cytoscape.plugin.cheminfo.similarity;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.plugin.cheminfo.ChemInfoPlugin.AttriType;

/**
 * This will calculate the similarity between the two CyNode that is 
 * connected to an CyEdge, if they both have Smiles/InChI specified. 
 *
 */
public abstract class SimilarityScore {
	/**
	 * An CyEdge instance
	 */
	protected CyEdge edge;
	
	protected CyNode node1;
	protected CyNode node2;
	protected String attribute;
	protected AttriType attrType;
	public SimilarityScore(CyEdge edge) {
		super();
		this.edge = edge;
		CyNode node1 = (CyNode) edge.getSource();
		CyNode node2 = (CyNode) edge.getTarget();		
	}
	
	public SimilarityScore(CyNode node1, CyNode node2, String attr, AttriType attrType) {
		this.node1 = node1;
		this.node2 = node2;
		this.attribute = attr;
		this.attrType = attrType;
	}
	
	/**
	 * This method will return the similarity score. 
	 * 
	 * @return
	 */
	public abstract double calculateSimilarity();

	public CyEdge getEdge() {
		return edge;
	}

	public void setEdge(CyEdge edge) {
		this.edge = edge;
	}
}
