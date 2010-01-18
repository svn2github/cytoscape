package cytoscape.coreplugins.biopax.action;

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.biopax.paxtools.io.simpleIO.SimpleExporter;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;

import cytoscape.Cytoscape;
import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;

public final class DisplayBiopaxXmlAction extends CytoscapeAction {
	private static final CyLogger log = CyLogger.getLogger(DisplayBiopaxXmlAction.class);

	private NodeView nodeView;
	
	public DisplayBiopaxXmlAction(NodeView nodeView) {
		this.nodeView = nodeView;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String nodeId = nodeView.getNode().getIdentifier();
		String biopaxId = Cytoscape.getNodeAttributes()
			.getStringAttribute(nodeId, MapBioPaxToCytoscape.BIOPAX_RDF_ID);
		Model m = BioPaxUtil.getNetworkModel(Cytoscape.getCurrentNetwork());
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
		JOptionPane.showMessageDialog( Cytoscape.getDesktop(), writer.toString());	
	}
	
}
