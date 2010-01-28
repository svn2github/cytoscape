package cytoscape.coreplugins.biopax.action;

import giny.view.NodeView;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.biopax.paxtools.io.simpleIO.SimpleExporter;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;

import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.coreplugins.biopax.util.BioPaxVisualStyleUtil;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;

public final class DisplayBiopaxXmlAction extends CytoscapeAction {
	public static final CyLogger log = CyLogger.getLogger(DisplayBiopaxXmlAction.class);

	private NodeView nodeView;
	
	public DisplayBiopaxXmlAction(NodeView nodeView) {
		this.nodeView = nodeView;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String nodeId = nodeView.getNode().getIdentifier();
		String biopaxId = Cytoscape.getNodeAttributes()
			.getStringAttribute(nodeId, MapBioPaxToCytoscape.BIOPAX_RDF_ID);
		Model m = BioPaxUtil.getNetworkModel(Cytoscape.getCurrentNetwork().getIdentifier());
		BioPAXElement bpe =  m.getByID(biopaxId);
		StringWriter writer = new StringWriter();
		if (bpe != null) {
			log.info("printing " + bpe + " OWL");
			try {
				SimpleExporter simpleExporter = new SimpleExporter(m.getLevel());
				//TODO Fix: it prints '<:null' instead '<bp:' when using writeObject method!
				simpleExporter.writeObject(writer, bpe);
			} catch (Exception e) {
				log.error("Faild printing '" + nodeId + "' to OWL", e);
			}
		} else {
			log.info("Node : " + nodeId 
					+ ", BP element not found : " + biopaxId);
		}
		
		String owlxml = writer.toString();
		String label = Cytoscape.getNodeAttributes().getStringAttribute(nodeId, BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
			
		Component component = Cytoscape.getDesktop()
			.findComponentAt((int)nodeView.getXPosition(), (int)nodeView.getYPosition());
		JOptionPane.showMessageDialog(component, owlxml,
				label, JOptionPane.PLAIN_MESSAGE);
	}
	
}
