package cytoscape.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.data.attr.MultiHashMapListener;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;


/**
 * Event handling for Property Sheet viewer
 * listens for SELECT events and creates a property sheet if there is a singly selected node
 * @author Allan Kuchinsky, Agilent Technologies
 *
 */
public class PropertySheetController 
implements PropertyChangeListener, SelectEventListener, MultiHashMapListener
{

	protected CyNode _selectedNode = null;
	protected CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
	protected Component cytoPanelComponent = null;
	protected CyNetwork currentNetwork = null;
	private boolean initialized = false;
	PropertySheetView pView = null;
	
	public PropertySheetController ()
	{
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent e) {
		

		if (e.getPropertyName() == Cytoscape.NETWORK_CREATED)
			System.out.println ("propertyChangeEvent: " + e.getPropertyName());
		{
			if (Cytoscape.getCurrentNetwork() == Cytoscape.getNullNetwork()) // do nothing if it's just the null network
			{
				return;
			}
			


			if (currentNetwork != null) {
				currentNetwork.removeSelectEventListener(this);
			}


			// Change the target network
			currentNetwork = Cytoscape.getCurrentNetwork();
			if (Cytoscape.getCurrentNetwork() == Cytoscape.getNullNetwork())
			{
				currentNetwork = null;
			}
				
			if (currentNetwork != null) {
				System.out.println("Adding selectEventListener to: " + currentNetwork);
				currentNetwork.addSelectEventListener(this);
				
			}
		}
	}

	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param arg0 DOCUMENT ME!
	 */
	public void onSelectEvent(SelectEvent event) {
//		System.out.println("Selected item: " + event.getTarget() + " of type: " + event.getTarget().getClass());
//		System.out.println("got selection Event Type: " + event.getTargetType());
		// TODO: it is always returning a node set for select event type!
//		if (event.getTargetType() == SelectEvent.SINGLE_NODE)
//		{
//			System.out.println("got selection Event Type: " + event.getTargetType());
//			Object target = event.getTarget();
//			if (target instanceof CyNode)
//			{
//				System.out.println("filling values from selected target: " + event.getTarget());
//				fillValuesFromSelectedNode((CyNode) target);
//			}
			// remove current property Sheet view if there is one

		_selectedNode = getSinglySelectedNode();
		if (_selectedNode != null)
		{
			updateInfoForSelectedNode();
		}
	}
	
	/**
	 * 
	 * @return CyNode for singly selectedNode, null if no nodes or multiple nodes are selected
	 */
	public CyNode getSinglySelectedNode()
	{
		Set nodes = currentNetwork.getSelectedNodes();
		CyNode myNode = null;
		if (nodes.size() == 1)
		{
			Iterator it = nodes.iterator();
			while (it.hasNext()) // loop will stop after 1 get
			{
				myNode = (CyNode) it.next();
				
			}
		}
		return myNode;
	}
	
	
	public void updateInfoForSelectedNode() {

//		if (pView != null) {
//			if (cytoPanelComponent != null) {
//				cytoPanel.remove(cytoPanelComponent);
//				cytoPanelComponent = null;
//				Cytoscape.getNodeAttributes().getMultiHashMap().removeDataListener(this);
//			}
//			pView = null;
//		}

		if (pView == null) {
			pView = new PropertySheetView("PropertySheet");
			JScrollPane scrollPane = new JScrollPane(pView.getTunablePanel());

			scrollPane.setBorder(BorderFactory.createEtchedBorder());
			scrollPane.setBackground(Cytoscape.getDesktop().getBackground());
	 
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			// pView.setSelectedNode(_selectedNode);
			cytoPanelComponent = cytoPanel.add("PropertySheet", 
//					pView.getTunablePanel());
					scrollPane);
			cytoPanel.setState(CytoPanelState.DOCK);
			// pView.initializePropertiesFromSelectedNode(_selectedNode);
			Cytoscape.getNodeAttributes().getMultiHashMap().addDataListener(this);
		}
		pView.fillValuesFromSelectedNode(_selectedNode);
		
	}
	
	/**
	 * 
	 * MapListener methods
	 * 
	 */
	public void attributeValueAssigned(java.lang.String objectKey, java.lang.String attributeName,
	                                   java.lang.Object[] keyIntoValue,
	                                   java.lang.Object oldAttributeValue,
	                                   java.lang.Object newAttributeValue) {
//		System.out.println("Attribute value assigned: " + objectKey + ", " + attributeName + ", " + oldAttributeValue + 
//				", " + newAttributeValue);
		if (pView != null)
		{
			if ((pView.isInitializing()) || (pView.isTunableChanging()))
			{
				return;
			}
		}
		_selectedNode = getSinglySelectedNode();
		if (_selectedNode == null) { return; }
		if (_selectedNode.getIdentifier().equals(objectKey))
		{
			updateInfoForSelectedNode();
		}		
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectKey DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 * @param keyIntoValue DOCUMENT ME!
	 * @param attributeValue DOCUMENT ME!
	 */
	public void attributeValueRemoved(java.lang.String objectKey, java.lang.String attributeName,
	                                  java.lang.Object[] keyIntoValue,
	                                  java.lang.Object attributeValue) {
		if (pView != null)
		{
			if ((pView.isInitializing()) || (pView.isTunableChanging()))
			{
				return;
			}
		}		
		_selectedNode = getSinglySelectedNode();
		if (_selectedNode == null) { return; }
		if (_selectedNode.getIdentifier().equals(objectKey))
		{
			updateInfoForSelectedNode();
		}		
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectKey DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 */
	public void allAttributeValuesRemoved(java.lang.String objectKey, java.lang.String attributeName) {
	}

	
}
